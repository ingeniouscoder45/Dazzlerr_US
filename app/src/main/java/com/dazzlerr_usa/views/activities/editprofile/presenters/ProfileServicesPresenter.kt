package com.dazzlerr_usa.views.activities.editprofile.presenters

interface ProfileServicesPresenter
{

    fun getServices(user_role : String ,user_id: String )
    fun updateServices(user_id: String ,services :String, other_services:String)
    fun cancelRetrofitRequest()
}