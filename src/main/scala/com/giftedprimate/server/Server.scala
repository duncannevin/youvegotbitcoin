package com.giftedprimate.server

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import com.giftedprimate.router.Routes

import scala.concurrent.{ExecutionContext, Future}

class Server (host: String, port: Int)
             (implicit system: ActorSystem, ec: ExecutionContext, mat: ActorMaterializer) {
  val router: Flow[HttpRequest, HttpResponse, NotUsed] = new Routes().routes
  def bind(): Future[ServerBinding] = Http().bindAndHandle(router, host, port)
}
