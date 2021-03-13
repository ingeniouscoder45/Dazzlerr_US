package com.dazzlerr_usa.views.activities.profileteam.views

import com.dazzlerr_usa.views.activities.profileteam.pojos.GetTeamPojo

interface ProfileTeamView
{
    fun onGetTeamSuccess(model: GetTeamPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean ,isShowProgressbar:Boolean)
}