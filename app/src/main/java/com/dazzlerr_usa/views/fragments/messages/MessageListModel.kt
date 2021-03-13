package com.dazzlerr_usa.views.fragments.messages

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import com.dazzlerr_usa.views.activities.messages.MessagesActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class MessageListModel(val mContext: Context, val mView : MessageListView ) : MessagesListPresenter
{

    var inboxService: Call<MessagesListPojo> ?= null
    var trashService: Call<MessagesListPojo> ?= null
    var deleteService: Call<MessageDeleteAndRestorePojo> ?= null
    var restoreService: Call<MessageDeleteAndRestorePojo> ?= null

    override fun cancelRetrofitRequest()
    {
        if(inboxService!=null)
        inboxService?.cancel()

        if(trashService!=null)
        trashService?.cancel()

        if(deleteService!=null)
            deleteService?.cancel()

        if(restoreService!=null)
            restoreService?.cancel()
    }


    override fun getChatList(user_id: String) {
        mView.showProgress(true)
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        inboxService = webServices.getChatlist("application/x-www-form-urlencoded"  ,Constants.auth_key,user_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)
        APIHelper.enqueueWithRetry(inboxService ,Constants.RETRY_COUNT ,object : Callback<MessagesListPojo>
        {
            override fun onResponse(call: Call<MessagesListPojo>, response: Response<MessagesListPojo>)
            {
                try
                {
                    mView.showProgress(false)
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("Chat list messages response: "+ responseStr)

                    if(response.body()?.success!!)
                    {
                        val results = response.body()
                        mView.onGetChatlistSuccess(results?.data!!)
                    }
                    else
                    {
                        // mView.onGetInboxSuccess(ArrayList(), messageType)
                        mView.onFailed(response.body()?.status!!)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<MessagesListPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled) {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again later")
                }
                else
                {
                    (mContext as MessagesActivity).stopProgressBarAnim()
                }
            }
        })

    }




    override fun getTrashMessages(user_id: String, page: String) {
        mView.showProgress(true)
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        trashService = webServices.getTrashMessages("application/x-www-form-urlencoded"  ,Constants.auth_key,user_id , page
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)
        APIHelper.enqueueWithRetry(trashService ,Constants.RETRY_COUNT ,object : Callback<MessagesListPojo>
        {
            override fun onResponse(call: Call<MessagesListPojo>, response: Response<MessagesListPojo>)
            {
                try
                {
                    mView.showProgress(false)
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("TrashMessages response: "+ responseStr)

                    if(response.body()?.success!!)
                    {
                        val results = response.body()
                        mView.onGetTrashSuccess(results?.data!!)
                    }
                    else
                    {
                        // mView.onGetInboxSuccess(ArrayList(), messageType)
                        mView.onFailed(response.body()?.status!!)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<MessagesListPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled) {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again later")
                }
                else
                {
                    (mContext as MessagesActivity).stopProgressBarAnim()
                }
            }
        })

    }

    override fun deleteMessages(thread_id: String, position: Int, messageType:Int) {
    mView.showProgress(true)
    val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
    deleteService = webServices.deleteMessages("application/x-www-form-urlencoded"  ,Constants.auth_key,thread_id
            , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
            , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
            , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

    APIHelper.enqueueWithRetry(deleteService ,Constants.RETRY_COUNT ,object : Callback<MessageDeleteAndRestorePojo>
    {
        override fun onResponse(call: Call<MessageDeleteAndRestorePojo>, response: Response<MessageDeleteAndRestorePojo>)
        {
            try
            {
                mView.showProgress(false)
                val gson = GsonBuilder().create()
                val responseStr = gson.toJson(response.body())

                Timber.e("DeleteMessage response: "+ responseStr)

                if(response.body()?.success!!)
                {
                    mView.onDeleteSuccess(position,messageType)
                }
                else
                {
                    // mView.onGetInboxSuccess(ArrayList(), messageType)
                    mView.onFailed(response.body()?.status!!)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                mView.onFailed("Something went wrong! Please try again later")
            }

        }

        override fun onFailure(call: Call<MessageDeleteAndRestorePojo>, t: Throwable) {
            Timber.e("Something went wrong "+ t.message)

            if(!call.isCanceled) {
                mView.showProgress(false)
                mView.onFailed("Something went wrong! Please try again later")
            }
            else
            {
                (mContext as MessagesActivity).stopProgressBarAnim()
            }
        }
    })

}

    override fun restoreMessages(thread_id: String, position: Int) {
        mView.showProgress(true)
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        restoreService = webServices.restoreMessages("application/x-www-form-urlencoded"  ,Constants.auth_key,thread_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)
        APIHelper.enqueueWithRetry(restoreService  ,Constants.RETRY_COUNT ,object : Callback<MessageDeleteAndRestorePojo>
        {
            override fun onResponse(call: Call<MessageDeleteAndRestorePojo>, response: Response<MessageDeleteAndRestorePojo>)
            {
                try
                {
                    mView.showProgress(false)
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("RestoreMessage response: "+ responseStr)

                    if(response.body()?.success!!)
                    {
                        mView.onRestoreSuccess(position)
                    }
                    else
                    {
                        // mView.onGetInboxSuccess(ArrayList(), messageType)
                        mView.onFailed(response.body()?.status!!)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<MessageDeleteAndRestorePojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled) {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again later")
                }
                else
                {
                    (mContext as MessagesActivity).stopProgressBarAnim()
                }
            }
        })

    }

}