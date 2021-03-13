package com.dazzlerr_usa.views.activities.profileteam.models

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.profileteam.pojos.AddOrUpdatePojo
import com.dazzlerr_usa.views.activities.profileteam.pojos.GetTeamPojo
import com.dazzlerr_usa.views.activities.profileteam.presenter.ProfileTeamPresenter
import com.dazzlerr_usa.views.activities.profileteam.views.ProfileTeamView
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class ProfileTeamModel(val mContext: Context, val mView: ProfileTeamView) : ProfileTeamPresenter
{

     var teamService :Call<GetTeamPojo> ? = null
     var addTeamService :Call<AddOrUpdatePojo> ? = null

    override fun cancelRetrofitRequest()
    {
        if(teamService!=null)
        teamService?.cancel()
    }

    override fun getTeamMembers(user_id: String, page: String, isShowProgressbar: Boolean)
    {

        mView.showProgress(true ,isShowProgressbar )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        teamService = webServices.getTeamApi("application/x-www-form-urlencoded" , Constants.auth_key,user_id  ,page
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(teamService ,Constants.RETRY_COUNT , object : Callback<GetTeamPojo>
        {
            override fun onResponse(call: Call<GetTeamPojo>, response: Response<GetTeamPojo>)
            {
                try
                {
                    mView.showProgress(false ,isShowProgressbar )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetTeam response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onGetTeamSuccess(response.body() as GetTeamPojo)

                    else
                        mView.onFailed(response.body()?.status!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<GetTeamPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false ,isShowProgressbar )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }



}