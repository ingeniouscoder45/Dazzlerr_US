package com.dazzlerr_usa.views.activities.calllogs

interface CallLogsView
{
    fun onSuccess(model: CallLogsPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}