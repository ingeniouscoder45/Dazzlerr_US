package com.dazzlerr_usa.views.activities.editprofile.models

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.OthersProfileActivity
import com.dazzlerr_usa.views.activities.editprofile.presenters.UpdateProfileImagePresenter
import com.dazzlerr_usa.views.activities.editprofile.views.UpdateProfileImageView
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.fragments.profile.pojo.UploadProfilePicPojo
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import okhttp3.MediaType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File
import okhttp3.RequestBody
import okhttp3.MultipartBody





class UpdateProfileImageModel(val mContext: Context, val mView:UpdateProfileImageView) : UpdateProfileImagePresenter
{



     var uploadProfilePicService :Call<UploadProfilePicPojo>?=null


    override fun cancelRetrofitRequest()
    {
        if(uploadProfilePicService!=null)
            uploadProfilePicService?.cancel()
    }


    override fun uploadProfilePic(user_id: String, imageFile: File)
    {

        mView.showProgress(true )

        val requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
        val fileupload = MultipartBody.Part.createFormData("image", imageFile.getName(), requestBody)
        val userid = RequestBody.create(MediaType.parse("text/plain"), user_id)
        val auth_key = RequestBody.create(MediaType.parse("text/plain"), Constants.auth_key)

        val device_token =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_TOKEN)
        val device_id =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_ID)
        val device_brand =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_BRAND)
        val device_model =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_MODEL)
        val device_type =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_TYPE)
        val device_version =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_VERSION)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        uploadProfilePicService = webServices.editProfilePicApi(fileupload,auth_key, userid ,device_id ,device_brand , device_model ,device_type , device_version )

        APIHelper.enqueueWithRetry(uploadProfilePicService , Constants.RETRY_COUNT , object : Callback<UploadProfilePicPojo>
        {
            override fun onResponse(call: Call<UploadProfilePicPojo>, response: Response<UploadProfilePicPojo>)
            {
                try {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("UpdateProfileResponse response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onProfilePicUploaded(response.body()?.data!!)

                    else
                        mView.onFailed("Something went wrong! Please try again later")

                } catch (e: Exception) {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later.")
                }

            }

            override fun onFailure(call: Call<UploadProfilePicPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false )
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later.")
                else
                {
                    if(mContext is HomeActivity)
                        (mContext).stopProgressBarAnim()

                    else if(mContext is OthersProfileActivity)
                        (mContext).stopProgressBarAnim()
                }
            }


        })

    }

}