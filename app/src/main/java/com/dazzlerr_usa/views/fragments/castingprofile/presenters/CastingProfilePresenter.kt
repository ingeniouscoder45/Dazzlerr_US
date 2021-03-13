package com.dazzlerr_usa.views.fragments.castingprofile.presenters

import java.io.File

interface CastingProfilePresenter
{
    fun getProfile(profile_id:String , user_id: String)
    fun uploadProfilePic(user_id: String, imageFile: File)
    fun likeOrDislike(user_id:String ,profile_id:String , status:String)
    fun followOrUnfollow(user_id:String ,profile_id:String , status:String)
    fun getMyJobs(user_id: String , page:String , status : String)
    fun getPreviousProjects(user_id: String , page:String)
    fun deleteProject(user_id: String ,project_id: String, position:Int)
    fun cancelRetrofitRequest()
}