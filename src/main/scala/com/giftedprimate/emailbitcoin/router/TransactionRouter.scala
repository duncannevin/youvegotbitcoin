package com.giftedprimate.emailbitcoin.router

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.giftedprimate.emailbitcoin.actors.WalletActor.CreateWallet
import com.giftedprimate.emailbitcoin.entities.{CreationForm, FundData, Session}
import com.giftedprimate.emailbitcoin.validators.{
  ClientDirectives,
  CreateWalletValidator,
  ValidatorDirectives,
  EBDirectives
}
import com.google.inject.Inject
import com.google.inject.name.Named
import akka.pattern.ask

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
              val reqPublicKey =
                (newWalletActor ? CreateWallet(creationForm))
                  .mapTo[Session]
              onSuccess(reqPublicKey) { session =>
                complete(s"/getpka?sessionid=${session.sessionId}")
              }
            }
          }
        }
      }
    }
}
