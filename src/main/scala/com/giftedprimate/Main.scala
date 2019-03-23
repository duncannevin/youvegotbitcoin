package com.giftedprimate

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.{Http}
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.settings.RoutingSettings
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import com.giftedprimate.configuration.ServerConfig
import com.giftedprimate.loggers.ServerLog
import com.giftedprimate.router.{PartialRoute, TransactionRouter}
import com.google.inject.{Guice, Inject}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object Main extends App {
  val injector = Guice.createInjector(new Module)
  implicit val actorSystem: ActorSystem =
    injector.getInstance(classOf[ActorSystem])
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()
  val transactionRouter = injector.getInstance(classOf[TransactionRouter])
  val serverLog = injector.getInstance(classOf[ServerLog])
  val config = injector.getInstance(classOf[ServerConfig])

  implicit val routingSettings: RoutingSettings =
    RoutingSettings.default

  lazy val partialRoutes: Seq[PartialRoute] = Seq(transactionRouter)

  lazy val allRoutes: Route = partialRoutes.foldRight[Route](reject) {
    (partial, builder) =>
      partial.router ~ builder
  }

  lazy val router: Flow[HttpRequest, HttpResponse, NotUsed] =
    Route.handlerFlow(allRoutes)

  lazy val binding: Future[ServerBinding] =
    Http().bindAndHandle(router, config.host, config.port)

  binding.onComplete {
    case Success(_)     => serverLog.successfulStart
    case Failure(error) => serverLog.failedStart(error.getMessage)
  }

  Await.result(binding, FiniteDuration(3, "seconds"))
}
