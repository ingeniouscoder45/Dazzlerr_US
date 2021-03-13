package com.dazzlerr_usa.views.activities.calllogs

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

class CallLogsModel(val mContext: Context, val mView:CallLogsView) : CallLogsPresenter
{


    var mLogsService :Call<CallLogsPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(mLogsService!=null)
        mLogsService?.cancel()
    }


    override fun getCallLogs(user_id: String) {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mLogsService = webServices.getCallLogsApi("application/x-www-form-urlencoded"   ,Constants.auth_key  ,user_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(mLogsService ,Constants.RETRY_COUNT , object : Callback<CallLogsPojo>
        {
            override fun onResponse(call: Call<CallLogsPojo>, response: Response<CallLogsPojo>)
            {
                try
                {

                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetRecommended Events response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSuccess(response.body() as CallLogsPojo)

                    else
                        mView.onFailed(response.body()?.success!!)

                }

                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<CallLogsPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }
        })
    }

}