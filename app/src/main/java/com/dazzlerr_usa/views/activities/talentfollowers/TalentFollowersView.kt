package com.dazzlerr_usa.views.activities.talentfollowers

interface TalentFollowersView
{
    fun onGetFollowersSuccess(model: TalentFollowersPojo)
    fun onFollowOrUnfollow(status:String , position:Int)//---1 for like and 0 for dislike
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean ,isShowProgressbar:Boolean)
}