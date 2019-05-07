package com.duncannevin.youvegotbitcoin.router

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.{Directives, Route}
import com.duncannevin.youvegotbitcoin.daos.{RecipientWalletDAO, SessionDAO}
import com.duncannevin.youvegotbitcoin.validators.{
  ClientDirectives,
  EBDirectives,
  ValidatorDirectives
}
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import com.duncannevin.youvegotbitcoin.utils.QRCodeUtil._

@Singleton
class HomeRouter @Inject()(
    @Named("new-wallet-actor") newWalletActor: ActorRef,
    implicit val actorSystem: ActorSystem,
    sessionDAO: SessionDAO,
    recipientWalletDAO: RecipientWalletDAO
) extends PartialRoute
    with Directives
    with EBDirectives
    with ValidatorDirectives
    with ClientDirectives {
  override def router: Route =
    pathEndOrSingleSlash {
      get {
        toHtml(html.createTransaction.render())
      }
    } ~ parameter('sessionid) { sessionId =>
      handleSession(sessionDAO.find(sessionId)) { session =>
        handleWallet(recipientWalletDAO.find(session.publicKey)) { wallet =>
          pathPrefix("pay") {
            handleStatus(session, "pending", isHtml = true) { _ =>
              val publicKeyAddress =
                wallet.publicKeyAddress
              toHtml(
                html.pay.render(publicKeyAddress, getQRCode(publicKeyAddress)))
            }
          } ~ handleStatus(session, "funded reclaimed received", isHtml = true) {
            _ =>
              pathPrefix("sender") {
                path("status") {
                  toHtml(html.senderStatus.render(session, wallet))
                }
              } ~ pathPrefix("recipient") {
                ???
              } ~ pathPrefix("recover") {
                toHtml(html.recover.render(session, wallet))
              }
          }
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
