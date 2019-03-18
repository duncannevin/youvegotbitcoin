package com.giftedprimate

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import com.giftedprimate.configuration.{ConfigModule, SystemConfig}
import com.giftedprimate.loggers.{ServerLog, TransactionLog}
import com.giftedprimate.notification.NotificationActor
import com.giftedprimate.server.Server
import com.giftedprimate.transaction.{TransactionActor, TransactionControl}

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}

object Main extends App with SystemConfig {
  implicit val system: ActorSystem = ActorSystem("emailbitcoin")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  import system.dispatcher

  // dependencies
  val config = new ConfigModule
  val serverLog: ServerLog = new ServerLog(config)
  val transactionLog: TransactionLog = new TransactionLog
  val transactionControl =
    new TransactionControl(config, notificationActor, transactionLog)

  // actors
  def transactionActor: ActorRef =
    system.actorOf(TransactionActor.props(config, transactionControl),
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
