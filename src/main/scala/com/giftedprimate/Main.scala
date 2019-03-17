package com.giftedprimate

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import com.giftedprimate.configuration.{ConfigModule, SystemConfig}
import com.giftedprimate.loggers.ServerLog
import com.giftedprimate.server.Server
import com.giftedprimate.transaction.TransactionActor

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

  // actors
  def transactionActor: ActorRef =
    system.actorOf(TransactionActor.props(config), "transaction-actor")

  val server: Server = new Server(config, transactionActor)
  val binding = server.bind()
  binding.onComplete {
    case Success(_)     => serverLog.successfulStart
    case Failure(error) => serverLog.failedStart(error.getMessage)
  }

  Await.result(binding, FiniteDuration(3, "seconds"))
}
