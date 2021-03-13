package com.dazzlerr_usa.views.activities.events.views

import com.dazzlerr_usa.views.activities.events.pojos.EventFilterPojo

interface EventFilterView
{
    fun onSuccess(model: EventFilterPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}