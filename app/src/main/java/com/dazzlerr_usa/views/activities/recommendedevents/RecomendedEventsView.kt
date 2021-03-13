package com.dazzlerr_usa.views.activities.recommendedevents

import com.dazzlerr_usa.views.activities.events.pojos.EventsListPojo

interface RecomendedEventsView
{
    fun onSuccess(model: EventsListPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean ,isShowProgressbar:Boolean)
}