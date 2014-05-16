/**
 * Copyright (C) 2014 Coinport Inc.
 */

package com.coinport.coinex.api.service

import com.coinport.coinex.data._
import com.coinport.coinex.api.model._
import akka.pattern.ask
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.coinport.coinex.data.Implicits._

object MarketService extends AkkaService {
  def getDepth(marketSide: MarketSide, depth: Int): Future[ApiResult] = {
    backend ? QueryMarketDepth(marketSide, depth) map {
      case result: QueryMarketDepthResult =>
        val depth: com.coinport.coinex.api.model.ApiMarketDepth = result.marketDepth
        ApiResult(data = Some(depth))
      case x => ApiResult(false)
    }
  }

  def getHistory(marketSide: MarketSide, timeDimension: ChartTimeDimension, from: Long, to: Long): Future[ApiResult] = {
    backend ? QueryCandleData(marketSide, timeDimension, from, to) map {
      case rv: QueryCandleDataResult =>
        val candles = rv.candleData
        val side = rv.candleData.side
        val timeSkip: Long = timeDimension
        val currency = side.outCurrency
        val ma7Seq = getMA(candles.items, 7).map(ma => ApiMAItem(ma._1 * timeSkip, PriceObject(side, ma._2)))
        val ma30Seq = getMA(candles.items, 30).map(ma => ApiMAItem(ma._1 * timeSkip, PriceObject(side, ma._2)))
        val candleSeq = candles.items.map(item =>
          ApiCandleItem(
            item.timestamp * timeSkip,
            PriceObject(side, item.open),
            PriceObject(side, item.high),
            PriceObject(side, item.low),
            PriceObject(side, item.close),
            CurrencyObject(currency, item.outAoumt)))

        ApiResult(data = Some(ApiHistory(candleSeq, ma7Seq, ma30Seq)))
      case x =>
        ApiResult(false)
    }
  }

  def getTransactions(marketSide: MarketSide, tid: Option[Long], uid: Option[Long], orderId: Option[Long], skip: Int, limit: Int): Future[ApiResult] = {
    val cursor = Cursor(skip, limit)
    backend ? QueryTransaction(tid, uid, orderId, Some(QueryMarketSide(marketSide, true)), cursor) map {
      case result: QueryTransactionResult =>
        val subject = marketSide._1
        val currency = marketSide._2
        val items = result.transactions map { t =>
          val takerSide = t.side
          val isSell = marketSide == takerSide

          val takerAmount = t.takerUpdate.previous.quantity - t.takerUpdate.current.quantity
          val makerAmount = t.makerUpdate.previous.quantity - t.makerUpdate.current.quantity
          val (sAmount, cAmount) = if (isSell) (takerAmount, makerAmount) else (makerAmount, takerAmount)
          val (takerPreAmount, takeCurAmount, makerPreAmount, makerCurAmount) =
            if (isSell)
              (CurrencyObject(subject, t.takerUpdate.previous.quantity),
                CurrencyObject(subject, t.takerUpdate.current.quantity),
                CurrencyObject(currency, t.makerUpdate.previous.quantity),
                CurrencyObject(currency, t.makerUpdate.current.quantity))
            else
              (CurrencyObject(currency, t.takerUpdate.previous.quantity),
                CurrencyObject(currency, t.takerUpdate.current.quantity),
                CurrencyObject(subject, t.makerUpdate.previous.quantity),
                CurrencyObject(subject, t.makerUpdate.current.quantity))

          val takerOrder = ApiOrderState(t.takerUpdate.current.id.toString, t.takerUpdate.current.userId.toString, takerPreAmount, takeCurAmount)
          val makerOrder = ApiOrderState(t.makerUpdate.current.id.toString, t.makerUpdate.current.userId.toString, makerPreAmount, makerCurAmount)

          ApiTransaction(
            id = t.id.toString,
            timestamp = t.timestamp,
            price = PriceObject(marketSide, cAmount.toDouble / sAmount.toDouble),
            subjectAmount = CurrencyObject(subject, sAmount),
            currencyAmount = CurrencyObject(currency, cAmount),
            taker = takerOrder.uid,
            maker = makerOrder.uid,
            sell = isSell,
            tOrder = takerOrder,
            mOrder = makerOrder
          )
        }
        ApiResult(data = Some(ApiPagingWrapper(skip, limit, items, result.count.toInt)))
    }
  }

  def getGlobalTransactions(marketSide: MarketSide, skip: Int, limit: Int): Future[ApiResult] = getTransactions(marketSide, None, None, None, skip, limit)

  def getTransactionsByUser(marketSide: MarketSide, uid: Long, skip: Int, limit: Int): Future[ApiResult] = getTransactions(marketSide, None, Some(uid), None, skip, limit)

  def getTransactionsByOrder(marketSide: MarketSide, orderId: Long, skip: Int, limit: Int): Future[ApiResult] = getTransactions(marketSide, None, None, Some(orderId), skip, limit)

  def getAsset(userId: Long, from: Long, to: Long, baseCurrency: Currency) = {
    backend ? QueryAsset(userId, from, to) map {
      case result: QueryAssetResult =>
        val timeSkip: Long = ChartTimeDimension.OneMinute
        val start = Math.min(from / timeSkip, to / timeSkip)
        val stop = Math.max(from / timeSkip, to / timeSkip)

        val historyAsset = result.historyAsset
        val historyPrice = result.historyPrice

        val currentPrice = result.currentPrice.priceMap
        val currencyPriceMap = scala.collection.mutable.Map.empty[Currency, Map[Long, Double]]
        historyPrice.priceMap.foreach {
          case (side, map) =>
            if (side._2 == baseCurrency) {
              var curPrice = currentPrice.get(side).get

              val priceMap = (start to stop).reverse.map { timeSpot =>
                curPrice = map.get(timeSpot).getOrElse(curPrice)
                timeSpot -> curPrice.externalValue(side)
              }.toMap
              currencyPriceMap.put(side._1, priceMap)
            }
        }

        val currentAsset = scala.collection.mutable.Map.empty[Currency, Long] ++ result.currentAsset.currentAsset
        val assetList = (start to stop).reverse.map { timeSpot =>
          val rv = (timeSpot, currentAsset.clone())

          historyAsset.currencyMap.get(timeSpot) match {
            case Some(curMap) =>
              curMap.foreach {
                case (cur, volume) =>
                  currentAsset.put(cur, currentAsset.get(cur).get - volume)
              }
            case None =>
          }
          rv
        }

        val items = assetList.map {
          case (timeSpot, assetMap) =>
            val amountMap = assetMap.map {
              case (cur, volume) =>
                val amount =
                  if (cur == baseCurrency) volume * 1
                  else currencyPriceMap.get(cur) match {
                    case Some(curHisMap) => curHisMap.get(timeSpot).get * volume
                    case None => 0.0
                  }
                cur.toString.toUpperCase -> amount.externalValue(cur)
            }.toMap
            ApiAssetItem(uid = userId.toString,
              assetMap = assetMap.map(a => a._1.toString.toUpperCase -> a._2.externalValue(a._1)).toMap,
              amountMap = amountMap,
              timestamp = timeSpot * timeSkip)
        }
        ApiResult(data = Some(items.reverse))
    }
  }

  def getTickers(marketSides: Seq[MarketSide]) = {
    backend ? QueryMetrics map {
      case result: Metrics =>
        val map = result.metricsByMarket
        val data = marketSides
          .filter(s => map.contains(s))
          .map {
            side: MarketSide =>
              val subject = side._1
              val metrics = map.get(side).get
              val price = metrics.price
              val high = metrics.high.getOrElse(0.0)
              val low = metrics.low.getOrElse(0.0)
              val internalVolume = metrics.volume
              val gain = metrics.gain
              val trend = Some(metrics.direction.toString.toLowerCase)

              ApiTicker(
                market = side.S,
                price = PriceObject(side, price),
                volume = CurrencyObject(subject, internalVolume),
                high = PriceObject(side, high),
                low = PriceObject(side, low),
                gain = gain,
                trend = trend
              )
          }
        ApiResult(data = Some(data))
    }
  }

  private def getMA(items: Seq[CandleDataItem], count: Int) = {
    val map = items.map(i => i.timestamp -> i.close).toMap
    items.map { item =>
      val key = item.timestamp
      val seqSum = (0 until count).map(j => map.get(key - j).getOrElse(-1.0)).filter(_ > 0)
      (key, seqSum.sum / seqSum.size)
    }
  }
}
