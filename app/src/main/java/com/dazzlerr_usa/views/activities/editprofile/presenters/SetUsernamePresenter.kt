package com.dazzlerr_usa.views.activities.editprofile.presenters

interface SetUsernamePresenter
{
    fun checkUsernameAvailability(user_id: String , username : String)
    fun setUsername(user_id: String , username : String)
    fun cancelRetrofitRequest()
}