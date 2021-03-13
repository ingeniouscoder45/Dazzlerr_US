package com.dazzlerr_usa.views.activities.eventcontact

import android.content.Context
import android.util.Patterns
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

class EventContactModel(val mContext: Context, val mView:EventContactView) : EventContactPresenter
{

    var modelsService :Call<EventContactPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(modelsService!=null)
        modelsService?.cancel()
    }

    override fun isValid(name: String, phone: String, email: String, msg: String, type: String, profile_name: String) {

        if (name.length == 0 || phone.length == 0 || email.length == 0 || msg.length == 0|| type.length == 0 || profile_name.length == 0)
        {
            mView.onFailed("Please fill all the fields.")
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            mView.onFailed("Please enter a valid email address.")
        }

        else if (!Patterns.PHONE.matcher(phone).matches())
        {
            mView.onFailed("Please enter a valid phone number.")
        }

        else if (phone.length >10 || phone.length<10)
        {
            mView.onFailed("Please enter a valid phone number.")
        }

        else
            mView.onValidate()
    }
    override fun contact(name: String, phone: String, email: String, msg: String, event_id: String, profile_name: String, username: String) {

        mView.showProgress(true )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        modelsService = webServices.eventContact("application/x-www-form-urlencoded"  ,Constants.auth_key,name, phone, email, msg, event_id, profile_name ,username
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(modelsService ,Constants.RETRY_COUNT ,object : Callback<EventContactPojo>
        {
            override fun onResponse(call: Call<EventContactPojo>, response: Response<EventContactPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("Event Contact response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSuccess()

                    else
                        mView.onFailed(response.body()?.message!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<EventContactPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }

}