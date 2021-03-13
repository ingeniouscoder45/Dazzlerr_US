package com.dazzlerr_usa.views.activities.mainactivity

interface MainActivityView
{
    fun onSuccess(model: MonetizeApiPojo)
    fun onFailed(message: String)
}