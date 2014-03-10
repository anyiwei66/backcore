/**
 * Copyright (C) 2014 Coinport Inc. <http://www.coinport.com>
 *
 */

package com.coinport.coinex

import com.typesafe.config.ConfigFactory
import com.coinport.coinex.domain._
import akka.actor._
import akka.cluster._
import akka.cluster.routing._
import akka.routing._
import akka.contrib.pattern._
import akka.persistence.Persistent
import Implicits._
import Currency._

object CoinexApp extends App {
  val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + args(0))
    .withFallback(ConfigFactory.load())

  implicit val system = ActorSystem("coinex", config)
  val cluster = Cluster(system)

  // ------------------------------------------------------------------------------------------------
  // Cluster-aware Routers Deployment
  val accountProcessor = system.actorOf(
    ClusterRouterGroup(
      RoundRobinGroup(Nil),
      ClusterRouterGroupSettings(
        totalInstances = Int.MaxValue,
        routeesPaths = List("/user/ap/singleton"),
        allowLocalRoutees = true,
        useRole = None)).props, "ap_router")

  val accountView = system.actorOf(
    ClusterRouterGroup(
      RoundRobinGroup(Nil),
      ClusterRouterGroupSettings(
        totalInstances = Int.MaxValue,
        routeesPaths = List("/user/av"),
        allowLocalRoutees = true,
        useRole = None)).props, "av_router")

  val markets = Seq(Btc ~> Rmb) // TODO(d): read supported markets from configuration.
  val marketProcessors = Map(
    markets map { market =>
      market -> system.actorOf(
        ClusterRouterGroup(
          RoundRobinGroup(Nil),
          ClusterRouterGroupSettings(
            totalInstances = Int.MaxValue,
            routeesPaths = List("/user/mp_" + market.asString + "/singleton"),
            allowLocalRoutees = true,
            useRole = None)).props, "mp_" + market.asString + "_router")
    }: _*)

  val marketViews = markets map { market =>
    market -> system.actorOf(
      ClusterRouterGroup(
        RoundRobinGroup(Nil),
        ClusterRouterGroupSettings(
          totalInstances = Int.MaxValue,
          routeesPaths = List("/user/mv_" + market.asString),
          allowLocalRoutees = true,
          useRole = None)).props, "mv_" + market.asString + "_router")
  }

  // ------------------------------------------------------------------------------------------------
  // Processors and Views Deployment

  // Account Processor
  system.actorOf(ClusterSingletonManager.props(
    singletonProps = Props(new AccountProcessor(marketProcessors)),
    singletonName = "singleton",
    terminationMessage = PoisonPill,
    role = Some("ap")),
    name = "ap")

  // Account View
  if (cluster.selfRoles.contains("av")) {
    system.actorOf(Props(classOf[AccountView]), "av")
  }

  // Market Processors and Views
  markets foreach { market =>
    system.actorOf(ClusterSingletonManager.props(
      singletonProps = Props(new MarketProcessor(market, accountProcessor.path)),
      singletonName = "singleton",
      terminationMessage = PoisonPill,
      role = Some("mp_" + market.asString)),
      name = "mp_" + market.asString)

    if (cluster.selfRoles.contains("mv_" + market.asString)) {
      system.actorOf(Props(new MarketView(market)), "mv_" + market.asString)
    }
  }

  val sleep = config.getInt("coinex.app-snap-seconds")
  println("sleeping for " + sleep + " seconds...")
  Thread.sleep(sleep * 1000) // give time for event replay

  println("============= Akka Node Ready =============\n\n")
}