package com.giftedprimate.models
import org.bson.codecs.configuration.CodecRegistries.{
  fromProviders,
  fromRegistries
}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.{DEFAULT_CODEC_REGISTRY, Macros}

object Transaction {
  def codecRegistry: CodecRegistry = {
    fromRegistries(
      fromProviders(
        Macros.createCodecProvider[Transaction](),
      ),
      DEFAULT_CODEC_REGISTRY
    )
  }
}

case class Transaction(
    publicKey: String,
    transactionId: String,
    senderEmail: String,
    recipientEmail: String
)
