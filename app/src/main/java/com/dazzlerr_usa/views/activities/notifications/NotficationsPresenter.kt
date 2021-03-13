package com.dazzlerr_usa.views.activities.notifications

interface NotficationsPresenter
{

    fun getNotifications(user_id:String,page : String , shouldShowProgressBar: Boolean)
    fun cancelRetrofitRequest()
}