package com.giftedprimate.router
import com.google.inject.Inject

class Routes @Inject()(
    transactionRouter: TransactionRouter,
    clientRouter: ClientRouter
) {
  def routes: Seq[PartialRoute] = Seq(transactionRouter, clientRouter)
}
