package com.dazzlerr_usa.views.fragments.forgotpassword.presenters

import android.content.Context

interface ValidateOtpPresenter
{
    fun isOtpValidate(context: Context, otp: String)
    fun validateOtpByEmail(context: Context, email: String , otp: String)
    fun validateOtpByPhone(context: Context, phone:String, otp: String)
    fun validateForgotPasswordOtp(context: Context, emailOrPhone:String, otp: String)
    fun cancelRetrofitRequest()
}