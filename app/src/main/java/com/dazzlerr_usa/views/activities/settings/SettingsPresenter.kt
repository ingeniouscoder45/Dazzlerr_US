package com.dazzlerr_usa.views.activities.settings

interface SettingsPresenter
{
    fun getProfileSettings(user_id:String , user_role:String)
    fun publisOrUnpublishProfile(user_id:String , user_role:String ,status : String)
    fun updateUserName(user_id:String , user_name:String)
    fun setSecurityQuestion(user_id:String , question_id:String , answer:String , password:String ,question:String)
    fun verifyPassword(user_id:String  , password:String)
    fun updateSecondaryRole(user_id:String  , secondary_role_id:String)
    fun cancelRetrofitRequest()
}