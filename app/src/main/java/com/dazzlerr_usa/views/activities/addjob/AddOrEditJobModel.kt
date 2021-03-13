package com.dazzlerr_usa.views.activities.addjob

import android.content.Context
import android.util.Patterns
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.utilities.retrofit.APIHelper
import com.dazzlerr_usa.views.activities.editprofile.pojos.CitiesPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.StatesPojo
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.utilities.retrofit.RetrofitClient
import com.dazzlerr_usa.utilities.retrofit.WebServices
import com.dazzlerr_usa.views.activities.editprofile.pojos.CountryPojo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class AddOrEditJobModel(val mContext: Context, val mView:AddOrEditJobView) : AddOrEditJobPresenter
{

    var addJobService :Call<AddOrEditJobPojo>?=null

    var countryService  : Call<CountryPojo>?=null
    var statesService  : Call<StatesPojo>?=null
    var citiesService  : Call<CitiesPojo>?=null

    override fun cancelRetrofitRequest()
    {
        if(addJobService!=null)
        addJobService?.cancel()
    }

    override fun isValid(title: String, description: String, start_date: String, end_date: String, budget: String
                         , address: String, country_id: String, city_id: String, state_id: String, job_role_id: String, gender: String
                         , min_age_range: String, max_age_range:String , tags: String, budget_duration: String, contact_email: String
                         , contact_mobile: String, contact_whatsapp: String, contact_person: String , vacancies: String) {

        if (title.length == 0 || description.length == 0 || start_date.length == 0
                || end_date.length == 0 || budget.length == 0|| address.length == 0
                || country_id.length == 0|| city_id.length == 0 || state_id.length == 0|| job_role_id.length == 0|| gender.length == 0|| min_age_range.length == 0|| max_age_range.length == 0
                || tags.length == 0 || budget_duration.length == 0|| contact_email.length == 0|| contact_mobile.length == 0|| contact_whatsapp.length == 0|| contact_person.length == 0|| vacancies.length == 0
        )
        {
            mView.onFailed("Please fill all the fields.")
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(contact_email).matches())
        {
            mView.onFailed("Please enter a valid email address.")
        }

        else if (!Patterns.PHONE.matcher(contact_mobile).matches())
        {
            mView.onFailed("Please enter a valid phone number.")
        }

        else if (contact_mobile.length >10 || contact_mobile.length<10)
        {
            mView.onFailed("Please enter a valid phone number.")
        }

        else if (!Patterns.PHONE.matcher(contact_whatsapp).matches())
        {
            mView.onFailed("Please enter a valid whatsapp number.")
        }

        else if (contact_whatsapp.length >10 || contact_whatsapp.length<10)
        {
            mView.onFailed("Please enter a valid whatsapp number.")
        }

        else if(min_age_range>=max_age_range)
        {
            mView.onFailed("Please enter a valid minimum and maximum age range.")
        }
        else
            mView.onValidate()
    }

    override fun addJob(user_id: String, employer_id: String, title: String, description: String, start_date: String, end_date: String, budget: String, address: String,country_id: String, city: String, city_id: String, state_id: String, job_role_id: String, gender: String, age_range: String, audition: String, vacancies: String, job_expiry: String, tags: String, budget_duration: String, contact_email: String, contact_mobile: String, contact_whatsapp: String, contact_person: String) {

        mView.showProgress(true )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        addJobService = webServices.addJob("application/x-www-form-urlencoded"  ,Constants.auth_key,user_id, employer_id, title, description, start_date, end_date, budget, address,country_id , city, city_id, state_id, job_role_id, gender, age_range, audition, vacancies, job_expiry, tags, budget_duration, contact_email, contact_mobile, contact_whatsapp, contact_person
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(addJobService ,Constants.RETRY_COUNT , object : Callback<AddOrEditJobPojo>
        {
            override fun onResponse(call: Call<AddOrEditJobPojo>, response: Response<AddOrEditJobPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("contact response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSuccess()

                    else
                        mView.onFailed(response.body()?.message!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<AddOrEditJobPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }


    override fun updateJob(user_id: String, call_id: String, title: String, description: String, start_date: String, end_date: String, budget: String, address: String, country_id: String, city: String, city_id: String, state_id: String, job_role_id: String, gender: String, age_range: String, audition: String, vacancies: String, job_expiry: String, tags: String, budget_duration: String, contact_email: String, contact_mobile: String, contact_whatsapp: String, contact_person: String) {

        mView.showProgress(true )

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        addJobService = webServices.updateJob("application/x-www-form-urlencoded" ,Constants.auth_key ,user_id, call_id, title, description, start_date, end_date, budget, address, country_id , city, city_id, state_id, job_role_id, gender, age_range, audition, vacancies, job_expiry, tags, budget_duration, contact_email, contact_mobile, contact_whatsapp, contact_person
                , DeviceInfoConstants.DEVICE_TOKEN, DeviceInfoConstants.DEVICE_ID
                , DeviceInfoConstants.DEVICE_BRAND , DeviceInfoConstants.DEVICE_MODEL
                , DeviceInfoConstants.DEVICE_TYPE, DeviceInfoConstants.DEVICE_VERSION)

        APIHelper.enqueueWithRetry(addJobService ,Constants.RETRY_COUNT ,object : Callback<AddOrEditJobPojo>
        {
            override fun onResponse(call: Call<AddOrEditJobPojo>, response: Response<AddOrEditJobPojo>)
            {
                try
                {
                    mView.showProgress(false )
                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("contact response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onSuccess()

                    else
                        mView.onFailed(response.body()?.message!!)

                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    mView.onFailed("Something went wrong! Please try again later")
                }

            }

            override fun onFailure(call: Call<AddOrEditJobPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)
                mView.showProgress(false  )
                if(!call.isCanceled)
                    mView.onFailed("Something went wrong! Please try again later")

            }


        })

    }

    override fun getCountries() {


        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        countryService = webServices.getCountries("application/x-www-form-urlencoded")

        APIHelper.enqueueWithRetry(countryService  ,Constants.RETRY_COUNT ,object : Callback<CountryPojo>
        {
            override fun onResponse(call: Call<CountryPojo>, response: Response<CountryPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetCountry response: "+ responseStr)

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



    override fun getStates(country_id : String)
    {

        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        statesService = webServices.getStatesByCountry("application/x-www-form-urlencoded" ,Constants.auth_key, country_id)

        APIHelper.enqueueWithRetry(statesService  ,Constants.RETRY_COUNT ,object : Callback<StatesPojo>
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


}