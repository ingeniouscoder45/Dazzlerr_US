package com.dazzlerr_usa.views.activities.talentlikes

interface TalentLikesPresenter
{

    fun getlikedTalents(user_id: String , page:String, isShowProgressbar:Boolean)
    fun cancelRetrofitRequest()
}