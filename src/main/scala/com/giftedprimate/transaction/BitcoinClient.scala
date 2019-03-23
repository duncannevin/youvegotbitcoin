package com.giftedprimate.transaction
import java.nio.file.Paths
import java.util

import akka.actor.ActorRef
import com.giftedprimate.configuration.{BitcoinConfig, SystemConfig}
import com.giftedprimate.loggers.{BitcoinLogger, TransactionLog}
import com.giftedprimate.models.{CreationForm, RecipientWallet}
import com.google.inject.Inject
import com.google.inject.name.Named
import org.bitcoinj.core._
import org.bitcoinj.core.listeners.PeerDataEventListener
import org.bitcoinj.net.discovery.DnsDiscovery
import org.bitcoinj.params.{MainNetParams, RegTestParams, TestNet3Params}
import org.bitcoinj.script.Script.ScriptType
import org.bitcoinj.store.{BlockStore, MemoryBlockStore}
import org.bitcoinj.utils.BriefLogFormatter
import org.bitcoinj.wallet.Wallet
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener

import scala.concurrent.Future

class BitcoinClient @Inject()(
    logger: BitcoinLogger,
    config: BitcoinConfig,
    systemConfig: SystemConfig,
    @Named("transaction-actor") transactionActor: ActorRef,
) {
  object tags {
    final val forwardingService = "forwarding-service"
    final val regtest = "regtest"
    final val testnet = "testnet"
  }

  // make logs more compact
  BriefLogFormatter.init()

  // figure out which network is to be used
  val (params, filePrefix): (NetworkParameters, String) = config.network match {
    case "testnet" =>
      (TestNet3Params.get(), s"${tags.forwardingService}-${tags.testnet}")
    case "regtest" =>
      (RegTestParams.get(), s"${tags.forwardingService}-${tags.regtest}")
    case "mainnet" => (MainNetParams.get(), s"${tags.forwardingService}")
    case _ =>
      throw new RuntimeException(s"un recognized network: ${config.network}")
  }

  val peerGroupContext = new Context(params)

  val tablePath: String =
    Paths.get(".").resolve(filePrefix).normalize().toAbsolutePath.toString

  val blockStore: BlockStore = config.network match {
    case "regtest" => new MemoryBlockStore(params)
    case "testnet" => new MemoryBlockStore(params)
  }

  val blockChain = new BlockChain(peerGroupContext, blockStore)

  var peerGroup: PeerGroup = new PeerGroup(peerGroupContext, blockChain)

  val walletListener: WalletCoinsReceivedEventListener =
    (wallet: Wallet, tx: Transaction, prevBalance: Coin, newBalance: Coin) => {
      transactionActor ! IncomingTransaction(wallet,
                                             tx,
                                             prevBalance,
                                             newBalance)
    }

  val blockChainDownloadListener: PeerDataEventListener =
    new PeerDataEventListener {
      override def getData(peer: Peer, m: GetDataMessage): util.List[Message] =
        null

      override def onChainDownloadStarted(peer: Peer, blocksLeft: Int): Unit =
        logger.blockChainDownloadStarted(blocksLeft)

      override def onPreMessageReceived(peer: Peer, msg: Message): Message = msg

      override def onBlocksDownloaded(
          peer: Peer,
          block: Block,
          filteredBlock: FilteredBlock,
          blocksLeft: Int
      ): Unit = {
        if (blocksLeft == 0) logger.blockChainDownloadFinished()
      }
    }

  def addWallet(creationForm: CreationForm): Future[RecipientWallet] = {
    val wallet: Wallet =
      Wallet.createDeterministic(params, ScriptType.P2PKH)
    val recipientWallet = RecipientWallet(wallet, creationForm)
    wallet.setDescription(
      s"for: ${creationForm.recipientEmail} from ${creationForm.senderEmail}")
    wallet.addCoinsReceivedEventListener(walletListener)
    wallet.setAcceptRiskyTransactions(true)
    peerGroup.addWallet(wallet)
    logger.watchingWallet(recipientWallet)
    Future.successful(recipientWallet)
  }

  private def init(): Unit = {
    Context.propagate(peerGroupContext)
    peerGroup.setUserAgent("email bitcoin", "0.1")
    peerGroup.setStallThreshold(10000, 1)
    peerGroup.setMaxConnections(30)
    config.network match {
      case "regtest" =>
        // to use this I must add a peer group here
        peerGroup.start()
      case _ =>
        peerGroup.addPeerDiscovery(new DnsDiscovery(params))
        peerGroup.start()
    }
    logger.startingBitcoin(config.network)
  }

  init()
}
