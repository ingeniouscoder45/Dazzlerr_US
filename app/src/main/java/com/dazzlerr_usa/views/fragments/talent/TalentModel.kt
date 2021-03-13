package com.dazzlerr_usa.views.fragments.talent

import android.content.Context
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.fragments.home.pojos.ModelsPojo
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class TalentModel(val mContext: Context, val mView:TalentView) : TalentPresenter
{

    var modelsService :Call<ModelsPojo> ? = null
    override fun cancelRetrofitRequest()
    {
        if(modelsService!=null)
        modelsService?.cancel()
    }

    override fun getTalentModels(city: String , page:String, model_name: String , gender:String , category_id : String , experience_type:String,isShowProgressbar:Boolean ,user_id:String , age_range: String , height_range: String)
    {

        mView.showProgress(true ,isShowProgressbar )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        modelsService = webServices.getTalentsApi("application/x-www-form-urlencoded" , Constants.auth_key,city  ,page ,model_name , gender ,category_id , experience_type ,user_id ,age_range , height_range
                ,DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL , DeviceInfoConstants.DEVICE_TYPE , DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(modelsService ,Constants.RETRY_COUNT , object : Callback<ModelsPojo>
        {

            override fun onResponse(call: Call<ModelsPojo>, response: Response<ModelsPojo>)
            {
                try
                {
                    mView.showProgress(false ,isShowProgressbar )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("Talents response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onTalentsSuccess(response.body() as ModelsPojo)

                    else
                        mView.onFailed(response.body()?.message!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<ModelsPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false ,isShowProgressbar )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")
                else
                {
                    (mContext as HomeActivity).stopProgressBarAnim()
                }
            }


        })

    }

}