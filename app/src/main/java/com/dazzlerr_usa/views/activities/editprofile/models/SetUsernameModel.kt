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
import com.dazzlerr_usa.views.activities.editprofile.presenters.SetUsernamePresenter
import com.dazzlerr_usa.views.activities.editprofile.views.SetUserrnameView
import com.dazzlerr_usa.views.activities.settings.UpdateUsernamePojo
import com.google.gson.GsonBuilder


class SetUsernameModel(var mContext: Context, var mView: SetUserrnameView) : SetUsernamePresenter
{
    var mServices  : Call<UpdateUsernamePojo>?=null


    override fun setUsername(user_id: String, username: String)
    {

        mView.showUsernameDialogProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mServices = webServices.updateUserNameApi("application/x-www-form-urlencoded" ,Constants.auth_key , user_id, username
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(mServices ,Constants.RETRY_COUNT ,object : Callback<UpdateUsernamePojo>
        {
            override fun onResponse(call: Call<UpdateUsernamePojo>, response: Response<UpdateUsernamePojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("SetUsername response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSetUsername(username)

                    else
                        mView.onUsernameFailed(response.body()?.message!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showUsernameDialogProgress(false)


            }

            override fun onFailure(call: Call<UpdateUsernamePojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled)
                {
                    mView.showUsernameDialogProgress(false)
                    mView.onUsernameFailed("Something went wrong! Please try again")
                }
            }
        })

    }

    override fun cancelRetrofitRequest() {

        if(mServices!=null)
            mServices?.cancel()
    }


    override fun checkUsernameAvailability(user_id: String, username: String)
    {

        mView.showUsernameDialogProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mServices = webServices.checkUsernameApi("application/x-www-form-urlencoded" ,Constants.auth_key , user_id, username
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(mServices ,Constants.RETRY_COUNT ,object : Callback<UpdateUsernamePojo>
        {
            override fun onResponse(call: Call<UpdateUsernamePojo>, response: Response<UpdateUsernamePojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("CheckUsername response: "+ responseStr)

                    if(response.body()?.status!!)
                    mView.onUsernameAvailable(username)

                    else
                        mView.onUsernameFailed(response.body()?.message!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showUsernameDialogProgress(false)


            }

            override fun onFailure(call: Call<UpdateUsernamePojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled)
                {
                    mView.showUsernameDialogProgress(false)
                    mView.onUsernameFailed("Something went wrong! Please try again")
                }
            }
        })

    }

}