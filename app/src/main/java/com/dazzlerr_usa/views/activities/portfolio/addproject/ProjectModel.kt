package com.dazzlerr_usa.views.activities.portfolio.addproject

import android.content.Context
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.Utils
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.google.gson.GsonBuilder


class ProjectModel(var mContext: Context, var mView: ProjectView) : ProjectPresenter
{

    var AddProjectServices  : Call<ProjectPojo>?=null
    var EditProjectServices  : Call<ProjectPojo>?=null
    override fun cancelRetrofitRequest()
    {
        if(AddProjectServices!=null)
            AddProjectServices?.cancel()

        if(EditProjectServices!=null)
            EditProjectServices?.cancel()
    }


    override fun validate(project_title: String, project_role: String, project_start_date: String, project_end_date: String, project_description: String) {

        if (project_title.length == 0 || project_role.length == 0 || project_description.length==0)
        {
            mView.onFailed("Please fill all the fields.")
        }

        else if(project_start_date.length == 0)
        {
            mView.onFailed("Please select start date.")
        }

        else if(project_end_date.length == 0)
        {
            mView.onFailed("Please select end date.")
        }

        else if(project_start_date.isNotEmpty()&& Utils.compareDate(project_start_date , project_end_date))
        {
            mView.onFailed("Please select a end date of the project after the start date")
        }
        else
            return mView.isValidate()

    }


    override fun addProject(user_id: String, project_title: String, project_role: String, project_start_date: String, project_end_date: String, project_description: String) {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        AddProjectServices = webServices.addProject("application/x-www-form-urlencoded" ,Constants.auth_key ,user_id, project_title, project_role, project_start_date, project_end_date, project_description
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(AddProjectServices ,Constants.RETRY_COUNT ,object : Callback<ProjectPojo>
        {
            override fun onResponse(call: Call<ProjectPojo>, response: Response<ProjectPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("AddProject response: "+ responseStr)

                    if(response.body()?.success!!)
                    mView.onSuccess()

                    else
                        mView.onFailed(response.body()?.status!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showProgress(false)


            }

            override fun onFailure(call: Call<ProjectPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled)
                {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again")
                }
            }
        })

    }

    override fun updateProject(user_id: String, project_id: String, project_title: String, project_role: String, project_start_date: String, project_end_date: String, project_description: String) {


        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        EditProjectServices = webServices.updateProject("application/x-www-form-urlencoded"  ,Constants.auth_key,user_id, project_id, project_title, project_role, project_start_date, project_end_date, project_description
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)
        APIHelper.enqueueWithRetry(EditProjectServices ,Constants.RETRY_COUNT ,object : Callback<ProjectPojo>
        {
            override fun onResponse(call: Call<ProjectPojo>, response: Response<ProjectPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("UpdateProject response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onSuccess()

                    else
                        mView.onFailed(response.body()?.status!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showProgress(false)


            }

            override fun onFailure(call: Call<ProjectPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled)
                {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again")
                }
            }
        })

    }

}