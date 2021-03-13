package com.dazzlerr_usa.views.fragments.castingdashboard

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class CastingDashboardModel(val mContext: Context, val mView:CastingDashboardView) : CastingDashboardPresenter
{

     var modelsService :Call<CastingDashboardPojo> ? = null

    override fun cancelRetrofitRequest()
    {
        if(modelsService!=null)
        modelsService?.cancel()
    }

    override fun getCastingDashboard(user_id: String) {

        mView.showProgress(true )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        modelsService = webServices.getCastingDashboardApi("application/x-www-form-urlencoded" , Constants.auth_key,user_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(modelsService ,Constants.RETRY_COUNT , object : Callback<CastingDashboardPojo>
        {
            override fun onResponse(call: Call<CastingDashboardPojo>, response: Response<CastingDashboardPojo>)
            {
                try
                {
                    mView.showProgress(false  )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("CastingDashboard response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onGetDashboardData(response.body() as CastingDashboardPojo)

                    else
                        mView.onFailed(response.body()?.status!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<CastingDashboardPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")
                else
                {
                    (mContext as HomeActivity).stopProgressBarAnim()
                }
            }


        })

    }

}