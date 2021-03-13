package com.dazzlerr_usa.views.activities.talentviews

interface TalentViewsPresenter
{

    fun getViewedTalents(user_id: String , page:String, isShowProgressbar:Boolean)
    fun cancelRetrofitRequest()
}