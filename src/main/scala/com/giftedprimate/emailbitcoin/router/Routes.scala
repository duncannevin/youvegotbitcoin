package com.giftedprimate.emailbitcoin.router
import com.google.inject.Inject

class Routes @Inject()(
    transactionRouter: TransactionRouter
) {
  def routes: Seq[PartialRoute] = Seq(transactionRouter)
}
