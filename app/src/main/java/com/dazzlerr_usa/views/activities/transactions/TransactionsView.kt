package com.dazzlerr_usa.views.activities.transactions

interface TransactionsView
{
    fun onSuccess(model: TransactionsPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean ,isShowProgressbar:Boolean)
}

