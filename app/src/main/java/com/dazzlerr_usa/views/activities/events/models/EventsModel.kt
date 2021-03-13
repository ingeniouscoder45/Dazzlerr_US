package com.dazzlerr_usa.views.activities.events.models

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.events.pojos.EventsPojo
import com.dazzlerr_usa.views.activities.events.presenters.EventsPresenter
import com.dazzlerr_usa.views.activities.events.views.EventsView
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class EventsModel(val mContext: Context, val mView: EventsView) : EventsPresenter
{
    var modelsService :Call<EventsPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(modelsService!=null)
        modelsService?.cancel()
    }

    override fun getEvents(page: String, month: String, year: String, mSearchStr: String, categoryId: String, city: String, venue: String, organizer: String, isShowProgressbar: Boolean)
    {

        mView.showProgress(true ,isShowProgressbar)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        modelsService = webServices.getEventsApi("application/x-www-form-urlencoded"   ,Constants.auth_key , mSearchStr , categoryId , city , venue , organizer , page ,month , year
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(modelsService ,Constants.RETRY_COUNT ,object : Callback<EventsPojo>
        {
            override fun onResponse(call: Call<EventsPojo>, response: Response<EventsPojo>)
            {
                try
                {
                    mView.showProgress(false ,isShowProgressbar )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("Events response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSuccess(response.body() as EventsPojo)

                    else
                        mView.onFailed(response.body()?.message!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<EventsPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false ,isShowProgressbar )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }

}