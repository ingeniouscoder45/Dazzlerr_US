package com.dazzlerr_usa.views.activities.transactions

interface TransactionsPresenter
{

    fun getTransactions(user_id: String , page:String, isShowProgressbar:Boolean)
    fun cancelRetrofitRequest()
}