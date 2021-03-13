package com.dazzlerr_usa.views.activities.membership

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

class MembershipModel(val mContext: Context, val mView: MembershipView) : MembershipPresenter
{
    var mService :Call<MembershipPojo>?=null
    var mTokenService :Call<GetTokenPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(mService!=null)
            mService?.cancel()

        if(mTokenService!=null)
            mTokenService?.cancel()
    }

    override fun getMembershipDetails(user_id : String) {

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mService = webServices.getMembershipDetailsApi("application/x-www-form-urlencoded" ,Constants.auth_key , user_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        mView.showProgress(true)
        APIHelper.enqueueWithRetry(mService ,Constants.RETRY_COUNT ,object : Callback<MembershipPojo>
        {
            override fun onResponse(call: Call<MembershipPojo>, response: Response<MembershipPojo>)
            {
                try
                {

                    mView.showProgress(false)
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetMembership response: "+ responseStr)

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

            override fun onFailure(call: Call<MembershipPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

                mView.showProgress(false)
            }


        })

    }

    override fun getTokenApi() {

        val webServices = RetrofitClient.getClient(Constants.liveClientTokenUrl).create(WebServices::class.java)
        mTokenService = webServices.getTokenApi()

        mView.showProgress(true)
        APIHelper.enqueueWithRetry(mTokenService ,Constants.RETRY_COUNT ,object : Callback<GetTokenPojo>
        {
            override fun onResponse(call: Call<GetTokenPojo>, response: Response<GetTokenPojo>)
            {
                try
                {

                    mView.showProgress(false)
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetToken response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onGetToken(response.body()!!)

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

            override fun onFailure(call: Call<GetTokenPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later")

                mView.showProgress(false)
            }


        })

    }

}