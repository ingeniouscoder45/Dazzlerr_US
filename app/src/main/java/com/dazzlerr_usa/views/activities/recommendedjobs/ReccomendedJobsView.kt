package com.dazzlerr_usa.views.activities.recommendedjobs

import com.dazzlerr_usa.views.fragments.jobs.JobsPojo

interface ReccomendedJobsView
{
    fun onSuccess(model: JobsPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean ,isShowProgressbar:Boolean)
}