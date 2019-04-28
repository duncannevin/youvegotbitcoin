package com.giftedprimate.emailbitcoin.entities

object EBBlock {
  def apply(transaction: EBTransaction): EBBlock = new EBBlock(
    txid = transaction.transactionId,
    blockhash = "",
    confirmations = 0,
    amount = transaction.value,
    fee = 0,
    time = transaction.createdAt
  )

  def apply(
      rawBlock: RawBlock
  ): EBBlock =
    new EBBlock(
      txid = rawBlock.txid,
      blockhash = rawBlock.blockhash,
      confirmations = rawBlock.confirmations,
      amount = convertBTCToSat(Math.abs(rawBlock.amount)),
      fee = convertBTCToSat(Math.abs(rawBlock.fee)),
      time = rawBlock.timereceived.toString
    )

  private def convertBTCToSat(btc: Double): Long =
    (btc * 100000000L).toLong
}

case class EBBlock(
    txid: String,
    blockhash: String,
    confirmations: Int,
    amount: Long,
    fee: Long,
    time: String
)
