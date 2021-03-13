package com.dazzlerr_usa.views.fragments.forgotpassword.views

interface ResetPasswordView
{
   fun onResetPasswordSuccess()
   fun onValidate()
   fun showProgress(showProgress: Boolean)
   fun onFailed(message: String)
}