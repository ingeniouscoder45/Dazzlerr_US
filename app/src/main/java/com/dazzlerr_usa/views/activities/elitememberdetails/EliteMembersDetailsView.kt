package com.dazzlerr_usa.views.activities.elitememberdetails

interface EliteMembersDetailsView
{
    fun onSuccess(model: EliteMemberDetailsPojo)
    fun onLikeOrDislike(status:String)//---1 for like and 0 for dislike
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}