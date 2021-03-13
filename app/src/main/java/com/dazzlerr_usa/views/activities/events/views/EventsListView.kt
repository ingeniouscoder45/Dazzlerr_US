package com.dazzlerr_usa.views.activities.events.views

import com.dazzlerr_usa.views.activities.events.pojos.EventsListPojo


interface EventsListView
{
    fun onSuccess(model: EventsListPojo, type: Int)// Type = 1 for featured, 2 for Popular and 3 for latest events
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}
