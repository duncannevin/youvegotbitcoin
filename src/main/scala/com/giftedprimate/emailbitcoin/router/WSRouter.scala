package com.giftedprimate.emailbitcoin.router

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.scaladsl.Flow
import com.giftedprimate.emailbitcoin.actors.TransactionStatusActor
import com.giftedprimate.emailbitcoin.daos.{EBTransactionDAO, SessionDAO}
import com.giftedprimate.emailbitcoin.entities.{ApiError, GetActorFlow}
import com.giftedprimate.emailbitcoin.validators.EBDirectives
import com.google.inject.Inject

import scala.util.{Failure, Success}

class WSRouter @Inject()(
    ebTransactionDAO: EBTransactionDAO,
    sessionDAO: SessionDAO,
    actorSystem: ActorSystem
) extends PartialRoute
    with EBDirectives {
  override def router: Route = pathPrefix("ws") {
    path("transtatus") {
      parameter("sessionid") { sessionId =>
        handleSessionWallet(sessionDAO.findWithWallet(sessionId)) {
          sessionWallet =>
            val transactionStatusActor =
              actorSystem.actorOf(
                TransactionStatusActor.props(sessionWallet.session,
                                             ebTransactionDAO))
            val futureFlow = (transactionStatusActor ? GetActorFlow())
              .mapTo[Flow[Message, Message, _]]
            onComplete(futureFlow) {
              case Success(flow)      => handleWebSocketMessages(flow)
              case Failure(exception) => complete(exception.getMessage)
            }
        }
      }
    }
  }
}
