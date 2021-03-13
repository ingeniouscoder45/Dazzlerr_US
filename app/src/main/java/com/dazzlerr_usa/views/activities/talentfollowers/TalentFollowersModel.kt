package com.dazzlerr_usa.views.activities.talentfollowers

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.fragments.profile.pojo.FollowOrUnfollowPojo
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class TalentFollowersModel(val mContext: Context, val mView:TalentFollowersView) : TalentFollowersPresenter
{
    lateinit var modelsService :Call<TalentFollowersPojo>
    lateinit var followUnfollowService :Call<FollowOrUnfollowPojo>

    override fun cancelRetrofitRequest()
    {
        if(modelsService!=null)
        modelsService.cancel()

        if(followUnfollowService!=null)
        followUnfollowService.cancel()
    }

    override fun getFollowers(user_id: String, page: String, isShowProgressbar: Boolean) {

        mView.showProgress(true ,isShowProgressbar )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        modelsService = webServices.getTalentFollowersApi("application/x-www-form-urlencoded" , Constants.auth_key,user_id  ,page
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(modelsService ,Constants.RETRY_COUNT ,object : Callback<TalentFollowersPojo>
        {
            override fun onResponse(call: Call<TalentFollowersPojo>, response: Response<TalentFollowersPojo>)
            {
                try
                {
                    mView.showProgress(false ,isShowProgressbar )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("Followers response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onGetFollowersSuccess(response.body() as TalentFollowersPojo)

                    else
                        mView.onFailed(response.body()?.message!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<TalentFollowersPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false ,isShowProgressbar )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }

    override fun followOrUnfollow(user_id: String, profile_id: String, status: String ,position:Int)
    {
        Timber.e(user_id+ "  "+ profile_id +"  "+status)
        mView.showProgress(true , true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        followUnfollowService = webServices.followOrUnfollowApi("application/x-www-form-urlencoded"    ,Constants.auth_key,user_id ,profile_id , status
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(followUnfollowService ,Constants.RETRY_COUNT ,object : Callback<FollowOrUnfollowPojo>
        {
            override fun onResponse(call: Call<FollowOrUnfollowPojo>, response: Response<FollowOrUnfollowPojo>)
            {
                try
                {
                    mView.showProgress(false ,true )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("followOrUnfollow response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onFollowOrUnfollow(status ,position)

                    else
                        mView.onFailed(response.body()?.success!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<FollowOrUnfollowPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false   ,true)
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later")
            }
        })

    }

}