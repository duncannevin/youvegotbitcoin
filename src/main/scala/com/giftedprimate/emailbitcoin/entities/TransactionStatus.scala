package com.giftedprimate.emailbitcoin.entities

object TransactionStatus {}
case class TransactionStatus(
    sessionId: String,
    status: String,
    blocks: Seq[EBBlock]
)
