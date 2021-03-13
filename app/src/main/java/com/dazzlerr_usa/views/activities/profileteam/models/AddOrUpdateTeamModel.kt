package com.dazzlerr_usa.views.activities.profileteam.models

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.profileteam.pojos.AddOrUpdatePojo
import com.dazzlerr_usa.views.activities.profileteam.presenter.AddOrUpdateTeamPresenter
import com.dazzlerr_usa.views.activities.profileteam.views.AddOrUpdateTeamView
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File

class AddOrUpdateTeamModel(val mContext: Context, val mView: AddOrUpdateTeamView) : AddOrUpdateTeamPresenter
{

     var updateTeamService :Call<AddOrUpdatePojo> ? = null
     var addTeamService :Call<AddOrUpdatePojo> ? = null


    override fun validateTeamMember(name: String, role: String, isImageUploaded: Boolean) {
        if(name.length==0)
        {
            mView.onFailed("Please enter your member's name")
        }

        else if(role.length==0)
        {
            mView.onFailed("Please enter your member's role")
        }

        else if(isImageUploaded==false)
        {
            mView.onFailed("Please upload your member's profile pic")
        }

        else
            mView.onValidateTeamMember()
    }
    override fun cancelRetrofitRequest()
    {
        if(addTeamService!=null)
            addTeamService?.cancel()

        if(updateTeamService!=null)
            updateTeamService?.cancel()
    }

    override fun addTeamMember(user_id: String, name: String, role: String, image: File)

    {

        mView.showProgress(true )
        var fileupload:MultipartBody.Part?=null

        val requestBody = RequestBody.create(MediaType.parse("image/*"), image)
        fileupload = MultipartBody.Part.createFormData("image", image.getName(), requestBody)
        val userid = RequestBody.create(MediaType.parse("text/plain"), user_id)
        val name = RequestBody.create(MediaType.parse("text/plain"), name)
        val role = RequestBody.create(MediaType.parse("text/plain"), role)
        val auth_key = RequestBody.create(MediaType.parse("text/plain"), Constants.auth_key)

        val device_id =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_ID)
        val device_brand =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_BRAND)
        val device_model =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_MODEL)
        val device_type =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_TYPE)
        val device_version =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_VERSION)



        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        addTeamService = webServices.addTeamMemberApi(fileupload!!,auth_key, userid , name , role,device_id ,device_brand , device_model ,device_type , device_version )

        APIHelper.enqueueWithRetry(addTeamService , Constants.RETRY_COUNT , object : Callback<AddOrUpdatePojo>
        {
            override fun onResponse(call: Call<AddOrUpdatePojo>, response: Response<AddOrUpdatePojo>)
            {
                try {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("UpdateProfileResponse response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onTeamAddOrUpdate()

                    else
                        mView.onFailed(response.body()?.message!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later.")
                }

            }

            override fun onFailure(call: Call<AddOrUpdatePojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false )
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later.")
            }
        })

    }


    override fun updateTeamMember(user_id: String, team_member_id: String, name: String, role: String, image: File) {


        mView.showProgress(true )
        var fileupload:MultipartBody.Part?=null


        if(image!=null)
        {
            val requestBody = RequestBody.create(MediaType.parse("image/*"), image)
            fileupload = MultipartBody.Part.createFormData("image", image.getName(), requestBody)
        }

        val userid = RequestBody.create(MediaType.parse("text/plain"), user_id)
        val name = RequestBody.create(MediaType.parse("text/plain"), name)
        val role = RequestBody.create(MediaType.parse("text/plain"), role)
        val auth_key = RequestBody.create(MediaType.parse("text/plain"), Constants.auth_key)

        val device_id =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_ID)
        val device_brand =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_BRAND)
        val device_model =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_MODEL)
        val device_type =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_TYPE)
        val device_version =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_VERSION)



        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        addTeamService = webServices.addTeamMemberApi(fileupload!!,auth_key, userid , name , role,device_id ,device_brand , device_model ,device_type , device_version )

        APIHelper.enqueueWithRetry(addTeamService , Constants.RETRY_COUNT , object : Callback<AddOrUpdatePojo>
        {
            override fun onResponse(call: Call<AddOrUpdatePojo>, response: Response<AddOrUpdatePojo>)
            {
                try {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("UpdateProfileResponse response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onTeamAddOrUpdate()

                    else
                        mView.onFailed(response.body()?.message!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later.")
                }

            }

            override fun onFailure(call: Call<AddOrUpdatePojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false )
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later.")
            }
        })

    }


}