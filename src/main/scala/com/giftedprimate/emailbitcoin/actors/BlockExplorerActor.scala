package com.giftedprimate.emailbitcoin.actors

import akka.actor.{Actor, Props}
import com.giftedprimate.emailbitcoin.configuration.BlockExplorerConfig
import com.giftedprimate.emailbitcoin.entities.EBTransaction
import javax.inject.Inject

object BlockExplorerActor {
  final case class GetMultipleTransactions(txs: Seq[EBTransaction])

  def props(blockExplorerConfig: BlockExplorerConfig): Props = Props(
    new BlockExplorerActor(blockExplorerConfig)
  )
}

class BlockExplorerActor @Inject()(
    blockExplorerConfig: BlockExplorerConfig
) extends Actor {
  import BlockExplorerActor._
  private val location = blockExplorerConfig.location
  override def receive: Receive = {
    case GetMultipleTransactions(txs) => ???
  }
}
