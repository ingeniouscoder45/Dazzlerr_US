package com.dazzlerr_usa.views.fragments.forgotpassword.models

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
import com.dazzlerr_usa.views.fragments.forgotpassword.presenters.ValidateSecurityQuestionPresenter
import com.dazzlerr_usa.views.fragments.forgotpassword.views.ValidateSecurityQuestionView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder


class ValidateSecurityQuestionModel(var mContext: Context, var mView: ValidateSecurityQuestionView) : ValidateSecurityQuestionPresenter
{

    var mServices  : Call<ValidateOtpPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(mServices!=null)
            mServices?.cancel()
    }

    override fun isSecurityQuestionValidate(context: Context, answer: String) {

            if (answer.length == 0) {
                val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
                Snackbar.make(parentLayout, "Please enter your answer", Snackbar.LENGTH_SHORT).show()
            }
                else
                mView.onValidate()

    }

    override fun validateOtp(context: Context, question_id: String, email: String, answer: String) {
      mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mServices = webServices.validateQuestion("application/x-www-form-urlencoded" ,Constants.auth_key ,email, question_id ,answer
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

                    Timber.e("ValidateSecurityQuestion response: "+ responseStr)

                    if(response.body()?.status!!)
                    mView.onValidateSecurityQuestion(response.body()?.user_id!!)

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