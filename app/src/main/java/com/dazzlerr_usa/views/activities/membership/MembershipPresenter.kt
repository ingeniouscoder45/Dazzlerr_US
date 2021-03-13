package com.dazzlerr_usa.views.activities.membership

interface MembershipPresenter
{

    fun getMembershipDetails(user_id : String)
    fun cancelRetrofitRequest()
    fun getTokenApi()
}