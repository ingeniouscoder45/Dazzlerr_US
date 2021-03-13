package com.dazzlerr_usa.views.activities.featuredccavenue

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

class CheckoutModel(val mContext: Context, val mView:CheckoutView) : CheckoutPresenter
{

    var modelsService :Call<CheckoutPojo>?=null
    override fun cancelRetrofitRequest()
    {
        if(modelsService!=null)
        modelsService?.cancel()
    }

    override fun checkout(user_id: String, billing: String, shipping: String, line_item: String, payment_method: String, transaction_id: String, order_total: String, wp_user_id: String) {
        mView.showProgress(true)
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        modelsService = webServices.featuredCheckoutApi("application/x-www-form-urlencoded" ,Constants.auth_key , user_id ,billing ,shipping ,line_item ,payment_method , transaction_id , order_total ,wp_user_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(modelsService ,Constants.RETRY_COUNT ,object : Callback<CheckoutPojo>
        {
            override fun onResponse(call: Call<CheckoutPojo>, response: Response<CheckoutPojo>)
            {
                try
                {
                    mView.showProgress(false)
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("FeaturedCheckout response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onSuccess()

                    else
                        mView.onFailed("Something went wrong! Please try again later.")

                }
                catch (e: Exception)
                {
                    mView.showProgress(false)
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<CheckoutPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false)
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }

}