package com.dazzlerr_usa.views.fragments.jobs

interface JobsPresenter
{

    fun getJobs(user_id : String,city: String , page:String, u_name: String , gender:String , category_id : String , experience_type:String ,type:String , isShowProgressbar:Boolean)
    fun getAppliedJobs(user_id : String , page : String ,isShowProgressbar:Boolean)
    fun shortList_job(user_id : String , job_id:String , status : String , position : Int)
    fun cancelRetrofitRequest()
}