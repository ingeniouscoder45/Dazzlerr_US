package com.dazzlerr_usa.views.activities.jobfilter

import android.content.Context
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.google.gson.GsonBuilder
import com.onlinepoundstore.fragments.login.JobFilterView


class JobFilterModel(var mContext: Context, var mView: JobFilterView) : JobFilterPresenter
{

    var citiesService  : Call<JobFilterPojo>?=null

    override fun cancelRetrofitRequest()
    {


        if(citiesService!=null)
            citiesService?.cancel()
    }


    override fun getJobCities() {


        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        citiesService = webServices.getJobCities("application/x-www-form-urlencoded")

        APIHelper.enqueueWithRetry(citiesService ,Constants.RETRY_COUNT ,object : Callback<JobFilterPojo>
        {
            override fun onResponse(call: Call<JobFilterPojo>, response: Response<JobFilterPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetCities response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onGetJobCities(response.body() as JobFilterPojo)

                    else
                        mView.onFailed(response.body()?.message!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showProgress(false)


            }

            override fun onFailure(call: Call<JobFilterPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled) {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again later")
                }
            }
        })

    }

}