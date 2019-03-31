package com.giftedprimate.emailbitcoin.actors

import akka.actor.{Actor, Props}
import com.giftedprimate.emailbitcoin.bitcoin.BitcoinClient
import com.giftedprimate.emailbitcoin.daos.{RecipientWalletDAO, SessionDAO}
import com.giftedprimate.emailbitcoin.entities.{CreationForm, FundData, Session}
import com.google.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global

object WalletActor {
  final case class CreateWallet(creationForm: CreationForm)

  def props(bitcoinClient: BitcoinClient,
            recipientWalletDAO: RecipientWalletDAO,
            sessionDAO: SessionDAO) = Props(
    new WalletActor(bitcoinClient, recipientWalletDAO, sessionDAO)
  )
}

class WalletActor @Inject()(
    bitcoinClient: BitcoinClient,
    recipientWalletDAO: RecipientWalletDAO,
    sessionDAO: SessionDAO
) extends Actor {
  import WalletActor._

  override def receive: Receive = {
    case CreateWallet(creationForm) =>
      val s = sender
      for {
        recipientWallet <- bitcoinClient.addWallet(creationForm)
        _ <- recipientWalletDAO.save(recipientWallet)
        session <- sessionDAO.save(Session(recipientWallet))
      } yield s ! session
  }
}
