package com.dazzlerr_usa.views.activities.editcastingprofile

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
import com.google.gson.GsonBuilder


class EditCastingProfileModel(var mContext: Context, var mView: EditCastingProfileView) : EditCastingProfilePresenter
{


    var mServices  : Call<EditCastingProfilePojo>?=null
    var statesService  : Call<StatesPojo>?=null
    var citiesService  : Call<CitiesPojo>?=null
    var countriesService  : Call<CountryPojo>?=null
    override fun cancelRetrofitRequest()
    {

        if(mServices!=null)
            mServices?.cancel()

        if(statesService!=null)
            statesService?.cancel()

        if(citiesService!=null)
            citiesService?.cancel()

    }

    override fun validateCastingProfile(name: String, representer: String,country_id : String ,  state_id: String, phone: String, city: String ,zipcode:String ,email : String , whatsapp: String , website :String, facebook : String , twitter: String , instagram :String) {

        if (phone.length==0 || name.length==0 || representer.length==0||country_id.length==0 || state_id.length==0|| phone.length==0|| city.length==0 || zipcode.length==0)
        {
            mView.onFailed("Please fill all the mandatory fields.")
        }

        else if (email.length == 0)
        {
            mView.onFailed("Please enter your email address.")
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            mView.onFailed("Please enter a valid email address.")
        }

        else if (phone.length >10 || phone.length<10)
        {
            mView.onFailed("Please enter a valid phone number.")
        }

        else if (!Patterns.PHONE.matcher(phone).matches())
        {
            mView.onFailed("Please enter a valid phone number.")
        }

        else if (whatsapp.isNotEmpty() && (whatsapp.length >10 || whatsapp.length<10))
        {
            mView.onFailed("Please enter a valid whatsapp number.")
        }

        else if (whatsapp.isNotEmpty() && (!Patterns.PHONE.matcher(whatsapp).matches()))
        {
            mView.onFailed("Please enter a valid whatsapp number.")
        }


        else if (website.isNotEmpty() && (!Patterns.WEB_URL.matcher(website).matches()))
        {
            mView.onFailed("Please enter a valid url for website")
        }

        else if (facebook.isNotEmpty() && (!Patterns.WEB_URL.matcher(facebook).matches()))
        {
            mView.onFailed("Please enter a valid url for facebook")
        }

        else if (twitter.isNotEmpty() && (!Patterns.WEB_URL.matcher(twitter).matches()))
        {
            mView.onFailed("Please enter a valid url for twitter")
        }
        else if (instagram.isNotEmpty() && (!Patterns.WEB_URL.matcher(instagram).matches()))
        {
            mView.onFailed("Please enter a valid url for instagram")
        }

        else
        {
            mView.onValidate()
        }
    }

    override fun updateCastingProfile(name: String, company_name: String, representer: String, about: String,country_id: String, state_id: String, zipcode: String, phone: String, whats_app: String, website: String, facebook: String, twitter: String, instagram: String, city: String, city_id: String, user_id: String) {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mServices = webServices.updateCastingProfile("application/x-www-form-urlencoded"  ,Constants.auth_key,name, company_name, representer, about,country_id, state_id, zipcode, phone, whats_app, website, facebook, twitter, instagram, city, city_id, user_id
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)
        APIHelper.enqueueWithRetry(mServices ,Constants.RETRY_COUNT ,object : Callback<EditCastingProfilePojo>
        {
            override fun onResponse(call: Call<EditCastingProfilePojo>, response: Response<EditCastingProfilePojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("EditCastingProfile response: "+ responseStr)

                    if(response.body()?.success!!)
                    mView.onSuccess()

                    else
                        mView.onFailed(response.body()?.status!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showProgress(false)


            }

            override fun onFailure(call: Call<EditCastingProfilePojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled)
                {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again")
                }
            }
        })

    }


    override fun getCountries()
    {
        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        countriesService = webServices.getCountries("application/x-www-form-urlencoded" )

        APIHelper.enqueueWithRetry(countriesService ,Constants.RETRY_COUNT ,object : Callback<CountryPojo>
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


    override fun getStates(country_id: String) {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        statesService = webServices.getStatesByCountry("application/x-www-form-urlencoded", Constants.auth_key , country_id )

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


    override fun getCities(state_id: String)
    {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        citiesService = webServices.getCities("application/x-www-form-urlencoded" , Constants.auth_key,state_id
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

}