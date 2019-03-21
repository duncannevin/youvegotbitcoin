package com.giftedprimate.configuration

class BitcoinConfig extends ConfigModule {
  lazy val network: String = emailbitcoin.bitcoin.network
}
