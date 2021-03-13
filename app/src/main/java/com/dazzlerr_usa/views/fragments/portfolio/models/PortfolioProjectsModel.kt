package com.dazzlerr_usa.views.fragments.portfolio.models

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.portfolio.PortfolioActivity
import com.dazzlerr_usa.views.fragments.portfolio.pojos.DeletePojo
import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioProjectsPojo
import com.dazzlerr_usa.views.fragments.portfolio.presenters.PortfolioProjectsPresenter
import com.dazzlerr_usa.views.fragments.portfolio.views.PortfolioProjectsView
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class PortfolioProjectsModel(val mContext: Context, val mView: PortfolioProjectsView) : PortfolioProjectsPresenter
{

    var projectsService :Call<PortfolioProjectsPojo>?=null
    var projectsDeleteService :Call<DeletePojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(projectsService!=null)
        projectsService?.cancel()

        if(projectsDeleteService!=null)
            projectsDeleteService?.cancel()

    }

    override fun getPortfolioProjects(user_id: String , page:String)
    {

        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        projectsService = webServices.getPortfolioProjects("application/x-www-form-urlencoded" ,Constants.auth_key, user_id  ,page
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(projectsService ,Constants.RETRY_COUNT ,object : Callback<PortfolioProjectsPojo>
        {
            override fun onResponse(call: Call<PortfolioProjectsPojo>, response: Response<PortfolioProjectsPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("PortfolioProjects response: "+ responseStr)

                    if(response.body()!!.success)
                        mView.onSuccess(response.body() as PortfolioProjectsPojo)

                    else
                        mView.onFailed(response.body()!!.status.toString())

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<PortfolioProjectsPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")
                else
                {
                    if(mContext is PortfolioActivity)
                    (mContext as PortfolioActivity).stopProgressBarAnim()
                }
            }

        })
    }

    override fun deleteProject(user_id: String ,project_id: String, position:Int) {

        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        projectsDeleteService = webServices.deletePortfolioProject("application/x-www-form-urlencoded" ,Constants.auth_key , user_id  ,project_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)
        APIHelper.enqueueWithRetry(projectsDeleteService ,Constants.RETRY_COUNT ,object : Callback<DeletePojo>
        {
            override fun onResponse(call: Call<DeletePojo>, response: Response<DeletePojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("DeleteProject response: "+ responseStr)

                    if(response.body()!!.success)
                        mView.onProjectDelete(position)

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