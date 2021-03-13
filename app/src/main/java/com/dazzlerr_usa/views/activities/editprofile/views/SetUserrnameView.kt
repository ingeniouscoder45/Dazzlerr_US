package com.dazzlerr_usa.views.activities.editprofile.views

interface SetUserrnameView
{
    fun onUsernameAvailable(username : String)
    fun onSetUsername(username: String)
    fun onUsernameFailed(message: String)
    fun showUsernameDialogProgress(showProgress: Boolean)
}
