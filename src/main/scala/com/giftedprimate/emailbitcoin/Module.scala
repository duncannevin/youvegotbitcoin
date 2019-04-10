package com.giftedprimate.emailbitcoin

import akka.actor.{ActorRef, ActorSystem}
import com.giftedprimate.emailbitcoin.actors.{
  NotificationActor,
  TransactionActor,
  WSSupervisorActor,
  WalletActor
}
import com.giftedprimate.emailbitcoin.bitcoin.BitcoinClient
import com.giftedprimate.emailbitcoin.configuration._
import com.giftedprimate.emailbitcoin.daos.{
  EBTransactionDAO,
  RecipientWalletDAO,
  SessionDAO
}
import com.giftedprimate.emailbitcoin.loggers._
import com.giftedprimate.emailbitcoin.router.{
  HomeRouter,
  Routes,
  TransactionRouter,
  WSRouter
}
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

    bind(classOf[HomeRouter]).asEagerSingleton()
    bind(classOf[TransactionRouter]).asEagerSingleton()
    bind(classOf[WSRouter]).asEagerSingleton()
    bind(classOf[ServerLogger]).asEagerSingleton()
    bind(classOf[TransactionLogger]).asEagerSingleton()
    bind(classOf[NotificationLogger]).asEagerSingleton()
    bind(classOf[RecipientWalletDAO]).asEagerSingleton()
    bind(classOf[EBTransactionDAO]).asEagerSingleton()
    bind(classOf[SessionDAO]).asEagerSingleton()
    bind(classOf[BitcoinClient]).asEagerSingleton()
    bind(classOf[EmailBitcoin]).asEagerSingleton()
    bind(classOf[BitcoinLogger]).asEagerSingleton()
    bind(classOf[Routes]).asEagerSingleton()
  }

  @Provides
  @Singleton
  @Named("transaction-actor")
  def getTransactionActor(
      actorSystem: ActorSystem,
      transactionLog: TransactionLogger,
      recipientWalletDAO: RecipientWalletDAO,
      sessionDAO: SessionDAO,
      transactionDAO: EBTransactionDAO,
      @Named("notification-actor") notificationActor: ActorRef): ActorRef =
    actorSystem.actorOf(
      TransactionActor.props(
        transactionLog,
        recipientWalletDAO,
        transactionDAO,
        sessionDAO,
        notificationActor
      ))

  @Provides
  @Singleton
  @Named("new-wallet-actor")
  def getNewWalletActor(
      actorSystem: ActorSystem,
      bitcoinClient: BitcoinClient,
      recipientWalletDAO: RecipientWalletDAO,
      sessionDAO: SessionDAO
  ): ActorRef =
    actorSystem.actorOf(
      WalletActor.props(bitcoinClient, recipientWalletDAO, sessionDAO))

  @Provides
  @Singleton
  @Named("notification-actor")
  def getNotificationActor(actorSystem: ActorSystem,
                           logger: NotificationLogger,
                           recipientWalletDAO: RecipientWalletDAO,
                           transactionDAO: EBTransactionDAO,
                           siteLocationConfig: SiteLocationConfig): ActorRef =
    actorSystem.actorOf(
      NotificationActor
        .props(logger, transactionDAO, recipientWalletDAO, siteLocationConfig))

  @Provides
  @Singleton
  @Named("ws-supervisor-actor")
  def getWSSupervisorActor(actorSystem: ActorSystem,
                           transactionDAO: EBTransactionDAO): ActorRef =
    actorSystem.actorOf(
      WSSupervisorActor.props(actorSystem, transactionDAO)
    )

  @Provides
  def mongoDb(configFactory: EBConfigFactory): MongoDatabase = {
    val mongoClient: MongoClient = MongoClient(
      configFactory.mongoConfig.location)
    mongoClient.getDatabase(configFactory.mongoConfig.name)
  }

  @Provides
  def serverConfig(configFactory: EBConfigFactory): ServerConfig =
    configFactory.serverConfig

  @Provides
  def bitcoinConfig(configFactory: EBConfigFactory): BitcoinConfig =
    configFactory.bitcoinConfig

  @Provides
  def mongoConfig(configFactory: EBConfigFactory): MongoConfig =
    configFactory.mongoConfig

  @Provides
  def systemConfig(configFactory: EBConfigFactory): SystemConfig =
    configFactory.systemConfig

  @Provides
  def siteLocationConfig(configFactory: EBConfigFactory): SiteLocationConfig =
    configFactory.siteLocationConfig
}
