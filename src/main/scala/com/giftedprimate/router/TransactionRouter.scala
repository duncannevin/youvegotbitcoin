package com.giftedprimate.router

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import com.giftedprimate.configuration.{ConfigModule, SystemConfig}

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
      }
    }
  }
}
