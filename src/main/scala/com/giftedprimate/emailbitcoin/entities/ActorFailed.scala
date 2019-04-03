package com.giftedprimate.emailbitcoin.entities

final case class ActorFailed(msg: String) extends Exception(msg)
