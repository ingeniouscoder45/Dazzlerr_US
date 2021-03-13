package com.dazzlerr_usa.views.fragments.modelaccountverification

import android.content.Context

interface AccountVerificationPresenter
{
    fun isOtpValidate(context: Context, otp: String)
    fun ForgotPasswordByEmail(context: Context, email: String, otp :String)
    fun ForgotPasswordByPhone(context: Context, phone: String, otp :String ,type: String)
    fun cancelRetrofitRequest()
}