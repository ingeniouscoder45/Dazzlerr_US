package com.dazzlerr_usa.views.fragments.forgotpassword.views

interface ValidateSecurityQuestionView
{
   fun onValidateSecurityQuestion(user_id:String)
   fun onValidate()
   fun showProgress(showProgress: Boolean)
   fun onFailed(message: String)
}