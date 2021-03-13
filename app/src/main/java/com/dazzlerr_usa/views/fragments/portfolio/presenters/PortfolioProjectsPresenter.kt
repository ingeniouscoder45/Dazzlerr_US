package com.dazzlerr_usa.views.fragments.portfolio.presenters

interface PortfolioProjectsPresenter
{

    fun getPortfolioProjects(user_id: String , page:String)
    fun deleteProject(user_id: String ,project_id: String, position:Int)
    fun cancelRetrofitRequest()
}