package com.giftedprimate

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.giftedprimate.configuration.ConfigModule
import com.giftedprimate.loggers.ServerLog
import com.giftedprimate.router.TransactionRouter
import com.giftedprimate.server.Server

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}

object Main extends App with ConfigModule {
  implicit val system: ActorSystem = ActorSystem("emailbitcoin")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  import system.dispatcher

  val transactionRouter: TransactionRouter = new TransactionRouter()
  val serverLog: ServerLog = new ServerLog
  val server: Server = new Server(host, port)

  val binding = server.bind()

  binding.onComplete {
    case Success(_) => serverLog.successfulStart
    case Failure(error) => serverLog.failedStart(error.getMessage)
  }

  Await.result(binding, FiniteDuration(3, "seconds"))
}
