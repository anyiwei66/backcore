/**
 * Copyright (C) 2014 Coinport Inc. <http://www.coinport.com>
 */

package com.coinport.coinex.markets

import org.specs2.mutable._
import scala.collection.immutable.SortedSet
import com.coinport.coinex.data._
import com.coinport.coinex.data.MarketState
import MarketState._
import Implicits._
import Currency._
import OrderStatus._

class MarketManagerSpec extends Specification {

  val takerSide = Btc ~> Rmb
  val makerSide = takerSide.reverse

  "MarketManager" should {
    "match limit-price order against existing limit-price order with take-limit and update take-limit" in {
      val manager = new MarketManager(Btc ~> Rmb)(() => 100000) // constant time
      val maker = Order(userId = 666, id = 1, price = Some(1.0 / 4500), quantity = 45099, takeLimit = Some(11), timestamp = Some(100000))
      val taker = Order(userId = 888, id = 3, price = Some(4000), quantity = 10, timestamp = Some(100000))

      manager.addOrder(makerSide, maker)
      val result = manager.addOrder(takerSide, taker)

      val updatedMaker = maker.copy(quantity = 99, takeLimit = Some(1))
      val updatedTaker = taker.copy(quantity = taker.quantity - 10)

      result mustEqual OrderSubmitted(
        OrderInfo(takerSide, taker, 10, 45000, FullyExecuted, Some(100000)),
        Seq(Transaction(100000, taker --> updatedTaker, maker --> updatedMaker)))

      manager().orderMap mustEqual Map(1 -> updatedMaker)
      manager().marketPriceOrderPool(takerSide) mustEqual EmptyOrderPool
      manager().marketPriceOrderPool(makerSide) mustEqual EmptyOrderPool
      manager().limitPriceOrderPool(makerSide) mustEqual SortedSet(updatedMaker)
      manager().limitPriceOrderPool(takerSide) mustEqual EmptyOrderPool
    }

    "match limit-price order with as many existing limit-price order with take-limit" in {
      val manager = new MarketManager(Btc ~> Rmb)(() => 0)
      val maker1 = Order(userId = 666, id = 1, price = Some(1.0 / 4500), quantity = 10000, takeLimit = Some(1), timestamp = Some(0))
      val maker2 = Order(userId = 777, id = 2, price = Some(1.0 / 5000), quantity = 15000, takeLimit = Some(3), timestamp = Some(0))
      val taker = Order(userId = 888, id = 3, price = Some(4000), quantity = 10, timestamp = Some(0))

      manager.addOrder(makerSide, maker1)
      manager.addOrder(makerSide, maker2)
      val result = manager.addOrder(takerSide, taker)

      val updatedMaker1 = maker1.copy(quantity = maker1.quantity - 4500, takeLimit = Some(0))
      val updatedMaker2 = maker2.copy(quantity = maker2.quantity - 15000, takeLimit = Some(0))
      val updatedTaker = taker.copy(quantity = 6)

      result mustEqual OrderSubmitted(
        OrderInfo(takerSide, taker, 4, 19500, PartiallyExecuted, Some(0)),
        Seq(
          Transaction(0, taker --> taker.copy(quantity = 7), maker2 --> updatedMaker2),
          Transaction(0, taker.copy(quantity = 7) --> updatedTaker, maker1 --> updatedMaker1)))

      manager().orderMap mustEqual Map(3 -> taker.copy(quantity = 6))
      manager().marketPriceOrderPool(takerSide) mustEqual EmptyOrderPool
      manager().marketPriceOrderPool(makerSide) mustEqual EmptyOrderPool
      manager().limitPriceOrderPool(takerSide) mustEqual SortedSet(updatedTaker)
      manager().limitPriceOrderPool(makerSide) mustEqual EmptyOrderPool
    }
  }

  "MarketManager" should {
    "allow multiple market-price orders to co-exist, even in an empty market" in {
      var time = 100000
      implicit def getTime(): Long = { time += 1; time }
      val manager = new MarketManager(Btc ~> Rmb)

      val maker1 = Order(userId = 888L, id = 1, price = None, quantity = 100)
      val maker2 = Order(userId = 888L, id = 2, price = None, quantity = 500)

      val result1 = manager.addOrder(makerSide, maker1)
      val result2 = manager.addOrder(makerSide, maker2)

      val updatedMaker1 = maker1.copy(timestamp = Some(100001))
      val updatedMaker2 = maker2.copy(timestamp = Some(100002))

      result1 mustEqual OrderSubmitted(OrderInfo(makerSide, updatedMaker1, 0, 0, Pending, None), Nil)
      result2 mustEqual OrderSubmitted(OrderInfo(makerSide, updatedMaker2, 0, 0, Pending, None), Nil)

      manager().orderMap mustEqual Map(1L -> updatedMaker1, 2 -> updatedMaker2)
      manager().limitPriceOrderPool(makerSide) mustEqual EmptyOrderPool
      manager().limitPriceOrderPool(takerSide) mustEqual EmptyOrderPool
      manager().marketPriceOrderPool(makerSide) mustEqual SortedSet(updatedMaker1, updatedMaker2)
      manager().marketPriceOrderPool(takerSide) mustEqual EmptyOrderPool
    }

    "NOT match new market-price taker order with existing market-price maker orders" in {
      val manager = new MarketManager(Btc ~> Rmb)(() => 0)
      val maker = Order(userId = 888L, id = 1, price = None, quantity = 100, timestamp = Some(0))
      val taker = Order(userId = 888L, id = 2, price = None, quantity = 100, timestamp = Some(0))

      manager.addOrder(makerSide, maker)
      val result = manager.addOrder(takerSide, taker)

      result.txs mustEqual Nil

      manager().orderMap mustEqual Map(1L -> maker, 2 -> taker)
      manager().limitPriceOrderPool(makerSide) mustEqual EmptyOrderPool
      manager().limitPriceOrderPool(takerSide) mustEqual EmptyOrderPool
      manager().marketPriceOrderPool(makerSide) mustEqual SortedSet(maker)
      manager().marketPriceOrderPool(takerSide) mustEqual SortedSet(taker)
    }

    "match new market-price taker order against existing limit-price maker orders and fully execute both orders " +
      "if quantity equals" in {
        var time = 1000
        implicit def getTime(): Long = { time += 1; time }
        val manager = new MarketManager(Btc ~> Rmb)

        val maker = Order(userId = 777L, id = 1, price = Some(1), quantity = 100, timestamp = Some(1))
        val taker = Order(userId = 888L, id = 2, price = None, quantity = 100, timestamp = Some(2))

        manager.addOrder(makerSide, maker)
        val result = manager.addOrder(takerSide, taker)

        val updatedMaker = maker.copy(quantity = 0, timestamp = Some(1001))
        val updatedTaker = taker.copy(quantity = 0, timestamp = Some(1002))

        result mustEqual OrderSubmitted(
          OrderInfo(takerSide, taker.copy(timestamp = Some(1002)), 100, 100, FullyExecuted, Some(1003)),
          Seq(
            Transaction(1003,
              taker.copy(timestamp = Some(1002)) --> updatedTaker,
              maker.copy(timestamp = Some(1001)) --> updatedMaker)))

        manager().orderMap.size mustEqual 0
        manager().limitPriceOrderPool(makerSide) mustEqual EmptyOrderPool
        manager().limitPriceOrderPool(takerSide) mustEqual EmptyOrderPool
        manager().marketPriceOrderPool(makerSide) mustEqual EmptyOrderPool
        manager().marketPriceOrderPool(takerSide) mustEqual EmptyOrderPool
      }

    "match new market-price taker order against existing limit-price maker orders and fully execute taker orders " +
      "if its quantity is smaller" in {
        var time = 1000
        implicit def getTime(): Long = { time += 1; time }
        val manager = new MarketManager(Btc ~> Rmb)

        val maker = Order(userId = 777L, id = 1, price = Some(1), quantity = 100)
        val taker = Order(userId = 888L, id = 2, price = None, quantity = 10)

        manager.addOrder(makerSide, maker)
        val result = manager.addOrder(takerSide, taker)

        val updatedMaker = maker.copy(quantity = 90, timestamp = Some(1001))
        val updatedTaker = taker.copy(quantity = 0, timestamp = Some(1002))

        result mustEqual OrderSubmitted(
          OrderInfo(takerSide, taker.copy(timestamp = Some(1002)), 10, 10, FullyExecuted, Some(1003)),
          Seq(Transaction(1003,
            taker.copy(timestamp = Some(1002)) --> updatedTaker,
            maker.copy(timestamp = Some(1001)) --> updatedMaker)))

        manager().orderMap mustEqual Map(1 -> updatedMaker)
        manager().limitPriceOrderPool(makerSide) mustEqual SortedSet(updatedMaker)
        manager().limitPriceOrderPool(takerSide) mustEqual EmptyOrderPool
        manager().marketPriceOrderPool(makerSide) mustEqual EmptyOrderPool
        manager().marketPriceOrderPool(takerSide) mustEqual EmptyOrderPool

      }

    "match new market-price taker order against existing limit-price maker orders and fully execute maker orders " +
      "if its quantity is smaller" in {
        val manager = new MarketManager(Btc ~> Rmb)(() => 0)
        val maker = Order(userId = 777L, id = 1, price = Some(1), quantity = 10, timestamp = Some(0))
        val taker = Order(userId = 888L, id = 2, price = None, quantity = 100, timestamp = Some(0))

        manager.addOrder(makerSide, maker)
        val result = manager.addOrder(takerSide, taker)

        val updatedMaker = maker.copy(quantity = 0)
        val updatedTaker = taker.copy(quantity = 90)

        result mustEqual OrderSubmitted(
          OrderInfo(takerSide, taker, 10, 10, PartiallyExecuted, Some(0)),
          Seq(Transaction(0, taker --> updatedTaker, maker --> updatedMaker)))

        manager().orderMap mustEqual Map(2 -> updatedTaker)
        manager().marketPriceOrderPool(makerSide) mustEqual EmptyOrderPool
        manager().marketPriceOrderPool(takerSide) mustEqual SortedSet(updatedTaker)
        manager().limitPriceOrderPool(makerSide) mustEqual EmptyOrderPool
        manager().limitPriceOrderPool(takerSide) mustEqual EmptyOrderPool
      }

    "match new market-price taker order against multiple existing limit-price maker orders and fully execute " +
      "taker order if its quantity is smaller" in {
        var time = 1000
        implicit def getTime(): Long = { time += 1; time }
        val manager = new MarketManager(Btc ~> Rmb)

        val maker1 = Order(userId = 666L, id = 1, price = Some(1), quantity = 100, timestamp = Some(1001)) // lower price
        val maker2 = Order(userId = 777L, id = 2, price = Some(0.5), quantity = 100, timestamp = Some(1002)) // higher price
        val taker = Order(userId = 888L, id = 10, price = None, quantity = 120, timestamp = Some(1003))

        manager.addOrder(makerSide, maker1)
        manager.addOrder(makerSide, maker2)
        val result = manager.addOrder(takerSide, taker)

        val updatedMaker1 = maker1.copy(quantity = 30)
        val updatedMaker2 = maker2.copy(quantity = 0)
        val updatedTaker = taker.copy(quantity = 0)

        result mustEqual OrderSubmitted(
          OrderInfo(takerSide, taker, 120, 170, FullyExecuted, Some(1005)),
          Seq(
            Transaction(1004, taker --> taker.copy(quantity = 70), maker2 --> updatedMaker2),
            Transaction(1005, taker.copy(quantity = 70) --> updatedTaker, maker1 --> updatedMaker1)))

        manager().orderMap mustEqual Map(1 -> updatedMaker1) //  100 x 0.5 + 100 x 1 - 120 = 30
        manager().limitPriceOrderPool(makerSide) mustEqual SortedSet(updatedMaker1)
        manager().limitPriceOrderPool(takerSide) mustEqual EmptyOrderPool
        manager().marketPriceOrderPool(makerSide) mustEqual EmptyOrderPool
        manager().marketPriceOrderPool(takerSide) mustEqual EmptyOrderPool
      }

    "match new market-price taker order against multiple existing limit-price maker orders and fully execute " +
      "all maker orders if their combined quantity is smaller" in {
        val manager = new MarketManager(Btc ~> Rmb)(() => 0)
        val maker1 = Order(userId = 666L, id = 1, price = Some(1), quantity = 20, timestamp = Some(0)) // lower price
        val maker2 = Order(userId = 777L, id = 2, price = Some(0.5), quantity = 100, timestamp = Some(0)) // higher price
        val taker = Order(userId = 888L, id = 10, price = None, quantity = 120, timestamp = Some(0))

        manager.addOrder(makerSide, maker1)
        manager.addOrder(makerSide, maker2)
        val result = manager.addOrder(takerSide, taker)

        val updatedMaker1 = maker1.copy(quantity = 0)
        val updatedMaker2 = maker2.copy(quantity = 0)
        val updatedTaker = taker.copy(quantity = 50)

        result mustEqual OrderSubmitted(
          OrderInfo(takerSide, taker, 70, 120, PartiallyExecuted, Some(0)),
          Seq(
            Transaction(0, taker --> taker.copy(quantity = 70), maker2 --> updatedMaker2),
            Transaction(0, taker.copy(quantity = 70) --> updatedTaker, maker1 --> updatedMaker1)))

        manager().orderMap mustEqual Map(10 -> taker.copy(quantity = 50)) //  120 - 100 x 0.5 + 20 x 1 - 120 = 50
        manager().marketPriceOrderPool(takerSide) mustEqual SortedSet(taker.copy(quantity = 50))
        manager().marketPriceOrderPool(makerSide) mustEqual EmptyOrderPool

        manager().limitPriceOrderPool(makerSide) mustEqual EmptyOrderPool
        manager().limitPriceOrderPool(takerSide) mustEqual EmptyOrderPool
      }
  }

  "MarketManager" should {
    "match new limit-price taker order against the highest limit-price maker order" in {
      val manager = new MarketManager(Btc ~> Rmb)(() => 0)
      val maker1 = Order(userId = 666, id = 1, price = Some(1), quantity = 20, timestamp = Some(0)) // lower price
      val maker2 = Order(userId = 777, id = 2, price = Some(0.5), quantity = 100, timestamp = Some(0)) // higher price
      val taker = Order(userId = 888L, id = 10, price = Some(1), quantity = 10, timestamp = Some(0))

      manager.addOrder(makerSide, maker1)
      manager.addOrder(makerSide, maker2)

      val result = manager.addOrder(takerSide, taker)
      val updatedMaker2 = maker2.copy(quantity = 80)
      val updatedTaker = taker.copy(quantity = 0)

      result mustEqual OrderSubmitted(
        OrderInfo(takerSide, taker, 10, 20, FullyExecuted, Some(0)),
        Seq(Transaction(0, taker --> updatedTaker, maker2 --> updatedMaker2)))

      manager().orderMap mustEqual Map(1 -> maker1, 2 -> updatedMaker2)
      manager().marketPriceOrderPool(takerSide) mustEqual EmptyOrderPool
      manager().marketPriceOrderPool(makerSide) mustEqual EmptyOrderPool
      manager().limitPriceOrderPool(makerSide) mustEqual SortedSet(maker1, updatedMaker2)
      manager().limitPriceOrderPool(takerSide) mustEqual EmptyOrderPool
    }

    "match new limit-price taker order fully against multiple limit-price maker orders" in {
      val manager = new MarketManager(Btc ~> Rmb)(() => 0)
      val maker1 = Order(userId = 666, id = 1, price = Some(1), quantity = 20, timestamp = Some(0)) // lower price
      val maker2 = Order(userId = 777, id = 2, price = Some(0.5), quantity = 100, timestamp = Some(0)) // higher price
      val taker = Order(userId = 888L, id = 10, price = Some(1), quantity = 60, timestamp = Some(0))

      manager.addOrder(makerSide, maker1)
      manager.addOrder(makerSide, maker2)
      val result = manager.addOrder(takerSide, taker)

      val updatedMaker1 = maker1.copy(quantity = 10)
      val updatedMaker2 = maker2.copy(quantity = 0)
      val updatedTaker = taker.copy(quantity = 0)

      result mustEqual OrderSubmitted(
        OrderInfo(takerSide, taker, 60, 110, FullyExecuted, Some(0)),
        Seq(
          Transaction(0, taker --> taker.copy(quantity = 10), maker2 --> updatedMaker2),
          Transaction(0, taker.copy(quantity = 10) --> updatedTaker, maker1 --> updatedMaker1)))

      manager().orderMap mustEqual Map(1 -> maker1.copy(quantity = 10))
      manager().marketPriceOrderPool(takerSide) mustEqual EmptyOrderPool
      manager().marketPriceOrderPool(makerSide) mustEqual EmptyOrderPool
      manager().limitPriceOrderPool(makerSide) mustEqual SortedSet(maker1.copy(quantity = 10))
      manager().limitPriceOrderPool(takerSide) mustEqual EmptyOrderPool
    }

    "match new limit-price taker order partially against multiple limit-price maker orders" in {
      val manager = new MarketManager(Btc ~> Rmb)(() => 0)
      val maker1 = Order(userId = 666, id = 1, price = Some(0.5), quantity = 20, timestamp = Some(0)) // lower price
      val maker2 = Order(userId = 777, id = 2, price = Some(0.4), quantity = 100, timestamp = Some(0)) // higher price
      val taker = Order(userId = 888L, id = 10, price = Some(2), quantity = 90, timestamp = Some(0))

      manager.addOrder(makerSide, maker1)
      manager.addOrder(makerSide, maker2)
      val result = manager.addOrder(takerSide, taker)

      val updatedMaker1 = maker1.copy(quantity = 0)
      val updatedMaker2 = maker2.copy(quantity = 0)
      val updatedTaker = taker.copy(quantity = 40)

      result mustEqual OrderSubmitted(
        OrderInfo(takerSide, taker, 50, 120, PartiallyExecuted, Some(0)),
        Seq(
          Transaction(0, taker --> taker.copy(quantity = 50), maker2 --> updatedMaker2),
          Transaction(0, taker.copy(quantity = 50) --> updatedTaker, maker1 --> updatedMaker1)))

      manager().orderMap mustEqual Map(10 -> updatedTaker) // 90 - 100x0.4 - 20x0.5
      manager().marketPriceOrderPool(takerSide) mustEqual EmptyOrderPool
      manager().marketPriceOrderPool(makerSide) mustEqual EmptyOrderPool
      manager().limitPriceOrderPool(makerSide) mustEqual EmptyOrderPool
      manager().limitPriceOrderPool(takerSide) mustEqual SortedSet(updatedTaker)
    }

    "match new limit-price taker order fully against existing market-price maker order 1" in {
      val manager = new MarketManager(Btc ~> Rmb)(() => 0)
      val maker1 = Order(userId = 666, id = 1, price = None, quantity = 20, timestamp = Some(0)) // high priority
      val maker2 = Order(userId = 777, id = 2, price = None, quantity = 100, timestamp = Some(0)) // low priority
      val taker = Order(userId = 888L, id = 10, price = Some(2), quantity = 5, timestamp = Some(0))

      manager.addOrder(makerSide, maker1)
      manager.addOrder(makerSide, maker2)
      val result = manager.addOrder(takerSide, taker)

      val updatedMaker1 = maker1.copy(quantity = 10)
      val updatedTaker = taker.copy(quantity = 0)

      result mustEqual OrderSubmitted(
        OrderInfo(takerSide, taker, 5, 10, FullyExecuted, Some(0)),
        Seq(Transaction(0, taker --> updatedTaker, maker1 --> updatedMaker1)))

      manager().orderMap mustEqual Map(1 -> updatedMaker1, 2 -> maker2)
      manager().marketPriceOrderPool(takerSide) mustEqual EmptyOrderPool
      manager().marketPriceOrderPool(makerSide) mustEqual SortedSet(updatedMaker1, maker2)
      manager().limitPriceOrderPool(makerSide) mustEqual EmptyOrderPool
      manager().limitPriceOrderPool(takerSide) mustEqual EmptyOrderPool
    }

    "match new limit-price taker order fully against existing market-price maker order 2" in {
      val manager = new MarketManager(Btc ~> Rmb)(() => 0)
      val maker1 = Order(userId = 666, id = 1, price = None, quantity = 20, timestamp = Some(0)) // higher priority
      val maker2 = Order(userId = 777, id = 2, price = None, quantity = 100, timestamp = Some(0)) // lower priority
      val taker = Order(userId = 888L, id = 10, price = Some(2), quantity = 30, timestamp = Some(0))

      manager.addOrder(makerSide, maker1)
      manager.addOrder(makerSide, maker2)
      val result = manager.addOrder(takerSide, taker)

      val updatedMaker1 = maker1.copy(quantity = 0)
      val updatedMaker2 = maker2.copy(quantity = 60)
      val updatedTaker = taker.copy(quantity = 0)

      result mustEqual OrderSubmitted(
        OrderInfo(takerSide, taker, 30, 60, FullyExecuted, Some(0)),
        Seq(
          Transaction(0, taker --> taker.copy(quantity = 20), maker1 --> updatedMaker1),
          Transaction(0, taker.copy(quantity = 20) --> updatedTaker, maker2 --> updatedMaker2)))

      manager().orderMap mustEqual Map(2 -> updatedMaker2)
      manager().marketPriceOrderPool(takerSide) mustEqual EmptyOrderPool
      manager().marketPriceOrderPool(makerSide) mustEqual SortedSet(updatedMaker2)
      manager().limitPriceOrderPool(makerSide) mustEqual EmptyOrderPool
      manager().limitPriceOrderPool(takerSide) mustEqual EmptyOrderPool
    }

    "match new limit-price taker order partially against existing market-price maker order" in {
      val manager = new MarketManager(Btc ~> Rmb)(() => 0)
      val maker1 = Order(userId = 666, id = 1, price = None, quantity = 20, timestamp = Some(0)) // lower price
      val maker2 = Order(userId = 777, id = 2, price = None, quantity = 100, timestamp = Some(0)) // higher price
      val taker = Order(userId = 888L, id = 10, price = Some(2), quantity = 300, timestamp = Some(0))

      manager.addOrder(makerSide, maker1)
      manager.addOrder(makerSide, maker2)
      val result = manager.addOrder(takerSide, taker)

      val updatedMaker1 = maker1.copy(quantity = 0)
      val updatedMaker2 = maker2.copy(quantity = 0)
      val updatedTaker = taker.copy(quantity = 240)

      result mustEqual OrderSubmitted(
        OrderInfo(takerSide, taker, 60, 120, PartiallyExecuted, Some(0)),
        Seq(
          Transaction(0, taker --> taker.copy(quantity = 290), maker1 --> updatedMaker1),
          Transaction(0, taker.copy(quantity = 290) --> updatedTaker, maker2 --> updatedMaker2)))

      manager().orderMap mustEqual Map(10 -> updatedTaker)
      manager().marketPriceOrderPool(takerSide) mustEqual EmptyOrderPool
      manager().marketPriceOrderPool(makerSide) mustEqual EmptyOrderPool
      manager().limitPriceOrderPool(makerSide) mustEqual EmptyOrderPool
      manager().limitPriceOrderPool(takerSide) mustEqual SortedSet(updatedTaker)
    }
  }
}