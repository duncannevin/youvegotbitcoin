package com.giftedprimate.router

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.settings.RoutingSettings
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow

import scala.concurrent.ExecutionContext

class Routes()
            (implicit system: ActorSystem, materializer: ActorMaterializer, ec: ExecutionContext) {
  implicit private val routingSettings: RoutingSettings = RoutingSettings.default

  private val partialRoutes: Seq[PartialRoute] = Seq(
    new TransactionRouter
  )

  private val allRoutes: Route =
    partialRoutes.foldRight[Route](reject) { (partial, builder) =>
      partial.router ~ builder
    }

  val routes: Flow[HttpRequest, HttpResponse, NotUsed] = Route.handlerFlow(allRoutes)
}
