package com.dazzlerr_usa.views.fragments.portfolio.views

import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioVideosPojo

interface PortfolioVideosView
{
    fun onSuccess(model: PortfolioVideosPojo)
    fun onVideoDelete(position:Int)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}