package com.giftedprimate.emailbitcoin.entities
import org.bitcoinj.core.{Coin, Transaction}
import org.bitcoinj.wallet.Wallet

case class IncomingTransaction(wallet: Wallet,
                               tx: Transaction,
                               prevBalance: Coin,
                               newBalance: Coin)
