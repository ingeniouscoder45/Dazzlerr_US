package com.dazzlerr_usa.views.fragments.talentdashboard

interface TalentDashboardPresenter
{

    fun getTalentDashboard(user_id: String)
    fun getTalentActivitySummary(user_id: String , type: String, sholdShowProgressBar : Boolean)
    fun cancelRetrofitRequest()
}