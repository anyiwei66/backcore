/**
 * Copyright 2014 Coinport Inc. All Rights Reserved.
 * Author: c@coinport.com (Chao Ma)
 */

package com.coinport.coinex.common

import com.coinport.coinex.data._
import Implicits._

///////////// WARNING ///////////////////////////////////
// TRY TO AVOID USING THIS CLASS!!!!!!!!!!!!!!!!!!!!!!!!

object Constants {
  val COINPORT_UID = 3142141421L

  // WARNING: use an enum somewhere else?
  val STOP_ORDER_ROBOT_TYPE = 1
  val TRAILING_STOP_ORDER_ROBOT_TYPE = 2

  // WARNING: this belongs to somewhere else
  type MarketEvent = (Option[Double], Option[Long]) // (price, volume)

  // WARNING: this belongs to somewhere else
  val _24_HOURS: Long = 3600 * 24 * 1000
  val _10_SECONDS: Long = 10 * 1000

  // WARNING: this belongs to somewhere else
  def ascending = (lhs: Double, rhs: Double) => lhs <= rhs
  def descending = (lhs: Double, rhs: Double) => lhs >= rhs
}
