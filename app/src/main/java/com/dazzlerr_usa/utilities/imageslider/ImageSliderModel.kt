package com.dazzlerr_usa.utilities.imageslider

import android.content.Context
import android.widget.ImageView
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


class ImageSliderModel(val mContext: Context, val mView: ImageSliderView) : ImageSliderPresenter
{



     var likeOrDislikeservice :Call<ImageSliderPojo>?=null
    override fun cancelRetrofitRequest()
    {
        if(likeOrDislikeservice!=null)
            likeOrDislikeservice?.cancel()
    }


    override fun like(user_id: String, profile_id: String, status: String, portfolio_id: String, status_type: String ,position:Int , likeBtn : ImageView) {

        Timber.e(user_id+ "  "+ profile_id +"  "+status +" "+ portfolio_id +" "+ status_type)


        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        likeOrDislikeservice = webServices.likeOrDislikePortfolioApi("application/x-www-form-urlencoded"   ,Constants.auth_key ,user_id ,profile_id ,portfolio_id, status , status_type
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(likeOrDislikeservice ,Constants.RETRY_COUNT ,object : Callback<ImageSliderPojo>
        {
            override fun onResponse(call: Call<ImageSliderPojo>, response: Response<ImageSliderPojo>)
            {
                try
                {
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("LikeOrDislike response: "+ responseStr)

                    if(response.body()?.status!!)
                    {

                        mView.onLikeOrDislike(status, position , likeBtn)
                    }

                    else
                        mView.onFailed(response.body()?.success!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<ImageSliderPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later")
            }
        })

    }

    override fun heart(user_id: String, profile_id: String, status: String, portfolio_id: String, status_type: String ,position:Int , heartBtn : ImageView) {

        Timber.e(user_id+ "  "+ profile_id +"  "+status +" "+ portfolio_id +" "+ status_type)


        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        likeOrDislikeservice = webServices.likeOrDislikePortfolioApi("application/x-www-form-urlencoded"   ,Constants.auth_key ,user_id ,profile_id ,portfolio_id, status , status_type
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(likeOrDislikeservice ,Constants.RETRY_COUNT ,object : Callback<ImageSliderPojo>
        {
            override fun onResponse(call: Call<ImageSliderPojo>, response: Response<ImageSliderPojo>)
            {
                try
                {
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("LikeOrDislike response: "+ responseStr)

                    if(response.body()?.status!!)
                    {
                            mView.onHeartOrDisheart(status ,position , heartBtn)
                    }

                    else
                        mView.onFailed(response.body()?.success!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<ImageSliderPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later")
            }
        })

    }


}