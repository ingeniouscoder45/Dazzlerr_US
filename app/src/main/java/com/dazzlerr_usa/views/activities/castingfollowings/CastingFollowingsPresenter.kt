package com.dazzlerr_usa.views.activities.castingfollowings

interface CastingFollowingsPresenter
{

    fun getfollowings(user_id: String , page:String, isShowProgressbar:Boolean)
    fun cancelRetrofitRequest()
}