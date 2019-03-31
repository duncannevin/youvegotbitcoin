package com.giftedprimate.emailbitcoin.router
import com.google.inject.Inject

class Routes @Inject()(
    homeRouter: HomeRouter,
    transactionRouter: TransactionRouter
) {
  def routes: Seq[PartialRoute] = Seq(homeRouter, transactionRouter)
}
