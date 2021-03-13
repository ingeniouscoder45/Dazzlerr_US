package com.dazzlerr_usa.views.activities.recommendedjobs

interface RecommendedJobsPresenter
{

    fun getRecommendedJobs(user_id:String,page : String , shouldShowProgressBar: Boolean)
    fun cancelRetrofitRequest()
}