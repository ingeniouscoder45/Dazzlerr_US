package com.dazzlerr_usa.views.fragments.portfolio.presenters

interface PortfolioVideosPresenter
{
    fun getPortfolioVideos(user_id: String , page:String)
    fun deleteVideo(user_id: String ,video_id: String, position:Int)
    fun cancelRetrofitRequest()
}