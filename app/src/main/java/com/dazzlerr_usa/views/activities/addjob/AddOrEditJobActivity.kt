package com.dazzlerr_usa.views.activities.addjob

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityAddOrEditJobBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.activities.jobsdetails.*
import com.dazzlerr_usa.views.activities.editprofile.pojos.CitiesPojo
import com.dazzlerr_usa.views.activities.editprofile.adapters.SkillsAndInterestsAdapter
import com.dazzlerr_usa.views.activities.editprofile.pojos.StatesPojo
import com.google.android.flexbox.*
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.utilities.itempicker.ItemPickerDialog
import com.dazzlerr_usa.utilities.itempicker.ItemPickerListener
import com.dazzlerr_usa.views.activities.editprofile.pojos.CountryPojo
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import java.text.SimpleDateFormat


class AddOrEditJobActivity : AppCompatActivity() , AddOrEditJobView ,View.OnClickListener , JobDetailsView
{

    override fun onGetContact()
    {
        //Not in use
    }

    override fun onApplicationViewed()
    {

    }
    override fun onPurposalShortlisted(status: String , position: Int) {
        //Not in use
    }

    override fun onJobShortlisted(status: String) {
        //Not in use
    }

    override fun onHiredOrRejected(request_sent: String , position : Int) {
        //Not in use
    }

    override fun onGetProposals(model: GetProposalsPojo)
    {
        //Not in use
    }

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var mPresenter: AddOrEditJobPresenter
    lateinit var getJobDetailsPresenter: JobDetailsPresenter
    lateinit var bindingObj: ActivityAddOrEditJobBinding
    var mCountriesList:ArrayList<String> = ArrayList()
    var mStatesList:ArrayList<String> = ArrayList()
    var mCityList:ArrayList<String> = ArrayList()
    var mCityIdsList:ArrayList<String> = ArrayList()
    var mStatesIdsList:ArrayList<String> = ArrayList()
    var mCountryIdsList:ArrayList<String> = ArrayList()
    var state_id : String = ""
    //var state_id : String = ""
    var country_id = ""
    var city : String = ""
    var city_id : String = ""

    var mTagsList : MutableList<String> = ArrayList()
    lateinit var tagsAdapter: SkillsAndInterestsAdapter
    var categoryIdsList : java.util.ArrayList<String> = java.util.ArrayList()
    var mRoleList : java.util.ArrayList<String> = java.util.ArrayList()
    var role_id  = ""
    var role_name  = ""
    var gender = ""
    var isAuditionRequired = ""
    var budgetDuration = ""
    var type= ""
    var call_id = ""
    var categoryPosition = 0
    var statePosition = 0
    var countryPosition = 0
    var cityPosition = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {

        super.onCreate(savedInstanceState)
        bindingObj  = DataBindingUtil.setContentView(this , R.layout.activity_add_or_edit_job)
        populateCategories()
        initializations()
        clickListeners()
        apiCalling()
    }

    fun initializations()
    {

        //----------------Injecting dagger into sharedepreferences
        (application as MyApplication).myComponent.inject(this)

        type = intent.extras?.getString("type" , "").toString()
        if(type.equals("Edit" , ignoreCase = true))
            call_id = intent.extras?.getString("call_id" , "")!!

        mPresenter = AddOrEditJobModel(this , this)
        getJobDetailsPresenter = JobDetailsModel(this , this)

        //profile_id = intent.extras!!.getString("profile_id","")
        bindingObj.titleLayout.rightbtn.visibility = View.GONE
        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)

        if(type.equals("Add" , ignoreCase = true))
            bindingObj.titleLayout.titletxt.text = "Add Job"

        else
            bindingObj.titleLayout.titletxt.text = "Edit Job"


        bindingObj.budgetCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
            {
                bindingObj.jobBudget.setText("As Per Profile")
            }

            else
            {
                bindingObj.jobBudget.setText("")
            }
        }

        bindingObj.jobDescription?.addTextChangedListener(object : TextWatcher
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

    fun tagsDataPopulate()
    {

        if(bindingObj.jobsBinder?.tags!=null && !bindingObj.jobsBinder?.tags.equals(""))
            mTagsList  =bindingObj.jobsBinder?.tags?.split((Regex("\\n|,"))) as MutableList<String>

        if(mTagsList.size==1)
        {
            val value = mTagsList.get(0)
            mTagsList =ArrayList()
            mTagsList.add(value)
        }

        val interestLayoutManager = FlexboxLayoutManager(this)
        interestLayoutManager.flexDirection = FlexDirection.ROW
        interestLayoutManager.justifyContent = JustifyContent.FLEX_START
        interestLayoutManager.alignItems= AlignItems.STRETCH
        interestLayoutManager.flexWrap = FlexWrap.WRAP
        bindingObj.jobsTagsRecyclerView.setLayoutManager(interestLayoutManager)
        tagsAdapter = SkillsAndInterestsAdapter(this, mTagsList)
        bindingObj.jobsTagsRecyclerView.adapter = tagsAdapter
    }

    fun dateofbirthPicker(mTextview:TextView ,type : Int)
    {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            var month = monthOfYear+1
            // Display Selected date in textbox
            mTextview.text = "" + dayOfMonth + "/" + month + "/" + year
        }, year, month, day)

        if(type==2)
        {
            //1. Create a Date from String
            val sdf = SimpleDateFormat("dd/M/yyyy")
            val dateInString = bindingObj.jobStartDate.text.toString()
            val date = sdf.parse(dateInString)

            val mCal = Calendar.getInstance()
            mCal.time  = date
            mCal.add(Calendar.DATE, 1)
            dpd.getDatePicker().setMinDate(mCal.timeInMillis)
        }

        else if(type==3)
        {

            //1. Create a Date from String
            val sdf = SimpleDateFormat("dd/M/yyyy")
            val dateInString = bindingObj.jobStartDate.text.toString()
            val date = sdf.parse(dateInString)

            val mCal = Calendar.getInstance()
            mCal.time  = date
            //mCal.add(Calendar.DATE, 1)
            dpd.getDatePicker().setMinDate(System.currentTimeMillis())
            dpd.getDatePicker().setMaxDate(mCal.timeInMillis)

        }
        else
            dpd.getDatePicker().setMinDate(System.currentTimeMillis())
        dpd.show()
    }


    fun categorySpinnerSettings()
    {

        for(i in mRoleList.indices)
        {
            if(bindingObj.jobsBinder?.job_role.equals(mRoleList.get(i) , ignoreCase = true)) {

                categoryPosition =i
                role_id = categoryIdsList.get(i)
                role_name = mRoleList.get(i)

            }
        }

        val representerPicker = ItemPickerDialog(this@AddOrEditJobActivity,mRoleList, categoryPosition)
        representerPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener
        {

            override fun onItemSelected(position: Int, selectedValue: String)
            {

                role_id = categoryIdsList.get(position)
                role_name = mRoleList.get(position)
                bindingObj.jobCategoryTxt.text = role_name
                categoryPosition = position
                representerPicker.dismiss()

            }
        })

        representerPicker.show()

    }

    fun apiCalling()
    {
        if(type.equals("Add" , ignoreCase = true))
        {
            tagsDataPopulate()

            if (isNetworkActiveWithMessage())
                mPresenter.getCountries()
        }

        else
        {
            Timber.e(call_id +"      "+ sharedPreferences.getString(Constants.User_id).toString())
            if (isNetworkActiveWithMessage())
                getJobDetailsPresenter.getjobDetails(call_id , sharedPreferences.getString(Constants.User_id).toString() ,sharedPreferences.getString(Constants.Membership_id).toString() )

        }
    }

    fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.submitBtn.setOnClickListener(this)
        bindingObj.jobStartDate.setOnClickListener(this)
        bindingObj.jobEndDate.setOnClickListener(this)
        bindingObj.jobExpiryDate.setOnClickListener(this)
        bindingObj.jobsAddTagsBtn.setOnClickListener(this)
        bindingObj.jobCategoryBtn.setOnClickListener(this)
        bindingObj.jobStateBtn.setOnClickListener(this)
        bindingObj.jobCityBtn.setOnClickListener(this)
        bindingObj.jobCountryBtn.setOnClickListener(this)
    }

    override fun onValidate()
    {
        if(type.equals("Add", ignoreCase = true))
        {

            mPresenter.addJob(sharedPreferences.getString(Constants.User_id).toString()
                    , sharedPreferences.getString(Constants.Employer_id).toString()
                    , bindingObj.jobTitle.text.toString().trim()
                    , bindingObj.jobDescription.text.toString().trim()
                    , bindingObj.jobStartDate.text.toString().trim()
                    , bindingObj.jobEndDate.text.toString().trim()
                    , bindingObj.jobBudget.text.toString().trim()
                    , bindingObj.jobLocation.text.toString().trim()
                    , country_id , city, city_id, state_id, role_id, gender
                    , bindingObj.JobMinAge.text.toString().trim()
                    + "-" + bindingObj.JobMaxAge.text.toString().trim()
                    , isAuditionRequired
                    , bindingObj.jobVacancies.text.toString().trim()
                    , bindingObj.jobExpiryDate.text.toString().trim()
                    , mTagsList.toString().replace("[" ,"").replace("]","")
                    , budgetDuration
                    , bindingObj.jobEmail.text.toString().trim()
                    , bindingObj.jobPhone.text.toString().trim()
                    , bindingObj.jobWhatsapp.text.toString().trim()
                    , bindingObj.jobContactPersonName.text.toString().trim())

        }
        else
        {

            mPresenter.updateJob(sharedPreferences.getString(Constants.User_id).toString()
                    , call_id
                    , bindingObj.jobTitle.text.toString().trim()
                    , bindingObj.jobDescription.text.toString().trim()
                    , bindingObj.jobStartDate.text.toString().trim()
                    , bindingObj.jobEndDate.text.toString().trim()
                    , bindingObj.jobBudget.text.toString().trim()
                    , bindingObj.jobLocation.text.toString().trim()
                    , country_id , city, city_id, state_id, role_id, gender
                    , bindingObj.JobMinAge.text.toString().trim()
                    + "-" + bindingObj.JobMaxAge.text.toString().trim()
                    , isAuditionRequired, bindingObj.jobVacancies.text.toString().trim()
                    , bindingObj.jobExpiryDate.text.toString().trim()
                    , mTagsList.toString().replace("[" ,"").replace("]","")
                    , budgetDuration
                    , bindingObj.jobEmail.text.toString().trim()
                    , bindingObj.jobPhone.text.toString().trim()
                    , bindingObj.jobWhatsapp.text.toString().trim()
                    , bindingObj.jobContactPersonName.text.toString().trim())

        }
    }

    // Add job on success
    override fun onSuccess()
    {
        if(type.equals("Add" , ignoreCase = true))
        {

            val mDialog = CustomDialog(this@AddOrEditJobActivity)
            mDialog.setTitle("Job Submitted Successfully!")
            mDialog.setMessage("Your job has been successfully submitted for approval. It will be live within 24-48 hours.")
            mDialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {
                mDialog.dismiss()
                setResult(101)
                finish()
            })
            mDialog.show()

        }
        else
        {
            val mDialog = CustomDialog(this@AddOrEditJobActivity)
            mDialog.setTitle("Job Edited Successfully!")
            mDialog.setMessage("Your job has been successfully edited and submitted for approval. It will be live within 24-48 hours.")
            mDialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {
                mDialog.dismiss()
                setResult(101)
                finish()
            })
            mDialog.show()
        }
    }

    // Get job details success
    override fun onSuccess(model: JobDetailsPojo)
    {
        if(model.data?.size!=0)
        {
            bindingObj.jobsBinder = model.data?.get(0)
            bindingObj.executePendingBindings()

            if (isNetworkActiveWithMessage())
                mPresenter.getCountries()


            bindingObj.jobCategoryTxt.text = model.data?.get(0)?.job_role
            bindingObj.jobStateTxt.text = model.data?.get(0)?.state_name
            bindingObj.jobCityTxt.text = model.data?.get(0)?.city


            for(i in mRoleList.indices)
            {
                if(bindingObj.jobsBinder?.job_role.equals(mRoleList.get(i) , ignoreCase = true)) {

                    categoryPosition =i
                    role_id = categoryIdsList.get(i)
                    role_name = mRoleList.get(i)
                    break
                }
            }


            tagsDataPopulate()

            if(model.data?.get(0)?.gender.equals("1"))
                bindingObj.jobMaleRadiobtn.isChecked = true

            else
                bindingObj.jobFemaleRadiobtn.isChecked = true


            if(model.data?.get(0)?.audition.equals("1"))
                bindingObj.jobIsAuditionRequiredYesBtn.isChecked = true

            else
                bindingObj.jobIsAuditionRequiredNoBtn.isChecked = true


            if(model.data?.get(0)?.budget_duration.equals("Per day" , ignoreCase = true))
                bindingObj.jobDurationPerdayBtn.isChecked = true

            else if(model.data?.get(0)?.budget_duration.equals("Per week" , ignoreCase = true))
                bindingObj.jobDurationPerweekBtn.isChecked = true

            else if(model.data?.get(0)?.budget_duration.equals("Per month" , ignoreCase = true))
                bindingObj.jobDurationPermonthBtn.isChecked = true

            else if(model.data?.get(0)?.budget_duration.equals("One time" , ignoreCase = true))
                bindingObj.jobDurationOnetimeBtn.isChecked = true


            if(model.data?.get(0)?.budget_amount!!.isEmpty())
            {
                bindingObj.budgetCheckbox.isChecked = true
            }

            else
            {
                bindingObj.jobBudget.setText(model.data?.get(0)?.budget_amount)
            }

            val ageRangeArray = model.data?.get(0)?.age_range?.split("-")

            try
            {
                bindingObj.JobMinAge.setText(ageRangeArray?.get(0).toString())
                if(ageRangeArray?.size!! > 1)
                {
                    bindingObj.JobMaxAge.setText(ageRangeArray?.get(1).toString())
                }
             }

            catch (e : Exception)
            {
                e.printStackTrace()
            }
        }
    }

    override fun onSendMessageSuccess(message:String)
    {
        // Not in use
    }

    override fun isMesssageValid(message: String)
    {
        //Not in use
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

                if(dataModel?.data?.get(i)?.country_name.toString().equals(bindingObj.jobsBinder?.country_name.toString(),ignoreCase = true))
                {
                    countryIndex = i
                    country_id = mCountryIdsList[i]
                    if (isNetworkActiveWithMessage())
                        mPresenter.getStates(country_id)

                    bindingObj.jobCountryTxt.text = dataModel?.data?.get(i)?.country_name
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

                if(dataModel?.data?.get(i)?.state_name.toString().equals(bindingObj.jobsBinder?.state_name.toString(),ignoreCase = true))
                {
                    stateIndex = i
                    state_id = mStatesIdsList[i]
                    if (isNetworkActiveWithMessage())
                        mPresenter.getCities(state_id)

                    bindingObj.jobStateTxt.text = dataModel?.data?.get(i)?.state_name
                }

            }
            statePosition = stateIndex

        }
    }


    override fun onGetCities(dataModel: CitiesPojo) {

        if(dataModel?.data?.size!=0)
        {

            mCityList.clear()
            mCityIdsList.clear()

            var cityindex=0

            for (i:Int in dataModel?.data!!.indices)
            {
                mCityList.add(dataModel?.data?.get(i)?.city_name.toString())
                mCityIdsList.add(dataModel?.data?.get(i)?.city_id.toString())

                if(dataModel?.data?.get(i)?.city_name.toString().equals(bindingObj.jobsBinder?.city.toString(),ignoreCase = true))
                {
                    cityindex = i
                    city = dataModel?.data?.get(i)?.city_name.toString()
                    city_id = dataModel?.data?.get(i)?.city_id.toString()
                    bindingObj.jobCityTxt.text = dataModel?.data?.get(i)?.city_name
                }
            }


            cityPosition = cityindex
        }

    }


    fun mCountryListener()
    {

        if(mCountriesList.size!=0)
        {

            val mCountryPicker = ItemPickerDialog(this@AddOrEditJobActivity, mCountriesList, countryPosition)
            mCountryPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
                override fun onItemSelected(position: Int, selectedValue: String) {

                    countryPosition = position
                    bindingObj.jobStateTxt.text = ""
                    state_id = ""
                    //city_id = ""

                    country_id = mCountryIdsList.get(position)
                    Timber.e("Selected Country " + country_id)

                    bindingObj.jobCountryTxt.text = mCountriesList.get(position)
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

            val mStatePicker = ItemPickerDialog(this@AddOrEditJobActivity, mStatesList, statePosition)
            mStatePicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
                override fun onItemSelected(position: Int, selectedValue: String) {

                    statePosition = position
                    bindingObj.jobCityTxt.text = ""
                    city = ""
                    city_id = ""

                    state_id = mStatesIdsList.get(position)
                    Timber.e("Selected State " + state_id)

                    bindingObj.jobStateTxt.text = mStatesList.get(position)
                    if (isNetworkActiveWithMessage())
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
            val mCityPicker = ItemPickerDialog(this@AddOrEditJobActivity, mCityList, cityPosition)
            mCityPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
                override fun onItemSelected(position: Int, selectedValue: String) {

                    cityPosition = position
                    bindingObj.jobCityTxt.text = mCityList.get(position)
                    city = mCityList.get(position)
                    city_id = mCityIdsList.get(position)
                    Timber.e("Selected city " + city)
                    mCityPicker.dismiss()
                }
            })

            mCityPicker.show()
        }
    }

    override fun showProgress(visiblity: Boolean) {

        if(visiblity)
            startProgressBarAnim()
        else
            stopProgressBarAnim()
    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }


    fun startProgressBarAnim() {

        bindingObj.aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {
        bindingObj.aviProgressBar.setVisibility(View.GONE)
    }

    fun showSnackbar(message: String)
    {
        try {
            val parentLayout = findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.leftbtn->
            {
                finish()
            }

            R.id.submitBtn->
            {
                postOrEditJob()

                /*if (sharedPreferences.getString(Constants.User_Role).equals("casting", ignoreCase = true)) {

                    if (sharedPreferences.getString(Constants.Is_Documement_Verified).toString().equals("1", ignoreCase = true)
                            && sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("1", ignoreCase = true))
                    {



                    } else if (sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("0", ignoreCase = true)
                            || sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("", ignoreCase = true)
                            || sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("null", ignoreCase = true)) {
                        val dialog = CustomDialog(this@AddOrEditJobActivity)
                        dialog.setMessage("Your identity is not verified. It is very important to help protect our community and ensure that Dazzlerr stays safe and secure. We verify identity in several ways:\n" +
                                "\n" +
                                "- Government ID Verification.\n" +
                                "- Video Verification.")
                        dialog.setPositiveButton("Verify now", DialogListenerInterface.onPositiveClickListener {
                            dialog.dismiss()
                            val bundle = Bundle()
                            bundle.putString("type", "documentVerification")
                            val newIntent = Intent(this@AddOrEditJobActivity, AccountVerification::class.java)
                            newIntent.putExtras(bundle)
                            startActivity(newIntent)
                        })
                        dialog.setNegativeButton("Later", DialogListenerInterface.onNegetiveClickListener {
                            dialog.dismiss()
                        })

                        dialog.show()

                    } else if (sharedPreferences.getString(Constants.Is_Documement_Verified).toString().equals("0", ignoreCase = true)
                            && sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("1", ignoreCase = true)) {
                        val dialog = CustomDialog(this@AddOrEditJobActivity)
                        dialog.setMessage("Your identity verification is under process. It will take 24-48 hours.")
                        dialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {
                            dialog.dismiss()
                        })

                        dialog.show()
                    }


                }
*/
            }

            R.id.jobStartDate->
            {
                dateofbirthPicker(bindingObj.jobStartDate ,1)
            }

            R.id.jobEndDate->
            {
                if(bindingObj.jobStartDate.text.length!=0)
                    dateofbirthPicker(bindingObj.jobEndDate ,2)

                else
                    showSnackbar("Please select job start date first")
            }
            R.id.jobExpiryDate->
            {
                if(bindingObj.jobEndDate.text.length!=0)
                    dateofbirthPicker(bindingObj.jobExpiryDate ,3)

                else
                    showSnackbar("Please select job start and end date first")
            }

            R.id.jobsAddTagsBtn->
            {
                if(bindingObj.jobsTagsEdittext.text.length!=0)
                {
                    mTagsList.add(bindingObj.jobsTagsEdittext.text.toString())
                    tagsAdapter.notifyDataSetChanged()
                    bindingObj.jobsTagsEdittext.text.clear()
                }
            }

            R.id.jobCategoryBtn->
            {
                categorySpinnerSettings()
            }

            R.id.jobCountryBtn->
            {
                mCountryListener()
            }

            R.id.jobStateBtn->
            {
                mStatesListener()
            }

            R.id.jobCityBtn->
            {
                mCityListener()
            }

        }
    }


    fun populateRadioBtnsData()
    {
        if (bindingObj.jobMaleRadiobtn.isChecked)
            gender = bindingObj.jobMaleRadiobtn.text.toString().trim()

        else if (bindingObj.jobFemaleRadiobtn.isChecked)
            gender = bindingObj.jobFemaleRadiobtn.text.toString().trim()

        if (bindingObj.jobIsAuditionRequiredYesBtn.isChecked)
            isAuditionRequired = "1"

        else if (bindingObj.jobIsAuditionRequiredNoBtn.isChecked)
            isAuditionRequired = "0"

        if (bindingObj.jobDurationPerdayBtn.isChecked)
            budgetDuration = bindingObj.jobDurationPerdayBtn.text.toString().trim()

        else if (bindingObj.jobDurationPerweekBtn.isChecked)
            budgetDuration = bindingObj.jobDurationPerweekBtn.text.toString().trim()

        else if (bindingObj.jobDurationPermonthBtn.isChecked)
            budgetDuration = bindingObj.jobDurationPermonthBtn.text.toString().trim()

        else if (bindingObj.jobDurationOnetimeBtn.isChecked)
            budgetDuration = bindingObj.jobDurationOnetimeBtn.text.toString().trim()

    }


    fun postOrEditJob()
    {
        populateRadioBtnsData()
        mPresenter.isValid(
                bindingObj.jobTitle.text.toString().trim()
                ,bindingObj.jobDescription.text.toString().trim()
                ,bindingObj.jobStartDate.text.toString().trim()
                ,bindingObj.jobEndDate.text.toString().trim()
                ,bindingObj.jobBudget.text.toString().trim()
                ,bindingObj.jobLocation.text.toString().trim()
                ,country_id
                ,city_id ,state_id ,role_id , gender
                ,bindingObj.JobMinAge.text.toString().trim()
                ,bindingObj.JobMaxAge.text.toString().trim()
                ,mTagsList.toString()
                ,budgetDuration
                ,bindingObj.jobEmail.text.toString().trim()
                ,bindingObj.jobPhone.text.toString().trim()
                ,bindingObj.jobWhatsapp.text.toString().trim()
                ,bindingObj.jobContactPersonName.text.toString().trim()
                ,bindingObj.jobVacancies.text.toString().trim())

/*                Timber.e(bindingObj.jobTitle.text.toString().trim())
                Timber.e(bindingObj.jobDescription.text.toString().trim())
                Timber.e(bindingObj.jobStartDate.text.toString().trim())
                Timber.e(bindingObj.jobEndDate.text.toString().trim())
                Timber.e(bindingObj.jobBudget.text.toString().trim())
                Timber.e(bindingObj.jobLocation.text.toString().trim())
                Timber.e(city_id +state +role_id + gender)
                Timber.e(bindingObj.JobMinAge.text.toString().trim())
                Timber.e(bindingObj.JobMaxAge.text.toString().trim())
                Timber.e(mTagsList.toString())
                Timber.e(budgetDuration)
                Timber.e(bindingObj.jobEmail.text.toString().trim())
                Timber.e(bindingObj.jobPhone.text.toString().trim())
                Timber.e(bindingObj.jobWhatsapp.text.toString().trim())
                Timber.e(bindingObj.jobContactPersonName.text.toString().trim())*/
    }
    fun populateCategories()
    {

        categoryIdsList.add("2")
        mRoleList.add("Model")

        categoryIdsList.add("3")
        mRoleList.add("Photographer")

        categoryIdsList.add("4")
        mRoleList.add("Makeup Artist")

        categoryIdsList.add("5")
        mRoleList.add("Stylist")

        categoryIdsList.add("7")
        mRoleList.add("Hair Stylist")

        categoryIdsList.add("8")
        mRoleList.add("Anchor")

        categoryIdsList.add("9")
        mRoleList.add("Dancer")

        categoryIdsList.add("10")
        mRoleList.add("Fashion Designer")

        categoryIdsList.add("11")
        mRoleList.add("Actor")

        categoryIdsList.add("12")
        mRoleList.add("Modelling Choreographer")

        categoryIdsList.add("13")
        mRoleList.add("Comedian")

        categoryIdsList.add("14")
        mRoleList.add("Singer")

        categoryIdsList.add("15")
        mRoleList.add("Influencer")

        categoryIdsList.add("16")
        mRoleList.add("Elite")
    }

}
