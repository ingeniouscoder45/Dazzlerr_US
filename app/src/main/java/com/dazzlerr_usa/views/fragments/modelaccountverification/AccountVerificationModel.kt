package com.dazzlerr_usa.views.fragments.modelaccountverification

import android.app.Activity
import android.content.Context
import android.view.View
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.fragments.forgotpassword.pojos.ValidateOtpPojo
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder


class AccountVerificationModel(var mContext: Context, var mView: AccountVerificationView) : AccountVerificationPresenter
{

    var mServices  : Call<ValidateOtpPojo>?=null

    override fun cancelRetrofitRequest() {

        if(mServices!=null)
            mServices?.cancel()

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


    override fun ForgotPasswordByEmail(context: Context, email: String, otp: String)
    {
        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mServices = webServices.validateOtpbyEmail("application/x-www-form-urlencoded"  ,Constants.auth_key,email, otp
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(mServices ,Constants.RETRY_COUNT ,object : Callback<ValidateOtpPojo>
        {
            override fun onResponse(call: Call<ValidateOtpPojo>, response: Response<ValidateOtpPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("ValidateOtp response: "+ responseStr)

                    if(response.body()?.status!!)
                    mView.onOtpValidateSuccess()

                    else
                        mView.onFailed(response.body()?.message!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showProgress(false)


            }

            override fun onFailure(call: Call<ValidateOtpPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled)
                {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again")
                }
            }
        })

    }

    override fun ForgotPasswordByPhone(context: Context, phone: String, otp: String ,type : String)
    {
        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mServices = webServices.validateOtpbyPhone("application/x-www-form-urlencoded"
                ,Constants.auth_key,phone, otp, type
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(mServices ,Constants.RETRY_COUNT ,object : Callback<ValidateOtpPojo>
        {
            override fun onResponse(call: Call<ValidateOtpPojo>, response: Response<ValidateOtpPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("ValidateOtp response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onOtpValidateSuccess()

                    else
                        mView.onFailed(response.body()?.message!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showProgress(false)


            }

            override fun onFailure(call: Call<ValidateOtpPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled)
                {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again")
                }
            }
        })

    }
}