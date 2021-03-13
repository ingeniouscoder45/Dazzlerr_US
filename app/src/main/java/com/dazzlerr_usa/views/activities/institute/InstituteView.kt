package com.dazzlerr_usa.views.activities.institute

interface InstituteView
{
    fun onSuccess(model: InstitutePojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean ,isShowProgressbar:Boolean)
}