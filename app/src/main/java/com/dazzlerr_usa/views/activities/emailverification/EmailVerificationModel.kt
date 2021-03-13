package com.dazzlerr_usa.views.activities.emailverification

import android.app.Activity
import android.content.Context
import android.util.Patterns
import android.widget.Toast
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
import com.google.gson.GsonBuilder


class EmailVerificationModel(var mContext: Context, var mView: EmailVerificationView) : EmailVerificationPresenter
{

    var mServices  : Call<ValidateOtpPojo>?=null

    override fun cancelRetrofitRequest() {

        if(mServices!=null)
            mServices?.cancel()

    }


    override fun isOtpValidate(otp: String) {

        if (otp.length == 0)
        {
            Toast.makeText( (mContext as Activity) , "Please enter otp" , Toast.LENGTH_LONG).show()

        }
        else if (otp.length < 4)
        {
            Toast.makeText( (mContext as Activity) , "Please enter a valid otp" , Toast.LENGTH_LONG).show()
        }
        else
            mView.onValidOtp()

    }

    override fun isEmailValidate(email: String) {
        if (email.length == 0)
        {
            Toast.makeText( (mContext as Activity) , "Please enter your email id" , Toast.LENGTH_LONG).show()

        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText( (mContext as Activity) , "Please enter a valid email id" , Toast.LENGTH_LONG).show()
        }
        else
            mView.onValidEmail(email )
    }


    override fun verifyEmailOtp(user_id : String , email: String, otp: String)
    {
        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mServices = webServices.verifyEmailOtp("application/x-www-form-urlencoded"  ,Constants.auth_key,user_id ,email, otp
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
                        Toast.makeText( (mContext as Activity) , response.body()?.message!!, Toast.LENGTH_LONG).show()

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

                    Toast.makeText( (mContext as Activity) , "Something went wrong! Please try again", Toast.LENGTH_LONG).show()

                }
            }
        })

    }

    override fun sendEmailOtp(user_id: String, email: String) {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mServices = webServices.sendEmailOtp("application/x-www-form-urlencoded"
                , Constants.auth_key,user_id, email
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

                    Timber.e("SendOtpOnEmail response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onOtpSentSuccess(email)

                    else
                        Toast.makeText( (mContext as Activity) , response.body()?.message!!, Toast.LENGTH_LONG).show()


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
                    Toast.makeText( (mContext as Activity) , "Something went wrong! Please try again", Toast.LENGTH_LONG).show()

                }
            }
        })


    }


}