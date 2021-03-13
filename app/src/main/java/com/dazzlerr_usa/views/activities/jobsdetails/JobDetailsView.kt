package com.dazzlerr_usa.views.activities.jobsdetails

interface JobDetailsView
{
    fun onSuccess(model: JobDetailsPojo)
    fun onGetProposals(model : GetProposalsPojo)
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)
    fun onSendMessageSuccess(message:String)
    fun isMesssageValid(message:String)
    fun onApplicationViewed()

    fun onPurposalShortlisted(status: String ,position : Int)
    fun onJobShortlisted(status: String)
    fun onHiredOrRejected(request_sent: String , position:Int)
    fun onGetContact()
}