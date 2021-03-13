package com.dazzlerr_usa.views.activities.myJobs

interface CastingMyJobsPresenter
{

    fun getMyJobs(user_id: String , page:String , status : String)
    fun updateJobStatus(user_id: String , call_id:String , status : String , position:Int)
    fun cancelRetrofitRequest()
}