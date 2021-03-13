package com.dazzlerr_usa.views.activities.talentlikes

import com.dazzlerr_usa.views.fragments.home.pojos.ModelsPojo

interface TalentLikesView
{
    fun onGetLikesSuccess(model: ModelsPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean ,isShowProgressbar:Boolean)
}