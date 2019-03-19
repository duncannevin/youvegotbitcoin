package com.giftedprimate

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import com.giftedprimate.configuration.{ConfigModule, SystemConfig}
import com.giftedprimate.loggers.{ServerLog, TransactionLog}
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
  val config = new ConfigModule
  val serverLog: ServerLog = new ServerLog(config)
  val transactionLog: TransactionLog = new TransactionLog
  val bitcoinClient: BitcoinClient =
    new BitcoinClient(transactionLog, config, notificationActor)
  val transactionControl =
    new TransactionControl(config, transactionLog, bitcoinClient)

  // actors
  def transactionActor: ActorRef =
    system.actorOf(
      TransactionActor.props(config, transactionControl, transactionLog, ec),
      "transaction-actor")
  def notificationActor: ActorRef =
    system.actorOf(NotificationActor.props, "notification-actor")

  val server: Server = new Server(config, transactionActor)
  val binding = server.bind()
  binding.onComplete {
    case Success(_)     => serverLog.successfulStart
    case Failure(error) => serverLog.failedStart(error.getMessage)
  }

  Await.result(binding, FiniteDuration(3, "seconds"))
}
