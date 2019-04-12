package com.giftedprimate.emailbitcoin.router

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.scaladsl.Flow
import com.giftedprimate.emailbitcoin.actors.TransactionStatusActor
import com.giftedprimate.emailbitcoin.daos.EBTransactionDAO
import com.giftedprimate.emailbitcoin.entities.GetActorFlow
import com.google.inject.Inject

import scala.util.{Failure, Success}

class WSRouter @Inject()(
    ebTransactionDAO: EBTransactionDAO,
    actorSystem: ActorSystem
) extends PartialRoute {
  override def router: Route = pathPrefix("ws") {
    path("transtatus") {
      val transactionStatusActor =
        actorSystem.actorOf(TransactionStatusActor.props(ebTransactionDAO))
      val futureFlow = (transactionStatusActor ? GetActorFlow())
        .mapTo[Flow[Message, Message, _]]
      onComplete(futureFlow) {
        case Success(flow)      => handleWebSocketMessages(flow)
        case Failure(exception) => complete(exception.getMessage)
      }
    }
  }
}
