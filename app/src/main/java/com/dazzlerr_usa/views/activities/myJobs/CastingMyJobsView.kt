package com.dazzlerr_usa.views.activities.myJobs

import com.dazzlerr_usa.views.fragments.castingprofile.pojo.CastingMyJobPojo

interface CastingMyJobsView
{
    fun onSuccess(model: CastingMyJobPojo)
    fun onFailed(message: String)
    fun onStatusFailed(message: String)

    fun showProgress(visiblity:Boolean)
    fun onUpdateJobStatus(status: String , position:Int)//0 for pause 1 for Active and 2  for completed
}