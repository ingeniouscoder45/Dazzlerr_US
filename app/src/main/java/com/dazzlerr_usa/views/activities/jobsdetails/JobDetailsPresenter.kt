package com.dazzlerr_usa.views.activities.jobsdetails

interface JobDetailsPresenter
{

    fun getjobDetails(call_id: String , user_id:String ,membership_id : String)
    fun getJobContact(call_id: String , user_id:String)
    fun getjobProposals(call_id: String , user_id:String , type: String)
    fun viewApplication(call_id: String , user_id:String , profile_id : String)
    fun sendMessage(call_id: String , user_id: String , message:String , email:String , name : String , title: String , user_name:String , phone : String)
    fun cancelRetrofitRequest()
    fun isMessageValid(message: String)
    fun shortListPurposal(creply_id:String, status : String , position : Int)
    fun shortList_job(user_id : String , job_id:String , status : String )
    fun hireOrReject(user_id : String, creply_id:String, request_sent: String, request_message:String ,position :Int)

}