/**
 * Copyright (C) 2014 Coinport Inc. <http://www.coinport.com>
 *
 *
 * MarketManager is the maintainer of a Market. It executes new orders before
 * they are added into a market as pending orders. As execution results, a list
 * of Transactions are generated and returned.
 *
 * MarketManager can be used by an Akka persistent processor or a view
 * to reflect pending orders and market depth.
 *
 * Note this class does NOT depend on event-sourcing framework we choose. Please
 * keep it plain old scala/java.
 */

package com.coinport.coinex.markets

import scala.collection.mutable.Map
import scala.collection.mutable.SortedSet
import com.coinport.coinex.common.Manager
import com.coinport.coinex.common.RedeliverFilter
import com.coinport.coinex.data._
import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import Implicits._
import OrderStatus._
import RefundReason._

object MarketManager {
  private[markets] implicit val ordering = new Ordering[Order] {
    def compare(a: Order, b: Order) = {
      if (a.vprice < b.vprice) -1
      else if (a.vprice > b.vprice) 1
      else if (a.id < b.id) -1
      else if (a.id > b.id) 1
      else 0
    }
  }
}

class MarketManager(val headSide: MarketSide) extends Manager[TMarketState] {
  private[markets] val orderPools = Map.empty[MarketSide, SortedSet[Order]]
  private[markets] val orderMap = Map.empty[Long, Order]
  private[markets] var priceRestriction: Option[Double] = None

  private val tailSide = headSide.reverse
  private val bothSides = Seq(headSide, tailSide)

  import MarketManager._

  def addOrderToMarket(takerSide: MarketSide, order: Order): OrderSubmitted = {
    val txsBuffer = new ListBuffer[Transaction]
    val makerSide = takerSide.reverse
    var lastTxId = order.id * 1000000

    // START of internal matching logic.
    case class TakerState(takerOrder: Order, totalOutAmount: Long, totalInAmount: Long)

    // Transaction ids are derived from order ids.  The first transaction id = orderId * 1000000 + 1,
    // The system will fail if one taker order matches more than 999999 maker orders. This is a safe
    // assumption in normal cases. If many maker orders were matched against one taker order, the 
    // OrderSutmitted message will probably hit Akka's limit and cause the system to fail. Therefore the
    // 999999 transactions per order limit will not easily happen.
    def getTxId() = { lastTxId += 1; lastTxId }

    // Returns a tuple, the first element will be defined if and only if the order after
    // being refunded shall still be put back to the market.
    def calculateRefund(order: Order): (Option[Order], Option[Refund]) = {
      val result =
        if (order.quantity == 0) (None, None)
        else if (order.hitTakeLimit) (None, Some(Refund(HitTakeLimit, order.quantity)))
        else if (order.isDust) (None, Some(Refund(Dust, order.quantity)))
        else if (order.price.isEmpty || order.onlyTaker.getOrElse(false)) (None, Some(Refund(AutoCancelled, order.quantity)))
        else order.takeLimit match {
          case Some(limit) if limit > 0 =>
            val overCharged = order.quantity - Math.ceil(limit / order.price.get).toLong
            if (overCharged > 0) (Some(order.copy(quantity = order.quantity - overCharged)), Some(Refund(OverCharged, overCharged)))
            else (Some(order), None)
          case _ => (Some(order), None)
        }

      // Assertions for making sure market depth calculations fit.
      if (order.price.isDefined) {
        val orderWithRefund = order.copy(refund = result._2) // same as in transaction
        result._1 match {
          case Some(addbackOrder) =>
            assert(orderWithRefund.maxOutAmount(order.price.get) == addbackOrder.maxOutAmount(order.price.get))
            assert(orderWithRefund.maxInAmount(order.price.get) == addbackOrder.maxInAmount(order.price.get))
          case None =>
            assert(orderWithRefund.maxOutAmount(order.price.get) == 0)
            assert(orderWithRefund.maxInAmount(order.price.get) == 0)
        }
      }
      result
    }

    @tailrec
    def recursivelyMatchOrder(state: TakerState): TakerState = {
      val takerOrder = state.takerOrder
      val makerOrderOption = orderPool(makerSide).headOption
      if (makerOrderOption.isEmpty || makerOrderOption.get.vprice * takerOrder.vprice > 1) {
        state
      } else {
        // We get the top maker order, the one with the lowest price, and we know for sure
        // the maker's price is lower than or equal to taker's price, so we found a match.
        val makerOrder = makerOrderOption.get

        // Maker orders defines market price, whenever a match is found, we always use maker's price,
        // note that taker's price will only be used to decide whether a match is found or not, it will
        // never be used in real transactions.
        val price = 1 / makerOrder.vprice

        // If we see the taker order as a order of selling BTC for CNY, then txOutAmount is 'the amount
        // of BTC in the new transaction'.
        val txOutAmount = Math.min(takerOrder.maxOutAmount(price), makerOrder.maxInAmount(1 / price))

        // txInAmount is 'the amount of CNY in the same transaction'.
        var txInAmount = Math.round(txOutAmount * price)

        // Because of precision problems introduced by Double math operations, we have to adjust txInAmount
        // by -1 to make sure the maker order can afford that much of CNY.
        if (makerOrder.maxOutAmount(1 / price) < txInAmount) txInAmount -= 1

        if (txOutAmount == 0 || txInAmount == 0) {
          state
        } else {
          val TakerState(takerOrder, totalOutAmount, totalInAmount) = state

          // Firstly we remove the in-memory maker order.
          removeOrder(makerOrder.id)

          // Now we update both taker and maker order.
          val updatedTaker = takerOrder.copy(
            quantity = takerOrder.quantity - txOutAmount,
            takeLimit = takerOrder.takeLimit.map(limit => Math.max(limit - txInAmount, 0L)),
            inAmount = takerOrder.inAmount + txInAmount)

          val updatedMaker = makerOrder.copy(
            quantity = makerOrder.quantity - txInAmount,
            takeLimit = makerOrder.takeLimit.map(limit => Math.max(limit - txOutAmount, 0L)),
            inAmount = makerOrder.inAmount + txOutAmount)

          // We now calculate how much money should unfreeze for the maker after this new transaction.
          val (updatedMakerToAddBack, refund) = calculateRefund(updatedMaker)
          val updatedMakerWithRefund = updatedMaker.copy(refund = refund)

          txsBuffer += Transaction(
            getTxId(),
            takerOrder.timestamp.getOrElse(0), // Always use taker's timestamp as transaction timestamp
            takerSide,
            takerOrder --> updatedTaker,
            makerOrder --> updatedMakerWithRefund)

          updatedMakerToAddBack match {
            case Some(makerOrder) =>
              addOrder(makerSide, makerOrder)
              TakerState(updatedTaker, totalOutAmount + txOutAmount, totalInAmount + txInAmount)
            case None =>
              recursivelyMatchOrder(TakerState(updatedTaker, totalOutAmount + txOutAmount, totalInAmount + txInAmount))
          }
        }
      }
    }
    // END of internal matching logic.

    val TakerState(takerOrder, totalOutAmount, totalInAmount) = recursivelyMatchOrder(TakerState(order, 0L, 0L))

    val status =
      if (takerOrder.soldOut || takerOrder.hitTakeLimit) OrderStatus.FullyExecuted
      else if (totalOutAmount > 0) {
        if (takerOrder.price.isEmpty || takerOrder.onlyTaker.getOrElse(false)) OrderStatus.PartiallyExecutedThenCancelledByMarket
        else OrderStatus.PartiallyExecuted
      } else if (takerOrder.price.isEmpty || takerOrder.onlyTaker.getOrElse(false)) OrderStatus.CancelledByMarket
      else OrderStatus.Pending

    val (updatedTakerToAdd, refund) = calculateRefund(takerOrder)

    updatedTakerToAdd foreach { addOrder(takerSide, _) }

    if (txsBuffer.nonEmpty && refund.isDefined) {
      val lastTx = txsBuffer.last
      txsBuffer.trimEnd(1)
      // If there is a over-charge refund, the current order in takerUpdate will still
      // show the quantity before the refund.
      txsBuffer += lastTx.copy(takerUpdate = lastTx.takerUpdate.copy(current = lastTx.takerUpdate.current.copy(refund = refund)))
    }

    val txs = txsBuffer.toSeq
    // If there is a over-charge refund, the order inside originOrderInfo will still
    // show the quantity before the refund.
    val orderInfo = OrderInfo(
      takerSide,
      if (txs.isEmpty) order.copy(refund = refund) else order,
      totalOutAmount,
      totalInAmount,
      status,
      txs.lastOption.map(_.timestamp))

    OrderSubmitted(orderInfo, txs)
  }

  def getSnapshot = TMarketState(
    orderPools.map(item => (item._1 -> item._2.toList)),
    orderMap.clone, priceRestriction, getFiltersSnapshot)

  def loadSnapshot(s: TMarketState) {
    orderPools.clear
    orderPools ++= s.orderPools.map(item => (item._1 -> (SortedSet.empty[Order] ++ item._2)))
    orderMap.clear
    orderMap ++= s.orderMap
    loadFiltersSnapshot(s.filters)
  }

  def isOrderPriceInGoodRange(takerSide: MarketSide, price: Option[Double]): Boolean = {
    if (price.isEmpty) true
    else if (price.get <= 0) false
    else if (priceRestriction.isEmpty || orderPool(takerSide).isEmpty) true
    else if (price.get / orderPool(takerSide).headOption.get.price.get - 1.0 <= priceRestriction.get) true
    else false
  }

  def addOrder(side: MarketSide, order: Order) = {
    assert(order.price.isDefined)
    orderPools += (side -> (orderPool(side) + order))
    orderMap += (order.id -> order)
  }

  def getOrderMarketSide(orderId: Long, userId: Long): Option[MarketSide] =
    orderMap.get(orderId) filter (_.userId == userId) map { order =>
      if (orderPool(tailSide).contains(order)) tailSide else headSide
    }

  def removeOrder(orderId: Long, userId: Long): (MarketSide, Order) = {
    val order = orderMap(orderId)
    assert(order.userId == userId)

    orderMap -= orderId
    if (orderPool(headSide).contains(order)) {
      orderPools += (headSide -> (orderPool(headSide) - order))
      (headSide, order)
    } else {
      orderPools += (tailSide -> (orderPool(tailSide) - order))
      (tailSide, order)
    }
  }

  private[markets] def orderPool(side: MarketSide) = orderPools.getOrElseUpdate(side, SortedSet.empty[Order])

  private def removeOrder(orderId: Long) = {
    val order = orderMap(orderId)
    orderMap -= orderId
    orderPools += (headSide -> (orderPool(headSide) - order))
    orderPools += (tailSide -> (orderPool(tailSide) - order))
  }
}
