package com.dazzlerr_usa.views.activities.settings

interface SettingsView
{
    fun onGetProfile(username: String , url: String ,answer :String , question_id:String ,questions: MutableList<GetProfileSettingsPojo.QuestionsBean>  , secondary_role_id:String , secondary_role:String)
    fun onPublisOrUnpublish(status:String)
    fun onUpdateUsername()
    fun onUpdateSecondaryRole()
    fun onSetSecurityQuestion(question: String , answer: String)
    fun onFailed(message: String)
    fun showProgress(visibility:Boolean)
    fun onVerifiedPassword(status:Boolean , message:String)
}