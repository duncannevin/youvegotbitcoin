package com.giftedprimate.transaction

import com.giftedprimate.configuration.SystemConfig
import com.giftedprimate.loggers.TransactionLog
import com.giftedprimate.models.{CreationForm, RecipientWallet}
import com.google.inject.Inject
import org.bitcoinj.script.Script.ScriptType
import org.bitcoinj.wallet.Wallet

import scala.concurrent.Future

class TransactionControl @Inject()(
    transactionLog: TransactionLog,
    bitcoinClient: BitcoinClient,
    systemConfig: SystemConfig
) {
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
    Future.successful(recipientWallet)
  }
}
