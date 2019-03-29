package com.giftedprimate.emailbitcoin.validators
import com.giftedprimate.emailbitcoin.messages.ApiError

trait Validator[T] {
  def validate(t: T): Option[ApiError]
}
