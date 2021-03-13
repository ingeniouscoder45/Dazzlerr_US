package com.dazzlerr_usa.views.activities.changepassword

interface ChangePasswordView
{
    fun onChangePasswordSuccess()
    fun onChangePasswordFailed(message: String)
    fun showProgress(showProgress: Boolean)
    fun isValidate(isValid: Boolean)
}
