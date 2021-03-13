package com.dazzlerr_usa.views.activities.elitememberdetails

interface EliteMembersDetailsPresenter
{

    fun getEliteMemberDetails(user_id:String ,profile_id:String)
    fun likeOrDislike(user_id:String ,profile_id:String , status:String)
    fun cancelRetrofitRequest()
}