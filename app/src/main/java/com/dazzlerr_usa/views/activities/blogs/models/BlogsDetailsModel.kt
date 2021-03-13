package com.dazzlerr_usa.views.activities.blogs.models

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.blogs.pojos.BlogDetailsPojo
import com.dazzlerr_usa.views.activities.blogs.pojos.BlogListPojo
import com.dazzlerr_usa.views.activities.blogs.presenter.BlogsDetailsPresenter
import com.dazzlerr_usa.views.activities.blogs.views.BlogDetailsView
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class BlogsDetailsModel(val mContext: Context, val mView:BlogDetailsView) : BlogsDetailsPresenter
{
    var mBlogsService :Call<BlogDetailsPojo>?=null
    var mSimilarBlogsService :Call<BlogListPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(mBlogsService!=null)
            mBlogsService?.cancel()

        if(mSimilarBlogsService!=null)
            mSimilarBlogsService?.cancel()
    }

    override fun getBlogsDetails(blog_id: String)
    {

        mView.showProgress(true)
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mBlogsService = webServices.getBlogDetailsApi("application/x-www-form-urlencoded"   ,Constants.auth_key ,blog_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(mBlogsService ,Constants.RETRY_COUNT ,object : Callback<BlogDetailsPojo>
        {
            override fun onResponse(call: Call<BlogDetailsPojo>, response: Response<BlogDetailsPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("BlogsDetails response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSuccess(response.body() as BlogDetailsPojo)

                    else
                        mView.onFailed(response.body()?.message!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<BlogDetailsPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false)
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }


    override fun getSimilarBlogs(cat_id: String)
    {

        mView.showProgress(false )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)

        mSimilarBlogsService = webServices.getSimilarBlogsApi("application/x-www-form-urlencoded"   ,Constants.auth_key ,cat_id, ""
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)


        APIHelper.enqueueWithRetry(mSimilarBlogsService ,Constants.RETRY_COUNT ,object : Callback<BlogListPojo>
        {
            override fun onResponse(call: Call<BlogListPojo>, response: Response<BlogListPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("Similar Blogs response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onGetSimilarPostsSuccess(response.body() as BlogListPojo)

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