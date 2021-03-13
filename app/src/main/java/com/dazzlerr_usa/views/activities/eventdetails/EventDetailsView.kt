package com.dazzlerr_usa.views.activities.eventdetails

interface EventDetailsView
{
    fun onSuccess(model: EventDetailsPojo)
    fun onLikeOrDislike(status:String)//---1 for like and 0 for dislike
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}