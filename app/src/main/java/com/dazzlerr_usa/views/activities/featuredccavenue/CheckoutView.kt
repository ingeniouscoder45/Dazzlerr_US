package com.dazzlerr_usa.views.activities.featuredccavenue

interface CheckoutView
{
    fun onSuccess()
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}