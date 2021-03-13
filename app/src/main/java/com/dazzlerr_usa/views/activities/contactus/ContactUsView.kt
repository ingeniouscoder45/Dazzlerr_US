package com.dazzlerr_usa.views.activities.contactus

interface ContactUsView
{
    fun onSuccess()
    fun onValidate()
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}