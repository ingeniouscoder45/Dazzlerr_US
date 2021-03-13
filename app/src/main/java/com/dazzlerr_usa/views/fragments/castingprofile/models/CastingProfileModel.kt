package com.dazzlerr_usa.views.fragments.castingprofile.models

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.OthersProfileActivity
import com.dazzlerr_usa.views.activities.elitememberdetails.LikeOrDislikePojo
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.fragments.castingprofile.pojo.CastingMyJobPojo
import com.dazzlerr_usa.views.fragments.castingprofile.pojo.CastingProfilePojo
import com.dazzlerr_usa.views.fragments.castingprofile.presenters.CastingProfilePresenter
import com.dazzlerr_usa.views.fragments.castingprofile.views.CastingProfileView
import com.dazzlerr_usa.views.fragments.portfolio.pojos.DeletePojo
import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioProjectsPojo
import com.dazzlerr_usa.views.fragments.profile.pojo.FollowOrUnfollowPojo
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



class CastingProfileModel(val mContext: Context, val mView: CastingProfileView) : CastingProfilePresenter
{

    var jobsService :Call<CastingMyJobPojo>?=null
     var profileService :Call<CastingProfilePojo>?=null
     var uploadProflilePicService :Call<UploadProfilePicPojo>?=null
     var likeOrDislikeservice :Call<LikeOrDislikePojo>?=null
     var followOrUnfollowService :Call<FollowOrUnfollowPojo>?=null
     var projectsService :Call<PortfolioProjectsPojo>?=null

    var projectsDeleteService :Call<DeletePojo>?=null

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

        if(jobsService!=null)
            jobsService?.cancel()

        if(projectsService!=null)
            projectsService?.cancel()

        if(projectsDeleteService!=null)
            projectsDeleteService?.cancel()

    }

    override fun getProfile(profile_id: String ,user_id: String)
    {

        mView.showProgress(true )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        profileService = webServices.getCastingProfileApi("application/x-www-form-urlencoded"
                ,Constants.auth_key,profile_id,user_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(profileService ,Constants.RETRY_COUNT ,object : Callback<CastingProfilePojo>
        {
            override fun onResponse(call: Call<CastingProfilePojo>, response: Response<CastingProfilePojo>)
            {
                try {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetCastingProfile response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onGetProfileSuccess(response.body() as CastingProfilePojo)

                    else
                        mView.onProfileFailed("Something went wrong! Please try again later")

                } catch (e: Exception) {
                    e.printStackTrace()
                    mView.onProfileFailed("Something went wrong! Please try again later.")
                }

            }

            override fun onFailure(call: Call<CastingProfilePojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
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
        uploadProflilePicService = webServices.editCastingProfilePicApi(fileupload, auth_key,  userid ,device_id ,device_brand , device_model ,device_type , device_version)

        APIHelper.enqueueWithRetry(uploadProflilePicService ,Constants.RETRY_COUNT ,object : Callback<UploadProfilePicPojo>
        {
            override fun onResponse(call: Call<UploadProfilePicPojo>, response: Response<UploadProfilePicPojo>)
            {
                try {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("UpdateCastingProfilePic response: "+ responseStr)

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

    override fun likeOrDislike(user_id: String, profile_id: String, status: String)
    {
        //Timber.e(user_id+ "  "+ profile_id +"  "+status)
        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        likeOrDislikeservice = webServices.CastinglikeOrDislikeApi("application/x-www-form-urlencoded"   ,Constants.auth_key ,user_id ,profile_id , status
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

                    Timber.e("CastingLikeOrDislike response: "+ responseStr)

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
       // Timber.e(user_id+ "  "+ profile_id +"  "+status)
        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        followOrUnfollowService = webServices.CastingFollowOrUnfollowApi("application/x-www-form-urlencoded"    ,Constants.auth_key,user_id ,profile_id , status
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

                    Timber.e("CastingFollowOrUnfollow response: "+ responseStr)

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

    override fun getMyJobs(user_id: String, page: String ,status: String)
    {

        mView.showProgress(true)
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        jobsService = webServices.getMyJobs("application/x-www-form-urlencoded" ,Constants.auth_key , user_id  ,page ,status
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(jobsService ,Constants.RETRY_COUNT ,object : Callback<CastingMyJobPojo>
        {
            override fun onResponse(call: Call<CastingMyJobPojo>, response: Response<CastingMyJobPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("CastingMyJobs response: "+ responseStr)

                    if(response.body()!!.success)
                        mView.onGetActiveJobsSuccess(response.body() as CastingMyJobPojo)

                    else
                        mView.onFailed(response.body()!!.status.toString())

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<CastingMyJobPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false )
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later")

            }

        })
    }


    override fun getPreviousProjects(user_id: String, page: String)
    {

        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        projectsService = webServices.getPortfolioProjects("application/x-www-form-urlencoded" , Constants.auth_key ,user_id  ,page
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

                    Timber.e("CastingProjects response: "+ responseStr)

                    if(response.body()!!.success)
                        mView.onGetProjectsSuccess(response.body() as PortfolioProjectsPojo)

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
        APIHelper.enqueueWithRetry(projectsDeleteService ,Constants.RETRY_COUNT , object : Callback<DeletePojo>
        {
            override fun onResponse(call: Call<DeletePojo>, response: Response<DeletePojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("DeleteCastingProject response: "+ responseStr)

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


            }


        })

    }
}