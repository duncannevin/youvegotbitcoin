package com.duncannevin.youvegotbitcoin.loggers

import com.duncannevin.youvegotbitcoin.entities.Session

class DirectiveLogger extends Logger {
  def sessionHasNoWallet(session: Session): Unit =
    logger.warn(
      s"sessionId ${session.sessionId} associated wallet does not exist. publicKey: ${session.publicKey}")
  def unknownReason: Unit =
    logger.warn("hit default case")
}
