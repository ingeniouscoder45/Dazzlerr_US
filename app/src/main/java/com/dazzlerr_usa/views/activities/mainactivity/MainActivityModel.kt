package com.dazzlerr_usa.views.activities.mainactivity

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

class MainActivityModel(val mContext: Context, val mView:MainActivityView) : MainActivityPresenter
{
    var modelsService :Call<MonetizeApiPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(modelsService!=null)
        modelsService?.cancel()
    }

    override fun getConstants(user_id: String)
    {
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        modelsService = webServices.getAppConstants("application/x-www-form-urlencoded"   ,Constants.auth_key ,user_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(modelsService ,Constants.RETRY_COUNT ,object : Callback<MonetizeApiPojo>
        {
            override fun onResponse(call: Call<MonetizeApiPojo>, response: Response<MonetizeApiPojo>)
            {
                try
                {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetMonetizeSettings response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSuccess(response.body() as MonetizeApiPojo)

                    else
                        mView.onFailed("Something went wrong! Please try again later.")

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<MonetizeApiPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }

}