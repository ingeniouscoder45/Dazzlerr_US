package com.dazzlerr_usa.views.activities.featuredccavenue

interface CheckoutPresenter
{

    fun checkout(user_id: String, billing: String, shipping: String, line_item: String, payment_method: String, transaction_id: String, order_total: String, wp_user_id: String)
    fun cancelRetrofitRequest()
}