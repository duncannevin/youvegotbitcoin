package com.duncannevin.youvegotbitcoin.entities

final case class ActorFailed(msg: String) extends Exception(msg)
