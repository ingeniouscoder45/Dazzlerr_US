package com.dazzlerr_usa.views.activities.membership

interface MembershipSuccessView
{
    fun onSuccess(transaction_id : String)
    fun onFreeMembershipSuccess()
    fun onFreeMembershipFailed(message: String)
    fun onMembershipFailed(message: String)
    fun showProgress(visiblity:Boolean)
}