package com.dazzlerr_usa.views.activities.editcastingprofile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityEditCastingProfileBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.activities.accountverification.AccountVerification
import com.dazzlerr_usa.views.activities.editprofile.pojos.CitiesPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.StatesPojo
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.itempicker.ItemPickerDialog
import com.dazzlerr_usa.utilities.itempicker.ItemPickerListener
import com.dazzlerr_usa.views.activities.editprofile.pojos.CountryPojo
import timber.log.Timber
import javax.inject.Inject
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.views.fragments.castingprofile.pojo.CastingProfilePojo


class EditCastingProfileActivity : AppCompatActivity(), View.OnClickListener , EditCastingProfileView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObj: ActivityEditCastingProfileBinding
    lateinit var mPresenter: EditCastingProfilePresenter
    var state : String = ""
    var city : String = ""
    var city_id : String = ""
    var country_id = ""
    var representer = ""

    var mStatesList:ArrayList<String> = ArrayList()
    var mCityList:ArrayList<String> = ArrayList()
    var mStatesIdsList:ArrayList<String> = ArrayList()
    var mCitiesIdList:ArrayList<String> = ArrayList()
    var mCountryIdsList:ArrayList<String> = ArrayList()
    var mCountriesList:ArrayList<String> = ArrayList()
    var profileDataJson=  ""
    lateinit var profileModel:CastingProfilePojo
    var statePosition = 0
    var cityPosition = 0
    var countryPosition = 0


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this, R.layout.activity_edit_casting_profile)
        initialization()
        clickListeners()

        if(profileModel.data?.size!=0)
        {
            bindingObj.castingRepresenterTxt.text = profileModel.data?.get(0)?.representer
            representer =  profileModel.data?.get(0)?.representer!!

            bindingObj.castingCityTxt.text = profileModel.data?.get(0)?.city
            city = profileModel.data?.get(0)?.city!!

            bindingObj.castingStateTxt.text = profileModel.data?.get(0)?.state_name
            bindingObj.castingCountryTxt.text = profileModel.data?.get(0)?.country



            if (isNetworkActiveWithMessage())
                mPresenter.getCountries()
        }
    }

    fun initialization()
    {
        //----------------Injecting dagger into sharedepreferences
        (application as MyApplication).myComponent.inject(this)
        bindingObj.titleLayout.titletxt.text = "Edit Profile"

        profileDataJson= intent.extras?.getString("data").toString()
        val gson = GsonBuilder().create()
        profileModel = gson.fromJson(profileDataJson, CastingProfilePojo::class.java)
        bindingObj.binderObj = profileModel.data?.get(0)
        bindingObj.executePendingBindings()

        mPresenter = EditCastingProfileModel(this , this)

        bindingObj.registerAboutCompany?.addTextChangedListener(object : TextWatcher
        {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int)
            {

            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int)
            {
                try
                {
                    bindingObj.characterLeftTxt?.text = "Characters left: " + (300 - s.length)
                }

                catch (e:Exception)
                {
                    e.printStackTrace()
                }
            }
        })

    }


    fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.editSubmitBtn.setOnClickListener(this)
        bindingObj.verifyPhoneBtn.setOnClickListener(this)
        bindingObj.verifyEmailBtn.setOnClickListener(this)
        bindingObj.castingRepresenterBtn.setOnClickListener(this)
        bindingObj.castingStateBtn.setOnClickListener(this)
        bindingObj.castingCityBtn.setOnClickListener(this)
        bindingObj.castingCountryBtn.setOnClickListener(this)
    }


    override fun onGetCountries(dataModel: CountryPojo) {

        if(dataModel?.data?.size!=0)
        {
            mCountriesList.clear()
            mCountryIdsList.clear()
            var countryIndex =0
            for (i:Int in dataModel?.data!!.indices)
            {
                mCountriesList.add(dataModel?.data?.get(i)?.country_name.toString())
                mCountryIdsList.add(dataModel?.data?.get(i)?.country_id.toString())

                if(dataModel?.data?.get(i)?.country_name.toString().equals(profileModel.data?.get(0)?.country,ignoreCase = true))
                {
                    countryIndex = i
                    country_id = mCountryIdsList[i]
                    if (isNetworkActiveWithMessage())
                        mPresenter.getStates(country_id)

                    bindingObj.castingCountryTxt.text = dataModel?.data?.get(i)?.country_name
                }
                else
                {
                    if(dataModel?.data?.get(i)?.country_name.toString().equals("india" , ignoreCase = true))
                    {
                        countryIndex = i
                    }
                }
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

            var stateIndex =0
            for (i:Int in dataModel?.data!!.indices)
            {
                mStatesList.add(dataModel?.data?.get(i)?.state_name.toString())
                mStatesIdsList.add(dataModel?.data?.get(i)?.state_id.toString())

                if(dataModel?.data?.get(i)?.state_name.toString().equals(profileModel.data?.get(0)?.state_name.toString(),ignoreCase = true))
                {
                    stateIndex = i
                    bindingObj.castingStateTxt.text = mStatesList[i]
                    state = mStatesIdsList.get(i)

                    if (isNetworkActiveWithMessage())
                        mPresenter.getCities(state)
                }
            }
            statePosition = stateIndex

        }
    }

    override fun onValidate()
    {
        mPresenter.updateCastingProfile(bindingObj.registerFirstname.text.toString().trim()
        ,bindingObj.registerCompanyname.text.toString().trim(),representer
        ,bindingObj.registerAboutCompany.text.toString().trim() ,country_id ,state
        ,bindingObj.registerZipcode.text.toString().trim()
        ,bindingObj.registerPhone.text.toString().trim()
        ,bindingObj.registerWhatsappno.text.toString().trim()
        ,bindingObj.registerWebsite.text.toString().trim()
        ,bindingObj.registerFacebookEdittext.text.toString().trim()
        ,bindingObj.registerTwitterEdittext.text.toString().trim()
        ,bindingObj.registerInstagramEdittext.text.toString().trim()
        ,city ,city_id, sharedPreferences.getString(Constants.User_id).toString())
    }

    override fun onSuccess()
    {
        Toast.makeText(this, "Profile has been updated successfully", Toast.LENGTH_LONG).show()
        setResult(102)
        finish()
    }

    override fun onGetCities(dataModel: CitiesPojo)
    {

        if(dataModel?.data?.size!=0)
        {

            mCityList.clear()
            mCitiesIdList.clear()

            var cityindex=0

            for (i:Int in dataModel?.data!!.indices)
            {
                mCityList.add(dataModel?.data?.get(i)?.city_name.toString())
                mCitiesIdList.add(dataModel?.data?.get(i)?.city_id.toString())

                if(dataModel?.data?.get(i)?.city_name.toString().equals(profileModel.data?.get(0)?.city.toString(),ignoreCase = true))
                {
                    cityindex = i
                    bindingObj.castingCityTxt.text = mCityList[i]
                    city_id = mCitiesIdList.get(i)
                }

            }

            cityPosition = cityindex

        }

    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    fun showSnackbar(message : String)
    {
        Snackbar.make(this.findViewById(android.R.id.content) , message , Snackbar.LENGTH_SHORT ).show()
    }
    override fun showProgress(showProgress: Boolean)
    {
        if(showProgress)
            startProgressBarAnim()

        else
            stopProgressBarAnim()
    }

    fun startProgressBarAnim() {

        bindingObj.aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {
        bindingObj.aviProgressBar.setVisibility(View.GONE)
    }

    fun representerSpinnerSettings()
    {
        val representerArray = resources.getStringArray(R.array.representer_array)
        var position = 0
        for(i in representerArray.indices)
        {
            if(profileModel.data?.get(0)?.representer.equals(representerArray[i] , ignoreCase = true))
                position =i
        }


        val representerPicker = ItemPickerDialog(this@EditCastingProfileActivity, representerArray.toCollection(ArrayList()), position)
        representerPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener
        {
            override fun onItemSelected(position: Int, selectedValue: String)
            {

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

            val mCountryPicker = ItemPickerDialog(this@EditCastingProfileActivity, mCountriesList, countryPosition)
            mCountryPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
                override fun onItemSelected(position: Int, selectedValue: String) {

                    countryPosition = position
                    bindingObj.castingStateTxt.text = ""
                    state = ""
                    //city_id = ""

                    country_id = mCountryIdsList.get(position)
                    Timber.e("Selected Country " + country_id)

                    bindingObj.castingCountryTxt.text = mCountriesList.get(position)
                    if (isNetworkActiveWithMessage())
                        mPresenter.getStates(country_id)

                    mCountryPicker.dismiss()
                }
            })
            mCountryPicker.show()
        }
    }

    fun mStatesListener()
    {

        if(mStatesList.size!=0)
        {

            val mStatePicker = ItemPickerDialog(this@EditCastingProfileActivity, mStatesList, statePosition)
            mStatePicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
                override fun onItemSelected(position: Int, selectedValue: String) {

                    statePosition = position
                    bindingObj.castingCityTxt.text = ""
                    city = ""
                    city_id = ""

                    state = mStatesIdsList.get(position)
                    bindingObj.castingStateTxt.text = mStatesList.get(position)
                    Timber.e("Selected current State " + state)
                    if (isNetworkActiveWithMessage())
                        mPresenter.getCities(state)

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
            val mCityPicker = ItemPickerDialog(this@EditCastingProfileActivity, mCityList, cityPosition)
            mCityPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
                override fun onItemSelected(position: Int, selectedValue: String) {

                    cityPosition = position
                    bindingObj.castingCityTxt.text = mCityList.get(position)
                    city = mCityList.get(position)
                    city_id = mCitiesIdList.get(position)
                    mCityPicker.dismiss()
                }
            })

            mCityPicker.show()
        }
    }

    override fun onClick(v: View?) {

        when(v?.id)
        {
            R.id.leftbtn-> finish()

            R.id.editSubmitBtn->
            {
                if(isNetworkActiveWithMessage())
                {
                    mPresenter.validateCastingProfile(bindingObj.registerFirstname.text.toString().trim()
                            , representer
                            , country_id
                            , state
                            , bindingObj.registerPhone.text.toString().trim()
                            , city
                            , bindingObj.registerZipcode.text.toString().trim()
                            , bindingObj.registerEmail.text.toString().trim()
                            , bindingObj.registerWhatsappno.text.toString().trim()
                            , bindingObj.registerWebsite.text.toString().trim()
                            , bindingObj.registerFacebookEdittext.text.toString().trim()
                            , bindingObj.registerTwitterEdittext.text.toString().trim()
                            , bindingObj.registerInstagramEdittext.text.toString().trim()
                    )

                }
            }

            R.id.verifyEmailBtn->
            {
                val bundle = Bundle()
                bundle.putString("type" , "emailOrPhoneVerification")
                val newIntent = Intent(this, AccountVerification::class.java)
                newIntent.putExtras(bundle)
                startActivity(newIntent)
            }

            R.id.verifyPhoneBtn->
            {
                val bundle = Bundle()
                bundle.putString("type" , "emailOrPhoneVerification")
                val newIntent = Intent(this, AccountVerification::class.java)
                newIntent.putExtras(bundle)
                startActivity(newIntent)
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

    override fun onResume()
    {
        super.onResume()

        bindingObj.registerEmail.setText(sharedPreferences.getString(Constants.User_Email))
        bindingObj.registerPhone.setText(sharedPreferences.getString(Constants.User_Phone))

/*

        if(sharedPreferences.getString(Constants.Is_Phone_Verified).equals("0")|| sharedPreferences.getString(Constants.Is_Phone_Verified).equals("")|| sharedPreferences.getString(Constants.Is_Phone_Verified).equals("null"))
        {
            bindingObj.verifyPhoneBtn.visibility = View.VISIBLE
            bindingObj.isPhoneVerifiedText.visibility =View.GONE
        }
        else
        {
            bindingObj.verifyPhoneBtn.visibility = View.GONE
            bindingObj.isPhoneVerifiedText.visibility =View.VISIBLE
        }
*/

        if(sharedPreferences.getString(Constants.Is_Email_Verified).equals("0")|| sharedPreferences.getString(Constants.Is_Email_Verified).equals("")|| sharedPreferences.getString(Constants.Is_Email_Verified).equals("null"))
        {
            bindingObj.verifyEmailBtn.visibility = View.VISIBLE
            bindingObj.isEmailVerifiedText.visibility =View.GONE
        }
        else
        {
            bindingObj.verifyEmailBtn.visibility = View.GONE
            bindingObj.isEmailVerifiedText.visibility =View.VISIBLE
        }

    }


}
