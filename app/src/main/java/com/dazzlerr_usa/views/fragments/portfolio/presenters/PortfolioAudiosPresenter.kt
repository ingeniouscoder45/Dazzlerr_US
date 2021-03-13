package com.dazzlerr_usa.views.fragments.portfolio.presenters

import java.io.File

interface PortfolioAudiosPresenter
{

    fun getPortfolioAudios(user_id: String, page:String)
    fun uploadAudio(user_id: String, audioFile:File , audio_title:String)
    fun deleteAudio(user_id: String, audio_id: String, position:Int)
    fun cancelRetrofitRequest()
}