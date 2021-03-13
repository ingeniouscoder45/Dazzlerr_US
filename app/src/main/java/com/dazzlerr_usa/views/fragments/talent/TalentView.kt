package com.dazzlerr_usa.views.fragments.talent

import com.dazzlerr_usa.views.fragments.home.pojos.ModelsPojo

interface TalentView
{
    fun onTalentsSuccess(model: ModelsPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean ,isShowProgressbar:Boolean)
}