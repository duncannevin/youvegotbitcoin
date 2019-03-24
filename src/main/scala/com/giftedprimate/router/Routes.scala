package com.giftedprimate.router
import com.google.inject.Inject

class Routes @Inject()(
    transactionRouter: TransactionRouter
) {
  def routes: Seq[PartialRoute] = Seq(transactionRouter)
}
