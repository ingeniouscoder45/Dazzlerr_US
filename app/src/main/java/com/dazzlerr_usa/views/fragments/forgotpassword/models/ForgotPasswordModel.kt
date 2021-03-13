package com.dazzlerr_usa.views.fragments.forgotpassword.models

import android.app.Activity
import android.content.Context
import android.util.Patterns
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
import com.dazzlerr_usa.views.fragments.forgotpassword.pojos.ForgotPasswordByQuestionPojo
import com.dazzlerr_usa.views.fragments.forgotpassword.pojos.ForgotPasswordPojo
import com.dazzlerr_usa.views.fragments.forgotpassword.presenters.ForgotPasswordPresenter
import com.dazzlerr_usa.views.fragments.forgotpassword.views.ForgotPasswordView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder


class ForgotPasswordModel(var mContext: Context, var mView: ForgotPasswordView) : ForgotPasswordPresenter
{

    var mServices  : Call<ForgotPasswordPojo>?=null
    var mSecurityQuestionServices  : Call<ForgotPasswordByQuestionPojo>?=null

    override fun cancelRetrofitRequest() {

        if(mServices!=null)
            mServices?.cancel()

        if(mSecurityQuestionServices!=null)
            mSecurityQuestionServices?.cancel()
    }


    override fun verifyAccountValidate(context: Context, emailOrPhone: String, type: String)
    {
        if (type.equals("mobile", ignoreCase = true)) {
            if (emailOrPhone.length == 0) {
                val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
                Snackbar.make(parentLayout, "Please enter your phone number.", Snackbar.LENGTH_SHORT).show()
            }
            else if (emailOrPhone.length >10 || emailOrPhone.length<10)
            {
                val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
                Snackbar.make(parentLayout, "Please enter a valid phone number.", Snackbar.LENGTH_SHORT)
                        .setAction("", null).show()
            }
            else if (!Patterns.PHONE.matcher(emailOrPhone).matches()) {
                val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
                Snackbar.make(parentLayout, "Please enter a valid phone number.", Snackbar.LENGTH_SHORT)
                        .setAction("", null).show()
            }

            else
                mView.onValidate()
        } else {
            if (emailOrPhone.length == 0) {
                val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
                Snackbar.make(parentLayout, "Please enter your email id.", Snackbar.LENGTH_SHORT).show()


            } else if (!Patterns.EMAIL_ADDRESS.matcher(emailOrPhone).matches()) {
                val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
                Snackbar.make(parentLayout, "Please enter a valid email id.", Snackbar.LENGTH_SHORT)
                        .setAction("", null).show()


            } else
                mView.onValidate()

        }
    }


    override fun ForgotPasswordByPhoneOrEmail(context: Context, email: String, Phone: String, email_type: String)
    {
        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mServices = webServices.forgotPasswordByEmailOrPhone("application/x-www-form-urlencoded"  ,Constants.auth_key,email, Phone ,email_type
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)// This api is using for forgot password as well as Account verification

        APIHelper.enqueueWithRetry(mServices ,Constants.RETRY_COUNT ,object : Callback<ForgotPasswordPojo>
        {

            override fun onResponse(call: Call<ForgotPasswordPojo>, response: Response<ForgotPasswordPojo>)
            {
                try
                {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("ForgotPasswordByOtp response: "+ responseStr)

                    if(response.body()?.status!!)
                    mView.onOtpSendSuccess()

                    else
                        mView.onFailed(response.body()?.message!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showProgress(false)


            }

            override fun onFailure(call: Call<ForgotPasswordPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

/*                if(!call.isCanceled)
                {
                    try {
                        mView.showProgress(false)
                        mView.onFailed("Something went wrong! Please try again")
                    }
                    catch(e: Exception)
                    {
                        e.printStackTrace()
                    }
                }*/
            }
        })

    }


    override fun ForgotPasswordByQuestion(context: Context, email: String) {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mSecurityQuestionServices = webServices.forgotPasswordByQuestion("application/x-www-form-urlencoded" ,Constants.auth_key ,email
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)
        APIHelper.enqueueWithRetry(mSecurityQuestionServices ,Constants.RETRY_COUNT ,object : Callback<ForgotPasswordByQuestionPojo>
        {
            override fun onResponse(call: Call<ForgotPasswordByQuestionPojo>, response: Response<ForgotPasswordByQuestionPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("ForgotPasswordByQuestion response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onGetQuestionSuccess(response.body() as ForgotPasswordByQuestionPojo)

                    else
                        mView.onFailed(response.body()?.message!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showProgress(false)


            }

            override fun onFailure(call: Call<ForgotPasswordByQuestionPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled)
                {
                    try {
                        mView.showProgress(false)
                        mView.onFailed("Something went wrong! Please try again")
                    }
                    catch(e: Exception)
                    {
                        e.printStackTrace()
                    }
                }
            }
        })


    }
}