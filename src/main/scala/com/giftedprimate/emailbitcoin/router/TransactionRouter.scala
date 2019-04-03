package com.giftedprimate.emailbitcoin.router

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.giftedprimate.emailbitcoin.actors.WalletActor.CreateWallet
import com.giftedprimate.emailbitcoin.entities.{CreationForm, FundData, Session}
import com.giftedprimate.emailbitcoin.validators.{
  ClientDirectives,
  CreateWalletValidator,
  EBDirectives,
  ValidatorDirectives
}
import com.google.inject.Inject
import com.google.inject.name.Named
import akka.pattern.ask
import com.giftedprimate.emailbitcoin.messages.ApiError

class TransactionRouter @Inject()(
    @Named("new-wallet-actor") newWalletActor: ActorRef,
    implicit val actorSystem: ActorSystem
) extends PartialRoute
    with Directives
    with EBDirectives
    with ValidatorDirectives
    with ClientDirectives {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  override def router: Route =
    pathPrefix("api") {
      path("createwallet") {
        post {
          entity(as[CreationForm]) { creationForm =>
            validateWith(CreateWalletValidator)(creationForm) {
              handleActor(
                (newWalletActor ? CreateWallet(creationForm)).mapTo[Session])(
                _ => ApiError.generic) { session =>
                complete(s"/getpka?sessionid=${session.sessionId}")
              }
            }
          }
        }
      }
    }
}
