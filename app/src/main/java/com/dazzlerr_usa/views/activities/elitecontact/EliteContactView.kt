package com.dazzlerr_usa.views.activities.elitecontact

interface EliteContactView
{
    fun onSuccess()
    fun onValidate()
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}