package com.onlinepoundstore.fragments.login

interface LoginPresenter
{

    fun login(email:String, password:String, device_id : String  , device_brand :String , device_model : String , device_type: String  , operating_system: String )
    fun validate(password:String, username :String )
}