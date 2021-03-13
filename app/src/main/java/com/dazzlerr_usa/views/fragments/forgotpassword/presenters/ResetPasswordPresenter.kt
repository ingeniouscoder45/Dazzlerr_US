package com.dazzlerr_usa.views.fragments.forgotpassword.presenters

import android.content.Context

interface ResetPasswordPresenter
{
    fun restPasswordValidate(context: Context, newPassword: String, confirmPassword: String)
    fun resetPassword(context: Context, user_id: String, password: String)
    fun cancelRetrofitRequest()
}