package com.dazzlerr_usa.views.activities.eventcontact

interface EventContactView
{
    fun onSuccess()
    fun onValidate()
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}