package com.dazzlerr_usa.views.activities.editprofile.views

interface UpdateProfileImageView
{

    fun onProfilePicUploaded(url: String)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}