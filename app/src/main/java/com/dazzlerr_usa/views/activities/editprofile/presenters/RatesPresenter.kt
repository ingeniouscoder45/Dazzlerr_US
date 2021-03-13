package com.dazzlerr_usa.views.activities.editprofile.presenters

interface RatesPresenter
{
    fun updateRatesAndTravel(user_id: String , full_day:String, half_day:String , hourly:String , test:String, will_fly:String
                             , passport_ready:String
                             , drive_miles:String
                             , payment_options:String
                             , availability:String
                             , facebook:String
                             , twitter:String
                             , insta:String
    )

    fun cancelRetrofitRequest()
}