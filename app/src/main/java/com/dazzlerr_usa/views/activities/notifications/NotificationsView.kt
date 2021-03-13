package com.dazzlerr_usa.views.activities.notifications

interface NotificationsView
{
    fun onSuccess(model: NotificationsPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean ,isShowProgressbar:Boolean)
}