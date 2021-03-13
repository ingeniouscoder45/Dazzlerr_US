package com.dazzlerr_usa.views.activities.contactus

interface ContactUsPresenter
{

    fun contact(name:String ,email:String,phone:String,subject:String,message:String)
    fun isValid(name:String ,email:String,phone:String,subject:String,message:String)
    fun cancelRetrofitRequest()
}