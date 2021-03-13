package com.dazzlerr_usa.views.activities.castingfollowings

import com.dazzlerr_usa.views.fragments.home.pojos.ModelsPojo

interface CastingFollowingsView
{
    fun onGetFollowingsSuccess(model: ModelsPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean ,isShowProgressbar:Boolean)
}