package com.duncannevin.youvegotbitcoin.loggers

import com.duncannevin.youvegotbitcoin.entities.EBTransaction

class SessionLogger extends Logger {
  def transactionFundedAgain(transaction: EBTransaction): Unit =
    logger.info(s"${transaction.publicKey} sent more btc: ${transaction.value}")
}
