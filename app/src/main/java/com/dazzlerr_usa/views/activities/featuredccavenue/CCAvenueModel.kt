package com.dazzlerr_usa.views.activities.featuredccavenue

import android.content.Context
import android.util.Patterns
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.editprofile.pojos.CitiesPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.CountryPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.StatesPojo
import com.dazzlerr_usa.views.activities.membership.GetTokenPojo
import com.google.gson.GsonBuilder


class CCAvenueModel(var mContext: Context, var mView: CCAvenueView) : CCAvenuePresenter
{
    var countryService  : Call<CountryPojo>?=null
    var statesService  : Call<StatesPojo>?=null
    var citiesService  : Call<CitiesPojo>?=null
    var mTokenService  : Call<GetTokenPojo>?=null


    var modelsService :Call<CheckoutPojo>?=null

    override fun cancelRetrofitRequest()
    {

        if(modelsService!=null)
            modelsService?.cancel()

        if(statesService!=null)
            statesService?.cancel()

        if(citiesService!=null)
            citiesService?.cancel()

        if(countryService!=null)
            countryService?.cancel()

        if(mTokenService!=null)
            mTokenService?.cancel()
    }

    override fun validate(billingName: String, billingAddress: String, billingCountry: String, billingState: String, billingCity: String, billingZip: String, billingTel: String, billingEmail: String) {


        if (billingName.length == 0 || billingAddress.length == 0 || billingCountry.length==0 || billingState.length==0 || billingCity.length==0|| billingZip.length==0|| billingTel.length==0|| billingEmail.length==0)
        {
            mView.onFailed("All the fields are mandatory. Please fill all the fields.")
        }

        else if (billingTel.length >10 || billingTel.length<10)
        {
            mView.onFailed("Please enter a valid phone number for billing.")
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(billingEmail).matches())
        {
            mView.onFailed("Please enter a valid email address for billing.")
        }

        else
            mView.isValidate()

    }


    override fun checkout(user_id: String, billing: String, shipping: String, line_item: String, payment_method: String, transaction_id: String, order_total: String, wp_user_id: String) {
        mView.showProgress(true)
        val webServices = RetrofitClient.getClient(Constants.liveClientTokenUrl).create(WebServices::class.java)
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


    override fun getCountries()
    {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        countryService = webServices.getCountries("application/x-www-form-urlencoded" )

        APIHelper.enqueueWithRetry(countryService ,Constants.RETRY_COUNT ,object : Callback<CountryPojo>
        {
            override fun onResponse(call: Call<CountryPojo>, response: Response<CountryPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetCountries response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onGetCountries(response.body() as CountryPojo)

                    else
                        mView.onFailed(response.body()?.message!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showProgress(false)


            }

            override fun onFailure(call: Call<CountryPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled) {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again later")
                }
            }
        })

    }


        override fun getStates(country_id: String)
        {


        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        statesService = webServices.getStatesByCountry("application/x-www-form-urlencoded" ,Constants.auth_key , country_id )

        APIHelper.enqueueWithRetry(statesService ,Constants.RETRY_COUNT ,object : Callback<StatesPojo>
        {
            override fun onResponse(call: Call<StatesPojo>, response: Response<StatesPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetStates response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onGetStates(response.body() as StatesPojo)

                    else
                        mView.onFailed(response.body()?.message!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showProgress(false)


            }

            override fun onFailure(call: Call<StatesPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled) {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again later")
                }
            }
        })

    }


    override fun getCities(state_id: String) {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        citiesService = webServices.getCities("application/x-www-form-urlencoded" ,Constants.auth_key, state_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(citiesService ,Constants.RETRY_COUNT ,object : Callback<CitiesPojo>
        {
            override fun onResponse(call: Call<CitiesPojo>, response: Response<CitiesPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetCities response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onGetCities(response.body() as CitiesPojo)

                    else
                        mView.onFailed(response.body()?.message!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showProgress(false)


            }

            override fun onFailure(call: Call<CitiesPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled) {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again later")
                }
            }
        })

    }


    override fun getTokenApi() {

        val webServices = RetrofitClient.getClient(Constants.liveClientTokenUrl).create(WebServices::class.java)
        mTokenService = webServices.getTokenApi()

        mView.showProgress(true)
        APIHelper.enqueueWithRetry(mTokenService ,Constants.RETRY_COUNT ,object : Callback<GetTokenPojo>
        {
            override fun onResponse(call: Call<GetTokenPojo>, response: Response<GetTokenPojo>)
            {
                try
                {

                    mView.showProgress(false)
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetToken response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onGetToken(response.body()!!)

                    else
                        mView.onFailed("Failed to connect!")

                }
                catch (e: Exception)
                {

                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                    mView.showProgress(false)
                }

            }

            override fun onFailure(call: Call<GetTokenPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later")

                mView.showProgress(false)
            }


        })

    }
}