package com.dazzlerr_usa.views.fragments.portfolio.models

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.portfolio.PortfolioActivity
import com.dazzlerr_usa.views.fragments.portfolio.pojos.DeletePojo
import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioVideosPojo
import com.dazzlerr_usa.views.fragments.portfolio.presenters.PortfolioVideosPresenter
import com.dazzlerr_usa.views.fragments.portfolio.views.PortfolioVideosView
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class PortfolioVideosModel(val mContext: Context, val mView:PortfolioVideosView) : PortfolioVideosPresenter
{
    var videosService :Call<PortfolioVideosPojo>?=null
    var deleteVideoService :Call<DeletePojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(videosService!=null)
        videosService?.cancel()

        if(deleteVideoService!=null)
            deleteVideoService?.cancel()
    }

    override fun getPortfolioVideos(user_id: String , page:String)
    {

        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        videosService = webServices.getPortfolioVideos("application/x-www-form-urlencoded" ,Constants.auth_key, user_id  ,page
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL , DeviceInfoConstants.DEVICE_TYPE , DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(videosService ,Constants.RETRY_COUNT ,object : Callback<PortfolioVideosPojo>
        {
            override fun onResponse(call: Call<PortfolioVideosPojo>, response: Response<PortfolioVideosPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("PortfolioVideos response: "+ responseStr)

                    if(response.body()!!.success)
                        mView.onSuccess(response.body() as PortfolioVideosPojo)

                    else
                        mView.onFailed(response.body()!!.status.toString())

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<PortfolioVideosPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")
                else
                {
                    (mContext as PortfolioActivity).stopProgressBarAnim()
                }
            }


        })

    }

    override fun deleteVideo(user_id: String, video_id: String, position: Int) {


        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        deleteVideoService = webServices.deletePortfolioVideo("application/x-www-form-urlencoded" ,Constants.auth_key,  user_id  ,video_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(deleteVideoService  ,Constants.RETRY_COUNT ,object : Callback<DeletePojo>
        {
            override fun onResponse(call: Call<DeletePojo>, response: Response<DeletePojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("DeleteVideo response: "+ responseStr)

                    if(response.body()!!.success)
                        mView.onVideoDelete(position)

                    else
                        mView.onFailed(response.body()!!.status.toString())

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<DeletePojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false )
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later")
                else
                {
                    (mContext as PortfolioActivity).stopProgressBarAnim()
                }
            }


        })

    }

}