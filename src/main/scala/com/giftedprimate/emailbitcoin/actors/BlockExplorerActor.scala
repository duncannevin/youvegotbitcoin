package com.giftedprimate.emailbitcoin.actors

import akka.actor.{Actor, Props}
import com.giftedprimate.emailbitcoin.configuration.BlockExplorerConfig
import javax.inject.Inject

object BlockExplorerActor {
  def props(blockExplorerConfig: BlockExplorerConfig): Props = Props(
    new BlockExplorerActor(blockExplorerConfig)
  )
}

class BlockExplorerActor @Inject()(
    blockExplorerConfig: BlockExplorerConfig
) extends Actor {
  private val location = blockExplorerConfig.location
  override def receive: Receive = ???
}
