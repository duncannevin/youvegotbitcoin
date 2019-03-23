package com.giftedprimate.router

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.ask
import com.giftedprimate.models.CreationForm
import com.giftedprimate.transaction.TransactionActor.CreateWallet
import com.giftedprimate.validators.{
  CreateWalletValidator,
  ValidatorDirectives,
  WalletDirectives
}
import com.google.inject.{Inject, Singleton}
import com.google.inject.name.Named

@Singleton
class TransactionRouter @Inject()(
    @Named("transaction-actor") transactionActor: ActorRef,
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
              (transactionActor ? CreateWallet(creationForm))
                .mapTo[String]
            onSuccess(reqPublicKey) { publicKeyAddress =>
              complete(StatusCodes.OK, publicKeyAddress)
            }
          }
        }
      }
    }
  }
}
