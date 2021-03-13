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
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.fragments.forgotpassword.pojos.ForgotPasswordPojo
import com.dazzlerr_usa.views.fragments.forgotpassword.presenters.ResetPasswordPresenter
import com.dazzlerr_usa.views.fragments.forgotpassword.views.ResetPasswordView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import java.util.regex.Matcher
import java.util.regex.Pattern


class ResetPasswordModel(var mContext: Context, var mView: ResetPasswordView) : ResetPasswordPresenter
{
    var mServices  : Call<ForgotPasswordPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(mServices!=null)
            mServices?.cancel()
    }

    override fun restPasswordValidate(context: Context, newPassword: String, confirmPassword: String) {

        if (newPassword.length == 0 && confirmPassword.length == 0) {
            val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Please fill all the fields.", Snackbar.LENGTH_SHORT).show()

        }

        else if (!isValidPassword(newPassword))
        {
            mView.onFailed("Please follow the instruction to set the password.")
        }

        else if (!newPassword.equals(confirmPassword, ignoreCase = true)) {
            val parentLayout = (context as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Password and confirm password doesn't matched.", Snackbar.LENGTH_SHORT).show()


        } else {
            mView.onValidate()
        }
    }


    //*****************************************************************
    fun isValidPassword(password: String): Boolean {

        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[A-Z]).{6,36}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)

        return matcher.matches()

    }


    override fun resetPassword(context: Context, user_id: String, password: String)
    {

      mView.showProgress(true)
        (mContext as MainActivity).getDeviceInfo()
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mServices = webServices.resetPassword("application/x-www-form-urlencoded"
                ,Constants.auth_key,user_id, password
                ,(mContext as MainActivity).sharedPreferences.getString(Constants.device_id).toString()
                ,(mContext as MainActivity).sharedPreferences.getString(Constants.DEVICE_BRAND).toString()
                , (mContext as MainActivity).sharedPreferences.getString(Constants.DEVICE_MODEL).toString()
                ,"Android"
                , (mContext as MainActivity).sharedPreferences.getString(Constants.DEVICE_VERSION).toString())

        APIHelper.enqueueWithRetry(mServices ,Constants.RETRY_COUNT ,object : Callback<ForgotPasswordPojo>
        {
            override fun onResponse(call: Call<ForgotPasswordPojo>, response: Response<ForgotPasswordPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("ResetPassword response: "+ responseStr)

                    if(response.body()?.status!!)
                    mView.onResetPasswordSuccess()

                    else
                        mView.onFailed(response.body()?.message!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showProgress(false)


            }

            override fun onFailure(call: Call<ForgotPasswordPojo>, t: Throwable) {
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