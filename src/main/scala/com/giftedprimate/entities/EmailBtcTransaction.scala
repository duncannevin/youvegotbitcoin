package com.giftedprimate.entities
import org.bson.codecs.configuration.CodecRegistries.{
  fromProviders,
  fromRegistries
}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.{DEFAULT_CODEC_REGISTRY, Macros}

object EmailBtcTransaction {
  def codecRegistry: CodecRegistry = {
    fromRegistries(
      fromProviders(
        Macros.createCodecProvider[EmailBtcTransaction](),
      ),
      DEFAULT_CODEC_REGISTRY
    )
  }
}

case class EmailBtcTransaction(
    publicKey: String,
    transactionId: String,
    senderEmail: String,
    recipientEmail: String
)
