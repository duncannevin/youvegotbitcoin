package com.giftedprimate.loggers
import com.giftedprimate.transaction.RecipientWallet

class TransactionLog extends Logger {
  def startingBitcoin(network: String): Unit =
    logger.info(s"starting bitcoin on $network network")
  def blockChainDownloadStarted(left: Long): Unit =
    logger.info(s"block chain download started: $left blocks left")
  def blockChainDownloadFinished(): Unit =
    logger.info("block chain download is finished")
  def unrecognizedMessageSentToActor(): Unit =
    logger.info("unrecognized message sent to actor")
  def watchingWallet(recipientWallet: RecipientWallet): Unit =
    logger.info(
      s"watching wallet: ${recipientWallet.publicKey} seed: ${recipientWallet.seed} sendTo: ${recipientWallet.publicKeyAddress}")
  override def info(msg: String): Unit = ???
  override def warn(msg: String): Unit = ???
  override def debug(msg: String): Unit = ???
}
