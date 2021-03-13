package com.dazzlerr_usa.views.activities.home

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

class HomeModel(val mView:HomeView) : HomePresenter {

     var service:Call<ClearDeviceIdPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(service!=null)
        service?.cancel()
    }

    override fun clearDeviceId(user_id: String) {


        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        service = webServices.clearDeviceid("application/x-www-form-urlencoded" ,Constants.auth_key , user_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(service ,Constants.RETRY_COUNT ,object : Callback<ClearDeviceIdPojo>
        {
            override fun onResponse(call: Call<ClearDeviceIdPojo>, response: Response<ClearDeviceIdPojo>)
            {
                try
                {
                    mView.showProgress(false)
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("ClearDeviceid response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onClearDeviceid()

                    else
                        mView.onFailed(response.body()?.message!!)
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }
            }

            override fun onFailure(call: Call<ClearDeviceIdPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled) {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }
        })

    }

}