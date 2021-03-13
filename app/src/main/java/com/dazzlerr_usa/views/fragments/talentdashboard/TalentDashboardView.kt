package com.dazzlerr_usa.views.fragments.talentdashboard

interface TalentDashboardView
{
    fun onGetDashboardData(model: TalentDashboardPojo)
    fun onGetTalentActivitySummary(model: ActivitySummaryPojo , type: String)
    fun onFailed(message: String)
    fun showProgress(visibility:Boolean)
    fun showProgress(visibility:Boolean , sholdShowProgressBar : Boolean)
}