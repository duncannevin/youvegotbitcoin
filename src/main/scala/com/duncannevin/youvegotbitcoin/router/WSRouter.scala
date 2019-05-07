package com.duncannevin.youvegotbitcoin.router

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.stream.scaladsl.Flow
import com.duncannevin.youvegotbitcoin.actors.StatusActor
import com.duncannevin.youvegotbitcoin.bitcoin.BitcoinClient
import com.duncannevin.youvegotbitcoin.daos.{
  EBTransactionDAO,
  RecipientWalletDAO,
  SessionDAO
}
import com.duncannevin.youvegotbitcoin.entities.{ApiError, GetActorFlow}
import com.duncannevin.youvegotbitcoin.validators.EBDirectives
import com.google.inject.Inject

import scala.util.{Failure, Success}

class WSRouter @Inject()(
    ebTransactionDAO: EBTransactionDAO,
    sessionDAO: SessionDAO,
    actorSystem: ActorSystem,
    bitcoinClient: BitcoinClient,
    recipientWalletDAO: RecipientWalletDAO,
) extends PartialRoute
    with EBDirectives {
  override def router: Route = pathPrefix("ws") {
    path("status") {
      parameter('sessionid) { sessionId =>
        handleSession(sessionDAO.find(sessionId)) { session =>
          val transactionStatusActor = actorSystem.actorOf(
            StatusActor.props(
              session,
              ebTransactionDAO,
              bitcoinClient,
              recipientWalletDAO,
              sessionDAO
            )
          )
          val futureFlow = (transactionStatusActor ? GetActorFlow())
            .mapTo[Flow[Message, Message, _]]
          onComplete(futureFlow) {
            case Success(flow) => handleWebSocketMessages(flow)
            case Failure(_) =>
              complete(ApiError.generic.statusCode, ApiError.generic.message)
          }
        }
      }
    }
  }
}
