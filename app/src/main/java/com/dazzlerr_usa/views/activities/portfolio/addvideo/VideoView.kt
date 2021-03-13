package com.dazzlerr_usa.views.activities.portfolio.addvideo

interface VideoView
{
    fun onSuccess()
    fun onFailed(message: String)
    fun showProgress(showProgress: Boolean)
    fun isValidate()
}
