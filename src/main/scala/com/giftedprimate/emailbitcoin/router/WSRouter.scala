package com.giftedprimate.emailbitcoin.router

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.scaladsl.Flow
import com.giftedprimate.emailbitcoin.actors.WSSupervisorActor.GetFlow
import com.google.inject.Inject
import com.google.inject.name.Named

import scala.util.{Failure, Success}

class WSRouter @Inject()(
    @Named("ws-supervisor-actor") WSSupervisorActor: ActorRef,
    implicit val actorSystem: ActorSystem
) extends PartialRoute {
  override def router: Route = pathPrefix("ws") {
    path("transtatus") {
      val transactionStatusActor =
        (WSSupervisorActor ? GetFlow("transaction-status"))
          .mapTo[Flow[Message, Message, _]]
      onComplete(transactionStatusActor) {
        case Success(flow)      => handleWebSocketMessages(flow)
        case Failure(exception) => complete(exception.getMessage)
      }
    }
  }
}
