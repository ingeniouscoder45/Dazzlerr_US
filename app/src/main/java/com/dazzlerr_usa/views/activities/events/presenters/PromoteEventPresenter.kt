package com.dazzlerr_usa.views.activities.events.presenters

interface PromoteEventPresenter
{

    fun promoteEvent(event_cat_id: String, event_title:String, event_date:String, event_venue:String, organizer_name:String , organizer_phone : String , organizer_email:String)
    fun validateEvent(event_cat_id: String, event_title:String, event_date:String, event_venue:String, organizer_name:String , organizer_phone : String , organizer_email:String )
    fun cancelRetrofitRequest()
    fun getEventsCategory()
}
