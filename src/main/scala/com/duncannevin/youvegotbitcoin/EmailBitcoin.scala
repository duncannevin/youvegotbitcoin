package com.duncannevin.youvegotbitcoin

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.settings.RoutingSettings
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import com.duncannevin.youvegotbitcoin.configuration.ServerConfig
import com.duncannevin.youvegotbitcoin.loggers.ServerLogger
import com.duncannevin.youvegotbitcoin.router.{PartialRoute, Routes}
import com.google.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

class EmailBitcoin @Inject()(
    routes: Routes,
    serverLog: ServerLogger,
    config: ServerConfig
)(
    implicit val actorSystem: ActorSystem
) {
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()
  implicit val routerSettings: RoutingSettings = RoutingSettings.default

  lazy val partialRoutes: Seq[PartialRoute] = routes.routes

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
