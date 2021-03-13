package com.onlinepoundstore.fragments.login

import com.dazzlerr_usa.views.activities.jobfilter.JobFilterPojo

interface JobFilterView
{
    fun onFailed(message: String)
    fun showProgress(showProgress: Boolean)
    fun onGetJobCities(dataModel: JobFilterPojo)//1 for city and 2 for current city
}
