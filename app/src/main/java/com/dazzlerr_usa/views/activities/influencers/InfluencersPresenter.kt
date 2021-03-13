package com.dazzlerr_usa.views.activities.influencers

interface InfluencersPresenter
{

    fun getInfluencers(page:String , isShowProgressbar:Boolean)
    fun cancelRetrofitRequest()
}