package com.dazzlerr_usa.views.activities.membership

interface MembershipView
{
    fun onSuccess(model : MembershipPojo)
    fun onGetToken(model : GetTokenPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}