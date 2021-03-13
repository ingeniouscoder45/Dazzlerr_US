package com.dazzlerr_usa.views.activities.institute

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

class InstituteModel(val mContext: Context, val mView:InstituteView) : InstitutePresenter
{
    var modelsService :Call<InstitutePojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(modelsService!=null)
        modelsService?.cancel()
    }

    override fun getInstitutez(city :String , category_id : String , page: String, isShowProgressbar: Boolean)
    {

        mView.showProgress(true ,isShowProgressbar)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        modelsService = webServices.getInstitutesApi("application/x-www-form-urlencoded"   ,Constants.auth_key , city , category_id , page
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(modelsService ,Constants.RETRY_COUNT ,object : Callback<InstitutePojo>
        {
            override fun onResponse(call: Call<InstitutePojo>, response: Response<InstitutePojo>)
            {
                try
                {
                    mView.showProgress(false ,isShowProgressbar )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetInstitute response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onSuccess(response.body() as InstitutePojo)

                    else
                        mView.onFailed(response.body()?.status!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<InstitutePojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false ,isShowProgressbar )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }

}