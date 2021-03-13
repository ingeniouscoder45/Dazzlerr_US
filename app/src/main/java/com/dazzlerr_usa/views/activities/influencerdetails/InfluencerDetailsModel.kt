package com.dazzlerr_usa.views.activities.influencerdetails

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.elitememberdetails.LikeOrDislikePojo
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class InfluencerDetailsModel(val mContext: Context, val mView:InfluencerDetailsView) : InfluencerDetailsPresenter
{


    var modelsService :Call<InfluencerDetailsPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(modelsService!=null)
        modelsService?.cancel()
    }

    override fun getInfluencerDetails(user_id: String, profile_id: String) {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        modelsService = webServices.getInfluencerDetailsApi("application/x-www-form-urlencoded"    ,Constants.auth_key,user_id ,profile_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(modelsService ,Constants.RETRY_COUNT ,object : Callback<InfluencerDetailsPojo>
        {
            override fun onResponse(call: Call<InfluencerDetailsPojo>, response: Response<InfluencerDetailsPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("InfluencerDetails response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSuccess(response.body() as InfluencerDetailsPojo)

                    else
                        mView.onFailed(response.body()?.success!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<InfluencerDetailsPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }
        })
    }

    override fun likeOrDislike(user_id: String, profile_id: String, status: String)
    {
        Timber.e(user_id+ "  "+ profile_id +"  "+status)
        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        val service = webServices.likeOrDislikeEliteApi("application/x-www-form-urlencoded"   ,Constants.auth_key ,user_id ,profile_id , status
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(service ,Constants.RETRY_COUNT ,object : Callback<LikeOrDislikePojo>
        {
            override fun onResponse(call: Call<LikeOrDislikePojo>, response: Response<LikeOrDislikePojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("LikeOrDislike response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onLikeOrDislike(status)

                    else
                        mView.onFailed(response.body()?.success!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<LikeOrDislikePojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later")
            }
        })

    }

}