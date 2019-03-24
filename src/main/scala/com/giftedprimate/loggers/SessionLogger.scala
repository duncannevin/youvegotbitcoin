package com.giftedprimate.loggers
import com.giftedprimate.entities.EmailBtcTransaction

class SessionLogger extends Logger {
  def transactionFundedAgain(transaction: EmailBtcTransaction): Unit =
    logger.info(s"${transaction.publicKey} sent more btc: ${transaction.value}")
}
