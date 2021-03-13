package com.dazzlerr_usa.views.activities.events.views

import com.dazzlerr_usa.views.activities.events.pojos.EventsCategoryPojo

interface PromoteEventView
{
    fun onPromoteEventSuccess()
    fun onEventValidate()
    fun onGetEventCategory(model: EventsCategoryPojo)
    fun onFailed(message: String)
    fun showProgress(visibility: Boolean)
}
