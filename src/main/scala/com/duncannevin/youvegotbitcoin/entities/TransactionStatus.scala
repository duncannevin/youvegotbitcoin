package com.duncannevin.youvegotbitcoin.entities

object TransactionStatus {}
case class TransactionStatus(
    sessionId: String,
    status: String,
    blocks: Seq[EBBlock]
)
