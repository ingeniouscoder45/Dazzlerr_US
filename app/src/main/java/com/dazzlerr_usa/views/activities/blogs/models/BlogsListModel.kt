package com.dazzlerr_usa.views.activities.blogs.models

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.blogs.pojos.BlogListPojo
import com.dazzlerr_usa.views.activities.blogs.presenter.BlogsListPresenter
import com.dazzlerr_usa.views.activities.blogs.views.BlogListView
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class BlogsListModel(val mContext: Context, val mView:BlogListView) : BlogsListPresenter
{
    var mBlogsService :Call<BlogListPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(mBlogsService!=null)
            mBlogsService?.cancel()
    }

    override fun getBlogslist(type: Int , cat_id : String , page:String)// Type = 1 for featured, 2 for Popular and 3 for recent Posts
    {

        mView.showProgress(true )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)

        if(type==1)
            mBlogsService = webServices.getFeaturedBlogsApi("application/x-www-form-urlencoded"   ,Constants.auth_key,page
                    , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                    , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                    , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        else if(type==2)
            mBlogsService = webServices.getPopularBlogsApi("application/x-www-form-urlencoded"   ,Constants.auth_key ,page
                    , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                    , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                    , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        else if(type==3)
            mBlogsService = webServices.getRecentBlogsApi("application/x-www-form-urlencoded"   ,Constants.auth_key ,page
                    , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                    , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                    , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        else if(type==4)
            mBlogsService = webServices.getSimilarBlogsApi("application/x-www-form-urlencoded"   ,Constants.auth_key ,cat_id ,page
                    , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                    , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                    , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(mBlogsService ,Constants.RETRY_COUNT ,object : Callback<BlogListPojo>
        {
            override fun onResponse(call: Call<BlogListPojo>, response: Response<BlogListPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e(""+type+" Blogs response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSuccess(response.body() as BlogListPojo ,type)

                    else
                        mView.onFailed(response.body()?.message!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<BlogListPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false)
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }

}