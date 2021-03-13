package com.dazzlerr_usa.views.activities.profileteam.views

interface AddOrUpdateTeamView
{
    fun onTeamAddOrUpdate()
    fun onValidateTeamMember()
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}