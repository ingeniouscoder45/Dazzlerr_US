package com.dazzlerr_usa.views.activities.talentfollowers

interface TalentFollowersPresenter
{

    fun getFollowers(user_id: String , page:String, isShowProgressbar:Boolean)
    fun followOrUnfollow(user_id: String, profile_id: String, status: String , position:Int)
    fun cancelRetrofitRequest()

}