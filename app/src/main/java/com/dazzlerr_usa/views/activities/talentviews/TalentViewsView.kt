package com.dazzlerr_usa.views.activities.talentviews

import com.dazzlerr_usa.views.fragments.home.pojos.ModelsPojo

interface TalentViewsView
{
    fun onGetViewsSuccess(model: ModelsPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean ,isShowProgressbar:Boolean)
}