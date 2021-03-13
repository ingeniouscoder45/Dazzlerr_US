package com.dazzlerr_usa.views.activities.portfolio.addvideo

import android.content.Context
import android.util.Patterns
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.google.gson.GsonBuilder


class VideoModel(var mContext: Context, var mView: VideoView) : VideoPresenter
{

    var AddProjectServices  : Call<VideoPojo>?=null
    override fun cancelRetrofitRequest()
    {
        if(AddProjectServices!=null)
            AddProjectServices?.cancel()

    }


    override fun validate(video_title: String, video_description: String, video_url: String) {

        if (video_title.length == 0 || video_description.length == 0 || video_url.length==0)
        {
            mView.onFailed("Please fill all the fields.")
        }

        else if(!Patterns.WEB_URL.matcher(video_url).matches())
        {
            mView.onFailed("Please enter a valid url.")
        }

        else
            return mView.isValidate()
    }


    override fun addVideo(user_id: String, video_title: String, video_description: String, video_url: String) {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        AddProjectServices = webServices.addPortfolioVideo("application/x-www-form-urlencoded" ,Constants.auth_key , user_id, video_title, video_description, video_url
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)
        APIHelper.enqueueWithRetry(AddProjectServices ,Constants.RETRY_COUNT , object : Callback<VideoPojo>
        {
            override fun onResponse(call: Call<VideoPojo>, response: Response<VideoPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("AddVideo response: "+ responseStr)

                    if(response.body()?.success!!)
                    mView.onSuccess()

                    else
                        mView.onFailed(response.body()?.status!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showProgress(false)


            }

            override fun onFailure(call: Call<VideoPojo>, t: Throwable) {
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