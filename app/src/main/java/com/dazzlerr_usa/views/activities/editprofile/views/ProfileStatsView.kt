package com.dazzlerr_usa.views.activities.editprofile.views

import com.dazzlerr_usa.views.activities.editprofile.pojos.ProfileStatsPojo

interface ProfileStatsView
{
    fun onSuccess(model: ProfileStatsPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}
