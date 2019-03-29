package com.giftedprimate.emailbitcoin.validators
import com.giftedprimate.emailbitcoin.messages.ApiError
import com.giftedprimate.emailbitcoin.entities.CreationForm
object CreateWalletValidator extends Validator[CreationForm] {
  private def isValid(email: String): Boolean =
    if ("""^[-a-z0-9!#$%&'*+/=?^_`{|}~]+(\.[-a-z0-9!#$%&'*+/=?^_`{|}~]+)*@([a-z0-9]([-a-z0-9]{0,61}[a-z0-9])?\.)*(aero|arpa|asia|biz|cat|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|net|org|pro|tel|travel|[a-z][a-z])$""".r
          .findFirstIn(email)
          .isEmpty) false
    else true
  override def validate(creationForm: CreationForm): Option[ApiError] = {
    (isValid(creationForm.senderEmail), isValid(creationForm.recipientEmail)) match {
      case (senderEmail, recipientEmail) if senderEmail && recipientEmail =>
        None
      case (senderEmail, recipientEmail) if !senderEmail && recipientEmail =>
        Some(ApiError.invalidEmail(creationForm.senderEmail))
      case (senderEmail, recipientEmail) if senderEmail && !recipientEmail =>
        Some(ApiError.invalidEmail(creationForm.recipientEmail))
      case (senderEmail, recipientEmail) if !senderEmail && !recipientEmail =>
        Some(
          ApiError.invalidEmail(
            s"${creationForm.senderEmail} ${creationForm.recipientEmail}"))
    }
  }
}
