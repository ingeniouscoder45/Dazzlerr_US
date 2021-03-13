package com.dazzlerr_usa.views.activities.portfolio.addproject

interface ProjectView
{
    fun onSuccess()
    fun onFailed(message: String)
    fun showProgress(showProgress: Boolean)
    fun isValidate()
}
