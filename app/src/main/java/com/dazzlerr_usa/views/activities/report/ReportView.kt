package com.dazzlerr_usa.views.activities.report

interface ReportView
{
    fun onSuccess()
    fun onValidate()
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
}