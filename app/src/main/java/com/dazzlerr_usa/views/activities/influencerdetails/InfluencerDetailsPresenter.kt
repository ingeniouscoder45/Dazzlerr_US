package com.dazzlerr_usa.views.activities.influencerdetails

interface InfluencerDetailsPresenter
{

    fun getInfluencerDetails(user_id:String ,profile_id:String)
    fun likeOrDislike(user_id:String ,profile_id:String , status:String)
    fun cancelRetrofitRequest()
}