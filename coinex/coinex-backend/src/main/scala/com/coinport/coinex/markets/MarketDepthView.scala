/**
 * Copyright (C) 2014 Coinport Inc. <http://www.coinport.com>
 *
 */

package com.coinport.coinex.markets

import akka.persistence.Persistent
import com.coinport.coinex.data._
import com.coinport.coinex.common.ExtendedView
import Implicits._

// TODO: make this a OrderSubmittedMarketDepthView
class MarketDepthView(marketSide: MarketSide) extends ExtendedView {
  override def processorId = "coinex_mup"

  def receive = {
    case DebugDump =>
    // log.info("state: {}", manager())

    case x =>
      log.info("~~~ saw: " + x)
      if (receiveMessage.isDefinedAt(x)) receiveMessage(x)
  }

  def receiveMessage: Receive = {
    case Persistent(DoCancelOrder(side, orderId), _) =>
    //  manager.removeOrder(side, orderId)
    /*
    case Persistent(OrderCashLocked(side, order), _) =>
      val orderSubmitted = manager.addOrder(side, order)
      lastPrice = orderSubmitted.lastPrice map { Price(side, _) }

    case QueryMarket(side, depth) =>
      val price = lastPrice map { p => if (p.side == side) p else p.reverse }
      println("state: " + manager())
      sender ! QueryMarketResult(price,
        manager().limitPriceOrderPool(side).take(depth).toSeq,
        manager().limitPriceOrderPool(side.reverse).take(depth).toSeq)
        * */

  }
}