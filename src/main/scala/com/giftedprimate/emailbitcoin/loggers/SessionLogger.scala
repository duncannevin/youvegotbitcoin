package com.giftedprimate.emailbitcoin.loggers
import com.giftedprimate.emailbitcoin.entities.EBTransaction

class SessionLogger extends Logger {
  def transactionFundedAgain(transaction: EBTransaction): Unit =
    logger.info(s"${transaction.publicKey} sent more btc: ${transaction.value}")
}
