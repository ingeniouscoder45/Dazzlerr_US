package com.dazzlerr_usa.views.activities.jobsdetails

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class JobDetailsModel(val mContext: Context, val mView: JobDetailsView) : JobDetailsPresenter
{

    var documentService :Call<JobDetailsPojo> ? = null
     var proposalsService :Call<GetProposalsPojo> ? = null
     var shortlistService :Call<GetProposalsPojo> ? = null
     var hireOrRejectService :Call<HireOrRejectPojo> ? = null
     var shortlistJobService :Call<ShortlistJobPojo> ? = null
     var viewApplicationService :Call<ViewApplicationPojo> ? = null
     var contactService :Call<GetJobContactPojo> ? = null


    override fun cancelRetrofitRequest()
    {

        if(documentService!=null)
        documentService?.cancel()

        if(proposalsService!=null)
            proposalsService?.cancel()

        if(shortlistService!=null)
            shortlistService?.cancel()

        if(hireOrRejectService!=null)
            hireOrRejectService?.cancel()

         if(contactService!=null)
             contactService?.cancel()

        if(shortlistJobService!=null)
        shortlistJobService?.cancel()

        if(viewApplicationService!=null)
        viewApplicationService?.cancel()

    }

    override fun getjobDetails(call_id: String , user_id:String ,membership_id : String)
    {

        mView.showProgress(true)
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        documentService = webServices.getJobDetails("application/x-www-form-urlencoded" ,Constants.auth_key ,  call_id  ,user_id ,membership_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(documentService ,Constants.RETRY_COUNT ,object : Callback<JobDetailsPojo>
        {
            override fun onResponse(call: Call<JobDetailsPojo>, response: Response<JobDetailsPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("JobDetails response: "+ responseStr)

                    if(response.body()!!.status)
                        mView.onSuccess(response.body() as JobDetailsPojo)

                    else
                        mView.onFailed(response.body()!!.status.toString())

                }

                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<JobDetailsPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                if(!call.isCanceled)
                {
                    mView.showProgress(false )
                    mView.onFailed("Something went wrong! Please try again later")
                }
            }
        })

    }

    override fun getjobProposals(call_id: String, user_id: String ,type : String)
    {

        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        proposalsService = webServices.getJobProposals("application/x-www-form-urlencoded" ,Constants.auth_key , call_id  ,user_id,type
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND, DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE,  DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(proposalsService ,Constants.RETRY_COUNT ,object : Callback<GetProposalsPojo>
        {
            override fun onResponse(call: Call<GetProposalsPojo>, response: Response<GetProposalsPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetProposals response: "+ responseStr)

                    if(response.body()!!.success)
                        mView.onGetProposals(response.body() as GetProposalsPojo)

                    else
                        mView.onFailed(response.body()!!.success.toString())

                }

                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<GetProposalsPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                if(!call.isCanceled)
                {
                    mView.showProgress(false )
                    mView.onFailed("Something went wrong! Please try again later")
                }
            }
        })

    }

    override fun isMessageValid(message: String)
    {
        if(message.length==0)
        {
            mView.onFailed("Please enter your message.")
        }
        else
        {
            mView.isMesssageValid(message)
        }
    }
    override fun sendMessage(call_id: String, user_id: String, message: String, email: String, name: String, title: String, user_name: String, phone: String) {


    mView.showProgress(true )
    val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
    val service = webServices.sendMessage("application/x-www-form-urlencoded" ,Constants.auth_key ,  call_id  ,user_id ,message ,name , email ,title, user_name ,phone
            , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
            , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
            , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

    APIHelper.enqueueWithRetry(service ,Constants.RETRY_COUNT ,object : Callback<AddMessagePojo>
    {
        override fun onResponse(call: Call<AddMessagePojo>, response: Response<AddMessagePojo>)
        {
            try
            {
                mView.showProgress(false )
                val gson = GsonBuilder().create()
                val responseStr = gson.toJson(response.body())

                Timber.e("SendMessage response: "+ responseStr)

                if(response.body()?.success!!)
                    mView.onSendMessageSuccess(message)

                else
                    mView.onFailed(response.body()!!.status.toString())

            }
            catch (e: Exception)
            {
                e.printStackTrace()
                mView.onFailed("Something went wrong! Please try again later")
            }

        }

        override fun onFailure(call: Call<AddMessagePojo>, t: Throwable)
        {
            Timber.e("Something went wrong "+ t.message)

            if(!call.isCanceled) {
                mView.showProgress(false )
                mView.onFailed("Something went wrong! Please try again later")
            }

        }
    })
}

    override fun shortListPurposal(creply_id: String, status: String , position : Int)
    {
        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        val service = webServices.castingShortlistApi("application/x-www-form-urlencoded" ,Constants.auth_key , creply_id , status
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(service ,Constants.RETRY_COUNT ,object : Callback<ShortlistPojo>
        {
            override fun onResponse(call: Call<ShortlistPojo>, response: Response<ShortlistPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("Shortlist response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onPurposalShortlisted(status ,position)

                    else
                        mView.onFailed(response.body()!!.message.toString())

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<ShortlistPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled) {
                    mView.showProgress(false )
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }


        })
    }

    override fun hireOrReject(user_id :String ,creply_id: String, request_sent: String, request_message: String , position : Int)
    {
        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        hireOrRejectService = webServices.hireOrRejectApi("application/x-www-form-urlencoded" ,Constants.auth_key ,request_sent ,request_message , creply_id ,user_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(hireOrRejectService ,Constants.RETRY_COUNT ,object : Callback<HireOrRejectPojo>
        {
            override fun onResponse(call: Call<HireOrRejectPojo>, response: Response<HireOrRejectPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("HiredOrReject response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onHiredOrRejected(request_sent ,position)

                    else
                        mView.onFailed(response.body()!!.message.toString())

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<HireOrRejectPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled) {
                    mView.showProgress(false )
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }


        })
    }


    override fun getJobContact(call_id: String, user_id: String) {

        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        contactService = webServices.getJobContact("application/x-www-form-urlencoded"  ,Constants.auth_key, call_id  ,user_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(contactService ,Constants.RETRY_COUNT ,object : Callback<GetJobContactPojo>
        {
            override fun onResponse(call: Call<GetJobContactPojo>, response: Response<GetJobContactPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetJobContact response: "+ responseStr)

                    if(response.body()!!.success)
                        mView.onGetContact()

                    else
                        mView.onFailed(response.body()!!.status.toString())

                }

                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<GetJobContactPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                if(!call.isCanceled)
                {
                    mView.showProgress(false )
                    mView.onFailed("Something went wrong! Please try again later")
                }
            }
        })

    }

    override fun shortList_job(user_id : String , job_id: String, status: String) {

        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        shortlistJobService = webServices.shortlistJobApi("application/x-www-form-urlencoded" ,Constants.auth_key ,user_id ,  job_id , status
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(shortlistJobService ,Constants.RETRY_COUNT ,object : Callback<ShortlistJobPojo>
        {
            override fun onResponse(call: Call<ShortlistJobPojo>, response: Response<ShortlistJobPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("Shortlist Job response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onJobShortlisted(status)

                    else
                        mView.onFailed(response.body()!!.success.toString())

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<ShortlistJobPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled) {
                    mView.showProgress(false )
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }


        })
    }


    override fun viewApplication(call_id: String, user_id: String, profile_id: String) {

        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        viewApplicationService = webServices.viewApplication("application/x-www-form-urlencoded" ,Constants.auth_key ,user_id ,  call_id , profile_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(viewApplicationService ,Constants.RETRY_COUNT ,object : Callback<ViewApplicationPojo>
        {
            override fun onResponse(call: Call<ViewApplicationPojo>, response: Response<ViewApplicationPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("View Application response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onApplicationViewed()

                    else
                        mView.onFailed(response.body()!!.success.toString())

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<ViewApplicationPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled) {
                    mView.showProgress(false )
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }


        })
    }

}