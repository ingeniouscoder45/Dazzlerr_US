package com.dazzlerr_usa.views.fragments.jobs

interface JobsView
{
    fun onJobsSuccess(model: JobsPojo)
    fun onAppliedJobsSuccess(model: AppliedJobsPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean ,isShowProgressbar:Boolean)
    fun onJobShortlisted(status: String , position : Int)//0 for pause 1 for Active and 2  for completed
}