package com.onlinepoundstore.fragments.login

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
import com.dazzlerr_usa.views.activities.editprofile.pojos.AppearancePojo
import com.dazzlerr_usa.views.activities.editprofile.presenters.AppearancePresenter
import com.dazzlerr_usa.views.activities.editprofile.views.AppearanceView
import com.google.gson.GsonBuilder


class AppearanceModel(var mContext: Context, var mView: AppearanceView) : AppearancePresenter
{
    var mServices  : Call<AppearancePojo>?=null
    override fun cancelRetrofitRequest() {

        if(mServices!=null)
            mServices?.cancel()
    }


    override fun updateAppearance(user_id: String, height: String, weight: String, biceps: String, chest: String, waist: String, hips: String, hair_color: String, hair_length: String, hair_type: String, eye_color: String, skintone: String, dress: String, shoes: String, tags: String) {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mServices = webServices.updateAppearance("application/x-www-form-urlencoded" ,Constants.auth_key,user_id, height, weight, biceps, chest, waist, hips, hair_color, hair_length, hair_type, eye_color, skintone, dress, shoes, tags
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(mServices ,Constants.RETRY_COUNT ,object : Callback<AppearancePojo>
        {
            override fun onResponse(call: Call<AppearancePojo>, response: Response<AppearancePojo>)
            {
                try
                {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("Appearance response: "+ responseStr)

                    if(response.body()?.success!!)
                    mView.onSuccess()

                    else
                        mView.onFailed(response.body()?.status!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showProgress(false)


            }

            override fun onFailure(call: Call<AppearancePojo>, t: Throwable) {
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