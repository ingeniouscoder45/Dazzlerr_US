package com.dazzlerr_usa.views.activities.mymembership

interface MyMembershipView
{
    fun onSuccess(model : MyMembershipPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}