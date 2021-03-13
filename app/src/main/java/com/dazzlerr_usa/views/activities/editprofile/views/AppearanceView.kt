package com.dazzlerr_usa.views.activities.editprofile.views

interface AppearanceView
{
    fun onSuccess()
    fun onFailed(message: String)
    fun showProgress(showProgress: Boolean)
}
