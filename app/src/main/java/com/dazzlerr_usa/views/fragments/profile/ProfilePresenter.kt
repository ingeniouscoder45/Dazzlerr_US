package com.dazzlerr_usa.views.fragments.profile

import java.io.File

interface ProfilePresenter
{
    fun getProfile(profile_id: String , user_id: String)
    fun uploadProfilePic(user_id: String, imageFile: File)
    fun getContactDetails(user_id:String ,profile_id:String)
    fun likeOrDislike(user_id:String ,profile_id:String , status:String)
    fun followOrUnfollow(user_id:String ,profile_id:String , status:String)
    fun shortList(user_id:String ,profile_id:String , status:String)
    fun cancelRetrofitRequest()
}