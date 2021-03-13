package com.dazzlerr_usa.views.activities.interestedtalents

interface InterestedTalentsView
{
    fun onGetInterestedTalentsSuccess(model: InterestedTalentsPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean ,isShowProgressbar:Boolean)
}