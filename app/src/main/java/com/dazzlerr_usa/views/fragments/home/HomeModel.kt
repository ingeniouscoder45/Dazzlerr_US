package com.dazzlerr_usa.views.fragments.home

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.fragments.home.pojos.HomeCategoryPojo
import com.dazzlerr_usa.views.fragments.home.pojos.ModelsPojo
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class HomeModel(val mContext: Context, val mView:HomeView) : HomePresenter
{

     var modelsService :Call<ModelsPojo>?=null
     var categoryService :Call<HomeCategoryPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(modelsService!=null)
        modelsService?.cancel()

        if(categoryService!=null)
        categoryService?.cancel()
    }

    override fun getHomeModels(user_id : String ,type : String, city: String , page:String)
    {

        mView.showProgress(true ,page)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        modelsService = webServices.getModelsApi("application/x-www-form-urlencoded" , Constants.Token ,Constants.auth_key , user_id,  city ,type ,page
                , DeviceInfoConstants.DEVICE_ID ,DeviceInfoConstants.DEVICE_BRAND ,DeviceInfoConstants.DEVICE_MODEL ,DeviceInfoConstants.DEVICE_TYPE ,DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(modelsService , Constants.RETRY_COUNT , object : Callback<ModelsPojo>
        {
            override fun onResponse(call: Call<ModelsPojo>, response: Response<ModelsPojo>)
            {
                try
                {
                    mView.showProgress(false ,page)
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("HomeModels response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onModelsSuccess(response.body() as ModelsPojo,type)

                    else
                        mView.onFailed(response.body()?.message!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }
            }

            override fun onFailure(call: Call<ModelsPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false ,page)
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")
                else
                {
                    (mContext as HomeActivity).stopProgressBarAnim()
                }
            }
        })

    }


    override fun getCategoriesProducts()
    {

        mView.showProgress(true ,""+1)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        categoryService = webServices.getCategoryApi()

        APIHelper.enqueueWithRetry(categoryService , Constants.RETRY_COUNT , object : Callback<HomeCategoryPojo>
        {
            override fun onResponse(call: Call<HomeCategoryPojo>, response: Response<HomeCategoryPojo>)
            {
                try {
                    mView.showProgress(false ,""+1)
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetCategories response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onGetCategory(response.body() as HomeCategoryPojo)

                    else
                        mView.onFailed(response.body()?.status!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }
            }

            override fun onFailure(call: Call<HomeCategoryPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false ,""+1)
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later")
                else
                {
                   // (mContext as DashboardActivity).stopProgressBarAnim()
                }
            }


        })

    }


}