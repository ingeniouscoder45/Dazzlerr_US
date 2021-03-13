package com.dazzlerr_usa.views.activities.home

interface HomeView
{

    fun onClearDeviceid()
    fun onFailed(message : String)
    fun showProgress(visibility:Boolean)
}