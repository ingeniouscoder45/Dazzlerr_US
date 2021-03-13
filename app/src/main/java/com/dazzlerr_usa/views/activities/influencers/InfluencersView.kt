package com.dazzlerr_usa.views.activities.influencers

interface InfluencersView
{
    fun onSuccess(model: InfluencersPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean ,isShowProgressbar:Boolean)
}