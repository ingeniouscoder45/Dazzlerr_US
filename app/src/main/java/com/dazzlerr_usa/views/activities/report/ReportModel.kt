package com.dazzlerr_usa.views.activities.report

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

class ReportModel(val mContext: Context, val mView:ReportView) : ReportPresenter
{

    var reportService :Call<ReportPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(reportService!=null)
            reportService?.cancel()
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

        else if (phone.length >13 || phone.length<8)
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


    override fun reportUser(username: String, name: String, email: String, phone: String, subject: String, message: String)
    {

        mView.showProgress(true )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        reportService = webServices.profileReport("application/x-www-form-urlencoded" ,Constants.auth_key , username,name ,  email, phone, subject, message
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(reportService ,Constants.RETRY_COUNT ,object : Callback<ReportPojo>
        {
            override fun onResponse(call: Call<ReportPojo>, response: Response<ReportPojo>)
            {

                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("ReportProfile response: "+ responseStr)

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

            override fun onFailure(call: Call<ReportPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }

    override fun reportPortfolio(username: String, name: String, email: String, phone: String, subject: String, message: String , portfolio_url:String)
    {

        mView.showProgress(true )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        reportService = webServices.portfolioReport("application/x-www-form-urlencoded" ,Constants.auth_key , username,name ,  email, phone, subject, message,portfolio_url
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)


        APIHelper.enqueueWithRetry(reportService ,Constants.RETRY_COUNT ,object : Callback<ReportPojo>
        {
            override fun onResponse(call: Call<ReportPojo>, response: Response<ReportPojo>)
            {

                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("ReportPortfolio response: "+ responseStr)

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

            override fun onFailure(call: Call<ReportPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }

}