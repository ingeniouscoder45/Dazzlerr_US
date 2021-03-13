package com.dazzlerr_usa.views.activities.becomefeatured

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

class BecomeFeaturedModel(val mContext: Context, val mView:BecomeFeaturedView) : BecomeFeaturedPresenter
{

    var modelsService :Call<BecomeFeaturedPojo>?=null
    override fun cancelRetrofitRequest()
    {
        if(modelsService!=null)
        modelsService?.cancel()
    }

    override fun addWpUser(user_id: String)
    {

        mView.showProgress(true)
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        modelsService = webServices.addWpUser("application/x-www-form-urlencoded" ,Constants.auth_key , user_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(modelsService ,Constants.RETRY_COUNT ,object : Callback<BecomeFeaturedPojo>
        {
            override fun onResponse(call: Call<BecomeFeaturedPojo>, response: Response<BecomeFeaturedPojo>)
            {
                try
                {
                    mView.showProgress(false)
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("AddWpUser response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSuccess(response.body() as BecomeFeaturedPojo)

                    else
                        mView.onFailed("Failed to connect!")

                }
                catch (e: Exception)
                {
                    mView.showProgress(false)
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<BecomeFeaturedPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false)
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }

}