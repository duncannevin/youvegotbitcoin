package com.giftedprimate.emailbitcoin.entities

object TransactionStatus {}
case class TransactionStatus(sessionId: String,
                             transactions: Seq[EBTransaction],
                             blocks: Seq[EBBlock])
