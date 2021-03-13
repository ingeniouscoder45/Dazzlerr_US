package com.dazzlerr_usa.views.fragments.modelaccountverification

interface AccountVerificationView
{

   fun onValidOtp()
   fun onOtpValidateSuccess()
   fun showProgress(showProgress: Boolean)
   fun onFailed(message: String)
}