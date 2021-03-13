package com.dazzlerr_usa.views.activities.featuredccavenue

interface CCAvenuePresenter
{

    fun checkout(user_id: String, billing: String, shipping: String, line_item: String, payment_method: String, transaction_id: String, order_total: String, wp_user_id: String)

    fun validate(billingName:String , billingAddress:String
                 , billingCountry:String, billingState:String, billingCity:String, billingZip:String, billingTel:String
                 , billingEmail:String)

    fun cancelRetrofitRequest()
    fun getTokenApi()
    fun getCountries()
    fun getStates(country_id :String)
    fun getCities(state_id :String)//1 for city and 2 for current city
}