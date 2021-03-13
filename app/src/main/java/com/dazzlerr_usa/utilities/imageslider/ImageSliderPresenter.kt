package com.dazzlerr_usa.utilities.imageslider

import android.widget.ImageView

interface ImageSliderPresenter
{

    fun like(user_id:String ,profile_id:String , status:String, portfolio_id:String , status_type: String ,position:Int ,likeBtn : ImageView)// status_type = is_like or is_heart
    fun heart(user_id:String ,profile_id:String , status:String, portfolio_id:String , status_type: String ,position:Int ,heartBtn : ImageView)// status_type = is_like or is_heart
    fun cancelRetrofitRequest()
}