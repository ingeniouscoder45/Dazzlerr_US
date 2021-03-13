package com.dazzlerr_usa.views.activities.synchcalling

interface AddCallLogsPresenter
{
    fun addCallLog(user_id :String , caller_name:String ,caller_id:String ,call_duration:String , call_type : String ,date_time :String,is_caller:String)
    fun cancelRetrofitRequest()
}