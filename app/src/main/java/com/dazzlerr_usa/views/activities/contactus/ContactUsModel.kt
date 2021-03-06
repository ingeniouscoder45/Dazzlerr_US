package com.dazzlerr_usa.views.activities.contactus

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

class ContactUsModel(val mContext: Context, val mView:ContactUsView) : ContactUsPresenter
{

    var contactService :Call<PojoContactUs>?=null

    override fun cancelRetrofitRequest()
    {
        if(contactService!=null)
        contactService?.cancel()
    }

    override fun isValid(name: String, email: String, phone: String, subject: String, message: String)
    {

        if (name.length == 0 || email.length == 0 || phone.length == 0 || subject.length == 0|| message.length == 0)
        {
            mView.onFailed("Please fill all the fields.")
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            mView.onFailed("Please enter a valid email address.")
        }

        else if (phone.length >10 || phone.length<10)
        {
            mView.onFailed("Please enter a valid phone number.")
        }

        else if (!Patterns.PHONE.matcher(phone).matches())
        {
            mView.onFailed("Please enter a valid phone number.")
        }

        else
            mView.onValidate()
    }

    override fun contact(name: String, email: String, phone: String, subject: String, message: String)
    {

        mView.showProgress(true )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        contactService = webServices.contactUsApi("application/x-www-form-urlencoded" ,Constants.auth_key ,name ,  email, phone, subject, message
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(contactService ,Constants.RETRY_COUNT ,object : Callback<PojoContactUs>
        {
            override fun onResponse(call: Call<PojoContactUs>, response: Response<PojoContactUs>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("contactUs response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSuccess()

                    else
                        mView.onFailed(response.body()?.success!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<PojoContactUs>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }

}