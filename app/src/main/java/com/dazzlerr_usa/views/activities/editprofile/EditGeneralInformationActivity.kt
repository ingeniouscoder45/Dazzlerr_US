package com.dazzlerr_usa.views.activities.editprofile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityEditGeneralSettingsBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.activities.accountverification.AccountVerification
import com.dazzlerr_usa.views.activities.editprofile.pojos.CitiesPojo
import com.dazzlerr_usa.views.activities.editprofile.adapters.SkillsAndInterestsAdapter
import com.dazzlerr_usa.views.activities.editprofile.pojos.StatesPojo
import com.dazzlerr_usa.views.fragments.profile.ProfileModel
import com.dazzlerr_usa.views.fragments.profile.ProfilePojo
import com.dazzlerr_usa.views.fragments.profile.ProfilePresenter
import com.dazzlerr_usa.views.fragments.profile.ProfileView
import com.google.android.flexbox.*
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.utilities.itempicker.ItemPickerDialog
import com.dazzlerr_usa.utilities.itempicker.ItemPickerListener
import com.dazzlerr_usa.views.activities.editprofile.models.GeneralInformationModel
import com.dazzlerr_usa.views.activities.editprofile.models.SetUsernameModel
import com.dazzlerr_usa.views.activities.editprofile.pojos.CountryPojo
import com.dazzlerr_usa.views.activities.editprofile.presenters.GeneralInformationPresenter
import com.dazzlerr_usa.views.activities.editprofile.presenters.SetUsernamePresenter
import com.dazzlerr_usa.views.activities.editprofile.views.GeneralInfoView
import com.dazzlerr_usa.views.activities.editprofile.views.SetUserrnameView
import com.dazzlerr_usa.views.activities.mymembership.MyMembershipActivity
import com.dazzlerr_usa.views.fragments.profile.pojo.GetContactDetailsPojo
import com.github.ybq.android.spinkit.SpinKitView
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class EditGeneralInformationActivity : AppCompatActivity() , View.OnClickListener , GeneralInfoView, ProfileView, SetUserrnameView
{

    override fun onShortList(status: String)
    {
        //Not in use
    }
    override fun onFollowOrUnfollow(status: String)
    {
        //Not in use
    }
    override fun onLikeOrDislike(status: String)
    {
        //Not in use
    }
    override fun onGetContactDetails(model: GetContactDetailsPojo)
    {
        //Not in use
    }

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var bindingObj : ActivityEditGeneralSettingsBinding
    lateinit var mPresenter : GeneralInformationPresenter
    lateinit var mUsernamePresenter:SetUsernamePresenter
    lateinit var profilePresenter : ProfilePresenter
    var mSkillsList : MutableList<String> = ArrayList()
    var mInterestsList : MutableList<String> = ArrayList()
    lateinit var skillsAdapter: SkillsAndInterestsAdapter
    lateinit var interestsAdapter: SkillsAndInterestsAdapter
    var mCountriesList:ArrayList<String> = ArrayList()
    var mStatesList:ArrayList<String> = ArrayList()
    var mCurrentStatesList:ArrayList<String> = ArrayList()
    var mCityList:ArrayList<String> = ArrayList()
    var mCurrentCityList:ArrayList<String> = ArrayList()
    var mStatesIdsList:ArrayList<String> = ArrayList()
    var mCurrentStatesIdsList:ArrayList<String> = ArrayList()
    var mCountryIdsList:ArrayList<String> = ArrayList()
    var gender : String = ""
    var isAgencySigned : String = ""
    var exp_type : String = ""
    var maritalStatus : String = ""
    var isPhonenoPublic : String = ""
    var country_id = ""
    var current_country_id = ""
    var state : String = ""
    var current_state : String = ""
    var city : String = ""
    var current_city : String = ""
    lateinit var profilePojo: ProfilePojo
    var isAgeRestrict:Boolean = false//if user age is below 18 then it will be true
    var selectedDateOfBirth =""
    var countryPosition = 0
    var currentCountryPosition = 0
    var statePosition = 0
    var currentStatePosition = 0
    var cityPosition = 0
    var currentCityPosition = 0
    var isUsernameAvailable = false
    lateinit var aviDialogProgressBar: SpinKitView
    lateinit var usernameMessageTxt : TextView
    lateinit var usernameDialog  :Dialog
    lateinit var validateUsernameBtn  :Button

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this ,R.layout.activity_edit_general_settings)
        initializations()
        clickListeners()
    }

    fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)

        bindingObj.titleLayout.titletxt.text = "General information"
        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        mPresenter = GeneralInformationModel(this as Activity , this)
        mUsernamePresenter = SetUsernameModel(this as Activity , this)
        profilePresenter = ProfileModel(this as Activity , this)

        if(isNetworkActiveWithMessage())
            profilePresenter.getProfile(sharedPreferences.getString(Constants.User_id).toString() ,"")


        bindingObj.generalInfoAboutEdittext?.addTextChangedListener(object : TextWatcher
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

    @SuppressLint("ClickableViewAccessibility")
    fun populateProfileData()
    {

        bindingObj.generalInfoName.setText(profilePojo.data?.get(0)?.name)
        bindingObj.generalInfoUserName.setText(profilePojo.data?.get(0)?.username)
        bindingObj.generalInfoPhoneno.setText(profilePojo.data?.get(0)?.phone.toString())
        bindingObj.generalInfoWhatsappno.setText(profilePojo.data?.get(0)?.whats_app.toString())
        bindingObj.GenralInfoAgencyname.setText(profilePojo.data?.get(0)?.agency_name)
        bindingObj.GenralInfoAgencyphone.setText(profilePojo.data?.get(0)?.agency_phone)
        bindingObj.GenralInfoAgencyemail.setText(profilePojo.data?.get(0)?.agency_email)

        bindingObj.genralInfoStateTxt.setText(profilePojo.data?.get(0)?.state_name)
        bindingObj.genralInfoCountryTxt.setText(profilePojo.data?.get(0)?.country)
        bindingObj.genralInfoCurrentStateTxt.setText(profilePojo.data?.get(0)?.current_state)
        bindingObj.generalInfoCityTxt.setText(profilePojo.data?.get(0)?.city)
        bindingObj.generalInfoCurrentCityTxt.setText(profilePojo.data?.get(0)?.current_city)

        if(profilePojo.data?.get(0)?.username?.isEmpty()!!)
            bindingObj.validateUsernameBtn.text = "Set"

        else
            bindingObj.validateUsernameBtn.text = "Reset"

        city = profilePojo.data?.get(0)?.city!!
        current_city = profilePojo.data?.get(0)?.current_city!!


        if(!profilePojo.data?.get(0)?.dob.equals(""))
        {
            selectedDateOfBirth = profilePojo.data?.get(0)?.dob!!
            dateFormat(bindingObj.generalInfoDateofbirth, profilePojo.data?.get(0)?.dob!!)
        }

        if(!profilePojo.data?.get(0)?.parent_name?.equals("")!!)
        {
            isAgeRestrict = true
            bindingObj.parentalControlLayout.visibility = View.VISIBLE
            bindingObj.generalInfoParentName.setText(profilePojo.data?.get(0)?.parent_name)
            bindingObj.generalInfoParentPhoneno.setText(profilePojo.data?.get(0)?.parent_contact)
        }

        else
        {
            isAgeRestrict = false
            bindingObj.parentalControlLayout.visibility = View.GONE
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            bindingObj.generalInfoAboutEdittext.setText(Html.fromHtml( profilePojo.data?.get(0)?.about, Html.FROM_HTML_MODE_LEGACY))
        }

        else
        {
            bindingObj.generalInfoAboutEdittext.setText(Html.fromHtml(profilePojo.data?.get(0)?.about))
        }

        if(profilePojo.data?.get(0)?.gender.toString().equals("male", ignoreCase = true))
            bindingObj.generalInfoMaleRadiobtn.isChecked =true

        else if(profilePojo.data?.get(0)?.gender.toString().equals("female", ignoreCase = true))
            bindingObj.generalInfoFemaleRadiobtn.isChecked =true

        if(profilePojo.data?.get(0)?.agency_signed.toString().equals("0", ignoreCase = true))
            bindingObj.generalInfoNoRadiobtn.isChecked =true

        else if(profilePojo.data?.get(0)?.agency_signed.toString().equals("1", ignoreCase = true))
            bindingObj.generalInfoYesRadiobtn.isChecked =true

        if(profilePojo.data?.get(0)?.marital_status.toString().equals("0", ignoreCase = true))
            bindingObj.generalInfoSingleRadiobtn.isChecked =true

        else if(profilePojo.data?.get(0)?.marital_status.toString().equals("1", ignoreCase = true))
            bindingObj.generalInfoMarriedRadiobtn.isChecked =true

        else if(profilePojo.data?.get(0)?.marital_status.toString().equals("2", ignoreCase = true))
            bindingObj.generalInfoDivorcedRadiobtn.isChecked =true

        if(profilePojo.data?.get(0)?.exp_type.toString().equals("Experienced", ignoreCase = true))
            bindingObj.generalInfoExp2Radiobtn.isChecked =true

        else if(profilePojo.data?.get(0)?.exp_type.toString().equals("Novice", ignoreCase = true))
            bindingObj.generalInfoExp1Radiobtn.isChecked =true

        else if(profilePojo.data?.get(0)?.exp_type.toString().equals("Professional", ignoreCase = true))
            bindingObj.generalInfoExp3Radiobtn.isChecked =true


        bindingObj.phonePublicSwitch.isChecked = profilePojo.data?.get(0)?.is_public.toString().equals("1")

        //Disabling free member to make contact info private and also hiding the username Layout
        if(sharedPreferences.getString(Constants.Membership_id).equals("0"))
        {
            bindingObj.generalInfoUsernameLayout.visibility = View.GONE

            if(bindingObj.phonePublicSwitch.isChecked)
            {
                bindingObj.phonePublicSwitch.isClickable = false

                bindingObj.phonePublicSwitch.setOnTouchListener { v, event ->

                    if(sharedPreferences.getString(Constants.Membership_id).equals("0"))
                    {
                        val dialog = CustomDialog(this@EditGeneralInformationActivity)
                        dialog.setTitle("Alert!")
                        dialog.setMessage("This feature is only available to premium or elite members, please purchase a membership.")
                        dialog.setPositiveButton("Buy now", DialogListenerInterface.onPositiveClickListener {
                            var bundle = Bundle()
                            bundle.putString("membership_type", "upgrade")
                            startActivityForResult(Intent(this@EditGeneralInformationActivity, MyMembershipActivity::class.java).putExtras(bundle), 100)
                        })

                        dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                            dialog.dismiss()
                        })

                        dialog.show()
                    }
                    else
                    {
                        bindingObj.phonePublicSwitch.isClickable = true
                    }
                    return@setOnTouchListener false


                }
            }
            else
                bindingObj.phonePublicSwitch.isClickable = true

        }
        else {
            bindingObj.phonePublicSwitch.isClickable = true
            bindingObj.generalInfoUsernameLayout.visibility = View.VISIBLE

        }



        if(profilePojo.data?.get(0)?.agency_signed.toString().equals("1", ignoreCase = true))
            bindingObj.genralInfoAgencylayout.visibility = View.VISIBLE

        else
            bindingObj.genralInfoAgencylayout.visibility = View.GONE

        bindingObj.generalInfoYesRadiobtn.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener
        {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean)
            {
                if(isChecked)
                    bindingObj.genralInfoAgencylayout.visibility = View.VISIBLE

                else
                {
                    bindingObj.genralInfoAgencylayout.visibility = View.GONE
                    bindingObj.GenralInfoAgencyname.setText("")
                    bindingObj.GenralInfoAgencyphone.setText("")
                    bindingObj.GenralInfoAgencyemail.setText("")
                }

            }
        })


    }

    fun dateFormat(view: TextView, date : String)
    {
        try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val mDate = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed
            val formatter = SimpleDateFormat("MMMM dd, yyyy") //If you need time just put specific format for time like 'HH:mm:ss'
            val dateStr = formatter.format(mDate)
            view.text = dateStr
        }
        catch (e:Exception)
        {
            e.printStackTrace()
            view.text = date
        }
    }


    fun languageKnownDataPopulate()
    {
        bindingObj.languageSpinner.setItems(getResources().getStringArray(R.array.languages))

        if(profilePojo.data?.get(0)?.languages!=null && !profilePojo.data?.get(0)?.languages.equals(""))
        {
            var mLanguageList: MutableList<String>
            mLanguageList = profilePojo.data?.get(0)?.languages?.split((Regex("\\n|,"))) as MutableList<String>

            if (mLanguageList.size == 1)
            {
                val value = mLanguageList.get(0)
                mLanguageList = ArrayList()
                mLanguageList.add(value)
            }

            bindingObj.languageSpinner.setSelectionlist(mLanguageList)
        }
    }

    fun preferedLocationDataPopulate()
    {
        bindingObj.generalInfoLocationSpinner.setItems(getResources().getStringArray(R.array.prefered_location))

        if(profilePojo.data?.get(0)?.pref_location!=null && !profilePojo.data?.get(0)?.pref_location.equals(""))
        {
            var preferedLocationList: MutableList<String>
            preferedLocationList = profilePojo.data?.get(0)?.pref_location?.split((Regex("\\n|,"))) as MutableList<String>



            if (preferedLocationList.size == 1)
            {
                val value = preferedLocationList.get(0)
                preferedLocationList = ArrayList()
                preferedLocationList.add(value)
            }


            Timber.e(preferedLocationList.toString())
            bindingObj.generalInfoLocationSpinner.setSelectionlist(preferedLocationList)
        }
    }

    fun availableForDataPopulate()
    {
        bindingObj.generalInfoAvailableForSpinner.setItems(getResources().getStringArray(R.array.available_for))

        if(profilePojo.data?.get(0)?.available_for!=null && !profilePojo.data?.get(0)?.available_for.equals(""))
        {
            var AvailableForList: MutableList<String>
            AvailableForList = profilePojo.data?.get(0)?.available_for?.split((Regex("\\n|,"))) as MutableList<String>

            if (AvailableForList.size == 1)
            {
                val value = AvailableForList.get(0)
                AvailableForList = ArrayList()
                AvailableForList.add(value)
            }

            bindingObj.generalInfoAvailableForSpinner.setSelectionlist(AvailableForList)
        }


    }

    fun dateofbirthPicker()
    {
        val c = Calendar.getInstance()
        var mYear = 0
        var mMonth = 0
        var day = 0
        if(selectedDateOfBirth.equals("") || selectedDateOfBirth.equals("null"))
        {
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            day = c.get(Calendar.DAY_OF_MONTH)
        }
        else
        {
            val array = selectedDateOfBirth.split("/")
            if(array?.size==3)
            {
                day = array[0].toInt()
                mMonth = array[1].toInt()-1
                mYear = array[2].toInt()
            }
        }

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            var month = monthOfYear+1
            // Display Selected date in textbox
            selectedDateOfBirth = "" + dayOfMonth + "/" + month + "/" + year
            dateFormat(bindingObj.generalInfoDateofbirth , "" + dayOfMonth + "/" + month + "/" + year)

            if((Calendar.getInstance().get(Calendar.YEAR) - year) < 18)
            {
                bindingObj.parentalControlLayout.visibility = View.VISIBLE
                isAgeRestrict = true
            }

            else {
                bindingObj.parentalControlLayout.visibility = View.GONE
                isAgeRestrict = false
            }


        }, mYear, mMonth, day)

        dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
        dpd.show()

    }

    fun populateRadioBtnsData()
    {
        if (bindingObj.generalInfoMaleRadiobtn.isChecked)
            gender = bindingObj.generalInfoMaleRadiobtn.text.toString().trim()

        else if (bindingObj.generalInfoFemaleRadiobtn.isChecked)
            gender = bindingObj.generalInfoFemaleRadiobtn.text.toString().trim()

        if (bindingObj.generalInfoYesRadiobtn.isChecked)
            isAgencySigned = "1"

        else if (bindingObj.generalInfoNoRadiobtn.isChecked)
            isAgencySigned = "0"

        if (bindingObj.generalInfoSingleRadiobtn.isChecked)
            maritalStatus = "0"

        else if (bindingObj.generalInfoMarriedRadiobtn.isChecked)
            maritalStatus = "1"

        else if (bindingObj.generalInfoDivorcedRadiobtn.isChecked)
            maritalStatus = "2"


        if (bindingObj.generalInfoExp1Radiobtn.isChecked)
            exp_type = bindingObj.generalInfoExp1Radiobtn.text.toString().trim()

        else if (bindingObj.generalInfoExp2Radiobtn.isChecked)
            exp_type = bindingObj.generalInfoExp2Radiobtn.text.toString().trim()

        else if (bindingObj.generalInfoExp3Radiobtn.isChecked)
            exp_type = bindingObj.generalInfoExp3Radiobtn.text.toString().trim()


        if (bindingObj.phonePublicSwitch.isChecked)
            isPhonenoPublic = "1"

        else
            isPhonenoPublic ="0"

    }

    fun skillsDataPopulate()
    {
        if(profilePojo.data?.get(0)?.skills!=null && !profilePojo.data?.get(0)?.skills.equals(""))
            mSkillsList  = profilePojo.data?.get(0)?.skills?.split((Regex("\\n|,"))) as MutableList<String>

        if(mSkillsList.size==1)
        {
            val value = mSkillsList.get(0)
            mSkillsList =ArrayList()
            mSkillsList.add(value)
        }

        val interestLayoutManager = FlexboxLayoutManager(this)
        interestLayoutManager.flexDirection = FlexDirection.ROW
        interestLayoutManager.justifyContent = JustifyContent.FLEX_START
        interestLayoutManager.alignItems= AlignItems.STRETCH
        interestLayoutManager.flexWrap = FlexWrap.WRAP
        bindingObj.skillsRecyclerView.setLayoutManager(interestLayoutManager)
        skillsAdapter = SkillsAndInterestsAdapter(this, mSkillsList)
        bindingObj.skillsRecyclerView.adapter = skillsAdapter
    }

    fun interestDataPopulate()
    {

        if(profilePojo.data?.get(0)?.interested_in!=null && !profilePojo.data?.get(0)?.interested_in.equals(""))
            mInterestsList  = profilePojo.data?.get(0)?.interested_in?.split((Regex("\\n|,"))) as MutableList<String>

        if(mInterestsList.size==1)
        {
            val value = mInterestsList.get(0)
            mInterestsList =ArrayList()
            mInterestsList.add(value)
        }

        val interestLayoutManager = FlexboxLayoutManager(this)
        interestLayoutManager.flexDirection = FlexDirection.ROW
        interestLayoutManager.justifyContent = JustifyContent.FLEX_START
        interestLayoutManager.alignItems= AlignItems.STRETCH
        interestLayoutManager.flexWrap = FlexWrap.WRAP
        bindingObj.interestsRecyclerView.setLayoutManager(interestLayoutManager)
        interestsAdapter = SkillsAndInterestsAdapter(this, mInterestsList)
        bindingObj.interestsRecyclerView.adapter = interestsAdapter

    }

    fun clickListeners()
    {
        bindingObj.generalInfoSubmitBtn.setOnClickListener(this)
        bindingObj.validateUsernameBtn.setOnClickListener(this)
        bindingObj.generalInfoCityBtn.setOnClickListener(this)
        bindingObj.generalInfoCurrentCityBtn.setOnClickListener(this)
        bindingObj.generalInfoStateBtn.setOnClickListener(this)
        bindingObj.generalInfoCurrentStateBtn.setOnClickListener(this)
        bindingObj.addSkiillsBtn.setOnClickListener(this)
        bindingObj.addInterestsBtn.setOnClickListener(this)
        bindingObj.generalInfoDateofbirth.setOnClickListener(this)
        bindingObj.verifyPhoneBtn.setOnClickListener(this)
        bindingObj.verifyEmailBtn.setOnClickListener(this)
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.titleLayout.rightbtn.visibility = View.GONE
    }


    fun mCountriesListener() {

        if (mCountriesList.size != 0) {

            val mCountryPicker = ItemPickerDialog(this@EditGeneralInformationActivity, mCountriesList, countryPosition)
            mCountryPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
                override fun onItemSelected(position: Int, selectedValue: String) {

                    countryPosition = position
                    bindingObj.genralInfoStateTxt.text = ""
                    bindingObj.generalInfoCityTxt.text = ""

                    country_id = mCountryIdsList.get(position)
                    bindingObj.genralInfoCountryTxt.text = mCountriesList.get(position)
                    Timber.e("Selected current country " + country_id)
                    if (isNetworkActiveWithMessage())
                        mPresenter.getStates(country_id, 1)//1 for city and 2 for current city

                    mCountryPicker.dismiss()
                }
            })
            mCountryPicker.show()
        }
    }

    fun mCurrentCountriesListeners() {

            if(mCountriesList.size!=0)
            {
                val mCurrentCountryPicker = ItemPickerDialog(this@EditGeneralInformationActivity, mCountriesList, currentCountryPosition)
                mCurrentCountryPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
                    override fun onItemSelected(position: Int, selectedValue: String) {

                        currentCountryPosition = position
                        bindingObj.genralInfoCurrentStateTxt.text = ""
                        bindingObj.generalInfoCurrentCityTxt.text = ""

                        bindingObj.genralInfoCurrentCountryTxt.text = mCountriesList.get(position)
                        current_country_id = mCountryIdsList.get(position)
                        Timber.e("Selected current country " + current_country_id)
                        if (isNetworkActiveWithMessage())
                            mPresenter.getStates(current_country_id, 2)//1 for city and 2 for current city

                        mCurrentCountryPicker.dismiss()
                    }
                })

                mCurrentCountryPicker.show()
            }
        }

    fun mStatesListener()
    {

        if(mStatesList.size!=0)
        {

            val mStatePicker = ItemPickerDialog(this@EditGeneralInformationActivity, mStatesList, statePosition)
            mStatePicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
                override fun onItemSelected(position: Int, selectedValue: String) {

                    statePosition = position
                    bindingObj.generalInfoCityTxt.text = ""

                    state = mStatesIdsList.get(position)
                    bindingObj.genralInfoStateTxt.text = mStatesList.get(position)
                    Timber.e("Selected current State " + state)
                    if (isNetworkActiveWithMessage())
                        mPresenter.getCities(state, 1)//1 for city and 2 for current city

                    mStatePicker.dismiss()
                }
            })
            mStatePicker.show()
        }
    }

    fun mCurrentStatesListeners()
    {

        if(mStatesList.size!=0)
        {
            val mCurrentStatePicker = ItemPickerDialog(this@EditGeneralInformationActivity, mStatesList, currentStatePosition)
            mCurrentStatePicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
                override fun onItemSelected(position: Int, selectedValue: String) {

                    currentStatePosition = position
                    bindingObj.generalInfoCurrentCityTxt.text = ""

                    bindingObj.genralInfoCurrentStateTxt.text = mStatesList.get(position)
                    current_state = mStatesIdsList.get(position)
                    Timber.e("Selected current State " + current_state)
                    if (isNetworkActiveWithMessage())
                        mPresenter.getCities(current_state, 2)//1 for city and 2 for current city

                    mCurrentStatePicker.dismiss()
                }
            })

            mCurrentStatePicker.show()
        }
    }

    fun mCityListener()
    {
        if(mCityList.size!=0) {
            val mCityPicker = ItemPickerDialog(this@EditGeneralInformationActivity, mCityList, cityPosition)
            mCityPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
                override fun onItemSelected(position: Int, selectedValue: String) {

                    cityPosition = position
                    bindingObj.generalInfoCityTxt.text = mCityList.get(position)
                    city = mCityList.get(position)
                    mCityPicker.dismiss()
                }
            })
            mCityPicker.show()
        }
    }

    fun mCurrentCityListener()
    {

        if(mCurrentCityList.size!=0) {
            val mCurrentCityPicker = ItemPickerDialog(this@EditGeneralInformationActivity, mCurrentCityList, currentCityPosition)
            mCurrentCityPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
                override fun onItemSelected(position: Int, selectedValue: String) {

                    currentCityPosition = position
                    bindingObj.generalInfoCurrentCityTxt.text = mCurrentCityList.get(position)
                    current_city = mCurrentCityList.get(position)
                    Timber.e("Selected city " + city)
                    mCurrentCityPicker.dismiss()

                }
            })

            mCurrentCityPicker.show()
        }
    }


    override fun onGetProfileSuccess(model: ProfilePojo)
    {
        profilePojo = model

        //skillsDataPopulate()
        //interestDataPopulate()
        languageKnownDataPopulate()
        preferedLocationDataPopulate()
        availableForDataPopulate()
        populateProfileData()

        if(isNetworkActiveWithMessage())
            mPresenter.getCountries()

    }

    override fun onSuccess()
    {
        Toast.makeText(this , "General information has been updated successfully" , Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onProfilePicUploaded(url: String) {
        //--- Not in use
    }



    override fun onGetCountries(dataModel: CountryPojo) {

        if(dataModel?.data?.size!=0)
        {
            mCountriesList.clear()
            mCountryIdsList.clear()
            var countryIndex =0
            var currentCountryIndex =0
            for (i:Int in dataModel?.data!!.indices)
            {
                mCountriesList.add(dataModel?.data?.get(i)?.country_name.toString())
                mCountryIdsList.add(dataModel?.data?.get(i)?.country_id.toString())

                if(dataModel?.data?.get(i)?.country_name.toString().equals(profilePojo.data?.get(0)?.country ,ignoreCase = true))
                {
                    countryIndex = i
                    country_id = mCountryIdsList[i]
                    if (isNetworkActiveWithMessage())
                        mPresenter.getStates(country_id ,1)//1 for state and 2 for current state

                    bindingObj.genralInfoCountryTxt.text = dataModel?.data?.get(i)?.country_name
                }
                else
                {
                    if(dataModel?.data?.get(i)?.country_name.toString().equals("india" , ignoreCase = true))
                    {
                        countryIndex = i
                    }
                }
/*
                if(dataModel?.data?.get(i)?.country_name.toString().equals(profilePojo.data?.get(0)?.current_country ,ignoreCase = true))
                {
                    currentCountryIndex = i
                    current_country_id = mCountryIdsList[i]
                    if (isNetworkActiveWithMessage())
                        mPresenter.getStates(current_country_id , 2)//1 for state and 2 for current state

                    bindingObj.genralInfoCurrentCountryTxt.text = dataModel?.data?.get(i)?.country_name
                }
                else
                {
                    if(dataModel?.data?.get(i)?.country_name.toString().equals("india" , ignoreCase = true))
                    {
                        currentCountryIndex = i
                    }
                }*/
            }
            countryPosition = countryIndex
          //  currentCountryPosition = currentCountryIndex

        }
    }

    override fun onGetStates(dataModel: StatesPojo, type: Int)
    {
        if(dataModel?.data?.size!=0)
        {
            if(type==1)
            {
                mStatesList.clear()
                mStatesIdsList.clear()
                var stateIndex =0

                for (i:Int in dataModel?.data!!.indices)
                {
                    mStatesList.add(dataModel?.data?.get(i)?.state_name.toString())
                    mStatesIdsList.add(dataModel?.data?.get(i)?.state_id.toString())


                    if(dataModel?.data?.get(i)?.state_name.toString().equals(profilePojo.data?.get(0)?.state_name.toString(),ignoreCase = true))
                    {
                        stateIndex = i
                        bindingObj.genralInfoStateTxt.text = mStatesList[i]
                        state = mStatesIdsList[i]
                        mPresenter.getCities(state, 1)//1 for city and 2 for current city
                    }

                }

                statePosition = stateIndex
            }
         /*   else
            {
                var currentstateIndex =0
                for (i:Int in dataModel?.data!!.indices)
                {
                    mCurrentStatesList.add(dataModel?.data?.get(i)?.state_name.toString())
                    mCurrentStatesIdsList.add(dataModel?.data?.get(i)?.state_id.toString())


                    if(dataModel?.data?.get(i)?.state_name.toString().equals(profilePojo.data?.get(0)?.current_state.toString(),ignoreCase = true))
                    {
                        currentstateIndex = i
                        bindingObj.genralInfoCurrentStateTxt.text = mCurrentStatesList[i]

                        current_state = mCurrentStatesIdsList[i]
                        mPresenter.getCities(current_state, 2)//1 for city and 2 for current city
                    }

                }

                currentStatePosition = currentstateIndex
            }*/



        }
    }


    override fun onGetCities(dataModel: CitiesPojo, type: Int)
    {

        if(dataModel?.data?.size!=0)
        {
            if(type==1)
            {
                mCityList.clear()
                var cityindex=0

                for (i:Int in dataModel?.data!!.indices)
                {
                    mCityList.add(dataModel?.data?.get(i)?.city_name.toString())
                    if(dataModel?.data?.get(i)?.city_name.toString().equals(profilePojo.data?.get(0)?.city.toString(),ignoreCase = true))
                    {
                        cityindex = i
                        bindingObj.generalInfoCityTxt.text = mCityList[i]
                    }
                }

                cityPosition = cityindex

            }

            else
            {

                mCurrentCityList.clear()

                var currentcityindex=0
                for (i:Int in dataModel?.data!!.indices)
                {
                    mCurrentCityList.add(dataModel?.data?.get(i)?.city_name.toString())

                    if(dataModel?.data?.get(i)?.city_name.toString().equals(profilePojo.data?.get(0)?.current_city.toString(),ignoreCase = true))
                    {
                        currentcityindex = i
                        bindingObj.generalInfoCurrentCityTxt.text = mCurrentCityList[i]
                    }
                }

                currentCityPosition = currentcityindex

            }
        }

    }
    override fun isValidate()
    {

        var parent_name = ""
        var parent_contact = ""

        if(isAgeRestrict)
        {
            parent_name =bindingObj.generalInfoParentName.text.toString().trim()
            parent_contact =bindingObj.generalInfoParentPhoneno.text.toString().trim()
        }

        else
        {
            parent_name = ""
            parent_contact = ""
        }

        Timber.e("State: "+state+" City: "+city+" Current state: "+ state + " Current city: " + current_city)
        mPresenter.updateGeneralInformation(sharedPreferences.getString(Constants.User_id).toString()
                ,sharedPreferences.getString(Constants.User_Email).toString()
                ,profilePojo.data?.get(0)?.user_role_id.toString()
                ,bindingObj.generalInfoName.text.toString().trim()
                ,bindingObj.generalInfoPhoneno.text.toString().trim()
                ,bindingObj.generalInfoWhatsappno.text.toString().trim()
                ,country_id
                ,state
                ,city
                ,exp_type
                ,bindingObj.languageSpinner.selectedItemsAsString
                ,selectedDateOfBirth
                ,maritalStatus
                ,isAgencySigned
                ,bindingObj.generalInfoAvailableForSpinner.selectedItemsAsString
                ,gender
                ,bindingObj.generalInfoLocationSpinner.selectedItemsAsString
                ,bindingObj.generalInfoAboutEdittext.text.toString().trim()
                ,""/*mInterestsList.toString().replace("[" ,"").replace("]" , "")*/
                ,""/*mSkillsList.toString().replace("[" ,"").replace("]" , "")*/
                ,bindingObj.GenralInfoAgencyname.text.toString().trim()
                ,isPhonenoPublic ,parent_name,parent_contact
                ,bindingObj.GenralInfoAgencyphone.text.toString().trim()
                ,bindingObj.GenralInfoAgencyemail.text.toString().trim()
                ,bindingObj.generalInfoUserName.text.toString().trim()
                )
    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }


    override fun onProfileFailed(message: String) {
        showSnackbar(message)
    }


    fun showSnackbar(message : String)
    {
        Snackbar.make(findViewById(android.R.id.content) , message , Snackbar.LENGTH_SHORT ).show()
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

    fun validateUsernameDialog()
    {
        isUsernameAvailable = false

        usernameDialog = Dialog(this)
        usernameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        usernameDialog.setContentView(R.layout.check_username_dialog_layout)
        val window = usernameDialog.window
        val wlp = window!!.attributes

        wlp.gravity = Gravity.CENTER
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
        window.attributes = wlp
        usernameDialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        validateUsernameBtn = usernameDialog.findViewById<Button>(R.id.validateUsernameBtn)
        val closeButton = usernameDialog.findViewById<ImageView>(R.id.closeButton)
        val usernameEdittext = usernameDialog.findViewById<EditText>(R.id.usernameEdittext)
         usernameMessageTxt = usernameDialog.findViewById<TextView>(R.id.usernameMessageTxt)
         aviDialogProgressBar = usernameDialog.findViewById<SpinKitView>(R.id.aviProgressBar)

        usernameEdittext.addTextChangedListener(object : TextWatcher
        {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               isUsernameAvailable = false
                validateUsernameBtn.text = "Validate Username"
                usernameMessageTxt.visibility = View.GONE
            }

        })
        validateUsernameBtn.setOnClickListener {

            if(usernameEdittext.text.isEmpty())
            {
                usernameMessageTxt.visibility = View.VISIBLE
                usernameMessageTxt.setTextColor(resources.getColor(R.color.appColor))
                usernameMessageTxt.text = "Enter your username!"
            }

            else
            {
                usernameMessageTxt.visibility = View.GONE
                if (isUsernameAvailable) {
                    if (isNetworkActiveWithMessage())
                        mUsernamePresenter.setUsername(sharedPreferences.getString(Constants.User_id).toString(), usernameEdittext.text.toString().trim())
                } else {
                    if (isNetworkActiveWithMessage())
                        mUsernamePresenter.checkUsernameAvailability(sharedPreferences.getString(Constants.User_id).toString(), usernameEdittext.text.toString().trim())

                }

            }

        }
        closeButton.setOnClickListener {
            usernameDialog.dismiss()
        }

        usernameDialog.show()

    }

    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.leftbtn ->  finish()

            R.id.validateUsernameBtn->
            {
                validateUsernameDialog()
            }

            R.id.generalInfoCountryBtn->
            {
                mCountriesListener()
            }

            R.id.generalInfoCurrentCountryBtn->
            {
                mCurrentCountriesListeners()
            }
            R.id.generalInfoStateBtn->
            {
                mStatesListener()
            }

            R.id.generalInfoCurrentStateBtn->
            {
                mCurrentStatesListeners()
            }

            R.id.generalInfoCityBtn->
            {
                mCityListener()
            }

            R.id.generalInfoCurrentCityBtn->
            {
                mCurrentCityListener()
            }

            R.id.generalInfoSubmitBtn->
            {

                if(isNetworkActiveWithMessage()) {
                    populateRadioBtnsData()

                    Timber.e(sharedPreferences.getString(Constants.User_id).toString()
                            + " Email " + sharedPreferences.getString(Constants.User_Email).toString()
                            + " Role id " + profilePojo.data?.get(0)?.user_role_id.toString()
                            + " name " + sharedPreferences.getString(Constants.User_name).toString()
                            + " Phone " + sharedPreferences.getString(Constants.User_Phone).toString()
                            + " whatsapp " + sharedPreferences.getString(Constants.User_Phone).toString()
                            + " State " + state
                            + " City " + city
                            + " CurrentState " + current_state
                            + " Current city " + current_city
                            + " Exp type " + exp_type
                            + " language " + bindingObj.languageSpinner.selectedItemsAsString
                            + " DOB " + selectedDateOfBirth
                            + " Marital " + maritalStatus
                            + " isAgencySigned " + isAgencySigned
                            + " isPhone Public " + isPhonenoPublic
                            + " Avaliable for " + bindingObj.generalInfoAvailableForSpinner.selectedItemsAsString
                            + " Gender " + gender
                            + " Prefered location " + bindingObj.generalInfoLocationSpinner.selectedItemsAsString
                            + " About " + bindingObj.generalInfoAboutEdittext.text.toString().trim()
                          //  + " Interest " + mInterestsList.toString().replace("[", "").replace("]", "")
                            //+ " skills " + mSkillsList.toString().replace("[", "").replace("]", "")
                             )

                    if (isAgencySigned.equals("1") && bindingObj.GenralInfoAgencyname.text.toString().trim().equals(""))
                    {
                        showSnackbar("Please fill agency/brand name")
                    }

                    else if(isAgencySigned.equals("1") && !bindingObj.GenralInfoAgencyphone.text.toString().trim().equals(""))
                    {
                    if (bindingObj.GenralInfoAgencyphone.text.toString().trim().length >10 || bindingObj.GenralInfoAgencyphone.text.toString().trim().length<10)
                    {
                        showSnackbar("Please enter a valid phone number.")
                    }

                    else
                    {
                        mPresenter.validate(
                                bindingObj.generalInfoPhoneno.text.toString().trim()
                                , bindingObj.generalInfoWhatsappno.text.toString().trim()
                                ,country_id
                                , state
                                , city
                                , exp_type
                                , bindingObj.languageSpinner.selectedItemsAsString
                                , maritalStatus
                                , isAgencySigned
                                , bindingObj.generalInfoAvailableForSpinner.selectedItemsAsString
                                , gender
                                , bindingObj.generalInfoLocationSpinner.selectedItemsAsString
                                , isAgeRestrict, bindingObj.generalInfoParentName.text.toString().trim()
                                , bindingObj.generalInfoParentPhoneno.text.toString().trim())

                    }

                    }
                    else if(isAgencySigned.equals("1") && !bindingObj.GenralInfoAgencyemail.text.toString().trim().equals(""))
                    {
                        if(!Patterns.EMAIL_ADDRESS.matcher(bindingObj.GenralInfoAgencyemail.text.toString()).matches())
                        {
                            showSnackbar("Please enter a valid agency email address.")
                        }

                        else
                        {
                                mPresenter.validate(
                                        bindingObj.generalInfoPhoneno.text.toString().trim()
                                        , bindingObj.generalInfoWhatsappno.text.toString().trim()
                                        , country_id
                                        , state
                                        , city
                                        , exp_type
                                        , bindingObj.languageSpinner.selectedItemsAsString
                                        , maritalStatus
                                        , isAgencySigned
                                        , bindingObj.generalInfoAvailableForSpinner.selectedItemsAsString
                                        , gender
                                        , bindingObj.generalInfoLocationSpinner.selectedItemsAsString
                                        , isAgeRestrict, bindingObj.generalInfoParentName.text.toString().trim()
                                        , bindingObj.generalInfoParentPhoneno.text.toString().trim())

                        }
                    }
                    else
                    {
                        mPresenter.validate(
                                bindingObj.generalInfoPhoneno.text.toString().trim()
                                , bindingObj.generalInfoWhatsappno.text.toString().trim()
                                , country_id
                                , state
                                , city
                                , exp_type
                                , bindingObj.languageSpinner.selectedItemsAsString
                                , maritalStatus
                                , isAgencySigned
                                , bindingObj.generalInfoAvailableForSpinner.selectedItemsAsString
                                , gender
                                , bindingObj.generalInfoLocationSpinner.selectedItemsAsString
                                , isAgeRestrict, bindingObj.generalInfoParentName.text.toString().trim()
                                , bindingObj.generalInfoParentPhoneno.text.toString().trim()
                        )
                    }

                }
            }
            R.id.generalInfoDateofbirth->
            {

                dateofbirthPicker()
            }

            R.id.addSkiillsBtn->
            {
                if(bindingObj.skillsEdittext.text.length!=0) {
                    mSkillsList.add(bindingObj.skillsEdittext.text.toString())
                    skillsAdapter.notifyDataSetChanged()
                    bindingObj.skillsEdittext.text.clear()
                }
            }

            R.id.addInterestsBtn->
            {
                if(bindingObj.interestEdittext.text.length!=0)
                {
                    mInterestsList.add(bindingObj.interestEdittext.text.toString())
                    interestsAdapter.notifyDataSetChanged()
                    bindingObj.interestEdittext.text.clear()
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

        }
    }

    override fun onResume() {
        super.onResume()

        //hiding the username Layout
                if(sharedPreferences.getString(Constants.Membership_id).equals("0")) {
                    bindingObj.generalInfoUsernameLayout.visibility = View.GONE
                    bindingObj.phonePublicSwitch.isClickable = false
                }
                else
                {
                    bindingObj.phonePublicSwitch.isClickable = true
                    bindingObj.generalInfoUsernameLayout.visibility = View.VISIBLE
                }

        bindingObj.generalInfoEmail.setText(sharedPreferences.getString(Constants.User_Email))
        bindingObj.generalInfoPhoneno.setText(sharedPreferences.getString(Constants.User_Phone))

        //Commenting the phone verification for now at 14/12/2020
/*        if(sharedPreferences.getString(Constants.Is_Phone_Verified).equals("0")|| sharedPreferences.getString(Constants.Is_Phone_Verified).equals("")|| sharedPreferences.getString(Constants.Is_Phone_Verified).equals("null"))
        {
            bindingObj.verifyPhoneBtn.visibility = View.VISIBLE
            bindingObj.isPhoneVerifiedText.visibility =View.GONE
        }
        else
        {
            bindingObj.verifyPhoneBtn.visibility = View.GONE
            bindingObj.isPhoneVerifiedText.visibility =View.VISIBLE
        }*/

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

    override fun onUsernameAvailable(username : String) {
        usernameMessageTxt.text = "Congrats! The username is available"
        usernameMessageTxt.visibility = View.VISIBLE
        usernameMessageTxt.setTextColor(resources.getColor(R.color.colorGreen))
        validateUsernameBtn.text = "Grab Now"
        isUsernameAvailable = true

    }

    override fun onSetUsername(username: String)
    {
        isUsernameAvailable = false
        usernameDialog.dismiss()
        showSnackbar("Username has been set successfully")
        bindingObj.generalInfoUserName.setText(username)
    }

    override fun onUsernameFailed(message: String)
    {
        usernameMessageTxt.visibility = View.VISIBLE
        usernameMessageTxt.setTextColor(resources.getColor(R.color.appColor))
        usernameMessageTxt.text = "Username is not available. Try other"
    }

    override fun showUsernameDialogProgress(showProgress: Boolean)
    {
        if(showProgress)
        aviDialogProgressBar.visibility = View.VISIBLE

        else
            aviDialogProgressBar.visibility = View.GONE
    }
}
