package com.giftedprimate.emailbitcoin.entities

case class EBBlock(
    txid: String,
    blockhash: String,
    confirmations: Int,
    amount: Double,
    fee: Double
)
