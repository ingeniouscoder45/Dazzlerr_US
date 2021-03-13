package com.dazzlerr_usa.views.activities.mymembership

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

class MyMembershipModel(val mContext: Context, val mView: MyMembershipView) : MyMembershipPresenter
{
    var mService :Call<MyMembershipPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(mService!=null)
            mService?.cancel()
    }

    override fun getMyMembership(user_id: String)
    {
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mService = webServices.getMyMembershipApi("application/x-www-form-urlencoded" ,Constants.auth_key , user_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        mView.showProgress(true)
        APIHelper.enqueueWithRetry(mService ,Constants.RETRY_COUNT ,object : Callback<MyMembershipPojo>
        {
            override fun onResponse(call: Call<MyMembershipPojo>, response: Response<MyMembershipPojo>)
            {
                try
                {

                    mView.showProgress(false)
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetMyMembership response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSuccess(response.body()!!)

                    else
                        mView.onFailed("Failed to connect!")

                }
                catch (e: Exception)
                {

                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                    mView.showProgress(false)
                }

            }

            override fun onFailure(call: Call<MyMembershipPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

                mView.showProgress(false)
            }


        })

    }

}