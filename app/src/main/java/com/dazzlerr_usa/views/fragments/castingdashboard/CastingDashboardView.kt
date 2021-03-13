package com.dazzlerr_usa.views.fragments.castingdashboard

interface CastingDashboardView
{
    fun onGetDashboardData(model: CastingDashboardPojo)
    fun onFailed(message: String)
    fun showProgress(visibility:Boolean)
}