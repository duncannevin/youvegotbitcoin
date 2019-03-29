package com.giftedprimate.emailbitcoin.router

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.ask
import com.giftedprimate.emailbitcoin.actors.NewWalletActor.CreateWallet
import com.giftedprimate.emailbitcoin.entities.{CreationForm, FundData}
import com.giftedprimate.emailbitcoin.validators.{
  CreateWalletValidator,
  ValidatorDirectives,
  WalletDirectives
}
import com.google.inject.{Inject, Singleton}
import com.google.inject.name.Named

@Singleton
class TransactionRouter @Inject()(
    @Named("new-wallet-actor") newWalletActor: ActorRef,
    implicit val actorSystem: ActorSystem
) extends PartialRoute
    with Directives
    with WalletDirectives
    with ValidatorDirectives {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  override def router: Route = pathPrefix("api") {
    pathEndOrSingleSlash {
      post {
        entity(as[CreationForm]) { creationForm =>
          validateWith(CreateWalletValidator)(creationForm) {
            val reqPublicKey =
              (newWalletActor ? CreateWallet(creationForm))
                .mapTo[FundData]
            onSuccess(reqPublicKey) { publicKeyAddress =>
              complete(StatusCodes.OK, publicKeyAddress)
            }
          }
        }
      }
    }
  }
}
