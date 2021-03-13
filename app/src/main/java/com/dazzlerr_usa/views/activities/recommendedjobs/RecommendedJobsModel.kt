package com.dazzlerr_usa.views.activities.recommendedjobs

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import com.dazzlerr_usa.views.fragments.jobs.JobsPojo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class RecommendedJobsModel(val mContext: Context, val mView:ReccomendedJobsView) : RecommendedJobsPresenter
{


    var mJobsService :Call<JobsPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(mJobsService!=null)
        mJobsService?.cancel()
    }


    override fun getRecommendedJobs(user_id: String, page: String, shouldShowProgressBar: Boolean) {

        mView.showProgress(true ,shouldShowProgressBar)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mJobsService = webServices.getRecommendedJobsApi("application/x-www-form-urlencoded"   ,Constants.auth_key  ,user_id ,page
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(mJobsService ,Constants.RETRY_COUNT , object : Callback<JobsPojo>
        {
            override fun onResponse(call: Call<JobsPojo>, response: Response<JobsPojo>)
            {
                try
                {

                    mView.showProgress(false ,shouldShowProgressBar )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetRecommended Jobs response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSuccess(response.body() as JobsPojo)

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
                mView.showProgress(false  ,shouldShowProgressBar )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }
        })
    }

}