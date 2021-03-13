package com.dazzlerr_usa.views.fragments.forgotpassword.views

interface ValidateOtpView
{
   fun onOtpValidateSuccess(user_id:String)
   fun onOtpValidate()
   fun showProgress(showProgress: Boolean)
   fun onFailed(message: String)

}