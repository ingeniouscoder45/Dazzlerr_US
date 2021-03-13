package com.dazzlerr_usa.views.activities.report

interface ReportPresenter
{

    fun reportUser(username:String ,name:String ,email:String,phone:String,subject:String,message:String)
    fun reportPortfolio(username:String ,name:String ,email:String,phone:String,subject:String,message:String ,portfolio_url:String)
    fun isValid(name:String ,email:String,phone:String,subject:String,message:String)
    fun cancelRetrofitRequest()
}
