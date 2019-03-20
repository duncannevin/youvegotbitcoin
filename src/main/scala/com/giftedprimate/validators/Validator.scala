package com.giftedprimate.validators
import com.giftedprimate.messages.ApiError

trait Validator[T] {
  def validate(t: T): Option[ApiError]
}
