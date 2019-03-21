package com.giftedprimate.transaction

import com.giftedprimate.configuration.{ConfigModule, SystemConfig}
import com.giftedprimate.loggers.TransactionLog
import org.bitcoinj.script.Script.ScriptType
import org.bitcoinj.wallet.Wallet

import scala.concurrent.{ExecutionContext, Future}

class TransactionControl(
    transactionLog: TransactionLog,
    bitcoinClient: BitcoinClient
)(
    implicit ec: ExecutionContext
) extends SystemConfig {
  def addWallet(creationForm: CreationForm): Future[RecipientWallet] = {
    val wallet: Wallet =
      Wallet.createDeterministic(bitcoinClient.params, ScriptType.P2PKH)
    val recipientWallet = RecipientWallet(wallet, creationForm)
    wallet.setDescription(
      s"for: ${creationForm.recipientEmail} from ${creationForm.senderEmail}")
    wallet.addCoinsReceivedEventListener(bitcoinClient.walletListener)
    wallet.setAcceptRiskyTransactions(true)
    bitcoinClient.peerGroup.addWallet(wallet)
    transactionLog.watchingWallet(recipientWallet)
//    for {
//      _ <- add recipientWallet to db
//    } yield respond with recipient wallet
    Future.successful(recipientWallet)
  }
}
