package com.dazzlerr_usa.views.activities.editprofile.models

import android.content.Context
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.editprofile.pojos.RatesPojo
import com.dazzlerr_usa.views.activities.editprofile.presenters.RatesPresenter
import com.dazzlerr_usa.views.activities.editprofile.views.RatesView
import com.google.gson.GsonBuilder


class RatesModel(var mContext: Context, var mView: RatesView) : RatesPresenter
{
    var mServices  : Call<RatesPojo>?=null
    override fun cancelRetrofitRequest() {

        if(mServices!=null)
            mServices?.cancel()
    }

    override fun updateRatesAndTravel(user_id: String, full_day: String, half_day: String, hourly: String, test: String, will_fly: String, passport_ready: String, drive_miles: String, payment_options: String, availability: String, facebook: String, twitter: String, insta: String) {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mServices = webServices.updateRatesAndTravel("application/x-www-form-urlencoded" ,Constants.auth_key , user_id, full_day, half_day, hourly, test, will_fly, passport_ready, drive_miles, payment_options, availability, facebook, twitter, insta
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)
        APIHelper.enqueueWithRetry(mServices ,Constants.RETRY_COUNT ,object : Callback<RatesPojo>
        {
            override fun onResponse(call: Call<RatesPojo>, response: Response<RatesPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("Rates response: "+ responseStr)

                    if(response.body()?.success!!)
                    mView.onSuccess()

                    else
                        mView.onFailed(response.body()?.status!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showProgress(false)


            }

            override fun onFailure(call: Call<RatesPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled)
                {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again")
                }
            }
        })

    }

}