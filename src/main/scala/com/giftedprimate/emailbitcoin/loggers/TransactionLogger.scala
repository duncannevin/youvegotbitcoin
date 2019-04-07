package com.giftedprimate.emailbitcoin.loggers

class TransactionLogger extends Logger {
  def existingTransaction(transactionId: String): Unit =
    logger.warn(s"seen transaction: $transactionId")
  def noWallet(publicKey: String): Unit =
    logger.warn(s"no wallet found with public key: $publicKey")
  def transactionPaid(txId: String): Unit =
    logger.info(s"transaction funded: $txId")
}
