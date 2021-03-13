package com.dazzlerr_usa.views.activities.interestedtalents

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

class InterestedTalentsModel(val mContext: Context, val mView:InterestedTalentsView) : InterestedTalentsPresenter
{
     var interestedTalentsService :Call<InterestedTalentsPojo> ? = null

    override fun cancelRetrofitRequest()
    {
        if(interestedTalentsService!=null)
            interestedTalentsService?.cancel()
    }

    override fun getInterestedTalents(user_id: String, page: String, isShowProgressbar: Boolean) {

        mView.showProgress(true ,isShowProgressbar )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        interestedTalentsService = webServices.getInterestedTalentsApi("application/x-www-form-urlencoded" ,Constants.auth_key , user_id ,page
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(interestedTalentsService ,Constants.RETRY_COUNT , object : Callback<InterestedTalentsPojo>
        {
            override fun onResponse(call: Call<InterestedTalentsPojo>, response: Response<InterestedTalentsPojo>)
            {
                try
                {
                    mView.showProgress(false ,isShowProgressbar )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("Talents response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onGetInterestedTalentsSuccess(response.body() as InterestedTalentsPojo)

                    else
                        mView.onFailed(response.body()?.success!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<InterestedTalentsPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false ,isShowProgressbar )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }

}