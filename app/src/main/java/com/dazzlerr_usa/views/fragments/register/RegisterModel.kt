package com.dazzlerr_usa.views.fragments.register

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
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.activities.editprofile.pojos.CitiesPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.CountryPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.StatesPojo
import com.google.gson.GsonBuilder
import java.util.regex.Matcher
import java.util.regex.Pattern


class RegisterModel(var mContext: Context, var mView: RegisterView) : RegisterPresenter
{


    var mServices  : Call<RegisterPojo>?=null
    var statesService  : Call<StatesPojo>?=null
    var citiesService  : Call<CitiesPojo>?=null
    var countryService  : Call<CountryPojo>?=null

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

    override fun validateModel(user_role_id: String, email: String, password: String, confirm_password: String, phone: String, fname:String, lname:String )
    {

        if (email.length == 0 || password.length == 0 || confirm_password.length == 0 || phone.length==0 || fname.length==0 || lname.length==0)
        {
            mView.onFailed("Please fill all the fields.")
        }

        else if (user_role_id.length == 0)
        {
            mView.onFailed("Please select your role.")
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


        else if (!isValidPassword(password))
        {
            mView.onFailed("Please follow the instruction to set the password.")
        }

        else if (!password.equals(confirm_password))
        {
            mView.onFailed("Password and confirm password doesn't matched.")
        }

        else
        {
            mView.isRegisterModelValidate(true)
        }
    }

    override fun validateCasting(first_name: String, last_name: String, email: String, password: String,confirm_password: String ,  phone: String, representer: String,country:String , state: String, city: String, zipcode: String ,whatsppNumber:String) {
        if (first_name.length == 0 ||last_name.length == 0||  email.length == 0 || password.length == 0 || confirm_password.length == 0 || phone.length==0 || phone.length==0 || representer.length==0 ||country.length==0 || state.length==0 || city.length==0|| zipcode.length==0 )
        {
            mView.onFailed("Please fill all the fields.")
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

        else if (whatsppNumber.isNotEmpty() && (whatsppNumber.length >10 || whatsppNumber.length<10))
        {
            mView.onFailed("Please enter a valid whatsapp number.")
        }

        else if (whatsppNumber.isNotEmpty() && (!Patterns.PHONE.matcher(whatsppNumber).matches()))
        {
            mView.onFailed("Please enter a valid whatsapp number.")
        }

        else if (!isValidPassword(password))
        {
            mView.onFailed("Please follow the instruction to set the password.")
        }

        else if (!password.equals(confirm_password))
        {
            mView.onFailed("Password and confirm password doesn't matched.")
        }

        else
        {
            mView.isRegisterCastingValidate(true)
        }
    }

    //*****************************************************************
    fun isValidPassword(password: String): Boolean {

        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[A-Z]).{6,36}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)

        return matcher.matches()

    }

    override fun registerModel(user_role_id: String, email: String, password: String, phone: String, name: String ,user_role:String, device_id: String, device_brand: String, device_model: String, device_type: String, operating_system: String ) {
        mView.showProgress(true)

        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mServices = webServices.registerApi("application/x-www-form-urlencoded" ,Constants.auth_key,user_role_id, email , password ,phone , name , user_role  , device_id , device_brand , device_model , device_type , operating_system , Constants.App_Version)
        APIHelper.enqueueWithRetry(mServices , Constants.RETRY_COUNT  , object : Callback<RegisterPojo>
        {
            override fun onResponse(call: Call<RegisterPojo>, response: Response<RegisterPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("Register response: "+ responseStr)

                    if(response.body()?.success!!)
                    mView.onRegisterSuccess(response.body()?.data!!)

                    else
                        mView.onFailed(response.body()?.status!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showProgress(false)


            }

            override fun onFailure(call: Call<RegisterPojo>, t: Throwable) {
                Timber.e("Something went wrong "+ t.message)

                if(!call.isCanceled)
                {
                    mView.showProgress(false)
                    mView.onFailed("Something went wrong! Please try again")
                }
            }
        })

    }

    override fun registerCasting(first_name: String, last_name: String, email: String, password: String, phone: String, whatsapp: String, website: String, company: String, about: String, representer: String,country:String, state: String, city: String, city_id : String, zipcode: String ,user_role:String )
    {
        mView.showProgress(true)

        (mContext as MainActivity).getDeviceInfo()
        val webServices = RetrofitClient.getClient(Constants.baseUrl).create(WebServices::class.java)
        mServices = webServices.registerCastingsApi("application/x-www-form-urlencoded"  ,Constants.auth_key,first_name+" "+last_name, email , phone, "6" ,password , company,  representer ,country ,  state , city ,city_id ,whatsapp , zipcode ,about ,user_role
                ,(mContext as MainActivity).sharedPreferences.getString(Constants.device_id).toString()
                ,(mContext as MainActivity).sharedPreferences.getString(Constants.DEVICE_BRAND).toString()
                , (mContext as MainActivity).sharedPreferences.getString(Constants.DEVICE_MODEL).toString()
                ,"Android"
                , (mContext as MainActivity).sharedPreferences.getString(Constants.DEVICE_VERSION).toString()
                , Constants.App_Version)
        APIHelper.enqueueWithRetry(mServices ,Constants.RETRY_COUNT ,object : Callback<RegisterPojo>
        {
            override fun onResponse(call: Call<RegisterPojo>, response: Response<RegisterPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("Register response: "+ responseStr)

                    if(response.body()?.success!!)
                        mView.onRegisterSuccess(response.body()?.data!!)

                    else
                        mView.onFailed(response.body()?.status!!)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mView.showProgress(false)


            }

            override fun onFailure(call: Call<RegisterPojo>, t: Throwable) {
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
        countryService = webServices.getCountries("application/x-www-form-urlencoded" )

        APIHelper.enqueueWithRetry(countryService ,Constants.RETRY_COUNT ,object : Callback<CountryPojo>
        {
            override fun onResponse(call: Call<CountryPojo>, response: Response<CountryPojo>)
            {
                try {

                    val gson = GsonBuilder().create()
                    val responseStr = gson.toJson(response.body())

                    Timber.e("GetCountry response: "+ responseStr)

                    if(response.body()?.status!!)
                        mView.onGetCountry(response.body() as CountryPojo)

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
        statesService = webServices.getStatesByCountry("application/x-www-form-urlencoded", Constants.auth_key ,country_id )

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