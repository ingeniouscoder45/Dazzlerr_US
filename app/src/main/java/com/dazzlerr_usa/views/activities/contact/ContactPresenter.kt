package com.dazzlerr_usa.views.activities.contact

interface ContactPresenter
{

    fun contact(membership_id : String , profile_id:String,user_id:String ,name:String ,email:String,phone:String,subject:String,message:String)
    fun isValid(name:String ,email:String,phone:String,subject:String,message:String)
    fun cancelRetrofitRequest()
}