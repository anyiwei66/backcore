package com.coinport.coinex.transfer

import com.coinport.coinex.data._
import com.coinport.coinex.data.TransferStatus._
import com.coinport.coinex.data.TransferType.UserToHot

object CryptoCurrencyTransferUserToHotHandler extends CryptoCurrencyTransferBase {

  override def innerHandleTx(currency: Currency, tx: CryptoCurrencyTransaction, timestamp: Option[Long]) {
    tx.ids match {
      case Some(idList) if idList.size > 0 =>
        idList foreach {
          id =>
            if (id2HandlerMap.contains(id)) {
              tx.status match {
                case Failed =>
                  handleFailed(id2HandlerMap(id).setTimeStamp(timestamp))
                case _ =>
                  id2HandlerMap(id).setTimeStamp(timestamp).onNormal(tx)
              }
            } else {
              logger.warning(s"""${"~" * 50} ${currency.toString} innerHandleTx() UserToHot item confirm id not contained in id2HandlerMap : ${tx.toString}""")
            }
        }
        updateSigId2MinerFee(tx)
      case _ =>
        logger.warning(s"""${"~" * 50} ${currency.toString} innerHandleTx() UserToHot tx not define ids : ${tx.toString}""")
    }
  }

  override def newHandlerFromItem(item: CryptoCurrencyTransferItem): CryptoCurrencyTransferHandler = {
    new CryptoCurrencyTransferUserToHotHandler(item)
  }

  override def handleFailed(handler: CryptoCurrencyTransferHandler, error: Option[ErrorCode] = None) {
    error match {
      case None =>
        handler.onFail(BitwayFailed)
      case _ =>
        handler.onFail()
        id2HandlerMap.remove(handler.item.id)
    }
  }

  override def item2CryptoCurrencyTransferInfo(item: CryptoCurrencyTransferItem): Option[CryptoCurrencyTransferInfo] = {
    Some(CryptoCurrencyTransferInfo(item.id, None, item.from.get.internalAmount, item.from.get.amount, Some(item.from.get.address)))
  }

  override def postProcessManualFail(handler: CryptoCurrencyTransferHandler, status: TransferStatus) {
    handler.onFail(status)
  }

  override def newHandlerFromAccountTransfer(t: AccountTransfer, from: Option[CryptoCurrencyTransactionPort], to: Option[CryptoCurrencyTransactionPort], timestamp: Option[Long]) {
    isRetry(t) match {
      case (true, handler) =>
        handler.setTimeStamp(timestamp).retry()
        msgBoxMap.put(handler.item.id, handler.item)
      case (false, _) =>
        logger.error(s"Retry not exists userToHot AccountTransfer ${t.toString} ")
    }
  }

  def createUserToHot(depositItem: CryptoCurrencyTransferItem, timestamp: Option[Long]) {
    val handler = new CryptoCurrencyTransferUserToHotHandler(depositItem, timestamp)
    id2HandlerMap.put(handler.item.id, handler)
    msgBoxMap.put(handler.item.id, handler.item)
  }
}

class CryptoCurrencyTransferUserToHotHandler extends CryptoCurrencyTransferHandler {
  def this(item: CryptoCurrencyTransferItem)(implicit env: TransferEnv) {
    this()
    setEnv(env, None)
    this.item = item
  }

  def this(item: CryptoCurrencyTransferItem, timestamp: Option[Long])(implicit env: TransferEnv) {
    this()
    setEnv(env, timestamp)
    createItemFromDepositItem(item, timestamp)
  }

  private def createItemFromDepositItem(depositItem: CryptoCurrencyTransferItem, timestamp: Option[Long]) {
    val transferId = manager.getTransferId
    manager.setLastTransferId(transferId)
    transferHandler.put(AccountTransfer(transferId, depositItem.userId.get, TransferType.UserToHot, depositItem.currency, depositItem.to.get.internalAmount.get, Confirming, timestamp, address = Some(depositItem.to.get.address)))
    // id, currency, sigId, txid, userId, from, to(hot address), includedBlock, txType, status, userToHotMapedDepositId, accountTransferId, created, updated
    item = CryptoCurrencyTransferItem(manager.getNewTransferItemId, depositItem.currency, None, None, depositItem.userId, depositItem.to, None, None, Some(UserToHot), Some(Confirming), Some(depositItem.id), Some(transferId), timestamp)
    saveItemToMongo()
  }

  override def onFail(failStatus: TransferStatus = Failed) {
    super.onFail(failStatus)
    if (failStatus != BitwayFailed) { //Failed by bitway should ignore
      updateDepositTx(false)
    }
  }

  override def onNormal(tx: CryptoCurrencyTransaction) {
    super.onNormal(tx)
    updateDepositTx()
  }

  private def updateDepositTx(isSucceeded: Boolean = true) {
    item.userToHotMapedDepositId match {
      case Some(depositId) =>
        CryptoCurrencyTransferDepositHandler.updateByUserToHot(depositId, isSucceeded)
      case None =>
        logger.error(s"""${"~" * 50} updateDepositTx() UserToHot item not define userToHotMapedDepositId : ${item.toString}""")
    }
  }

}
