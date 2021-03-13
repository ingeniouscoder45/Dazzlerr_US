package com.dazzlerr_usa.views.activities.institutedetails

interface InstituteDetailsView
{
    fun onSuccess(model: InstituteDetailsPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}