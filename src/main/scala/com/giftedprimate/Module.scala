package com.giftedprimate
import akka.actor.{ActorRef, ActorSystem}
import com.giftedprimate.configuration._
import com.giftedprimate.daos.RecipientWalletDAO
import com.giftedprimate.loggers.{ServerLog, TransactionLog}
import com.giftedprimate.notification.NotificationActor
import com.giftedprimate.router.TransactionRouter
import com.giftedprimate.transaction.{
  BitcoinClient,
  TransactionActor,
  TransactionControl
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

    bind(classOf[TransactionRouter]).asEagerSingleton()
    bind(classOf[ServerLog]).asEagerSingleton()
    bind(classOf[TransactionLog]).asEagerSingleton()
    bind(classOf[RecipientWalletDAO]).asEagerSingleton()
    bind(classOf[BitcoinClient]).asEagerSingleton()
    bind(classOf[TransactionControl]).asEagerSingleton()
  }

  @Provides
  @Singleton
  @Named("transaction-actor")
  def getTransactionActor(actorSystem: ActorSystem,
                          transactionControl: TransactionControl,
                          transactionLog: TransactionLog,
                          recipientWalletDAO: RecipientWalletDAO): ActorRef =
    actorSystem.actorOf(
      TransactionActor.props(
        transactionControl,
        transactionLog,
        recipientWalletDAO
      ))

  @Provides
  @Singleton
  @Named("notification-actor")
  def getNotificationActor(actorSystem: ActorSystem): ActorRef =
    actorSystem.actorOf(NotificationActor.props)

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
