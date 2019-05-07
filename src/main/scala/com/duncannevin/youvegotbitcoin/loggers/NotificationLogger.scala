package com.duncannevin.youvegotbitcoin.loggers

class NotificationLogger extends Logger {
  def notifySender(email: String, url: String): Unit =
    logger.info(s"notify sender: $email with url: $url")
  def notifyRecipient(email: String, url: String): Unit =
    logger.info(s"notify recipient: $email with url: $url")
}
