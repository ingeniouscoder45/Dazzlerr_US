package com.dazzlerr_usa.views.activities.editprofile.models

import android.content.Context
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
import com.dazzlerr_usa.views.activities.editprofile.pojos.GeneralInformationPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.StatesPojo
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.views.activities.editprofile.views.GeneralInfoView
import com.dazzlerr_usa.views.activities.editprofile.presenters.GeneralInformationPresenter


class GeneralInformationModel(var mContext: Context, var mView: GeneralInfoView) : GeneralInformationPresenter
{


    var mServices  : Call<GeneralInformationPojo>?=null
    var countryService  : Call<CountryPojo>?=null
    var statesService  : Call<StatesPojo>?=null
    var citiesService  : Call<CitiesPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(mServices!=null)
            mServices?.cancel()

        if(statesService!=null)
            statesService?.cancel()

        if(citiesService!=null)
            citiesService?.cancel()

        if(countryService!=null)
            countryService?.cancel()
    }

    override fun validate(phone: String, whats_app: String,country_id : String , state_id: String, city: String, exp_type: String, languages: String, marital_status: String, agency_signed: String, available_for: String, gender: String, pref_location: String, isAgeRestrict: Boolean, parent_name: String, parent_contact: String) {
        if (phone.length == 0 || whats_app.length == 0 || country_id.length==0 || state_id.length==0 || city.length==0 || exp_type.length==0|| languages.length==0|| marital_status.length==0|| agency_signed.length==0|| available_for.length==0|| gender.length==0|| pref_location.length==0)
        {
            mView.onFailed("Please fill all the fields.")
        }

        else if (phone.length >10 || phone.length<10)
        {
            mView.onFailed("Please enter a valid phone number.")
        }

        else if (whats_app.length >10 || whats_app.length<10)
        {
            mView.onFailed("Please enter a valid whatsapp number.")
        }

        else if(isAgeRestrict)//if user age is below 18 then this condition will run
        {
            if(parent_name.equals(""))
                mView.onFailed("Please enter your parent or guardian name.")

            else if(parent_contact.equals(""))
                mView.onFailed("Please enter your parent or guardian contact number.")

            else if (parent_contact.length >10 || parent_contact.length<10)
            {
                mView.onFailed("Please enter a valid parent or guardian contact number.")
            }
            else
                mView.isValidate()

        }
        else
            mView.isValidate()

    }


    override fun updateGeneralInformation(user_id: String, email: String, user_role_id: String, name: String, phone: String, whats_app: String,country_id :String, state_id: String, city: String, exp_type: String, languages: String, dob: String, marital_status: String, agency_signed: String, available_for: String, gender: String, pref_location: String, about: String, interested_in: String, skills: String, agency_name: String, is_public: String, parent_name: String, parent_contact: String, agency_phone: String, agency_email: String, username: String) {
        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mServices = webServices.updateGeneralInformation("application/x-www-form-urlencoded",Constants.auth_key ,user_id, email, user_role_id, name, phone, whats_app,country_id ,  state_id, city, exp_type, languages, dob, marital_status, agency_signed, available_for, gender, pref_location, about, interested_in, skills ,agency_name ,is_public ,parent_name , parent_contact ,agency_phone ,agency_email ,username
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(mServices ,Constants.RETRY_COUNT ,object : Callback<GeneralInformationPojo>
        {
            override fun onResponse(call: Call<GeneralInformationPojo>, response: Response<GeneralInformationPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GeneralInformation response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onSuccess()

                    else
                        mView.onFailed(response.body()?.status!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showProgress(false)


            }

            override fun onFailure(call: Call<GeneralInformationPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled)
                {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again")
                }
            }
        })

    }


    override fun getCountries() {
        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        countryService = webServices.getCountries("application/x-www-form-urlencoded")

        APIHelper.enqueueWithRetry(countryService ,Constants.RETRY_COUNT ,object : Callback<CountryPojo>
        {
            override fun onResponse(call: Call<CountryPojo>, response: Response<CountryPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetStates response: "+ responseStr)

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
    override fun getStates(country_id: String, type: Int)
    {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        statesService = webServices.getStatesByCountry("application/x-www-form-urlencoded" , Constants.auth_key , country_id )

        APIHelper.enqueueWithRetry(statesService ,Constants.RETRY_COUNT ,object : Callback<StatesPojo>
        {
            override fun onResponse(call: Call<StatesPojo>, response: Response<StatesPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetStates response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onGetStates(response.body() as StatesPojo , type)

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


    override fun getCities(state_id: String, type: Int) {

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
                        mView.onGetCities(response.body() as CitiesPojo, type)

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