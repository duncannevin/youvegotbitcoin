package com.giftedprimate
import akka.actor.{ActorRef, ActorSystem}
import com.giftedprimate.actors.{
  NewWalletActor,
  NotificationActor,
  TransactionActor
}
import com.giftedprimate.bitcoin.BitcoinClient
import com.giftedprimate.configuration._
import com.giftedprimate.daos.{RecipientWalletDAO, EmailBtcTransactionDAO}
import com.giftedprimate.loggers.{
  BitcoinLogger,
  NotificationLogger,
  ServerLog,
  TransactionLog
}
import com.giftedprimate.router.{Routes, TransactionRouter}
import com.google.inject.{AbstractModule, Inject, Provides}
import com.sandinh.akuice.AkkaGuiceSupport
import javax.inject.{Named, Singleton}
import org.mongodb.scala.{MongoClient, MongoDatabase}

import scala.concurrent.ExecutionContext

class Module @Inject()(implicit val ec: ExecutionContext)
    extends AbstractModule
    with AkkaGuiceSupport {
  override def configure(): Unit = {
    bind(classOf[ActorSystem]).toInstance(ActorSystem("emailbitcoin"))

    bind(classOf[TransactionRouter])
    bind(classOf[ServerLog])
    bind(classOf[TransactionLog])
    bind(classOf[NotificationLogger])
    bind(classOf[RecipientWalletDAO])
    bind(classOf[EmailBtcTransactionDAO])
    bind(classOf[BitcoinClient])
    bind(classOf[EmailBitcoin])
    bind(classOf[BitcoinLogger])
    bind(classOf[Routes])
  }

  @Provides
  @Singleton
  @Named("transaction-actor")
  def getTransactionActor(actorSystem: ActorSystem,
                          transactionLog: TransactionLog,
                          recipientWalletDAO: RecipientWalletDAO,
                          transactionDAO: EmailBtcTransactionDAO): ActorRef =
    actorSystem.actorOf(
      TransactionActor.props(
        transactionLog,
        recipientWalletDAO,
        transactionDAO
      ))

  @Provides
  @Singleton
  @Named("new-wallet-actor")
  def getNewWalletActor(
      actorSystem: ActorSystem,
      bitcoinClient: BitcoinClient,
      recipientWalletDAO: RecipientWalletDAO
  ): ActorRef =
    actorSystem.actorOf(NewWalletActor.props(bitcoinClient, recipientWalletDAO))

  @Provides
  @Singleton
  @Named("notification-actor")
  def getNotificationActor(actorSystem: ActorSystem,
                           logger: NotificationLogger,
                           recipientWalletDAO: RecipientWalletDAO,
                           transactionDAO: EmailBtcTransactionDAO): ActorRef =
    actorSystem.actorOf(
      NotificationActor.props(logger, transactionDAO, recipientWalletDAO))

  @Provides
  def mongoDb(configFactory: EmailBitcoinConfigFactory): MongoDatabase = {
    val mongoClient: MongoClient = MongoClient(
      configFactory.mongoConfig.location)
    mongoClient.getDatabase(configFactory.mongoConfig.name)
  }

  @Provides
  def serverConfig(configFactory: EmailBitcoinConfigFactory): ServerConfig =
    configFactory.serverConfig

  @Provides
  def bitcoinConfig(configFactory: EmailBitcoinConfigFactory): BitcoinConfig =
    configFactory.bitcoinConfig

  @Provides
  def mongoConfig(configFactory: EmailBitcoinConfigFactory): MongoConfig =
    configFactory.mongoConfig

  @Provides
  def systemConfig(configFactory: EmailBitcoinConfigFactory): SystemConfig =
    configFactory.systemConfig
}
