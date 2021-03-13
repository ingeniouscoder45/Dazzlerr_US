package com.dazzlerr_usa.views.activities.portfolio.addvideo

interface VideoPresenter
{

    fun addVideo(user_id: String ,video_title: String , video_description:String, video_url:String)
    fun validate(video_title: String , video_description:String, video_url:String)
    fun cancelRetrofitRequest()
}