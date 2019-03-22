package com.giftedprimate

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import com.giftedprimate.configuration._
import com.giftedprimate.daos.RecipientWalletDAO
import com.giftedprimate.loggers.{DAOLogger, ServerLog, TransactionLog}
import com.giftedprimate.notification.NotificationActor
import com.giftedprimate.server.Server
import com.giftedprimate.transaction.{
  BitcoinClient,
  TransactionActor,
  TransactionControl
}

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}

object Main extends App with SystemConfig {
  implicit val system: ActorSystem = ActorSystem("emailbitcoin")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val ec: ExecutionContext = ExecutionContext.global

  // dependencies
  val serverConfig = new ServerConfig
  val bitcoinConfig = new BitcoinConfig
  val mongoConfig = new MongoConfig
  val serverLog: ServerLog = new ServerLog(serverConfig)
  val transactionLog: TransactionLog = new TransactionLog
  val recipientWalletDAO = new RecipientWalletDAO(mongoConfig.mongoDb)
  val bitcoinClient: BitcoinClient =
    new BitcoinClient(transactionLog, bitcoinConfig, notificationActor)
  val transactionControl =
    new TransactionControl(transactionLog, bitcoinClient)

  // actors
  def transactionActor: ActorRef =
    system.actorOf(
      TransactionActor.props(transactionControl, transactionLog, ec),
      "transaction-actor")
  def notificationActor: ActorRef =
    system.actorOf(NotificationActor.props, "notification-actor")

  val server: Server = new Server(serverConfig, transactionActor)
  val binding = server.bind()
  binding.onComplete {
    case Success(_)     => serverLog.successfulStart
    case Failure(error) => serverLog.failedStart(error.getMessage)
  }

  Await.result(binding, FiniteDuration(3, "seconds"))
}
