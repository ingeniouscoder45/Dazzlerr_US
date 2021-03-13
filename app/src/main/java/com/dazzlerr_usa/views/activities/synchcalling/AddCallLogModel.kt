package com.dazzlerr_usa.views.activities.synchcalling

import android.app.Activity
import android.content.Context
import com.google.android.material.snackbar.Snackbar
import android.view.View
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.google.gson.GsonBuilder


class AddCallLogModel(var mContext: Context, var mView: AddCallLogsView) : AddCallLogsPresenter
{
     var mService :  Call<AddCallLogPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(mService!=null)
        mService?.cancel()
    }


    override fun addCallLog(user_id: String, caller_name: String, caller_id: String, call_duration: String, call_type: String, date_time :String ,is_caller :String) {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mService = webServices.addCallLogsApi("application/x-www-form-urlencoded",Constants.auth_key , user_id ,caller_name ,caller_id , call_duration, call_type, date_time,is_caller
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(mService ,Constants.RETRY_COUNT, object : Callback<AddCallLogPojo>
        {
            override fun onResponse(call: Call<AddCallLogPojo>, response: Response<AddCallLogPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("AddCallLogs response: "+ responseStr)

                    if(response.body()?.status!!)
                    mView.onAddCallLogs()

                    else
                        mView.onFailed(response.body()?.message!!)


                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later.")
                }

                mView.showProgress(false)

            }

            override fun onFailure(call: Call<AddCallLogPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false)
                mView.onFailed("Something went wrong! Please try again")
            }
        })
    }

    fun showSnackbar(message: String)
    {
        var snackbar:Snackbar
        val parentLayout = (mContext as Activity).findViewById<View>(android.R.id.content)
        snackbar  = Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT)
        snackbar.show()
    }

}