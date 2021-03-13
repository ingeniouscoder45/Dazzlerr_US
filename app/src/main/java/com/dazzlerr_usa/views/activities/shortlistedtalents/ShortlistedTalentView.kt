package com.dazzlerr_usa.views.activities.shortlistedtalents

import com.dazzlerr_usa.views.fragments.home.pojos.ModelsPojo

interface ShortlistedTalentView
{
    fun onTalentsSuccess(model: ModelsPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean ,isShowProgressbar:Boolean)
}