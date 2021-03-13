package com.dazzlerr_usa.views.fragments.portfolio.views

import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioProjectsPojo

interface PortfolioProjectsView
{
    fun onSuccess(model: PortfolioProjectsPojo)
    fun onProjectDelete(position:Int)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}