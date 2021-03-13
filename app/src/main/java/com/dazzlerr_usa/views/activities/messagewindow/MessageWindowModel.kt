package com.dazzlerr_usa.views.activities.messagewindow

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import com.dazzlerr_usa.views.activities.OthersProfileActivity
import com.dazzlerr_usa.views.activities.home.HomeActivity
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File

class MessageWindowModel(val mContext: Context, val mView: MessageWindowView) : MessageWindowPresenter
{

    var messageListService :Call<MessageWindowPojo>?= null
    var blockUserService :Call<BlockUserPojo>?= null
    var replyMessageService :Call<ReplyMessagePojo>?= null
    var multimediaMessageService :Call<MultimediaChatPojo>?= null
    var generateThreadService :Call<GenerateThraedPojo>?= null

    override fun cancelRetrofitRequest()
    {
        if(messageListService!=null)
        messageListService?.cancel()

        if(replyMessageService!=null)
            replyMessageService?.cancel()

        if(multimediaMessageService!=null)
            multimediaMessageService?.cancel()

        if(blockUserService!=null)
            blockUserService?.cancel()

        if(generateThreadService!=null)
            generateThreadService?.cancel()
    }

    override fun getAllMessages(user_id : String , thread_id: String)
    {

        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)

        messageListService = webServices.getAllMessages("application/x-www-form-urlencoded"  ,Constants.auth_key ,user_id ,thread_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(messageListService ,Constants.RETRY_COUNT ,object : Callback<MessageWindowPojo>
        {
            override fun onResponse(call: Call<MessageWindowPojo>, response: Response<MessageWindowPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetAllMessages response: "+ responseStr)

                    if(response.body()!!.success)
                        mView.OnGetMessagesSuccess(response.body() as MessageWindowPojo)

                    else
                        mView.onFailed(response.body()!!.status.toString())

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }
            }

            override fun onFailure(call: Call<MessageWindowPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled)
                {
                    mView.showProgress(false )
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }
        })
    }

    override fun sendMessage(thread_id: String, sender_id: String, name: String, email: String, phone: String, message: String) {

        //mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)

        replyMessageService = webServices.replyToMessage("application/x-www-form-urlencoded" ,Constants.auth_key, thread_id ,sender_id , name , email, phone , message
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(replyMessageService ,Constants.RETRY_COUNT ,object : Callback<ReplyMessagePojo>
        {
            override fun onResponse(call: Call<ReplyMessagePojo>, response: Response<ReplyMessagePojo>)
            {
                try
                {
                    //mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("MessageReply response: "+ responseStr)

                    if(response.body()!!.success)
                        //mView.onReplySuccess(response.body()?.data!!)

                    else
                        mView.onFailed(response.body()!!.status.toString())

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<ReplyMessagePojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled) {
                    //mView.showProgress(false )
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }


        })

    }

    override fun sendMultimediaMessage(imageFile: File, thread_id: String, sender_id: String, name: String, email: String, phone: String)
    {
        mView.showProgress(true )

        val requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
        val fileupload = MultipartBody.Part.createFormData("image", imageFile.getName(), requestBody)
        val sender_id_ = RequestBody.create(MediaType.parse("text/plain"), sender_id)
        val thread_id_ = RequestBody.create(MediaType.parse("text/plain"), thread_id)
        val name_ = RequestBody.create(MediaType.parse("text/plain"), name)
        val email_ = RequestBody.create(MediaType.parse("text/plain"), email)
        val phone_ = RequestBody.create(MediaType.parse("text/plain"), phone)
        val auth_key = RequestBody.create(MediaType.parse("text/plain"), Constants.auth_key)

        val device_id = RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_ID)
        val device_brand = RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_BRAND)
        val device_model = RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_MODEL)
        val device_type = RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_TYPE)
        val device_version = RequestBody.create(MediaType.parse("text/plain"), DeviceInfoConstants.DEVICE_VERSION)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        multimediaMessageService = webServices.uploadMultimediaChat(fileupload,auth_key,thread_id_, sender_id_,name_ , email_, phone_ ,device_id ,device_brand , device_model ,device_type , device_version )

        APIHelper.enqueueWithRetry(multimediaMessageService , Constants.RETRY_COUNT , object : Callback<MultimediaChatPojo>
        {
            override fun onResponse(call: Call<MultimediaChatPojo>, response: Response<MultimediaChatPojo>)
            {
                try {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("UpdateProfileResponse response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onChatReplySuccess(response.body()!!)

                    else
                        mView.onFailed(response.body()?.status.toString())

                } catch (e: Exception) {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later.")
                }

            }
            override fun onFailure(call: Call<MultimediaChatPojo>, t: Throwable) {
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

    override fun blockUser(user_id : String , thread_id: String , status : String)
    {

        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)

        blockUserService = webServices.blockUser("application/x-www-form-urlencoded"  ,Constants.auth_key ,user_id ,thread_id ,status
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(blockUserService ,Constants.RETRY_COUNT ,object : Callback<BlockUserPojo>
        {
            override fun onResponse(call: Call<BlockUserPojo>, response: Response<BlockUserPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetAllMessages response: "+ responseStr)

                    if(response.body()!!.status)
                        mView.onUserBlockedSuccess()

                    else
                        mView.onFailed(response.body()!!.status.toString())

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }
            }

            override fun onFailure(call: Call<BlockUserPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled)
                {
                    mView.showProgress(false )
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }
        })
    }


    override fun generateThread(user_id: String , profile_id  :String) {

        mView.showProgress(true )
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)

        generateThreadService = webServices.generateThread("application/x-www-form-urlencoded"
                ,Constants.auth_key ,user_id ,profile_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(generateThreadService ,Constants.RETRY_COUNT ,object : Callback<GenerateThraedPojo>
        {
            override fun onResponse(call: Call<GenerateThraedPojo>, response: Response<GenerateThraedPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetAllMessages response: "+ responseStr)

                    if(response.body()!!.success)
                        mView.onThreadIdGenrated(response.body()!!)

                    else
                        mView.onFailed(response.body()!!.status.toString())

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }
            }

            override fun onFailure(call: Call<GenerateThraedPojo>, t: Throwable)
            {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled)
                {
                    mView.showProgress(false )
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }
        })
    }
}