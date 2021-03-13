package com.dazzlerr_usa.views.activities.events.views

import com.dazzlerr_usa.views.activities.events.pojos.EventsPojo

interface EventsView
{
    fun onSuccess(model: EventsPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean ,isShowProgressbar:Boolean)
}