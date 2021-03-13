package com.dazzlerr_usa.views.activities.events.models

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
import com.dazzlerr_usa.views.activities.events.pojos.EventsCategoryPojo
import com.dazzlerr_usa.views.activities.events.pojos.PromoteEventPojo
import com.dazzlerr_usa.views.activities.events.presenters.PromoteEventPresenter
import com.dazzlerr_usa.views.activities.events.views.PromoteEventView
import com.google.gson.GsonBuilder

class PromoteEventModel(var mContext: Context, var mView: PromoteEventView) : PromoteEventPresenter
{

    var mEventCategory  : Call<EventsCategoryPojo>?=null
    var mPromoteEvent  : Call<PromoteEventPojo>?=null
    override fun cancelRetrofitRequest()
    {

        if(mEventCategory!=null)
            mEventCategory?.cancel()

        if(mPromoteEvent!=null)
            mPromoteEvent?.cancel()
    }

    override fun validateEvent(event_cat_id: String, event_title: String, event_date: String, event_venue: String, organizer_name: String, organizer_phone: String, organizer_email: String) {

        if (event_title.length == 0 || event_venue.length == 0 || organizer_name.length == 0 || organizer_phone.length==0 || organizer_email.length==0)
        {
            mView.onFailed("Please fill all the fields.")
        }

        else if (event_cat_id.length == 0)
        {
            mView.onFailed("Please select your event type.")
        }

        else if (event_date.length == 0)
        {
            mView.onFailed("Please select you event date.")
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(organizer_email).matches())
        {
            mView.onFailed("Please enter a valid email address.")
        }

        else if (organizer_phone.length >10 || organizer_phone.length<10)
        {
            mView.onFailed("Please enter a valid mobile number.")
        }

        else if (!Patterns.PHONE.matcher(organizer_phone).matches())
        {
            mView.onFailed("Please enter a valid mobile number.")
        }


        else
        {
            mView.onEventValidate()
        }
    }


    override fun promoteEvent(event_cat_id: String, event_title: String, event_date: String, event_venue: String, organizer_name: String, organizer_phone: String, organizer_email: String) {


        Timber.e(event_cat_id+ " "+ event_title+" "+ event_date+" "+ event_venue+" "+ organizer_name+" "+ organizer_phone+" "+ organizer_email)
        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mPromoteEvent = webServices.promoteEventsApi("application/x-www-form-urlencoded" ,Constants.auth_key,event_cat_id, event_title , event_date ,event_venue, organizer_name,organizer_phone,organizer_email
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(mPromoteEvent , Constants.RETRY_COUNT  , object : Callback<PromoteEventPojo>
        {
            override fun onResponse(call: Call<PromoteEventPojo>, response: Response<PromoteEventPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("PromoteEvent response: "+ responseStr)

                    if(response.body()?.status!!)
                    mView.onPromoteEventSuccess()

                    else
                        mView.onFailed(response.body()?.success!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showProgress(false)


            }

            override fun onFailure(call: Call<PromoteEventPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled)
                {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again")
                }
            }
        })

    }


    override fun getEventsCategory()
    {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mEventCategory = webServices.getEventsCategoryApi("application/x-www-form-urlencoded", Constants.auth_key
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(mEventCategory ,Constants.RETRY_COUNT ,object : Callback<EventsCategoryPojo>
        {
            override fun onResponse(call: Call<EventsCategoryPojo>, response: Response<EventsCategoryPojo>)
            {
                try
                {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetStates response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onGetEventCategory(response.body() as EventsCategoryPojo)

                    else
                        mView.onFailed(response.body()?.message!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showProgress(false)

            }

            override fun onFailure(call: Call<EventsCategoryPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled) {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again later")
                }
            }
        })

    }

}