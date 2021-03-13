package com.dazzlerr_usa.views.activities.setprimarydevice

interface SetPrimaryDeviceView
{
    fun onSuccess()
    fun onValidOtp()
    fun onFailed(message: String)
    fun showProgress(visibility:Boolean)
}