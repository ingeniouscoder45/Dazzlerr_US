package com.dazzlerr_usa.views.fragments.talentdashboard

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class TalentDashboardModel(val mContext: Context, val mView:TalentDashboardView) : TalentDashboardPresenter
{

     var modelsService :Call<TalentDashboardPojo> ? = null
     var activityService :Call<ActivitySummaryPojo> ? = null

    override fun cancelRetrofitRequest()
    {
        if(modelsService!=null)
        modelsService?.cancel()
        if(activityService!=null)
            activityService?.cancel()
    }

    override fun getTalentDashboard(user_id: String) {

        mView.showProgress(true )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        modelsService = webServices.getTalentDashboardApi("application/x-www-form-urlencoded" , Constants.auth_key,user_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(modelsService ,Constants.RETRY_COUNT , object : Callback<TalentDashboardPojo>
        {
            override fun onResponse(call: Call<TalentDashboardPojo>, response: Response<TalentDashboardPojo>)
            {
                try
                {
                    mView.showProgress(false  )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("Talents response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onGetDashboardData(response.body() as TalentDashboardPojo)

                    else
                        mView.onFailed(response.body()?.status!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<TalentDashboardPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")
                else
                {
                    (mContext as HomeActivity).stopProgressBarAnim()
                }
            }


        })

    }

    override fun getTalentActivitySummary(user_id: String, type: String, sholdShowProgressBar : Boolean) {

        mView.showProgress(true  ,sholdShowProgressBar)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        activityService = webServices.getTalentActivitySummaryApi("application/x-www-form-urlencoded" , Constants.auth_key,user_id ,type
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(activityService ,Constants.RETRY_COUNT , object : Callback<ActivitySummaryPojo>
        {
            override fun onResponse(call: Call<ActivitySummaryPojo>, response: Response<ActivitySummaryPojo>)
            {
                try
                {
                    mView.showProgress(false ,sholdShowProgressBar  )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("TalentsActivity response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onGetTalentActivitySummary(response.body() as ActivitySummaryPojo ,type)

                    else
                        mView.onFailed(response.body()?.message!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<ActivitySummaryPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false ,sholdShowProgressBar  )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")
                else
                {
                    (mContext as HomeActivity).stopProgressBarAnim()
                }
            }


        })

    }

}