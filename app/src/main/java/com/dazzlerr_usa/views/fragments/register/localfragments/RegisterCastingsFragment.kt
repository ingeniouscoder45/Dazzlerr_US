package com.dazzlerr_usa.views.fragments.register.localfragments


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentRegisterCastingsBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.fragments.ParentFragment
import com.dazzlerr_usa.views.fragments.modelaccountverification.AccountVerificationFragment
import com.dazzlerr_usa.views.activities.editprofile.pojos.CitiesPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.StatesPojo
import com.dazzlerr_usa.views.fragments.register.RegisterModel
import com.dazzlerr_usa.views.fragments.register.RegisterPojo
import com.dazzlerr_usa.views.fragments.register.RegisterPresenter
import com.dazzlerr_usa.views.fragments.register.RegisterView
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.views.activities.weblinks.WebLinksActivity
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.itempicker.ItemPickerDialog
import com.dazzlerr_usa.utilities.itempicker.ItemPickerListener
import com.dazzlerr_usa.views.activities.editprofile.pojos.CountryPojo
import com.google.android.gms.ads.AdRequest
import timber.log.Timber
import java.lang.Exception
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */

class RegisterCastingsFragment : ParentFragment() , View.OnClickListener ,RegisterView
{

    var state_id : String = ""
    var state_name : String = ""
    var country_id = ""
    var city : String = ""
    var city_id : String = ""
    var representer = ""
    lateinit var mPresenter: RegisterPresenter
    lateinit var bindingObj:FragmentRegisterCastingsBinding

    var mStatesList:ArrayList<String> = ArrayList()
    var mCityList:ArrayList<String> = ArrayList()
    var mCountriesList:ArrayList<String> = ArrayList()
    var mStatesIdsList:ArrayList<String> = ArrayList()
    var mCitiesIdList:ArrayList<String> = ArrayList()
    var mCountryIdsList:ArrayList<String> = ArrayList()
    var statePosition = 0
    var cityPosition = 0
    var countryPosition = 0
    var representerPosition = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        bindingObj =  DataBindingUtil.inflate(inflater , R.layout.fragment_register_castings, container, false)
        initializations()
        clickListeners()
        apiCalling()
        return bindingObj.root
    }

    fun initializations()
    {
        //Initializing Google ads
/*        val testDeviceIds: List<String> = Arrays.asList("A86FED701CDC6E77D341DEBA488306E3")
        val configuration: RequestConfiguration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)*/

        //MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        bindingObj.adView.visibility = View.VISIBLE
        bindingObj.adView.loadAd(adRequest)
        //--------------------------


        bindingObj.titleLayout.titletxt.text = "Register as Casting/Recruiter"

        bindingObj.titleLayout.rightbtn.visibility = View.GONE
        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        mPresenter = RegisterModel(activity as Activity , this)

    }

    private fun apiCalling()
    {

        if((activity as MainActivity).isNetworkActive())
        {
            mPresenter.getCountries()
        }

        else
        {

            val dialog = CustomDialog(activity as Activity)
                    dialog.setTitle(resources.getString(R.string.no_internet_txt))
                    dialog.setMessage(resources.getString(R.string.connection_txt))
                    dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                        apiCalling()
                    })
                    dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })
                    dialog.show()
        }
    }

    fun clickListeners()
    {

        bindingObj.termsOfUseBtn.setOnClickListener(this)
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.registerSubmitBtn.setOnClickListener(this)
        bindingObj.castingRepresenterBtn.setOnClickListener(this)
        bindingObj.castingStateBtn.setOnClickListener(this)
        bindingObj.castingCityBtn.setOnClickListener(this)
        bindingObj.castingCountryBtn.setOnClickListener(this)
    }

    private fun representerSpinnerSettings()
    {
        val representerArray = resources.getStringArray(R.array.representer_array)
        val representerPicker = ItemPickerDialog(activity as Activity, representerArray.toCollection(ArrayList()), representerPosition)
        representerPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener
        {
            override fun onItemSelected(position: Int, selectedValue: String)
            {

                representerPosition = position
                representer = representerArray[position]
                bindingObj.castingRepresenterTxt.text = representer
                representerPicker.dismiss()
            }
        })

        representerPicker.show()

    }


    fun mCountryListener()
    {

        if(mCountriesList.size!=0)
        {

            val mCountryPicker = ItemPickerDialog(activity as Activity, mCountriesList, countryPosition)
            mCountryPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
                override fun onItemSelected(position: Int, selectedValue: String) {

                    countryPosition = position
                    bindingObj.castingStateTxt.text = ""
                    state_id = ""
                    //city_id = ""

                    country_id = mCountryIdsList.get(position)
                    Timber.e("Selected Country " + country_id)

                    bindingObj.castingCountryTxt.text = mCountriesList.get(position)
                    if (activity?.isNetworkActiveWithMessage()!!)
                        mPresenter.getStates(country_id)

                    mCountryPicker.dismiss()
                }
            })
            mCountryPicker.show()
        }
    }

    private fun mStatesListener()
    {

        if(mStatesList.size!=0)
        {

            val mStatePicker = ItemPickerDialog(activity as Activity, mStatesList, statePosition)
            mStatePicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
                override fun onItemSelected(position: Int, selectedValue: String) {

                    statePosition = position
                    bindingObj.castingCityTxt.text = ""
                    bindingObj.castingStateTxt.text = mStatesList.get(position)
                    city = ""
                    city_id = ""

                    state_id = mStatesIdsList.get(position)
                    state_name = mStatesList.get(position)
                    Timber.e("Selected State " + state_name)
                    if((activity as Activity).isNetworkActiveWithMessage())
                        mPresenter.getCities(state_id)


                    mStatePicker.dismiss()
                }
            })
            mStatePicker.show()
        }
    }

    fun mCityListener()
    {
        if(mCityList.size!=0)
        {
            val mCityPicker = ItemPickerDialog(activity as Activity, mCityList, cityPosition)
            mCityPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
                override fun onItemSelected(position: Int, selectedValue: String) {

                    cityPosition = position
                    bindingObj.castingCityTxt.text = mCityList.get(position)
                    city = mCityList.get(position)
                    city_id = mCitiesIdList.get(position)
                    Timber.e("Selected city and id " + city +"  "+ city_id)
                    mCityPicker.dismiss()
                }
            })

            mCityPicker.show()
        }
    }



    override fun onRegisterSuccess(dataModel: MutableList<RegisterPojo.DataBean>)
    {

        if(dataModel.size!=0)
        {
            showSnackbar("Registered successfully")
            (activity as MainActivity).sharedPreferences.saveString(Constants.User_id , ""+dataModel.get(0).user_id)
            (activity as MainActivity).sharedPreferences.saveString(Constants.Employer_id , ""+dataModel.get(0).employer_id)
            (activity as MainActivity).sharedPreferences.saveString(Constants.Token , "")
            (activity as MainActivity).sharedPreferences.saveString(Constants.User_name , bindingObj.registerFirstname.text.toString().trim()+" "+bindingObj.registerLastname.text.toString().trim())
            (activity as MainActivity).sharedPreferences.saveString(Constants.User_Role , "casting")
            (activity as MainActivity).sharedPreferences.saveString(Constants.User_RoleId , "6")
            (activity as MainActivity).sharedPreferences.saveString(Constants.Exp_type , "")
            (activity as MainActivity).sharedPreferences.saveString(Constants.Current_city , city)
            (activity as MainActivity).sharedPreferences.saveString(Constants.Current_state , state_name)
            (activity as MainActivity).sharedPreferences.saveString(Constants.User_pic , "")
            (activity as MainActivity).sharedPreferences.saveString(Constants.User_Email , bindingObj.registerEmail.text.toString().trim())
            (activity as MainActivity).sharedPreferences.saveString(Constants.User_Phone ,  bindingObj.registerPhone.text.toString().trim())
            (activity as MainActivity).sharedPreferences.saveString(Constants.Is_Email_Verified , "0")// 0 or 1 format
            (activity as MainActivity).sharedPreferences.saveString(Constants.Is_Phone_Verified ,"0") // 0 or 1 format
            (activity as MainActivity).sharedPreferences.saveString(Constants.Is_Documement_Submitted ,"0") // 0 or 1 format
            (activity as MainActivity).sharedPreferences.saveString(Constants.Is_Documement_Verified ,"0") // 0 or 1 format
            (activity as MainActivity).sharedPreferences.saveString(Constants.IsProfile_published , "1") // 0 or 1 format
            (activity as MainActivity).sharedPreferences.saveString(Constants.Account_type , "Free") // 0 or 1 format
            (activity as MainActivity).sharedPreferences.saveString(Constants.identity_proof , "")
            (activity as MainActivity).sharedPreferences.saveString(Constants.identity_video , "")
            (activity as MainActivity).sharedPreferences.saveString(Constants.User_pic,""+dataModel.get(0).profile_pic.toString())
            //(activity as MainActivity).fragment_manager.popBackStackImmediate()

            val bundle = Bundle()
            bundle.putString("registerType" , "Casting")
            val fragment = AccountVerificationFragment()
            fragment.arguments = bundle
            (activity as MainActivity).LoadFragment(fragment)

        }
    }


    override fun onGetCountry(dataModel: CountryPojo)
    {
        if(dataModel?.data?.size!=0)
        {
            mCountriesList.clear()
            mCountryIdsList.clear()
            var countryIndex =0
            for (i:Int in dataModel?.data!!.indices)
            {
                mCountriesList.add(dataModel?.data?.get(i)?.country_name.toString())
                mCountryIdsList.add(dataModel?.data?.get(i)?.country_id.toString())

            }
            countryPosition = countryIndex

        }
    }

    override fun onGetStates(dataModel: StatesPojo)
    {
        if(dataModel?.data?.size!=0)
        {
            mStatesList.clear()
            mStatesIdsList.clear()


            for (i:Int in dataModel?.data!!.indices)
            {
                mStatesList.add(dataModel?.data?.get(i)?.state_name.toString())
                mStatesIdsList.add(dataModel?.data?.get(i)?.state_id.toString())
            }
        }
    }


    override fun onGetCities(dataModel: CitiesPojo)
    {

        if(dataModel?.data?.size!=0)
        {

                mCityList.clear()
                mCitiesIdList.clear()
                for (i:Int in dataModel?.data!!.indices)
                {
                    mCityList.add(dataModel?.data?.get(i)?.city_name.toString())
                    mCitiesIdList.add(dataModel?.data?.get(i)?.city_id.toString())
                }


        }

    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    fun showSnackbar(message : String)
    {
        try {
            Snackbar.make((activity as Activity).findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
    override fun showProgress(showProgress: Boolean)
    {
        try {

        if(showProgress)
            (activity as MainActivity).startProgressBarAnim()

        else
            (activity as MainActivity).stopProgressBarAnim()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun isRegisterModelValidate(isValid: Boolean)
    {

    }

    @SuppressLint("DefaultLocale")
    override fun isRegisterCastingValidate(isValid: Boolean) {
        mPresenter.registerCasting(bindingObj.registerFirstname.text.toString().trim()
                ,bindingObj.registerLastname.text.toString().trim()
                ,bindingObj.registerEmail.text.toString().trim().toLowerCase()
                ,bindingObj.registerPassword.text.toString().trim()
                ,bindingObj.registerPhone.text.toString().trim()
                ,bindingObj.registerWhatsappno.text.toString().trim()
                ,bindingObj.registerWebsite.text.toString().trim()
                ,bindingObj.registerCompanyname.text.toString().trim()
                ,bindingObj.registerAboutCompany.text.toString().trim()
                ,representer,country_id ,state_id ,city ,city_id,bindingObj.registerZipcode.text.toString().trim(),"casting")
    }

    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.leftbtn->(activity as MainActivity).backpress()

            R.id.registerSubmitBtn->
            {
                if((activity as MainActivity).isNetworkActive())
                {
                    mPresenter.validateCasting(bindingObj.registerFirstname.text.toString().trim()
                    ,bindingObj.registerLastname.text.toString().trim()
                    ,bindingObj.registerEmail.text.toString().trim()
                    ,bindingObj.registerPassword.text.toString().trim()
                    ,bindingObj.registerConfirmPassword.text.toString().trim()
                    ,bindingObj.registerPhone.text.toString().trim()
                    ,representer,country_id, state_id ,city ,bindingObj.registerZipcode.text.toString().trim()
                            ,bindingObj.registerWhatsappno.text.toString().trim())
                }

                else
                {

                    val dialog = CustomDialog(activity as Activity)
                            dialog.setTitle(resources.getString(R.string.no_internet_txt))
                            dialog.setMessage(resources.getString(R.string.connection_txt))
                            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                                apiCalling()
                            })
                            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                                dialog.dismiss()
                            })
                            dialog.show()
                }
            }

            R.id.termsOfUseBtn->
            {

                val bundle = Bundle()
                bundle.putString("type", "Terms")
                val newIntent = Intent(activity, WebLinksActivity::class.java)
                newIntent.putExtras(bundle)
                activity?.startActivity(newIntent)
            }


            R.id.castingRepresenterBtn->
            {
                representerSpinnerSettings()
            }

            R.id.castingCountryBtn->
            {
                mCountryListener()
            }

            R.id.castingStateBtn->
            {
                mStatesListener()
            }

            R.id.castingCityBtn->
            {
                mCityListener()
            }

        }

    }


}
