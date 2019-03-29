package com.giftedprimate.emailbitcoin.entities
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
        Macros.createCodecProvider[CreationForm]()
      ),
      DEFAULT_CODEC_REGISTRY
    )
  }
}

case class EmailBtcTransaction(
    createdAt: String,
    publicKey: String,
    transactionId: String,
    creationForm: CreationForm,
    value: Long
)
