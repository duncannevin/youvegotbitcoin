package com.giftedprimate.emailbitcoin.validators

import com.giftedprimate.emailbitcoin.entities.ApiError

trait Validator[T] {
  def validate(t: T): Option[ApiError]
}
