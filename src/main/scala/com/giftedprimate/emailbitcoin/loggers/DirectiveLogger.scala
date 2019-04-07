package com.giftedprimate.emailbitcoin.loggers

import com.giftedprimate.emailbitcoin.entities.Session

class DirectiveLogger extends Logger {
  def sessionHasNoWallet(session: Session): Unit =
    logger.warn(
      s"sessionId ${session.sessionId} associated wallet does not exist. publicKey: ${session.publicKey}")
  def unknownReason: Unit =
    logger.warn("hit default case")
}
