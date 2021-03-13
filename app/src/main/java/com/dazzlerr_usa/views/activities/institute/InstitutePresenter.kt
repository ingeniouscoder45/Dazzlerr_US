package com.dazzlerr_usa.views.activities.institute

interface InstitutePresenter
{

    fun getInstitutez(city : String , category_id : String  , page:String , isShowProgressbar:Boolean)
    fun cancelRetrofitRequest()
}