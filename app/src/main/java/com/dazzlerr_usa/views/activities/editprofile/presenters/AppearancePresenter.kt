package com.dazzlerr_usa.views.activities.editprofile.presenters

interface AppearancePresenter
{
    fun updateAppearance(user_id: String
                             , height: String
                             , weight:String
                             , biceps:String
                             , chest:String
                             , waist:String
                             , hips:String
                             , hair_color:String
                             , hair_length:String
                             , hair_type:String
                             , eye_color:String
                             , skintone:String
                             , dress:String
                             , shoes:String
                             , tags:String
    )

        fun cancelRetrofitRequest()
}