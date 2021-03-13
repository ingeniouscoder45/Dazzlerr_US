package com.dazzlerr_usa.views.activities.membership

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

class MembershipSuccessModel(val mContext: Context, val mView:MembershipSuccessView) : MembershipSuccessPresenter
{

    var modelsService :Call<MembershipSuccessPojo>?=null
    override fun cancelRetrofitRequest()
    {
        if(modelsService!=null)
        modelsService?.cancel()
    }


    override fun checkout(user_id: String, membership_id: String, transaction_id: String, discount_total: String, total: String, referred_by: String, payment_method: String, status: String ,membership_type: String , discount_type : String,address : String , country : String , city: String , state :String , zipcode :String) {

        mView.showProgress(true)
        val webServices = RetrofitClient.getClient(Constants.liveClientTokenUrl).create(WebServices::class.java)
        modelsService = webServices.membershipPaymentApi("application/x-www-form-urlencoded" ,Constants.auth_key , user_id ,membership_id ,transaction_id ,discount_total ,total , referred_by , payment_method ,status ,membership_type, discount_type ,address ,country, city , state , zipcode
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(modelsService ,Constants.RETRY_COUNT ,object : Callback<MembershipSuccessPojo>
        {
            override fun onResponse(call: Call<MembershipSuccessPojo>, response: Response<MembershipSuccessPojo>)
            {
                try
                {
                    mView.showProgress(false)
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("FeaturedCheckout response: "+ responseStr)

                    if(response.body()?.status!!)
                        response.body()?.data?.let { mView.onSuccess(it) }

                    else
                        mView.onMembershipFailed("Something went wrong! Please try again later.")

                }
                catch (e: Exception)
                {
                    mView.showProgress(false)
                    e.printStackTrace()
                    mView.onMembershipFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<MembershipSuccessPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false)
                if(!call.isCanceled)
                mView.onMembershipFailed("Something went wrong! Please try again later")

            }


        })

    }

    override fun buyFreeMembership(user_id: String) {

        mView.showProgress(true)
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        var modelsService = webServices.freeMembershipApi("application/x-www-form-urlencoded"
                , Constants.auth_key , user_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(modelsService ,Constants.RETRY_COUNT ,object : Callback<FreeMembershipPojo>
        {
            override fun onResponse(call: Call<FreeMembershipPojo>, response: Response<FreeMembershipPojo>)
            {
                try
                {
                    mView.showProgress(false)
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("FreeMembership response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onFreeMembershipSuccess()

                    else
                        mView.onFreeMembershipFailed("Something went wrong! Please try again later.")

                }
                catch (e: Exception)
                {
                    mView.showProgress(false)
                    e.printStackTrace()
                    mView.onFreeMembershipFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<FreeMembershipPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false)
                if(!call.isCanceled)
                mView.onMembershipFailed("Something went wrong! Please try again later")

            }


        })

    }

}