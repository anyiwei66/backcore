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

package com.coinport.coinex.domain

/**
 * available: the amount the user can spend or withdraw.
 * locked: the amount that has been put on lock for pending orders.
 */
case class CashAccount(currency: Currency, available: BigDecimal, locked: BigDecimal) {
  def balance: BigDecimal = available + locked
}

object UserAccounts {
  type CashAccounts = Map[Currency, CashAccount]
  val EmptyCashAccounts = Map.empty[Currency, CashAccount]
}

case class UserAccounts(userId: Long,
  cashAccounts: UserAccounts.CashAccounts = UserAccounts.EmptyCashAccounts)
