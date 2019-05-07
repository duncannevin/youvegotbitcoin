package com.duncannevin.youvegotbitcoin.actors

import akka.actor.{Actor, Props}
import com.duncannevin.youvegotbitcoin.bitcoin.BitcoinClient
import com.duncannevin.youvegotbitcoin.daos.{RecipientWalletDAO, SessionDAO}
import com.duncannevin.youvegotbitcoin.entities.{CreationForm, Session}
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
