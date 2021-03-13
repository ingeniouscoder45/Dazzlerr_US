package com.dazzlerr_usa.views.activities.influencerdetails

interface InfluencerDetailsView
{
    fun onSuccess(model: InfluencerDetailsPojo)
    fun onLikeOrDislike(status:String)//---1 for like and 0 for dislike
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}