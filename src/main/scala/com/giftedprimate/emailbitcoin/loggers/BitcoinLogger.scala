package com.giftedprimate.emailbitcoin.loggers
import com.giftedprimate.emailbitcoin.entities.RecipientWallet

class BitcoinLogger extends Logger {
  def startingBitcoin(network: String): Unit =
    logger.info(s"starting bitcoin on $network network")
  def blockChainDownloadStarted(left: Long): Unit =
    logger.info(s"block chain download started: $left blocks left")
  def blockChainDownloadFinished(): Unit =
    logger.info("block chain download is finished")
  def watchingWallet(recipientWallet: RecipientWallet): Unit =
    logger.info(s"watching wallet: ${recipientWallet.publicKey}")
  def reloadingExistingWallets(): Unit =
    logger.info("starting reload of existing wallets")
}
