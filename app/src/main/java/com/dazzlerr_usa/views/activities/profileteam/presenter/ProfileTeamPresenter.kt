package com.dazzlerr_usa.views.activities.profileteam.presenter

interface ProfileTeamPresenter
{

    fun getTeamMembers(user_id: String ,page : String , isShowProgressbar:Boolean)
    fun cancelRetrofitRequest()
}