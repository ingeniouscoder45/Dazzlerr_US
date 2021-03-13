package com.dazzlerr_usa.views.activities.events.presenters

interface EventsPresenter
{

    fun getEvents(page:String , month:String , year : String, mSearchStr :String ,categoryId:String , city:String , venue : String , organizer: String , isShowProgressbar: Boolean)
    fun cancelRetrofitRequest()
}