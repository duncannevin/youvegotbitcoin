package com.giftedprimate.loggers
import com.giftedprimate.entities.RecipientWallet

class BitcoinLogger extends Logger {
  def startingBitcoin(network: String): Unit =
    logger.info(s"starting bitcoin on $network network")
  def blockChainDownloadStarted(left: Long): Unit =
    logger.info(s"block chain download started: $left blocks left")
  def blockChainDownloadFinished(): Unit =
    logger.info("block chain download is finished")
  def watchingWallet(recipientWallet: RecipientWallet): Unit =
    logger.info(s"watching wallet: ${recipientWallet.publicKey}")
}
