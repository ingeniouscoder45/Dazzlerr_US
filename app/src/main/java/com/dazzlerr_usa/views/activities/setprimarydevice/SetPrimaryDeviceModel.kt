package com.dazzlerr_usa.views.activities.setprimarydevice

import android.app.Activity
import android.content.Context
import android.view.View
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class SetPrimaryDeviceModel(val mContext: Context, val mView:SetPrimaryDeviceView) : SetPrimaryDevicePresenter
{

    var reportService :Call<SetPrimaryDevicePojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(reportService!=null)
            reportService?.cancel()
    }


    override fun setPrimaryDevice(user_id: String, device_id: String, device_brand: String, device_model: String, device_type: String, operating_system: String) {

        mView.showProgress(true )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        reportService = webServices.setPrimaryDevice("application/x-www-form-urlencoded" ,Constants.auth_key , user_id, device_id , device_brand , device_model , device_type , operating_system)

        APIHelper.enqueueWithRetry(reportService ,Constants.RETRY_COUNT ,object : Callback<SetPrimaryDevicePojo>
        {
            override fun onResponse(call: Call<SetPrimaryDevicePojo>, response: Response<SetPrimaryDevicePojo>)
            {

                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("SetPrimaryDevice response: "+ responseStr)

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

            override fun onFailure(call: Call<SetPrimaryDevicePojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }

    override fun isOtpValidate(context: Context, otp: String) {

        if (otp.length == 0) {
            val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Please enter otp", Snackbar.LENGTH_SHORT).show()
        }
        else if (otp.length < 4) {
            val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Please enter a correct otp", Snackbar.LENGTH_SHORT).show()
        }
        else
            mView.onValidOtp()

    }


    override fun verifyDeviceOtp(context: Context, user_id: String, otp: String) {

        mView.showProgress(true )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)

        reportService = webServices.verifyDeviceOtp("application/x-www-form-urlencoded" ,Constants.auth_key , user_id, otp
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(reportService ,Constants.RETRY_COUNT ,object : Callback<SetPrimaryDevicePojo>
        {
            override fun onResponse(call: Call<SetPrimaryDevicePojo>, response: Response<SetPrimaryDevicePojo>)
            {

                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("Verify Device response: "+ responseStr)

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

            override fun onFailure(call: Call<SetPrimaryDevicePojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later")

            }


        })
    }
}