package com.giftedprimate.emailbitcoin.router

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.{Directives, Route}
import com.giftedprimate.emailbitcoin.daos.{RecipientWalletDAO, SessionDAO}
import com.giftedprimate.emailbitcoin.utils.QRCodeUtil.getQRCode
import com.giftedprimate.emailbitcoin.validators.{
  ClientDirectives,
  EBDirectives,
  ValidatorDirectives
}
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import com.github.scala_opennode._

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
      handleSessionWallet(sessionDAO.findWithWallet(sessionId), isHtml = true) {
        rawSessionWallet =>
          pathPrefix("pay") {
            handleStatus(rawSessionWallet, "pending", isHtml = true) {
              sessionWallet =>
                val publicKeyAddress =
                  sessionWallet.recipientWallet.publicKeyAddress
                toHtml(html.payTransaction.render(publicKeyAddress,
                                                  getQRCode(publicKeyAddress)))
            }
          } ~ pathPrefix("sender") {
            path("status") {
              handleStatus(rawSessionWallet, "funded", isHtml = true) {
                sessionWallet =>
                  toHtml(html.senderStatus.render())
              }
            }
          } ~ pathPrefix("recipient") {
            ???
          } ~ pathPrefix("recover") {
            path("seed") {
              ???
            } ~ path("address") {
              ???
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
