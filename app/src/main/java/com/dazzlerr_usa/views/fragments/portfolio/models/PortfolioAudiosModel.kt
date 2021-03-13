package com.dazzlerr_usa.views.fragments.portfolio.models

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.portfolio.PortfolioActivity
import com.dazzlerr_usa.views.fragments.portfolio.pojos.*
import com.dazzlerr_usa.views.fragments.portfolio.presenters.PortfolioAudiosPresenter
import com.dazzlerr_usa.views.fragments.portfolio.views.PortfolioAudiosView
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

class PortfolioAudiosModel(val mContext: Context, val mView: PortfolioAudiosView) : PortfolioAudiosPresenter
{


    var documentService :Call<PortfolioAudiosPojo>?= null
    var documentDeleteService :Call<DeletePojo>?=null
    var uploadAudioService :Call<UploadAudioPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(documentService!=null)
            documentService?.cancel()

        if(documentDeleteService!=null)
            documentDeleteService?.cancel()

       if(uploadAudioService!=null)
           uploadAudioService?.cancel()

    }

    override fun getPortfolioAudios(user_id: String, page:String)
    {

        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        documentService = webServices.getPortfolioAudios("application/x-www-form-urlencoded" ,Constants.auth_key , user_id  ,page
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(documentService ,Constants.RETRY_COUNT ,object : Callback<PortfolioAudiosPojo>
        {
            override fun onResponse(call: Call<PortfolioAudiosPojo>, response: Response<PortfolioAudiosPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("PortfolioAudioss response: "+ responseStr)

                    if(response.body()!!.success)
                        mView.onSuccess(response.body() as PortfolioAudiosPojo)

                    else
                        mView.onFailed(response.body()!!.status.toString())

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<PortfolioAudiosPojo>, t: Throwable)
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

    override fun deleteAudio(user_id: String, audio_id: String, position:Int)
    {

        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        documentDeleteService = webServices.deletePortfolioAudio("application/x-www-form-urlencoded" ,Constants.auth_key ,  user_id  ,audio_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(documentDeleteService ,Constants.RETRY_COUNT ,object : Callback<DeletePojo>
        {
            override fun onResponse(call: Call<DeletePojo>, response: Response<DeletePojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("DeleteDocument response: "+ responseStr)

                    if(response.body()!!.success)
                        mView.onAudioDelete(position)

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

    override fun uploadAudio(user_id: String, audioFile: File ,audio_title:String)
    {

        mView.showProgress(1 )

        val requestBody = RequestBody.create(MediaType.parse("*/*"), audioFile)
        val fileupload = MultipartBody.Part.createFormData("audio_url", audioFile.getName(), requestBody)
        val mUserid = RequestBody.create(MediaType.parse("text/plain"), user_id)
        val mAudio_title = RequestBody.create(MediaType.parse("text/plain"), audio_title)
        val auth_key = RequestBody.create(MediaType.parse("text/plain"), Constants.auth_key)


        val device_id =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_ID)
        val device_brand =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_BRAND)
        val device_model =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_MODEL)
        val device_type =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_TYPE)
        val device_version =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_VERSION)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        uploadAudioService = webServices.UploadPortfolioAudioApi(fileupload,auth_key ,  mUserid, mAudio_title  ,device_id ,device_brand , device_model ,device_type , device_version )

        APIHelper.enqueueWithRetry(uploadAudioService ,Constants.RETRY_COUNT ,object : Callback<UploadAudioPojo>
        {
            override fun onResponse(call: Call<UploadAudioPojo>, response: Response<UploadAudioPojo>)
            {
                try {
                    mView.showProgress(0 )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("UploadDocument response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onAudioUpload(response.body() as UploadAudioPojo)

                    else
                        mView.onFailed("Something went wrong! Please try again later")

                } catch (e: Exception) {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later.")
                }

            }

            override fun onFailure(call: Call<UploadAudioPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(0 )
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