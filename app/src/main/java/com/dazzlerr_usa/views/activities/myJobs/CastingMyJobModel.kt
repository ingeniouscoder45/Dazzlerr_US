package com.dazzlerr_usa.views.activities.myJobs

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.fragments.castingprofile.pojo.CastingMyJobPojo
import com.dazzlerr_usa.views.fragments.castingprofile.pojo.UpdateJobPojo
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class CastingMyJobModel(val mContext: Context, val mView: CastingMyJobsView) : CastingMyJobsPresenter
{

    var projectsService :Call<CastingMyJobPojo>?=null
    var updateJobService :Call<UpdateJobPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(projectsService!=null)
        projectsService?.cancel()

        if(updateJobService!=null)
            updateJobService?.cancel()
    }

    override fun getMyJobs(user_id: String, page: String ,status: String)
    {

        mView.showProgress(true)
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        projectsService = webServices.getMyJobs("application/x-www-form-urlencoded" ,Constants.auth_key , user_id  ,page ,status
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(projectsService ,Constants.RETRY_COUNT ,object : Callback<CastingMyJobPojo>
        {
            override fun onResponse(call: Call<CastingMyJobPojo>, response: Response<CastingMyJobPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("CastingMyJobs response: "+ responseStr)

                    if(response.body()!!.success)
                        mView.onSuccess(response.body() as CastingMyJobPojo)

                    else
                        mView.onFailed(response.body()!!.status.toString())

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<CastingMyJobPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }

        })
    }

    override fun updateJobStatus(user_id: String, call_id: String, status: String , position: Int)
    {

        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        updateJobService = webServices.updateJobStatus("application/x-www-form-urlencoded" ,Constants.auth_key , user_id  ,call_id ,status
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(updateJobService ,Constants.RETRY_COUNT ,object : Callback<UpdateJobPojo>
        {
            override fun onResponse(call: Call<UpdateJobPojo>, response: Response<UpdateJobPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("UpdateJobStatus response: "+ responseStr)

                    if(response.body()!!.status)
                        mView.onUpdateJobStatus(status , position)

                    else
                        mView.onStatusFailed(response.body()!!.message.toString())

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<UpdateJobPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false )
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later")

            }

        })
    }


}