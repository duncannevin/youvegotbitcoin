package com.duncannevin.youvegotbitcoin.router

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server.{Directives, Route}
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
import com.duncannevin.youvegotbitcoin.validators.{
  ClientDirectives,
  EBDirectives,
  ValidatorDirectives
}
import com.google.inject.Inject
import javax.inject.Singleton

import scala.util.{Failure, Success}

@Singleton
class WSRouter @Inject()(
    ebTransactionDAO: EBTransactionDAO,
    sessionDAO: SessionDAO,
    actorSystem: ActorSystem,
    bitcoinClient: BitcoinClient,
    recipientWalletDAO: RecipientWalletDAO,
) extends PartialRoute
    with Directives
    with EBDirectives
    with ValidatorDirectives
    with ClientDirectives {
  override def router: Route = pathPrefix("ws") {
    pathPrefix("status") {
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
