package com.duncannevin.youvegotbitcoin.entities

case class RawBlock(
    txid: String,
    blockhash: String,
    confirmations: Int,
    amount: Double,
    fee: Double,
    timereceived: Long
)
