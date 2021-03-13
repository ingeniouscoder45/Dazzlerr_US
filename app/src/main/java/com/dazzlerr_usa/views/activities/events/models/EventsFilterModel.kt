package com.dazzlerr_usa.views.activities.events.models

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.events.pojos.EventFilterPojo
import com.dazzlerr_usa.views.activities.events.presenters.EventsFilterPresenter
import com.dazzlerr_usa.views.activities.events.views.EventFilterView
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class EventsFilterModel(val mContext: Context, val mView:EventFilterView) : EventsFilterPresenter
{
    var mEventsFilterService :Call<EventFilterPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(mEventsFilterService!=null)
            mEventsFilterService?.cancel()
    }

    override fun getEventsFilters()
    {
        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)

        mEventsFilterService = webServices.getEventFilterDataApi("application/x-www-form-urlencoded"   ,Constants.auth_key
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(mEventsFilterService ,Constants.RETRY_COUNT ,object : Callback<EventFilterPojo>
        {
            override fun onResponse(call: Call<EventFilterPojo>, response: Response<EventFilterPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("EventFilter response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSuccess(response.body() as EventFilterPojo)

                    else
                        mView.onFailed(response.body()?.message!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<EventFilterPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false)
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }

}