package com.giftedprimate.transaction
import java.nio.file.Paths
import java.util

import akka.actor.ActorRef
import com.giftedprimate.configuration.{ConfigModule, SystemConfig}
import com.giftedprimate.loggers.TransactionLog
import org.bitcoinj.core.{NetworkParameters, _}
import org.bitcoinj.core.listeners.PeerDataEventListener
import org.bitcoinj.net.discovery.DnsDiscovery
import org.bitcoinj.params.{MainNetParams, RegTestParams, TestNet3Params}
import org.bitcoinj.store.{BlockStore, MemoryBlockStore}
import org.bitcoinj.utils.BriefLogFormatter
import org.bitcoinj.wallet.Wallet
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener

import scala.concurrent.ExecutionContext

class TransactionControl(
    config: ConfigModule,
    notificationActor: ActorRef,
    transactionLog: TransactionLog
)(
    implicit ec: ExecutionContext
) extends SystemConfig {
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
      println(wallet)
      // todo -> react to payment
    }

  val blockChainDownloadListener: PeerDataEventListener =
    new PeerDataEventListener {
      override def getData(peer: Peer, m: GetDataMessage): util.List[Message] =
        null

      override def onChainDownloadStarted(peer: Peer, blocksLeft: Int): Unit =
        transactionLog.blockChainDownloadStarted(blocksLeft)

      override def onPreMessageReceived(peer: Peer, msg: Message): Message = msg

      override def onBlocksDownloaded(
          peer: Peer,
          block: Block,
          filteredBlock: FilteredBlock,
          blocksLeft: Int
      ): Unit = {
        if (blocksLeft == 0) transactionLog.blockChainDownloadFinished()
      }
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
    transactionLog.startingBitcoin(config.network)
  }

  init()
}
