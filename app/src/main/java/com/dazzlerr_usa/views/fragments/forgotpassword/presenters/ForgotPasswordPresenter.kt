package com.dazzlerr_usa.views.fragments.forgotpassword.presenters

import android.content.Context

interface ForgotPasswordPresenter
{
    fun verifyAccountValidate(context: Context, emailOrPhone: String, type: String)
    fun ForgotPasswordByPhoneOrEmail(context: Context, email: String, Phone :String , email_type: String)
    fun ForgotPasswordByQuestion(context: Context, email: String)
    fun cancelRetrofitRequest()
}