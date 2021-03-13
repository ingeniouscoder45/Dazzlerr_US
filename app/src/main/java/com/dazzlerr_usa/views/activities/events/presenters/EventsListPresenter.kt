package com.dazzlerr_usa.views.activities.events.presenters

interface EventsListPresenter
{
    fun getEventslist(type: Int ,page:String)//1 for featured, 2 for Popular and 3 for latest events
    fun cancelRetrofitRequest()
}