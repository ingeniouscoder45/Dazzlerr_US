package com.dazzlerr_usa.views.activities.eventcontact

interface EventContactPresenter
{

    fun contact(name:String ,phone:String,email:String,msg:String,event_id:String,profile_name:String,username:String)
    fun isValid(name:String ,phone:String,email:String,msg:String,type:String,profile_name:String)
    fun cancelRetrofitRequest()
}