package com.dazzlerr_usa.views.activities.settings

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class SettingsModel(val mContext: Context, val mView:SettingsView) : SettingsPresenter
{

    var settingsService :Call<GetProfileSettingsPojo>?=null
    var publishOrunpublishService :Call<PublishOrUnpublishProfilePojo>?=null
    var updateUsernameService :Call<UpdateUsernamePojo>?=null
    var updateRoleService :Call<SetSecurityQuestionPojo>?=null
    var verifyPasswordService :Call<UpdateUsernamePojo>?=null
    var setSecurityQuestionService :Call<SetSecurityQuestionPojo>?=null

    override fun cancelRetrofitRequest()
    {

        if(settingsService!=null)
            settingsService?.cancel()

        if(publishOrunpublishService!=null)
            publishOrunpublishService?.cancel()

         if(updateUsernameService!=null)
             updateUsernameService?.cancel()

         if(updateRoleService!=null)
             updateRoleService?.cancel()

    }

    override fun getProfileSettings(user_id: String, user_role: String)
    {
        mView.showProgress(true)
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        settingsService = webServices.getProfileSettingsApi("application/x-www-form-urlencoded" ,Constants.auth_key , user_id ,user_role
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(settingsService ,Constants.RETRY_COUNT ,object : Callback<GetProfileSettingsPojo>
        {
            override fun onResponse(call: Call<GetProfileSettingsPojo>, response: Response<GetProfileSettingsPojo>)
            {
                try
                {

                    mView.showProgress(false)

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("getProfileSettings response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onGetProfile(response.body()?.username!! ,response.body()?.data!!,response.body()?.set_answer!!,response.body()?.set_question_id!! , response.body()?.questions!! , response.body()?.secondary_role_id!!, response.body()?.secondary_role!!)

                    else
                        mView.onFailed(response.body()?.status!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<GetProfileSettingsPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                if(!call.isCanceled) {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }


        })

    }


    override fun publisOrUnpublishProfile(user_id: String, user_role: String, status: String)
    {

        mView.showProgress(true)
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        publishOrunpublishService = webServices.publisOrUnpublishProfileApi("application/x-www-form-urlencoded" ,Constants.auth_key , user_id ,user_role ,status
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(publishOrunpublishService ,Constants.RETRY_COUNT ,object : Callback<PublishOrUnpublishProfilePojo>
        {
            override fun onResponse(call: Call<PublishOrUnpublishProfilePojo>, response: Response<PublishOrUnpublishProfilePojo>)
            {
                try
                {
                    mView.showProgress(false)
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("PublisOrUnpublishProfile response: "+ responseStr+" Status: "+status)

                    if(response.body()?.success!!)
                        mView.onPublisOrUnpublish(status)

                    else
                        mView.onFailed(response.body()?.status!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<PublishOrUnpublishProfilePojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                if(!call.isCanceled) {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }


        })

    }

    override fun updateUserName(user_id: String, user_name: String) {

        mView.showProgress(true)
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        updateUsernameService = webServices.updateUserNameApi("application/x-www-form-urlencoded" ,Constants.auth_key , user_id ,user_name
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(updateUsernameService ,Constants.RETRY_COUNT ,object : Callback<UpdateUsernamePojo>
        {
            override fun onResponse(call: Call<UpdateUsernamePojo>, response: Response<UpdateUsernamePojo>)
            {
                try
                {
                    mView.showProgress(false)
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("updateUserName response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onUpdateUsername()

                    else
                        mView.onFailed(response.body()?.message!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<UpdateUsernamePojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                if(!call.isCanceled) {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again later")
                }
            }
        })

    }

    override fun updateSecondaryRole(user_id: String, secondary_role_id: String) {

        mView.showProgress(true)
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        updateRoleService = webServices.updateSecondaryRoleApi("application/x-www-form-urlencoded" ,Constants.auth_key , user_id ,secondary_role_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(updateRoleService ,Constants.RETRY_COUNT ,object : Callback<SetSecurityQuestionPojo>
        {
            override fun onResponse(call: Call<SetSecurityQuestionPojo>, response: Response<SetSecurityQuestionPojo>)
            {
                try
                {
                    mView.showProgress(false)
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("UpdateSecondaryRole response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onUpdateSecondaryRole()

                    else
                        mView.onFailed(response.body()?.status!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<SetSecurityQuestionPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                if(!call.isCanceled) {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }


        })

    }

    override fun verifyPassword(user_id: String, password: String) {

        mView.showProgress(true)
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        verifyPasswordService = webServices.verifyPasswordApi("application/x-www-form-urlencoded" ,Constants.auth_key , user_id ,password
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(verifyPasswordService ,Constants.RETRY_COUNT ,object : Callback<UpdateUsernamePojo>
        {
            override fun onResponse(call: Call<UpdateUsernamePojo>, response: Response<UpdateUsernamePojo>)
            {
                try
                {
                    mView.showProgress(false)
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("VerifyPassword response: "+ responseStr)

                        mView.onVerifiedPassword(response.body()?.status!! , response.body()?.message!!)


                }

                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<UpdateUsernamePojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                if(!call.isCanceled) {
                    mView.showProgress(false)
                    mView.onVerifiedPassword(false ,"Something went wrong! Please try again later")
                }

            }

        })

    }

    override fun setSecurityQuestion(user_id: String, question_id: String, answer: String, password: String , question : String) {

        mView.showProgress(true)
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        setSecurityQuestionService = webServices.setSecurityQuestionApi("application/x-www-form-urlencoded" ,Constants.auth_key , user_id ,question_id , answer ,password
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(setSecurityQuestionService ,Constants.RETRY_COUNT, object : Callback<SetSecurityQuestionPojo>
        {
            override fun onResponse(call: Call<SetSecurityQuestionPojo>, response: Response<SetSecurityQuestionPojo>)
            {
                try
                {
                    mView.showProgress(false)
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("setSecurityQuestion response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onSetSecurityQuestion(question ,answer)

                    else
                        mView.onFailed(response.body()?.status!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<SetSecurityQuestionPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                if(!call.isCanceled) {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }


        })

    }


}