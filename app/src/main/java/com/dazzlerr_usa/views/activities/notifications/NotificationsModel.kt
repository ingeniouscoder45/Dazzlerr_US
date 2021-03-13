package com.dazzlerr_usa.views.activities.notifications

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

class NotificationsModel(val mContext: Context, val mView:NotificationsView) : NotficationsPresenter
{


    var mEventService :Call<NotificationsPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(mEventService!=null)
        mEventService?.cancel()
    }

    override fun getNotifications(user_id: String,page: String , shouldShowProgressBar: Boolean) {


        mView.showProgress(true ,shouldShowProgressBar)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mEventService = webServices.getNotificationsApi("application/x-www-form-urlencoded"   ,Constants.auth_key  ,user_id ,page
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(mEventService ,Constants.RETRY_COUNT , object : Callback<NotificationsPojo>
        {
            override fun onResponse(call: Call<NotificationsPojo>, response: Response<NotificationsPojo>)
            {
                try
                {

                    mView.showProgress(false ,shouldShowProgressBar )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("Notifications response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSuccess(response.body() as NotificationsPojo)

                    else
                        mView.onFailed(response.body()?.success!!)

                }

                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<NotificationsPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  ,shouldShowProgressBar )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }
        })
    }

}