package com.dazzlerr_usa.views.activities.interestedtalents

interface InterestedTalentsPresenter
{

    fun getInterestedTalents( user_id:String , page:String ,  isShowProgressbar:Boolean)
    fun cancelRetrofitRequest()
}