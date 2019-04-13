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
    } ~ pathPrefix("getpka") {
      parameter('sessionid) { sessionId =>
        handleSessionWalletHtml(sessionDAO.findWithWallet(sessionId))("pending") {
          (_, recipientWallet) =>
            html.payTransaction.render(
              recipientWallet.publicKeyAddress,
              getQRCode(recipientWallet.publicKeyAddress))
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
