package com.onlinepoundstore.fragments.login

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
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.fragments.login.LoginPojo
import com.google.gson.GsonBuilder



class LoginModel(var mContext: Context , var mLoginView: LoginView) : LoginPresenter
{
   // var mView:LoginView  = mContext as LoginView


    override fun validate(password: String , username :String)
    {
        if (password.length == 0 || username.length == 0 )
        {
            showSnackbar("Please fill all the fields.")
        }
        else if (password.length < 5)
        {
            showSnackbar("Password should be atleast 5 digits long.")
        }
        else
        {
            mLoginView.isLoginValidate(true)
        }
    }


    override fun login(email: String, password: String, device_id: String, device_brand: String, device_model: String, device_type: String, operating_system: String) {

        mLoginView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        val service = webServices.loginApi("application/x-www-form-urlencoded" ,Constants.auth_key, email , password , device_id , device_brand , device_model , device_type , operating_system, Constants.App_Version)
        APIHelper.enqueueWithRetry(service , Constants.RETRY_COUNT , object : Callback<LoginPojo>
        {
            override fun onResponse(call: Call<LoginPojo>, response: Response<LoginPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("Login response: "+ responseStr)

                    if(response.body()?.status!!)
                    mLoginView.onLoginSuccess(response.body() as LoginPojo)

                    else
                        mLoginView.onLoginFailed(response.body()?.message!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mLoginView.showProgress(false)


            }

            override fun onFailure(call: Call<LoginPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mLoginView.showProgress(false)
                mLoginView.onLoginFailed("Something went wrong! Please try again")
            }
        })

    }

    fun showSnackbar(message: String)
    {
        val parentLayout = (mContext as Activity).findViewById<View>(android.R.id.content)
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
    }
}