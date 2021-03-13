package com.dazzlerr_usa.views.activities.contact

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

class ContactModel(val mContext: Context, val mView:ContactView) : ContactPresenter
{

    var contactService :Call<ContactPojo>?=null

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

    override fun contact(membership_id : String , profile_id: String, user_id: String, name: String, email: String, phone: String, subject: String, message: String) {

        mView.showProgress(true )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        contactService = webServices.profileContact("application/x-www-form-urlencoded" ,Constants.auth_key ,membership_id , profile_id, user_id,name ,  email, phone, subject, message
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(contactService ,Constants.RETRY_COUNT ,object : Callback<ContactPojo>
        {
            override fun onResponse(call: Call<ContactPojo>, response: Response<ContactPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("contact response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onSuccess()

                    else
                        mView.onFailed(response.body()?.status!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<ContactPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }

}