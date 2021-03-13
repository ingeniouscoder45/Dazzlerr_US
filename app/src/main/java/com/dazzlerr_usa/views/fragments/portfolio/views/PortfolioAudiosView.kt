package com.dazzlerr_usa.views.fragments.portfolio.views

import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioAudiosPojo
import com.dazzlerr_usa.views.fragments.portfolio.pojos.UploadAudioPojo

interface PortfolioAudiosView
{
    fun onSuccess(model: PortfolioAudiosPojo)
    fun onAudioUpload(model: UploadAudioPojo)
    fun onAudioDelete(position:Int)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
    fun showProgress(visiblity: Int)
}