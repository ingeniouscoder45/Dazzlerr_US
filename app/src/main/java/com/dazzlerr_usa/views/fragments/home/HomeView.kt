package com.dazzlerr_usa.views.fragments.home

import com.dazzlerr_usa.views.fragments.home.pojos.HomeCategoryPojo
import com.dazzlerr_usa.views.fragments.home.pojos.ModelsPojo

interface HomeView
{
    fun onModelsSuccess(model: ModelsPojo, type : String)
    fun onGetCategory(model: HomeCategoryPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean , page : String)
}