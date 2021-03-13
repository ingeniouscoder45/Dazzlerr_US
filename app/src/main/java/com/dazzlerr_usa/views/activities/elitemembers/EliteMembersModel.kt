package com.dazzlerr_usa.views.activities.elitemembers

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

class EliteMembersModel(val mContext: Context, val mView:EliteMembersView) : EliteMembersPresenter
{
    var modelsService :Call<EliteMembersPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(modelsService!=null)
        modelsService?.cancel()
    }


    override fun getEliteMembers(page: String ,isShowProgressbar: Boolean)
    {
        mView.showProgress(true ,isShowProgressbar)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        modelsService = webServices.getEliteMembersApi("application/x-www-form-urlencoded"    ,Constants.auth_key,page
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(modelsService ,Constants.RETRY_COUNT ,object : Callback<EliteMembersPojo>
        {
            override fun onResponse(call: Call<EliteMembersPojo>, response: Response<EliteMembersPojo>)
            {
                try
                {
                    mView.showProgress(false ,isShowProgressbar )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("EliteMembers response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSuccess(response.body() as EliteMembersPojo)

                    else
                        mView.onFailed(response.body()?.success!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<EliteMembersPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false ,isShowProgressbar )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }

        })

    }

}