/**
 * Copyright (C) 2014 Coinport Inc.
 */
package com.coinport.coinex.api.model

import org.specs2.mutable._
import org.json4s.JsonDSL._
import com.coinport.coinex.data.Currency._
import com.coinport.coinex.data.Implicits._

class JsonTest extends Specification {
  "models to JSON" should {
    "ApiResult to JSON" in {
      val result = ApiResult(true, 0, "some message")
      val json =
        ("success" -> true) ~
          ("code" -> 0) ~
          ("message" -> "some message")

      result.toJson mustEqual json
    }

    "CurrencyObject to JSON" in {
      var result = CurrencyObject("CNY", 12345)
      var json =
        ("currency" -> "CNY") ~
          ("value_int" -> 12345) ~
          ("value" -> 123.45) ~
          ("display" -> "123.45") ~
          ("display_short" -> "123.45")

      result.toJson mustEqual json

      result = CurrencyObject("BTC", 12345)
      json =
        ("currency" -> "BTC") ~
          ("value_int" -> 12345) ~
          ("value" -> 12.345) ~
          ("display" -> "12.345") ~
          ("display_short" -> "12.35")

      result.toJson mustEqual json
    }

    "PriceObject to JSON" in {
      val result = PriceObject(Btc ~> Cny, 123.45)
      val json =
        ("item" -> "BTC") ~
          ("currency" -> "CNY") ~
          ("value_int" -> 123.45) ~
          ("value" -> 1234.5) ~
          ("display" -> "1234.50") ~
          ("display_short" -> "1234.50")

      result.toJson mustEqual json
    }
  }
}