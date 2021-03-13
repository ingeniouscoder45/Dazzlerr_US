package com.dazzlerr_usa.views.activities.membership

interface MembershipSuccessPresenter
{

    fun checkout(user_id: String, membership_id: String, transaction_id: String, discount_total: String, total: String, referred_by: String, payment_method: String, status: String,membership_type:String , discount_type : String ,address : String ,country:String ,  city: String , state :String , zipcode :String)
    fun buyFreeMembership(user_id:String)
    fun cancelRetrofitRequest()
}