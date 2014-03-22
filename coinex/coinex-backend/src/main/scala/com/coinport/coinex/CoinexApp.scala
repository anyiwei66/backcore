/**
 * Copyright (C) 2014 Coinport Inc. <http://www.coinport.com>
 *
 */

package com.coinport.coinex

import com.typesafe.config.ConfigFactory

import akka.actor._
import akka.pattern.ask
import akka.cluster.Cluster
import data._
import com.coinport.coinex.data._
import Implicits._
import Currency._
import akka.persistence._
import scala.concurrent.duration._
import akka.util.Timeout
import java.net.InetAddress

object CoinexApp extends App {
  if (args.length < 2 || args.length > 4) {
    val message = """please supply 1 to 4 parameters:
        required args(0): port - supply 0 to select a port randomly
        required args(1): seeds - seed note seperated by comma, i.e, "127.0.0.1:25551,127.0.0.1:25552"
        optioanl args(2): roles - "*" for all roles, "" for empty node, and "a,b,c" for 3 roles
        optioanl args(3): hostname - self hostname
      """
    println(message)
    System.exit(1)
  }

  val ALL_ROLES = """
    user_processor,
    account_processor,
    marke_update_processor,
    market_processor_btc_rmb,
    market_depth_view_btc_rmb,
    user_view,
    account_view,
    user_orders_view,
    candle_data_view_btc_rmb,
    """

  val seedNodes = args(1).split(",").map(_.stripMargin).filter(_.nonEmpty).map("\"akka.tcp://coinex@" + _ + "\"").mkString(",")

  val roles =
    if (args.length < 3) ""
    else if (args(2) == "*") ALL_ROLES
    else args(2).split(",").map(_.stripMargin).filter(_.nonEmpty).map("\"" + _ + "\"").mkString(",")

  val hostName =
    if (args.length < 4) InetAddress.getLocalHost.getHostAddress
    else args(3)

  val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + args(0))
    .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.hostname=" + hostName))
    .withFallback(ConfigFactory.parseString("akka.cluster.roles=[" + roles + "]"))
    .withFallback(ConfigFactory.parseString("akka.cluster.seed-nodes=[" + seedNodes + "]"))
    .withFallback(ConfigFactory.load())

  implicit val system = ActorSystem("coinex", config)
  implicit val cluster = Cluster(system)

  println("roles: " + cluster.selfRoles)
  val markets = Seq(Btc ~> Rmb)

  val routers = new LocalRouters(markets)
  val deployer = new Deployer(markets)
  deployer.deploy(routers)

  Thread.sleep(5000)
  val summary = "============= Akka Node Ready =============\n" +
    "with hostname: " + hostName + "\n" +
    "with roles: " + roles + "\n" +
    "with seeds: " + seedNodes + "\n"

  println(summary)
}
