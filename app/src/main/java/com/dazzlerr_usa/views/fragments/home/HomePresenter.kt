package com.dazzlerr_usa.views.fragments.home

interface HomePresenter
{

    fun getHomeModels(user_id : String ,type : String, city: String , page:String)
    fun getCategoriesProducts()
    fun cancelRetrofitRequest()
}