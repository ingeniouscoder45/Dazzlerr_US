package com.dazzlerr_usa.views.activities.editprofile.presenters

import java.io.File

interface UpdateProfileImagePresenter
{
    fun uploadProfilePic(user_id: String, imageFile: File)
    fun cancelRetrofitRequest()
}