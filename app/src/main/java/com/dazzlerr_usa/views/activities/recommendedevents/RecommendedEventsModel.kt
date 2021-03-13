package com.dazzlerr_usa.views.activities.recommendedevents

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import com.dazzlerr_usa.views.activities.events.pojos.EventsListPojo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class RecommendedEventsModel(val mContext: Context, val mView:RecomendedEventsView) : RecommendedEventsPresenter
{


    var mEventsService :Call<EventsListPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(mEventsService!=null)
        mEventsService?.cancel()
    }


    override fun getRecommendedEvents(user_id: String, page: String, shouldShowProgressBar: Boolean) {

        mView.showProgress(true ,shouldShowProgressBar)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mEventsService = webServices.getRecommendedEventsApi("application/x-www-form-urlencoded"   ,Constants.auth_key  ,user_id ,page
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(mEventsService ,Constants.RETRY_COUNT , object : Callback<EventsListPojo>
        {
            override fun onResponse(call: Call<EventsListPojo>, response: Response<EventsListPojo>)
            {
                try
                {

                    mView.showProgress(false ,shouldShowProgressBar )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetRecommended Events response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSuccess(response.body() as EventsListPojo)

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
                mView.showProgress(false  ,shouldShowProgressBar )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }
        })
    }

}