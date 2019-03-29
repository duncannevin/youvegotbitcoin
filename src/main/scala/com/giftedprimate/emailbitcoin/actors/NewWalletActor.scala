package com.giftedprimate.emailbitcoin.actors

import akka.actor.{Actor, Props}
import com.giftedprimate.emailbitcoin.bitcoin.BitcoinClient
import com.giftedprimate.emailbitcoin.daos.RecipientWalletDAO
import com.giftedprimate.emailbitcoin.entities.{CreationForm, FundData}
import com.google.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global

object NewWalletActor {
  final case class CreateWallet(creationForm: CreationForm)

  def props(bitcoinClient: BitcoinClient,
            recipientWalletDAO: RecipientWalletDAO) = Props(
    new NewWalletActor(bitcoinClient, recipientWalletDAO)
  )
}

class NewWalletActor @Inject()(
    bitcoinClient: BitcoinClient,
    recipientWalletDAO: RecipientWalletDAO
) extends Actor {
  import NewWalletActor._

  override def receive: Receive = {
    case CreateWallet(creationForm) =>
      val s = sender
      for {
        recipientWallet <- bitcoinClient.addWallet(creationForm)
        _ <- recipientWalletDAO.save(recipientWallet)
      } yield
        s ! FundData(recipientWallet.createForm.recipientEmail,
                     recipientWallet.publicKeyAddress)
  }
}
