package com.dazzlerr_usa.views.activities.transactions

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

class TransactionsModel(val mContext: Context, val mView:TransactionsView) : TransactionsPresenter
{
    lateinit var transactionsService :Call<TransactionsPojo>

    override fun cancelRetrofitRequest()
    {
        transactionsService.cancel()
    }

    override fun getTransactions(user_id: String, page: String, isShowProgressbar: Boolean)
    {

        mView.showProgress(true ,isShowProgressbar )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        transactionsService = webServices.getMyTransactionsApi("application/x-www-form-urlencoded" , Constants.auth_key,page  ,user_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(transactionsService ,Constants.RETRY_COUNT ,object : Callback<TransactionsPojo>
        {
            override fun onResponse(call: Call<TransactionsPojo>, response: Response<TransactionsPojo>)
            {
                try
                {
                    mView.showProgress(false ,isShowProgressbar )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("My Transactions response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onSuccess(response.body() as TransactionsPojo)

                    else
                        mView.onFailed(response.body()?.status!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<TransactionsPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false ,isShowProgressbar )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }

}