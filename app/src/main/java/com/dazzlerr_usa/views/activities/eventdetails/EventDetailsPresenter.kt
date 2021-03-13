package com.dazzlerr_usa.views.activities.eventdetails

interface EventDetailsPresenter
{

    fun getEventDetails(user_id:String ,event_id:String)
    fun likeOrDislike(user_id:String ,event_id:String , status:String)
    fun cancelRetrofitRequest()
}