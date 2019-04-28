package com.giftedprimate.emailbitcoin.entities

case class RawBlock(
    txid: String,
    blockhash: String,
    confirmations: Int,
    amount: Double,
    fee: Double,
    timereceived: Long
)
