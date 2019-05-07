package com.duncannevin.youvegotbitcoin.validators

import com.duncannevin.youvegotbitcoin.entities.ApiError

trait Validator[T] {
  def validate(t: T): Option[ApiError]
}
