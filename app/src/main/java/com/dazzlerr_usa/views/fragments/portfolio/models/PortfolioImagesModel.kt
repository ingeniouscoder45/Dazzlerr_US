package com.dazzlerr_usa.views.fragments.portfolio.models

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.portfolio.PortfolioActivity
import com.dazzlerr_usa.views.fragments.portfolio.pojos.DeletePojo
import com.dazzlerr_usa.views.fragments.portfolio.presenters.PortfolioImagesPresenter
import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioImagesPojo
import com.dazzlerr_usa.views.fragments.portfolio.pojos.UploadImagePojo
import com.dazzlerr_usa.views.fragments.portfolio.views.PortfolioImagesView
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
import java.util.ArrayList

class PortfolioImagesModel(val mContext: Context, val mView:PortfolioImagesView) : PortfolioImagesPresenter
{

    var modelsService :Call<PortfolioImagesPojo>?= null
    var uploadImageService :Call<UploadImagePojo>?=null
    var deleteImageService :Call<DeletePojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(modelsService!=null)
            modelsService?.cancel()

        if(deleteImageService!=null)
            deleteImageService?.cancel()

        if(uploadImageService!=null)
            uploadImageService?.cancel()
    }

    override fun getPortfolioImages(user_id: String ,my_user_id:String , page:String,explore_portfolio :String )
    {
        Timber.e(user_id+ "  "+ my_user_id )
        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        modelsService = webServices.getPortfolioImages("application/x-www-form-urlencoded"
                ,Constants.auth_key, user_id  , my_user_id , explore_portfolio, page
                ,DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL , DeviceInfoConstants.DEVICE_TYPE , DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(modelsService ,Constants.RETRY_COUNT ,object : Callback<PortfolioImagesPojo>
        {
            override fun onResponse(call: Call<PortfolioImagesPojo>, response: Response<PortfolioImagesPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("PortfolioImages response: "+ responseStr)

                    if(response.body()!!.success)
                        mView.onSuccess(response.body() as PortfolioImagesPojo)

                    else
                        mView.onFailed(response.body()!!.status.toString())

                } catch (e: Exception) {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }
            }
            override fun onFailure(call: Call<PortfolioImagesPojo>, t: Throwable)
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

    override fun deleteImage(user_id: String, selectedItemsToDelete: ArrayList<String>)
    {

        Timber.e("Delete items ID: "+selectedItemsToDelete.toString())
        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        deleteImageService = webServices.deletePortfolioImage("application/x-www-form-urlencoded" ,Constants.auth_key ,  user_id  ,selectedItemsToDelete.toString().replace("[" , "").replace("]" ,"")
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(deleteImageService ,Constants.RETRY_COUNT ,object : Callback<DeletePojo>
        {
            override fun onResponse(call: Call<DeletePojo>, response: Response<DeletePojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("DeleteImage response: "+ responseStr)

                    if(response.body()!!.success)
                        mView.onImageDelete(selectedItemsToDelete)

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

    override fun uploadImage(user_id: String, imageFile: File)
    {

        mView.showProgress(1 )

        val requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
        val fileupload = MultipartBody.Part.createFormData("image", imageFile.getName(), requestBody)
        val userid = RequestBody.create(MediaType.parse("text/plain"), user_id)
        val auth_key = RequestBody.create(MediaType.parse("text/plain"), Constants.auth_key)

        val device_id =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_ID)
        val device_brand =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_BRAND)
        val device_model =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_MODEL)
        val device_type =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_TYPE)
        val device_version =RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_VERSION)


        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)

        uploadImageService = webServices.UploadPortfolioImageApi(fileupload, auth_key, userid ,device_id ,device_brand , device_model ,device_type , device_version)

        APIHelper.enqueueWithRetry(uploadImageService ,Constants.RETRY_COUNT ,object : Callback<UploadImagePojo>
        {
            override fun onResponse(call: Call<UploadImagePojo>, response: Response<UploadImagePojo>)
            {
                try {
                    mView.showProgress(0 )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("UploadImage response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onImageUpload(response.body() as UploadImagePojo)

                    else
                        mView.onFailed("Something went wrong! Please try again later")

                } catch (e: Exception) {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later.")
                }

            }

            override fun onFailure(call: Call<UploadImagePojo>, t: Throwable) {
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