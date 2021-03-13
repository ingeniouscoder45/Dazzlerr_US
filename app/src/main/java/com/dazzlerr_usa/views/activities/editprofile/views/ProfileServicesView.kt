package com.dazzlerr_usa.views.activities.editprofile.views

import com.dazzlerr_usa.views.activities.editprofile.pojos.ProfileServicesPojo

interface ProfileServicesView
{
    fun onSuccess(model: ProfileServicesPojo)
    fun onUpdateServiceSuccess()
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}