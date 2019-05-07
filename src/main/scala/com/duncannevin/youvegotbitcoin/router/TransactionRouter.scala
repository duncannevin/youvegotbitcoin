package com.duncannevin.youvegotbitcoin.router

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.ask
import com.duncannevin.youvegotbitcoin.actors.WalletActor.CreateWallet
import com.duncannevin.youvegotbitcoin.entities.{
  ApiError,
  CreationForm,
  Session
}
import com.duncannevin.youvegotbitcoin.validators.{
  ClientDirectives,
  CreateWalletValidator,
  EBDirectives,
  ValidatorDirectives
}
import com.google.inject.Inject
import com.google.inject.name.Named
import javax.inject.Singleton

@Singleton
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
              handleActor {
                (newWalletActor ? CreateWallet(creationForm)).mapTo[Session]
              }(_ => ApiError.generic) { session =>
                complete(s"/pay?sessionid=${session.sessionId}")
              }
            }
          }
        }
      }
    }
}
