package com.dazzlerr_usa.views.activities.emailverification

interface EmailVerificationView
{

   fun onValidOtp()
   fun onValidEmail(email : String)
   fun onOtpValidateSuccess()
   fun onOtpSentSuccess(email:String)
   fun showProgress(visibility: Boolean)
   fun onFailed(message: String)
}