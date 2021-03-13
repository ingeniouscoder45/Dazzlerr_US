package com.dazzlerr_usa.views.activities.recommendedevents

interface RecommendedEventsPresenter
{

    fun getRecommendedEvents(user_id:String,page : String , shouldShowProgressBar: Boolean)
    fun cancelRetrofitRequest()
}