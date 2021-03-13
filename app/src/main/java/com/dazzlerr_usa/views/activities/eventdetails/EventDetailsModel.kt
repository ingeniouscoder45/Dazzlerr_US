package com.dazzlerr_usa.views.activities.eventdetails

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

class EventDetailsModel(val mContext: Context, val mView:EventDetailsView) : EventDetailsPresenter
{


    var mEventService :Call<EventDetailsPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(mEventService!=null)
        mEventService?.cancel()
    }

    override fun getEventDetails(user_id: String, event_id: String)
    {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mEventService = webServices.getEventDetailsApi("application/x-www-form-urlencoded"   ,Constants.auth_key ,user_id ,event_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(mEventService ,Constants.RETRY_COUNT , object : Callback<EventDetailsPojo>
        {
            override fun onResponse(call: Call<EventDetailsPojo>, response: Response<EventDetailsPojo>)
            {
                try
                {

                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("EventDetails response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSuccess(response.body() as EventDetailsPojo)

                    else
                        mView.onFailed(response.body()?.message!!)

                }

                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<EventDetailsPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }
        })
    }

    override fun likeOrDislike(user_id: String, event_id: String, status: String)
    {
        Timber.e(user_id+ "  "+ event_id +"  "+status)
        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        val service = webServices.likeOrDislikeEventApi("application/x-www-form-urlencoded"  ,Constants.auth_key  ,user_id ,event_id , status
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