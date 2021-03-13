package com.dazzlerr_usa.views.activities.editprofile.models

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.editprofile.presenters.ProfileServicesPresenter
import com.dazzlerr_usa.views.activities.editprofile.views.ProfileServicesView
import com.dazzlerr_usa.views.activities.editprofile.pojos.ProfileServicesPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.UpdateServicePojo
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class ProfileServicesModel(val mContext: Context, val mView: ProfileServicesView) : ProfileServicesPresenter
{
    var modelsService :Call<ProfileServicesPojo>?=null
    var updateService :Call<UpdateServicePojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(modelsService!=null)
        modelsService?.cancel()
    }

    override fun getServices(user_role: String , user_id : String)
    {
        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        modelsService = webServices.getProfileServices("application/x-www-form-urlencoded"   ,Constants.auth_key ,user_role ,user_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(modelsService ,Constants.RETRY_COUNT, object : Callback<ProfileServicesPojo>
        {
            override fun onResponse(call: Call<ProfileServicesPojo>, response: Response<ProfileServicesPojo>)
            {
                try
                {
                    mView.showProgress(false  )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("ProfileServices response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onSuccess(response.body() as ProfileServicesPojo)

                    else
                        mView.onFailed(response.body()?.status!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<ProfileServicesPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }


    override fun updateServices(user_id: String, services: String , other_services:String) {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        updateService = webServices.updateServices("application/x-www-form-urlencoded"   ,Constants.auth_key  ,user_id , services, other_services
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(updateService ,Constants.RETRY_COUNT, object : Callback<UpdateServicePojo>
        {
            override fun onResponse(call: Call<UpdateServicePojo>, response: Response<UpdateServicePojo>)
            {
                try
                {
                    mView.showProgress(false  )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("UpdateServices response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onUpdateServiceSuccess()

                    else
                        mView.onFailed(response.body()?.status!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<UpdateServicePojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }


}