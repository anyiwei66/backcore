/**
 * Copyright 2014 Coinport Inc. All Rights Reserved.
 * Author: c@coinport.com (Chao Ma)
 */

package com.coinport.coinex.bitway

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import scala.collection.mutable.Set

import com.coinport.coinex.api.model._
import com.coinport.coinex.common.Manager
import com.coinport.coinex.data._
import Currency._

object BlockContinuityEnum extends Enumeration {
  type BlockContinuity = Value
  val SUCCESSOR, GAP, REORG, OTHER_BRANCH, DUP = Value
}

object BitwayManager {
  final val INDEX_LIST_LIMIT: Map[Currency, Int] = Map(
    Btc -> 10,
    Ltc -> 20,
    Pts -> 30
  )
}

class BitwayManager(supportedCurrency: Currency) extends Manager[TBitwayState] {

  val unusedAddresses = Set.empty[String]
  val usedAddresses = Set.empty[String]
  val hotAddresses = Set.empty[String]
  val coldAddresses = Set.empty[String]
  val blockIndexes = ArrayBuffer.empty[BlockIndex]

  val FAUCET_THRESHOLD: Double = 0.5
  val INIT_ADDRESS_NUM: Int = 100

  def getSnapshot = TBitwayState(
    supportedCurrency,
    blockIndexes,
    unusedAddresses,
    usedAddresses,
    hotAddresses,
    coldAddresses
  )

  def loadSnapshot(s: TBitwayState) {
    unusedAddresses.clear
    unusedAddresses ++= s.unusedAddresses
    usedAddresses.clear
    usedAddresses ++= s.usedAddresses
    hotAddresses.clear
    hotAddresses ++= s.hotAddresses
    coldAddresses.clear
    coldAddresses ++= s.coldAddresses
    blockIndexes.clear
    blockIndexes ++= s.blockIndexes.to[ArrayBuffer]
  }

  def isDryUp = unusedAddresses.size == 0 || usedAddresses.size > unusedAddresses.size * FAUCET_THRESHOLD

  def allocateAddress: (Option[String], Boolean /* need fetch from bitway */ ) = {
    if (unusedAddresses.isEmpty) {
      (None, true)
    } else {
      val validAddress = unusedAddresses.headOption
      if (isDryUp)
        (validAddress, true)
      else
        (validAddress, false)
    }
  }

  def addressAllocated(address: String) {
    assert(unusedAddresses.contains(address))
    unusedAddresses.remove(address)
    usedAddresses.add(address)
  }

  def faucetAddress(addresses: Set[String]) {
    unusedAddresses ++= addresses
  }

  def getSupportedCurrency = supportedCurrency

  def getBlockIndexes: Option[ArrayBuffer[BlockIndex]] = Option(blockIndexes)

  def getCurrentBlockIndex: Option[BlockIndex] = {
    if (blockIndexes.size > 0)
      Some(blockIndexes(0))
    else None
  }

  def getCryptoCurrencyTransactionType(inputs: Set[String],
    outputs: Set[String]): Option[CryptoCurrencyTransactionType] = {
    object AddressSetEnum extends Enumeration {
      type AddressSet = Value
      val UNUSED, USED, HOT, COLD = Value
    }

    import AddressSetEnum._

    def getIntersectSet(set: Set[String]): ValueSet = {
      var enumSet = ValueSet.empty
      if ((set & unusedAddresses).nonEmpty)
        enumSet += UNUSED
      if ((set & usedAddresses).nonEmpty)
        enumSet += USED
      if ((set & hotAddresses).nonEmpty)
        enumSet += HOT
      if ((set & coldAddresses).nonEmpty)
        enumSet += COLD
      return enumSet
    }

    // Transfer will disable someone withdrawal to his deposit address.
    // Which means one CryptoCurrencyTransaction can't has two types: Deposit as well as Withdrawal
    val inputsMatched = getIntersectSet(inputs)
    val outputsMatched = getIntersectSet(outputs)
    if (inputsMatched.contains(USED) && outputsMatched.contains(HOT)) {
      Some(CryptoCurrencyTransactionType.UserToHot)
    } else if (inputsMatched.contains(HOT)) {
      assert(outputsMatched.contains(USED))
      if (outputsMatched.contains(COLD)) {
        Some(CryptoCurrencyTransactionType.HotToCold)
      } else {
        Some(CryptoCurrencyTransactionType.Withdrawal)
      }
    } else if (inputsMatched.contains(COLD) && outputsMatched.contains(HOT)) {
      Some(CryptoCurrencyTransactionType.ColdToHot)
    } else if (outputsMatched.contains(USED)) {
      Some(CryptoCurrencyTransactionType.Deposit)
    } else if (inputsMatched.nonEmpty || outputsMatched.nonEmpty) {
      Some(CryptoCurrencyTransactionType.Unknown)
    } else {
      None
    }
  }

  import BlockContinuityEnum._

  def getBlockContinuity(blocksMsg: CryptoCurrencyBlocksMessage): BlockContinuity = {
    getBlockIndexes match {
      case None => SUCCESSOR
      case Some(indexList) if indexList.size > 0 =>
        assert(blocksMsg.blocks.size > 0)
        blocksMsg.startIndex match {
          case None =>
            if (blocksMsg.blocks.head.prevIndex.id == indexList.last.id)
              SUCCESSOR
            else if (indexList.exists(i => i.id == blocksMsg.blocks.head.index.id))
              DUP
            else
              GAP
          case Some(BlockIndex(Some(id), _)) =>
            if (id == indexList.last.id) SUCCESSOR else REORG
          case Some(BlockIndex(None, _)) => OTHER_BRANCH
        }
      case _ => SUCCESSOR
    }
  }

  def completeCryptoCurrencyTransaction(
    tx: CryptoCurrencyTransaction,
    prevBlock: Option[BlockIndex] = None,
    includedBlock: Option[BlockIndex] = None): Option[CryptoCurrencyTransaction] = {
    val CryptoCurrencyTransaction(_, _, _, inputs, outputs, _, _, _, status) = tx
    val txType = getCryptoCurrencyTransactionType(Set.empty[String] ++ inputs.get.map(_.address),
      Set.empty[String] ++ outputs.get.map(_.address))
    if (txType.isDefined) {
      val regularizeInputs = inputs.map(_.map(i => i.copy(
        innerAmount = i.amount.map(new CurrencyWrapper(_).internalValue(supportedCurrency)))))
      val regularizeOutputs = outputs.map(_.map(i => i.copy(
        innerAmount = i.amount.map(new CurrencyWrapper(_).internalValue(supportedCurrency)))))
      Some(tx.copy(inputs = regularizeInputs, outputs = regularizeOutputs,
        prevBlock = if (prevBlock.isDefined) prevBlock else getCurrentBlockIndex,
        includedBlock = includedBlock, txType = txType))
    } else {
      None
    }
  }

  def extractTxsFromBlocks(blocks: List[CryptoCurrencyBlock]): List[CryptoCurrencyTransaction] = {
    blocks.flatMap { block =>
      val CryptoCurrencyBlock(index, prevIndex, txsInBlock) = block
      txsInBlock.map(completeCryptoCurrencyTransaction(_, Some(prevIndex), Some(index))).filter(_.isDefined).map(_.get)
    }
  }

  import BitwayManager._

  def appendBlockChain(chain: List[BlockIndex], startIndex: Option[BlockIndex] = None) {
    val reorgPos = blockIndexes.indexWhere(Option(_) == startIndex) + 1
    if (reorgPos > 0) {
      blockIndexes.remove(reorgPos, blockIndexes.length - reorgPos)
    }
    blockIndexes ++= chain
    if (blockIndexes.length > INDEX_LIST_LIMIT.getOrElseUpdate(supportedCurrency, 10))
      blockIndexes.remove(0, blockIndexes.length - INDEX_LIST_LIMIT(supportedCurrency))
  }
}
