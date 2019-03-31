package com.giftedprimate.emailbitcoin.router

import java.util.NoSuchElementException

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.{Directives, Route}
import com.giftedprimate.emailbitcoin.daos.{RecipientWalletDAO, SessionDAO}
import com.giftedprimate.emailbitcoin.messages.ApiError
import com.giftedprimate.emailbitcoin.validators.{
  ClientDirectives,
  ValidatorDirectives,
  WalletDirectives
}
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class HomeRouter @Inject()(
    @Named("new-wallet-actor") newWalletActor: ActorRef,
    implicit val actorSystem: ActorSystem,
    sessionDAO: SessionDAO,
    recipientWalletDAO: RecipientWalletDAO
) extends PartialRoute
    with Directives
    with WalletDirectives
    with ValidatorDirectives
    with ClientDirectives {

  override def router: Route =
    pathEndOrSingleSlash {
      get {
        toHtml(html.createTransaction.render())
      }
    } ~ pathPrefix("getpka") {
      parameter('sessionid) { sessionId =>
        handle(sessionDAO.findWithWallet(sessionId))(v =>
          ApiError.noSessionError) {
          case (Some(session), None)         => ???
          case (None, Some(wallet))          => ???
          case (Some(session), Some(wallet)) => ???
          case _                             => ???
        }
      }
    } ~ pathPrefix("css") {
      getFromDirectory("public/css")
    } ~ pathPrefix("js") {
      getFromDirectory("public/js")
    } ~ pathPrefix("bower_components") {
      getFromDirectory("public/bower_components")
    }
}
