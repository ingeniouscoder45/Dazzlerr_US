package com.dazzlerr_usa.views.activities.elitemembers

interface EliteMembersPresenter
{

    fun getEliteMembers(page:String , isShowProgressbar:Boolean)
    fun cancelRetrofitRequest()
}