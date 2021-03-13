package com.dazzlerr_usa.views.fragments.profile

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.OthersProfileActivity
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.activities.elitememberdetails.LikeOrDislikePojo
import com.dazzlerr_usa.views.fragments.profile.pojo.FollowOrUnfollowPojo
import com.dazzlerr_usa.views.fragments.profile.pojo.GetContactDetailsPojo
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





class ProfileModel(val mContext: Context, val mView:ProfileView) : ProfilePresenter
{


     var profileService :Call<ProfilePojo>?=null
     var uploadProflilePicService :Call<UploadProfilePicPojo>?=null
     var likeOrDislikeservice :Call<LikeOrDislikePojo>?=null
     var followOrUnfollowService :Call<FollowOrUnfollowPojo>?=null
     var shortListService :Call<FollowOrUnfollowPojo>?=null
     var contactDetailsService :Call<GetContactDetailsPojo>?=null


    override fun cancelRetrofitRequest()
    {
        if(profileService!=null)
        profileService?.cancel()

        if(uploadProflilePicService!=null)
            uploadProflilePicService?.cancel()

        if(likeOrDislikeservice!=null)
            likeOrDislikeservice?.cancel()

        if(followOrUnfollowService!=null)
            followOrUnfollowService?.cancel()

        if(contactDetailsService!=null)
            contactDetailsService?.cancel()
    }

    override fun getProfile(profile_id: String ,user_id: String)
    {

        mView.showProgress(true )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        profileService = webServices.getProfileApi("application/x-www-form-urlencoded" ,Constants.auth_key, profile_id,user_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL , DeviceInfoConstants.DEVICE_TYPE , DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(profileService , Constants.RETRY_COUNT , object : Callback<ProfilePojo>
        {
            override fun onResponse(call: Call<ProfilePojo>, response: Response<ProfilePojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetProfile response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onGetProfileSuccess(response.body() as ProfilePojo)

                    else
                        mView.onProfileFailed("Something went wrong! Please try again later")

                }

                catch (e: Exception) {
                    e.printStackTrace()
                    mView.onProfileFailed("Something went wrong! Please try again later.")
                }
            }

            override fun onFailure(call: Call<ProfilePojo>, t: Throwable)
            {
                Timber.e("On Failure: Something went wrong "+ t.message)
                mView.showProgress(false )
                if(!call.isCanceled)
                mView.onProfileFailed("Something went wrong! Please try again later.")
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
        uploadProflilePicService = webServices.editProfilePicApi(fileupload,auth_key, userid ,device_id ,device_brand , device_model ,device_type , device_version)

        APIHelper.enqueueWithRetry(uploadProflilePicService , Constants.RETRY_COUNT , object : Callback<UploadProfilePicPojo>
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

    override fun getContactDetails(user_id: String, profile_id: String) {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        contactDetailsService = webServices.getContactDetailsApi("application/x-www-form-urlencoded"   ,Constants.auth_key ,user_id ,profile_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(contactDetailsService ,Constants.RETRY_COUNT ,object : Callback<GetContactDetailsPojo>
        {
            override fun onResponse(call: Call<GetContactDetailsPojo>, response: Response<GetContactDetailsPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("LikeOrDislike response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onGetContactDetails(response.body() as GetContactDetailsPojo)

                    else
                        mView.onFailed(response.body()?.success!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<GetContactDetailsPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later")
            }
        })

    }



    override fun likeOrDislike(user_id: String, profile_id: String, status: String)
    {
        Timber.e(user_id+ "  "+ profile_id +"  "+status)
        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        likeOrDislikeservice = webServices.likeOrDislikeEliteApi("application/x-www-form-urlencoded"   ,Constants.auth_key ,user_id ,profile_id , status
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(likeOrDislikeservice ,Constants.RETRY_COUNT ,object : Callback<LikeOrDislikePojo>
        {
            override fun onResponse(call: Call<LikeOrDislikePojo>, response: Response<LikeOrDislikePojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("LikeOrDislike response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onLikeOrDislike(status)

                    else
                        mView.onFailed(response.body()?.success!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<LikeOrDislikePojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later")
            }
        })

    }

    override fun followOrUnfollow(user_id: String, profile_id: String, status: String)
    {
        Timber.e(user_id+ "  "+ profile_id +"  "+status)
        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        followOrUnfollowService = webServices.followOrUnfollowApi("application/x-www-form-urlencoded"    ,Constants.auth_key,user_id ,profile_id , status
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(followOrUnfollowService ,Constants.RETRY_COUNT ,object : Callback<FollowOrUnfollowPojo>
        {
            override fun onResponse(call: Call<FollowOrUnfollowPojo>, response: Response<FollowOrUnfollowPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("followOrUnfollow response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onFollowOrUnfollow(status)

                    else
                        mView.onFailed(response.body()?.success!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<FollowOrUnfollowPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later")
            }
        })

    }

    override fun shortList(user_id: String, profile_id: String, status: String)
    {
        Timber.e(user_id+ "  "+ profile_id +"  "+status)
        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        shortListService = webServices.shortListApi("application/x-www-form-urlencoded"   ,Constants.auth_key ,user_id ,profile_id , status
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(shortListService ,Constants.RETRY_COUNT ,object : Callback<FollowOrUnfollowPojo>
        {
            override fun onResponse(call: Call<FollowOrUnfollowPojo>, response: Response<FollowOrUnfollowPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("ShortList Api response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onShortList(status)

                    else
                        mView.onFailed(response.body()?.success!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<FollowOrUnfollowPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later")
            }
        })

    }

}