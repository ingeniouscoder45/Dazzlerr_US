package com.dazzlerr_usa.views.activities.synchcalling

interface AddCallLogsView
{
    fun onAddCallLogs()
    fun onFailed(message: String)
    fun showProgress(showProgress: Boolean)
}
