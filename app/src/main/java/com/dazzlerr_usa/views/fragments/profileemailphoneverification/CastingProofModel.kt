package com.dazzlerr_usa.views.fragments.profileemailphoneverification

import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
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
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.utilities.retrofit.ProgressRequestBody


class CastingProofModel(val mListener: ProgressRequestBody.UploadCallbacks, val mView: CastingProofView) : CastingProofPresenter
{

    var uploadCastingProofService :Call<CastingProofPojo>?=null
     var uploadCastingVideoProofService :Call<CastingProofPojo>?=null

    override fun cancelRetrofitRequest()
    {

        if(uploadCastingProofService!=null)
            uploadCastingProofService?.cancel()

        if(uploadCastingVideoProofService!=null)
            uploadCastingVideoProofService?.cancel()

    }

    override fun uploadCastingImageProof(user_id: String, proof: File, type: String)
    {

        mView.showProgress(true )

        val requestBody = RequestBody.create(MediaType.parse("*/*"), proof)
        val fileupload = MultipartBody.Part.createFormData("proof", proof.getName(), requestBody)
        val userid = RequestBody.create(MediaType.parse("text/plain"), user_id)
        val mType = RequestBody.create(MediaType.parse("text/plain"), type)
        val auth_key = RequestBody.create(MediaType.parse("text/plain"), Constants.auth_key)

        val device_id =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_ID)
        val device_brand =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_BRAND)
        val device_model =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_MODEL)
        val device_type =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_TYPE)
        val device_version =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_VERSION)


        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        uploadCastingProofService = webServices.uploadCastingProof(fileupload, auth_key , userid ,mType,device_id ,device_brand , device_model ,device_type , device_version)

        APIHelper.enqueueWithRetry(uploadCastingProofService ,Constants.RETRY_COUNT ,object : Callback<CastingProofPojo>
        {
            override fun onResponse(call: Call<CastingProofPojo>, response: Response<CastingProofPojo>)
            {
                try {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("UploadCastingProof response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onUpload()

                    else
                        mView.onFailed("Something went wrong! Please try again later")

                } catch (e: Exception) {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later.")
                }

            }

            override fun onFailure(call: Call<CastingProofPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false )
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later.")

            }


        })

    }

    override fun uploadCastingVideoProof(user_id: String, proof: File, type: String)
    {

        mView.showProgress(true )

       // val requestBody = RequestBody.create(MediaType.parse("image/*"), proof)
        //val fileupload = MultipartBody.Part.createFormData("proof", proof.getName(), requestBody)
        val userid = RequestBody.create(MediaType.parse("text/plain"), user_id)
        val mType = RequestBody.create(MediaType.parse("text/plain"), type)
        val auth_key = RequestBody.create(MediaType.parse("text/plain"), Constants.auth_key)

        val fileBody = ProgressRequestBody(proof, mListener, "*/*")
        val fileupload = MultipartBody.Part.createFormData("proof", proof.getName() + "", fileBody)

        val device_id =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_ID)
        val device_brand =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_BRAND)
        val device_model =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_MODEL)
        val device_type =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_TYPE)
        val device_version =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_VERSION)


        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        uploadCastingVideoProofService = webServices.uploadCastingProof(fileupload,auth_key, userid ,mType,device_id ,device_brand , device_model ,device_type , device_version)

        APIHelper.enqueueWithRetry(uploadCastingVideoProofService ,Constants.RETRY_COUNT ,object : Callback<CastingProofPojo>
        {
            override fun onResponse(call: Call<CastingProofPojo>, response: Response<CastingProofPojo>)
            {
                try {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("UploadCastingProof response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onUpload()

                    else
                        mView.onFailed("Something went wrong! Please try again later")

                } catch (e: Exception) {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later.")
                }

            }

            override fun onFailure(call: Call<CastingProofPojo>, t: Throwable) {

                Timber.e("Cause "+t.printStackTrace())
                if(t.message?.contains("Unable to resolve host")!!)
                {
                    mView.onFailed("No internet connection found!")
                }

                else {
                    Timber.e("Something went wrong " + t.message)
                    mView.showProgress(false)
                    if (!call.isCanceled)
                        mView.onFailed("Something went wrong! Please try again later.")
                }

            }


        })

    }
    fun streamFileType(file: File): String
    {

        val exsistingFileName = file.name
        if (exsistingFileName.endsWith(".jpg")) {
            return "image/jpg"
        }
        if (exsistingFileName.endsWith(".JPG")) {

            return "image/jpg"
        }
        if (exsistingFileName.endsWith(".png")) {

            return "image/png"

        }
        if (exsistingFileName.endsWith(".PNG")) {

            return "image/png"
        }
        if (exsistingFileName.endsWith(".gif")) {

            return "image/gif"

        }
        if (exsistingFileName.endsWith(".GIF")) {

            return "image/gif"

        }
        if (exsistingFileName.endsWith(".jpeg")) {

            return "image/jpeg"

        }
        if (exsistingFileName.endsWith(".JPEG")) {

            return "image/jpeg"

        }
        if (exsistingFileName.endsWith(".mp3")) {

            return "audio/mp3"

        }
        if (exsistingFileName.endsWith(".MP3")) {

            return "audio/mp3"

        }
        if (exsistingFileName.endsWith(".mp4")) {

            return "video/mp4"

        }
        if (exsistingFileName.endsWith(".avi")) {
            return "video/.avi"

        }
        if (exsistingFileName.endsWith(".ogg")) {

            return "video/ogg"

        }
        return if (exsistingFileName.endsWith(".3gp")) {

            "video/3gp"
        } else ""

    }
}