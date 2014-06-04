/**
 * Copyright (C) 2014 Coinport Inc.
 */

package com.coinport.coinex.api.model

import com.coinport.coinex.data.Currency._
import com.coinport.coinex.data.Implicits._
import com.coinport.coinex.data.{ MarketSide, Currency }

object CurrencyConversion {
  // exponent (10-based) of the factor between internal value and external value
  // Btc -> 3 means: 1 BTC(external value) equals 1 * 10E3 MBTC(internal value)
  val exponents = Map[Currency, Double](
    Btc -> 8,
    Ltc -> 8,
    Pts -> 8,
    Dog -> 8,
    Cny -> 5,
    Usd -> 5
  )

  val multipliers: Map[Currency, Double] = exponents map {
    case (k, v) =>
      k -> math.pow(10, v)
  }

  def getExponent(currency: Currency) = exponents.get(currency).getOrElse(1.0).toInt

  def getMultiplier(currency: Currency) = multipliers.get(currency).getOrElse(1.0)
}

class CurrencyWrapper(val value: Double) {
  def externalValue(currency: Currency): Double = {
    (BigDecimal(value) / CurrencyConversion.multipliers(currency)).toDouble
  }

  def internalValue(currency: Currency): Long = {
    (BigDecimal(value) * CurrencyConversion.multipliers(currency)).toLong
  }

  def E(currency: Currency) = externalValue(currency)

  def I(currency: Currency) = internalValue(currency)
}

class PriceWrapper(val value: Double) {
  def externalValue(marketSide: MarketSide): Double = {
    val exp = CurrencyConversion.exponents(marketSide._1) - CurrencyConversion.exponents(marketSide._2)

    (value * math.pow(10, exp)).!!!
  }

  def internalValue(marketSide: MarketSide): Double = {
    val exp = CurrencyConversion.exponents(marketSide._2) - CurrencyConversion.exponents(marketSide._1)

    (value * math.pow(10, exp)).!!!
  }

  def E(marketSide: MarketSide) = externalValue(marketSide)

  def I(marketSide: MarketSide) = internalValue(marketSide)
}