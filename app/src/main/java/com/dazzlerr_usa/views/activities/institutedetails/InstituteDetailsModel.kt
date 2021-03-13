package com.dazzlerr_usa.views.activities.institutedetails

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

class InstituteDetailsModel(val mContext: Context, val mView:InstituteDetailsView) : InstituteDetailsPresenter
{


    var mEventService :Call<InstituteDetailsPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(mEventService!=null)
        mEventService?.cancel()
    }

    override fun getInstituteDetails(institute_id: String)
    {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mEventService = webServices.getInstituteDetailsApi("application/x-www-form-urlencoded"   ,Constants.auth_key  ,institute_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(mEventService ,Constants.RETRY_COUNT , object : Callback<InstituteDetailsPojo>
        {
            override fun onResponse(call: Call<InstituteDetailsPojo>, response: Response<InstituteDetailsPojo>)
            {
                try
                {

                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("InstituteDetails response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onSuccess(response.body() as InstituteDetailsPojo)

                    else
                        mView.onFailed(response.body()?.status!!)

                }

                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<InstituteDetailsPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }
        })
    }

}