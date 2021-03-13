package com.dazzlerr_usa.views.fragments.forgotpassword.views

import com.dazzlerr_usa.views.fragments.forgotpassword.pojos.ForgotPasswordByQuestionPojo

interface ForgotPasswordView
{
   fun onOtpSendSuccess()
   fun onValidate()
   fun onGetQuestionSuccess(model: ForgotPasswordByQuestionPojo)
   fun showProgress(showProgress: Boolean)
   fun onFailed(message: String)
}