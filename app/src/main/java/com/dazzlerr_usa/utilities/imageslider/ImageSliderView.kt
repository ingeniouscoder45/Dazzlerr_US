package com.dazzlerr_usa.utilities.imageslider

import android.widget.ImageView

interface ImageSliderView
{

    fun onLikeOrDislike(status:String ,position:Int ,likeBtn : ImageView)//---1 for like and 0 for dislike
    fun onHeartOrDisheart(status:String ,position: Int ,heartBtn : ImageView)//---1 for follow and 0 for unfollow
    fun onFailed(message: String)
}