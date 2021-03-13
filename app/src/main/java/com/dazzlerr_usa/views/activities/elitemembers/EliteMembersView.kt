package com.dazzlerr_usa.views.activities.elitemembers

interface EliteMembersView
{
    fun onSuccess(model: EliteMembersPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean ,isShowProgressbar:Boolean)
}