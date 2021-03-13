package com.dazzlerr_usa.views.activities.changepassword

interface ChangePasswordPresenter
{
    fun changePassword(old_password :String , user_id:String ,password:String)
    fun validate(old_password :String ,newPassword:String , confirmPassword:String)
    fun cancelRetrofitRequest()
}