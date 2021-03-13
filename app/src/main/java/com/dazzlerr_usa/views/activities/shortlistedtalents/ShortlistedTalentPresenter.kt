package com.dazzlerr_usa.views.activities.shortlistedtalents

interface ShortlistedTalentPresenter
{

    fun getShortlistedTalents(user_id: String , page:String, isShowProgressbar:Boolean)
    fun cancelRetrofitRequest()
}