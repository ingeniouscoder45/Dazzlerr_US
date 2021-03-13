package com.dazzlerr_usa.views.activities.profileteam.presenter

import java.io.File

interface AddOrUpdateTeamPresenter
{
    fun validateTeamMember(name:String, role:String , isImageUploaded : Boolean)
    fun addTeamMember(user_id: String , name:String, role:String , image : File)
    fun updateTeamMember(user_id: String ,team_member_id: String, name:String, role:String , image : File)
    fun cancelRetrofitRequest()
}