package com.dazzlerr_usa.views.activities.calllogs

interface CallLogsPresenter
{

    fun getCallLogs(user_id:String)
    fun cancelRetrofitRequest()
}