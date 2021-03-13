package com.onlinepoundstore.fragments.login

import com.dazzlerr_usa.views.fragments.login.LoginPojo

interface LoginView
{
    fun onLoginSuccess(dataModel: LoginPojo)
    fun onLoginFailed(message: String)
    fun showProgress(showProgress: Boolean)
    fun isLoginValidate(isValid: Boolean)
}
