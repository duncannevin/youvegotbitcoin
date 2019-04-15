package com.giftedprimate.emailbitcoin.entities

import io.circe.Encoder
import io.circe.generic.semiauto._

object TransactionStatus {
//  implicit val encoder: Encoder[TransactionStatus] =
//    deriveEncoder[TransactionStatus]
}
case class TransactionStatus(sessionId: String,
                             transactions: Seq[EBTransaction],
                             blocks: Seq[EBBlock])
