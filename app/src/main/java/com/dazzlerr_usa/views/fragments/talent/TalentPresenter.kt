package com.dazzlerr_usa.views.fragments.talent

interface TalentPresenter
{

    fun getTalentModels(city: String , page:String, model_name: String , gender:String , category_id : String , experience_type:String , isShowProgressbar:Boolean, user_id:String , age_range: String , height_range: String)
    fun cancelRetrofitRequest()
}