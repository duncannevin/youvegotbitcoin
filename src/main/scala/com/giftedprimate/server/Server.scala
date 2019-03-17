package com.giftedprimate.server

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import com.giftedprimate.configuration.ConfigModule
import com.giftedprimate.router.Routes

import scala.concurrent.{ExecutionContext, Future}

class Server(
    config: ConfigModule,
    transactionActor: ActorRef
)(implicit system: ActorSystem, ec: ExecutionContext, mat: ActorMaterializer) {

  val router: Flow[HttpRequest, HttpResponse, NotUsed] = new Routes(
    config,
    transactionActor
  ).routes
  def bind(): Future[ServerBinding] =
    Http().bindAndHandle(router, config.host, config.port)
}
