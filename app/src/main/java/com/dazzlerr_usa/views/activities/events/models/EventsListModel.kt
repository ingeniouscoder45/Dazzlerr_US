package com.dazzlerr_usa.views.activities.events.models

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.events.pojos.EventsListPojo
import com.dazzlerr_usa.views.activities.events.presenters.EventsListPresenter
import com.dazzlerr_usa.views.activities.events.views.EventsListView
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class EventsListModel(val mContext: Context, val mView:EventsListView) : EventsListPresenter
{
    var mEventsService :Call<EventsListPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(mEventsService!=null)
            mEventsService?.cancel()
    }

    override fun getEventslist(type: Int, page: String)
    {

        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)

        if(type==1)
            mEventsService = webServices.getFeaturedEventsApi("application/x-www-form-urlencoded"   ,Constants.auth_key,page
                    , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                    , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                    , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        else if(type==2)
            mEventsService = webServices.getPopularEventsApi("application/x-www-form-urlencoded"   ,Constants.auth_key ,page
                    , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                    , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                    , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        else if(type==3)
            mEventsService = webServices.getLatestEventsApi("application/x-www-form-urlencoded"   ,Constants.auth_key ,page
                    , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                    , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                    , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(mEventsService ,Constants.RETRY_COUNT ,object : Callback<EventsListPojo>
        {
            override fun onResponse(call: Call<EventsListPojo>, response: Response<EventsListPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("EventList response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSuccess(response.body() as EventsListPojo ,type)

                    else
                        mView.onFailed(response.body()?.message!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<EventsListPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false)
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }

}