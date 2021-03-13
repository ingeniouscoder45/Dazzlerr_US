package com.dazzlerr_usa.views.activities.changepassword

import android.app.Activity
import android.content.Context
import com.google.android.material.snackbar.Snackbar
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
import com.google.gson.GsonBuilder
import java.util.regex.Matcher
import java.util.regex.Pattern


class ChangePasswordModel(var mContext: Context, var mView: ChangePasswordView) : ChangePasswordPresenter
{
     var forgotpasswordService :  Call<ChangePasswordPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(forgotpasswordService!=null)
        forgotpasswordService?.cancel()
    }

    override fun validate(old_password :String ,newPassword:String , confirmPassword:String)
    {
        if (old_password.length == 0 || newPassword.length == 0 || confirmPassword.length == 0 )
        {
            showSnackbar("Please fill all the fields.")
        }

        else if (old_password.length == 0)
        {
            showSnackbar("Please enter your old password.")
        }

        else if (newPassword.length == 0)
        {
            showSnackbar("Please enter your new password.")
        }

        else if (confirmPassword.length == 0)
        {
            showSnackbar("Please enter your confirm password.")
        }
        else if (!isValidPassword(newPassword))
        {
            showSnackbar("Please follow the instruction to set the password.")
        }
        else if(!newPassword.equals(confirmPassword , ignoreCase = true))
        {
            showSnackbar("Password doesn't matched.")
        }
        else
        {
            mView.isValidate(true)
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

    override fun changePassword(old_password :String ,user_id:String , password: String)
    {
        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        forgotpasswordService = webServices.changePasswordApi("application/x-www-form-urlencoded",Constants.auth_key , user_id, old_password, password
                ,(mContext as ChangePasswordActivity).sharedPreferences.getString(DeviceInfoConstants.DEVICE_TOKEN).toString()
                ,(mContext as ChangePasswordActivity).sharedPreferences.getString(Constants.device_id).toString()
                ,(mContext as ChangePasswordActivity).sharedPreferences.getString(Constants.DEVICE_BRAND).toString()
                , (mContext as ChangePasswordActivity).sharedPreferences.getString(Constants.DEVICE_MODEL).toString()
                ,"Android", (mContext as ChangePasswordActivity).sharedPreferences.getString(Constants.DEVICE_VERSION).toString())

        APIHelper.enqueueWithRetry(forgotpasswordService ,Constants.RETRY_COUNT, object : Callback<ChangePasswordPojo>
        {
            override fun onResponse(call: Call<ChangePasswordPojo>, response: Response<ChangePasswordPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("Change Password response: "+ responseStr)

                    if(response.body()?.status!!)
                    mView.onChangePasswordSuccess()

                    else
                        mView.onChangePasswordFailed(response.body()?.message!!)


                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onChangePasswordFailed("Something went wrong! Please try again later.")
                }

                mView.showProgress(false)

            }

            override fun onFailure(call: Call<ChangePasswordPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false)
                mView.onChangePasswordFailed("Something went wrong! Please try again")
            }
        })
    }

    fun showSnackbar(message: String)
    {
        var snackbar:Snackbar
        val parentLayout = (mContext as Activity).findViewById<View>(android.R.id.content)
        snackbar  = Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT)
        snackbar.show()
    }

}