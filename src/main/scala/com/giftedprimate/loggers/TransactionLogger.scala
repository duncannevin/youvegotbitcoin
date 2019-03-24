package com.giftedprimate.loggers

class TransactionLogger extends Logger {
  def existingTransaction(transactionId: String): Unit =
    logger.warn(s"transaction already stored transaction: $transactionId")
  def noWallet(publicKey: String): Unit =
    logger.warn(s"no wallet found with public key: $publicKey")
}
