package com.giftedprimate.router

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.ask
import com.giftedprimate.configuration.{ConfigModule, SystemConfig}
import com.giftedprimate.messages.ApiError
import com.giftedprimate.models.CreationForm
import com.giftedprimate.transaction.TransactionActor.CreateWallet
import com.giftedprimate.validators.{
  CreateWalletValidator,
  ValidatorDirectives,
  WalletDirectives
}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class TransactionRouter(
    config: ConfigModule,
    transactionActor: ActorRef
)(implicit ec: ExecutionContext)
    extends PartialRoute
    with SystemConfig
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
