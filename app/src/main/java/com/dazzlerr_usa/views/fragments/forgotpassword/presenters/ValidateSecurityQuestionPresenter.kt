package com.dazzlerr_usa.views.fragments.forgotpassword.presenters

import android.content.Context

interface ValidateSecurityQuestionPresenter
{
    fun isSecurityQuestionValidate(context: Context, answer: String)
    fun validateOtp(context: Context, question_id: String ,email:String, answer: String)
    fun cancelRetrofitRequest()
}