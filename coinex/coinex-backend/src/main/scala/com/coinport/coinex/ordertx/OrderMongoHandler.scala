package com.coinport.coinex.ordertx

import com.coinport.coinex.data._
import com.mongodb.casbah.Imports._
import com.coinport.coinex.serializers.ThriftBinarySerializer
import Implicits._

trait OrderMongoHandler {
  val OID = "_id"
  val UID = "uid"
  val IN_AMOUNT = "ia"
  val ORIGIN_ORDER = "oo"
  val STATUS = "st"
  val CREATED_TIME = "c@"
  val UPDATED_TIME = "u@"
  val MARKET = "m"
  val SIDE = "s"
  val QUANTITY = "q"
  val REFUND = "r"

  val converter = new ThriftBinarySerializer

  val coll: MongoCollection

  def addItem(item: OrderInfo, quantity: Long) = coll.save(toBson(item, quantity), com.mongodb.WriteConcern.ACKNOWLEDGED)

  def updateItem(orderId: Long, inAmount: Long, quantity: Long, status: Int, side: MarketSide, timestamp: Long, refund: Option[Refund]) = {
    if (refund.isDefined)
      coll.update(MongoDBObject(OID -> orderId), $set(IN_AMOUNT -> inAmount, QUANTITY -> quantity, STATUS -> status,
        UPDATED_TIME -> timestamp, SIDE -> side.ordered, REFUND -> converter.toBinary(refund.get)), false, false, com.mongodb.WriteConcern.ACKNOWLEDGED)
    else
      coll.update(MongoDBObject(OID -> orderId), $set(IN_AMOUNT -> inAmount, QUANTITY -> quantity, STATUS -> status,
        UPDATED_TIME -> timestamp, SIDE -> side.ordered), false, false, com.mongodb.WriteConcern.ACKNOWLEDGED)
  }

  def cancelItem(orderId: Long) =
    coll.update(MongoDBObject(OID -> orderId), $set(STATUS -> OrderStatus.Cancelled.getValue()), false, false, com.mongodb.WriteConcern.ACKNOWLEDGED)

  def countItems(q: QueryOrder): Long =
    try {
      coll.count(mkQuery(q))
    } catch {
      case e: Exception =>
        0L
    }

  def getItems(q: QueryOrder): Seq[OrderInfo] =
    try {
      coll.find(mkQuery(q)).sort(DBObject(OID -> -1)).skip(q.cursor.skip).limit(q.cursor.limit).map(toClass(_)).toSeq
    } catch {
      case e: Exception =>
        Seq.empty[OrderInfo]
    }

  private def toBson(item: OrderInfo, quantity: Long) = {
    val side = item.side
    val obj = MongoDBObject(
      OID -> item.order.id, UID -> item.order.userId, ORIGIN_ORDER -> converter.toBinary(item.order),
      IN_AMOUNT -> item.inAmount, QUANTITY -> quantity, MARKET -> side.market.toString,
      SIDE -> side.ordered, CREATED_TIME -> item.order.timestamp.getOrElse(0), STATUS -> item.status.getValue())
    if (item.order.refund.isDefined) obj.put(REFUND, converter.toBinary(item.order.refund.get))

    if (item.lastTxTimestamp.isDefined) obj ++ (UPDATED_TIME -> item.lastTxTimestamp.get)
    else obj
  }

  private def toClass(obj: MongoDBObject) = {
    val o = converter.fromBinary(obj.getAsOrElse(ORIGIN_ORDER, null), Some(classOf[Order.Immutable])).asInstanceOf[Order]
    val refund = obj.getAs[Array[Byte]](REFUND) match {
      case Some(bytes) => Some(converter.fromBinary(bytes, Some(classOf[Refund.Immutable])).asInstanceOf[Refund])
      case None => None
    }

    OrderInfo(
      order = mergeRefund(o, refund), inAmount = obj.getAsOrElse[Long](IN_AMOUNT, 0),
      outAmount = o.quantity - obj.getAsOrElse[Long](QUANTITY, 0),
      side = obj.getAsOrElse[String](MARKET, "").getMarketSide(obj.getAsOrElse[Boolean](SIDE, true)),
      lastTxTimestamp = obj.getAs[Long](UPDATED_TIME),
      status = OrderStatus.get(obj.getAsOrElse[Int](STATUS, 0)).getOrElse(OrderStatus.Pending))
  }

  private def mergeRefund(o: Order, refund: Option[Refund]) =
    Order(userId = o.userId, id = o.id, quantity = o.quantity, price = o.price, takeLimit = o.takeLimit,
      timestamp = o.timestamp, robotType = o.robotType, robotId = o.robotId, onlyTaker = o.onlyTaker,
      inAmount = o.inAmount, refund = refund)

  private def mkQuery(q: QueryOrder) = {
    var query = MongoDBObject()
    if (q.oid.isDefined) query = query ++ (OID -> q.oid.get)
    if (q.uid.isDefined) query = query ++ (UID -> q.uid.get)
    if (q.side.isDefined) query = {
      val querySide = q.side.get
      val side = querySide.side
      if (querySide.bothSide) query ++ (MARKET -> side.market.toString)
      else query ++ (MARKET -> side.market.toString, SIDE -> side.ordered)
    }

    if (!q.statusList.isEmpty) query ++= $or(q.statusList.map(STATUS -> _): _*)
    query
  }
}
