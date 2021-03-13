package com.dazzlerr_usa.views.activities.becomefeatured

interface BecomeFeaturedView
{
    fun onSuccess(model: BecomeFeaturedPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}