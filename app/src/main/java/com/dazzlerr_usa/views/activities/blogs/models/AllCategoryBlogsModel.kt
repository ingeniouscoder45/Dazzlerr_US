package com.dazzlerr_usa.views.activities.blogs.models

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.blogs.pojos.AllCategoryBlogsPojo
import com.dazzlerr_usa.views.activities.blogs.pojos.BlogCategoriesPojo
import com.dazzlerr_usa.views.activities.blogs.presenter.AllCategoryBlogsPresenter
import com.dazzlerr_usa.views.activities.blogs.views.AllCategoryBlogsView
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class AllCategoryBlogsModel(val mContext: Context, val mView:AllCategoryBlogsView) : AllCategoryBlogsPresenter
{


    var mBlogsService :Call<AllCategoryBlogsPojo>?=null
    var mCategoriesService :Call<BlogCategoriesPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(mBlogsService!=null)
            mBlogsService?.cancel()

        if(mCategoriesService!=null)
            mCategoriesService?.cancel()
    }

    override fun getAllCategories(page: String)
    {
        mView.showProgress(true )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)

        mCategoriesService = webServices.getAllBlogCategoriesApi("application/x-www-form-urlencoded"   ,Constants.auth_key ,page
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(mCategoriesService ,Constants.RETRY_COUNT ,object : Callback<BlogCategoriesPojo>
        {
            override fun onResponse(call: Call<BlogCategoriesPojo>, response: Response<BlogCategoriesPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("AllBlogCategories response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onGetCategories(response.body() as BlogCategoriesPojo)

                    else
                        mView.onFailed(response.body()?.message!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<BlogCategoriesPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false)
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }
    override fun getAllCategoryBlogs(cat_id: String, tag_id: String, uname: String, page: String) {


        Timber.e(" CatID: "+cat_id+" TagID: "+tag_id+" uname: "+ uname +" page: "+page)
        mView.showProgress(true )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)

            mBlogsService = webServices.getAllCategoryBlogApi("application/x-www-form-urlencoded"   ,Constants.auth_key ,cat_id ,tag_id ,uname ,page
                    , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                    , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                    , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)


        APIHelper.enqueueWithRetry(mBlogsService ,Constants.RETRY_COUNT ,object : Callback<AllCategoryBlogsPojo>
        {
            override fun onResponse(call: Call<AllCategoryBlogsPojo>, response: Response<AllCategoryBlogsPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("AllCategoryBlogs response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSuccess(response.body() as AllCategoryBlogsPojo)

                    else
                        mView.onFailed(response.body()?.message!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<AllCategoryBlogsPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false)
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }

}