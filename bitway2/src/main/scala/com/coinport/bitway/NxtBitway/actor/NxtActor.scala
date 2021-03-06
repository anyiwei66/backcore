package com.coinport.bitway.NxtBitway.actor

/**
 * Created by chenxi on 7/17/14.
 */
import akka.actor.{ActorLogging, Actor}
import akka.event.LoggingReceive
import scala.concurrent.duration._
import com.redis.serialization.Parse.Implicits.parseByteArray
import com.coinport.coinex.serializers.ThriftBinarySerializer
import com.coinport.coinex.data.{BitwayMessage, BitwayRequest, Currency}
import com.coinport.coinex.data.BitwayRequestType._
import com.coinport.bitway.NxtBitway.processor.NxtProcessor
import com.coinport.bitway.NxtBitway.BitwayConfig

class NxtActor(processor: NxtProcessor, config: BitwayConfig) extends Actor with ActorLogging {
  val client = processor.getRedisClient
  val serializer = new ThriftBinarySerializer()

  val responseChannel = config.responseChannelPrefix + Currency.Nxt.value.toString
  val requestChannel = config.requestChannelPrefix + Currency.Nxt.value.toString

  implicit val executeContext = context.system.dispatcher

  override def preStart = {
    super.preStart
    sendMessageToSelf(1)
  }

  def receive = LoggingReceive {
    case ListenAtRedis =>
      processor.getNewBlock.foreach{m =>
        log.info("find new block, height: %d, id: %s".format(m.blockMsg.get.block.index.height.get, m.blockMsg.get.block.index.id.get))
        client.rpush(responseChannel, serializer.toBinary(m))}

      processor.getUnconfirmedTransactions.foreach{m =>
        log.info("find unconfirmed transactions: %s".format(m.tx.get.txid))
        client.rpush(responseChannel, serializer.toBinary(m))}

      client.lpop(requestChannel) match {
        case Some(s) =>
          val request = serializer.fromBinary(s, classOf[BitwayRequest.Immutable]).asInstanceOf[BitwayRequest]
          val message: Seq[BitwayMessage] = request.`type` match {
            case GenerateAddress => processor.generateAddresses(request.generateAddresses.get)
            case Transfer => processor.sendMoney(request.transferCryptoCurrency.get)
            case MultiTransfer => processor.multiSendMoney(request.multiTransferCryptoCurrency.get)
            case GetMissedBlocks => processor.getMissedBlocks(request.getMissedCryptoCurrencyBlocksRequest.get)
            case SyncHotAddresses => processor.syncHotAddresses(request.syncHotAddresses.get)
            case SyncPrivateKeys => processor.syncPrivateKeys(request.syncPrivateKeys.get)
            case x => Nil
          }
          message.foreach(m => client.rpush(responseChannel, serializer.toBinary(m)))
          sendMessageToSelf(0)
        case None =>
          sendMessageToSelf(1)
      }
  }

  private def sendMessageToSelf(timeout: Long = 0) {
    context.system.scheduler.scheduleOnce(timeout.seconds, self, ListenAtRedis)(context.system.dispatcher)
  }

  case object ListenAtRedis
}

