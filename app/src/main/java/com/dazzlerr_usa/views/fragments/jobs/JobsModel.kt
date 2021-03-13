package com.dazzlerr_usa.views.fragments.jobs

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import com.dazzlerr_usa.views.activities.jobsdetails.ShortlistJobPojo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class JobsModel(val mContext: Context, val mView:JobsView) : JobsPresenter
{

     var modelsService :Call<JobsPojo>?=null
     var appliedJobsService :Call<AppliedJobsPojo>?=null
     var shortlistJobService :Call<ShortlistJobPojo>?=null

    override fun cancelRetrofitRequest()
    {
       if(modelsService!=null)
           modelsService?.cancel()

        if(appliedJobsService!=null)
            appliedJobsService?.cancel()

        if(shortlistJobService!=null)
            shortlistJobService?.cancel()
    }

    override fun getJobs(user_id : String , city: String , page:String, u_name: String , gender:String , category_id : String , experience_type:String,type:String,isShowProgressbar:Boolean)
    {

        mView.showProgress(true ,isShowProgressbar )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        modelsService = webServices.getJobsApi("application/x-www-form-urlencoded" ,Constants.auth_key , user_id,  city  ,page ,u_name , gender ,category_id , experience_type ,type.toLowerCase()
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(modelsService ,Constants.RETRY_COUNT ,object : Callback<JobsPojo>
        {
            override fun onResponse(call: Call<JobsPojo>, response: Response<JobsPojo>)
            {
                try
                {
                    mView.showProgress(false ,isShowProgressbar )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("Jobs response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onJobsSuccess(response.body() as JobsPojo)

                    else
                        mView.onFailed(response.body()?.message!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<JobsPojo>, t: Throwable)
            {

                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false ,isShowProgressbar )

                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

                else
                {
                    (mContext as HomeActivity).stopProgressBarAnim()
                }
            }

        })

    }

    override fun getAppliedJobs(user_id: String, page: String ,isShowProgressbar :Boolean) {


        mView.showProgress(true ,isShowProgressbar )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        appliedJobsService = webServices.getAppliedJobsApi("application/x-www-form-urlencoded" ,Constants.auth_key ,  user_id , page
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(appliedJobsService ,Constants.RETRY_COUNT ,object : Callback<AppliedJobsPojo>
        {
            override fun onResponse(call: Call<AppliedJobsPojo>, response: Response<AppliedJobsPojo>)
            {
                try
                {
                    mView.showProgress(false ,isShowProgressbar )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("AppliedJobs response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onAppliedJobsSuccess(response.body() as AppliedJobsPojo)

                    else
                        mView.onFailed(response.body()?.status!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<AppliedJobsPojo>, t: Throwable)
            {

                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false ,isShowProgressbar )

                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later")

                else
                {
                    (mContext as HomeActivity).stopProgressBarAnim()
                }
            }

        })

    }

    override fun shortList_job(user_id : String , job_id: String, status: String , position : Int) {

        mView.showProgress(true , true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        shortlistJobService = webServices.shortlistJobApi("application/x-www-form-urlencoded" ,Constants.auth_key , user_id ,  job_id , status
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(shortlistJobService ,Constants.RETRY_COUNT ,object : Callback<ShortlistJobPojo>
        {
            override fun onResponse(call: Call<ShortlistJobPojo>, response: Response<ShortlistJobPojo>)
            {
                try
                {
                    mView.showProgress(false , true )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("Shortlist Job response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onJobShortlisted(status ,position)

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
                mView.showProgress(false ,true )

                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later")

                else
                {
                    (mContext as HomeActivity).stopProgressBarAnim()
                }

            }


        })
    }

}