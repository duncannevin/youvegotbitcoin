package com.giftedprimate.router

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import com.giftedprimate.configuration.{ConfigModule, SystemConfig}
import com.giftedprimate.transaction.CreationForm
import com.giftedprimate.transaction.TransactionActor.CreateWallet

import scala.concurrent.ExecutionContext

class TransactionRouter(
    config: ConfigModule,
    transactionActor: ActorRef
)(implicit ec: ExecutionContext)
    extends PartialRoute
    with SystemConfig {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  override def router: Route = pathPrefix("transaction") {
    pathEndOrSingleSlash {
      get {
        val str = (transactionActor ? "BlaBla").mapTo[String]
        onSuccess(str)(str => complete((StatusCodes.OK, str)))
      } ~ post {
        entity(as[CreationForm]) { creationForm =>
          val publicKeyAddress =
            (transactionActor ? CreateWallet(creationForm)).mapTo[String]
          onSuccess(publicKeyAddress)(addr => complete(StatusCodes.OK, addr))
        }
      }
    }
  }
}
