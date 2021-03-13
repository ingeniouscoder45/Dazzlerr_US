package com.dazzlerr_usa.views.activities.emailverification

interface EmailVerificationPresenter
{
    fun isOtpValidate(otp: String)
    fun isEmailValidate(email: String)
    fun verifyEmailOtp(user_id : String ,  email: String, otp :String)
    fun sendEmailOtp(user_id : String ,  email: String)
    fun cancelRetrofitRequest()
}