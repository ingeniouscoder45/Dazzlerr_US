package com.dazzlerr_usa.views.activities.contact

interface ContactView
{
    fun onSuccess()
    fun onValidate()
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}