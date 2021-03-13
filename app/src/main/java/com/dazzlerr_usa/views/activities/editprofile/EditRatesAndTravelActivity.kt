package com.dazzlerr_usa.views.activities.editprofile

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityEditRatesAndTravelBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.fragments.profile.ProfileModel
import com.dazzlerr_usa.views.fragments.profile.ProfilePojo
import com.dazzlerr_usa.views.fragments.profile.ProfilePresenter
import com.dazzlerr_usa.views.fragments.profile.ProfileView
import com.dazzlerr_usa.views.activities.editprofile.models.RatesModel
import com.dazzlerr_usa.views.activities.editprofile.presenters.RatesPresenter
import com.dazzlerr_usa.views.activities.editprofile.views.RatesView
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.itempicker.ItemPickerDialog
import com.dazzlerr_usa.utilities.itempicker.ItemPickerListener
import com.dazzlerr_usa.views.fragments.profile.pojo.GetContactDetailsPojo
import kotlinx.android.synthetic.main.fragment_rates.*
import javax.inject.Inject

class EditRatesAndTravelActivity : AppCompatActivity() ,View.OnClickListener , RatesView, ProfileView
{
    override fun onShortList(status: String) {
        //Not in use
    }


    override fun onFollowOrUnfollow(status: String) {
        //Not in use
    }

    override fun onLikeOrDislike(status: String) {
        // Not in use
    }

    override fun onGetContactDetails(model: GetContactDetailsPojo) {
        //Not in use
    }
    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var bindingObj: ActivityEditRatesAndTravelBinding
    lateinit var mPresenter: RatesPresenter
    lateinit var profilePresenter : ProfilePresenter
    var fullday_rate= ""
    var halfday_rate= ""
    var hourly_rate= ""
    var will_test= ""
    var will_travel= ""
    var ispassport_ready= ""
    var payment_options= ""
    var availability= ""

    lateinit var profilePojo: ProfilePojo
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_edit_rates_and_travel)
        initializations()
        clickListeners()
    }


    fun initializations()
    {

        if (Constants.CURRENT_CURRENCY.isEmpty() || Constants.CURRENT_CURRENCY.equals("inr", ignoreCase = true))
        {
        bindingObj.fulldayRateEdittext.hint = "eg. 3000(INR)"
        bindingObj.halfdayRateEdittext.hint = "eg. 3000(INR)"
        bindingObj.hourlyRateEdittext.hint = "eg. 3000(INR)"

        }
        else
        {
            bindingObj.fulldayRateEdittext.hint = "eg. 300(USD)"
            bindingObj.halfdayRateEdittext.hint = "eg. 300(USD)"
            bindingObj.hourlyRateEdittext.hint = "eg. 300(USD)"
        }

        (application as MyApplication).myComponent.inject(this)
        bindingObj.titleLayout.titletxt.text = "Rates & Travel"
        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)

        bindingObj.titleLayout.rightbtn.visibility = View.GONE
        mPresenter = RatesModel(this, this)

        profilePresenter = ProfileModel(this as Activity , this)

        if(isNetworkActiveWithMessage())
            profilePresenter.getProfile(sharedPreferences.getString(Constants.User_id).toString(),"")

    }

    fun populateProfileData()
    {
        bindingObj.rateAvailabilityTxt.text = profilePojo.data?.get(0)?.availability

        availability = profilePojo.data?.get(0)?.availability!!
        bindingObj.fulldayRateEdittext.setText(profilePojo.data?.get(0)?.full_day)
        bindingObj.halfdayRateEdittext.setText(profilePojo.data?.get(0)?.half_day)
        bindingObj.hourlyRateEdittext.setText(profilePojo.data?.get(0)?.hourly)
        bindingObj.rateDistanceEdittext.setText(profilePojo.data?.get(0)?.drive_miles)
        bindingObj.rateFacebookEdittext.setText(profilePojo.data?.get(0)?.facebook.toString().toLowerCase())
        bindingObj.rateTwitterEdittext.setText(profilePojo.data?.get(0)?.twitter.toString().toLowerCase())
        bindingObj.rateInstagramEdittext.setText(profilePojo.data?.get(0)?.insta.let { profilePojo.data?.get(0)?.insta.toString().toLowerCase() })

        if(profilePojo.data?.get(0)?.full_day.toString().equals("Available on request", ignoreCase = true))
            bindingObj.fulldayRateCheckbox.isChecked =true

        if(profilePojo.data?.get(0)?.half_day.toString().equals("Available on request", ignoreCase = true))
            bindingObj.halfdayRateCheckbox.isChecked =true

         if(profilePojo.data?.get(0)?.hourly.toString().equals("Available on request", ignoreCase = true))
            bindingObj.hourlyRateCheckbox.isChecked =true


        if(profilePojo.data?.get(0)?.test.toString().equals("0", ignoreCase = true))
            bindingObj.rateTFPBtn2.isChecked =true

         else if(profilePojo.data?.get(0)?.test.toString().equals("1", ignoreCase = true))
            bindingObj.rateTFPBtn1.isChecked =true

         else if(profilePojo.data?.get(0)?.test.toString().equals("2", ignoreCase = true))
                    bindingObj.rateTFPBtn3.isChecked =true


        if(profilePojo.data?.get(0)?.will_fly.toString().equals("1", ignoreCase = true))
            bindingObj.rateTravelBtn1.isChecked =true

        else if(profilePojo.data?.get(0)?.will_fly.toString().equals("0", ignoreCase = true))
                    bindingObj.rateTravelBtn2.isChecked =true

        if(profilePojo.data?.get(0)?.passport_ready.toString().equals("1", ignoreCase = true))
            bindingObj.ratePassportBtn1.isChecked =true

        else if(profilePojo.data?.get(0)?.passport_ready.toString().equals("0", ignoreCase = true))
                    bindingObj.ratePassportBtn2.isChecked =true

        else if(profilePojo.data?.get(0)?.passport_ready.toString().equals("2", ignoreCase = true))
                            bindingObj.ratePassportBtn3.isChecked =true

    }

    fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.rateSubmitBtn.setOnClickListener(this)
        bindingObj.rateAvailabilityBtn.setOnClickListener(this)
    }

    fun populateAvailabilitySpinnerData()
    {
        val availabilityArray = resources.getStringArray(R.array.availability_array)
        var position = 0

        for(i in availabilityArray.indices)
        {
            if(profilePojo.data?.get(0)?.availability.equals(availabilityArray[i] , ignoreCase = true))
               position = i
        }

        val shoesSizePicker = ItemPickerDialog(this@EditRatesAndTravelActivity, availabilityArray.toCollection(ArrayList()), position)
        shoesSizePicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener
        {
            override fun onItemSelected(position: Int, selectedValue: String)
            {
                availability = availabilityArray[position]
                bindingObj.rateAvailabilityTxt.text = availability
                shoesSizePicker.dismiss()
            }
        })

        shoesSizePicker.show()

    }

    fun populatePaymentSpinnerData()
    {
        bindingObj.ratePaymentSpinner.setItems(getResources().getStringArray(R.array.paymentmode_array))

        if(profilePojo.data?.get(0)?.payment_options!=null && !profilePojo.data?.get(0)?.payment_options.equals("")) {
            var mPaymentList: MutableList<String>
            mPaymentList = profilePojo.data?.get(0)?.payment_options?.split((Regex("\\n|,"))) as MutableList<String>

            if (mPaymentList.size == 1) {
                val value = mPaymentList.get(0)
                mPaymentList = ArrayList()
                mPaymentList.add(value)
            }

            bindingObj.ratePaymentSpinner.setSelectionlist(mPaymentList)
        }
    }

    fun checkboxSettings()
    {
        bindingObj.fulldayRateCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
            {
                fulldayRateEdittext.setText("Available On Request")
            }

            else
            {
                fulldayRateEdittext.setText("")
            }
        }

        bindingObj.halfdayRateCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
            {
                halfdayRateEdittext.setText("Available On Request")
            }

            else
            {
                halfdayRateEdittext.setText("")
            }
        }

        bindingObj.hourlyRateCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
            {
                hourlyRateEdittext.setText("Available On Request")
            }

            else
            {
                hourlyRateEdittext.setText("")
            }
        }

    }

    fun populateRadioBtnsData()
    {
        if (bindingObj.fulldayRateCheckbox.isChecked)
            fullday_rate = "Available On Request"
        else
            fullday_rate = bindingObj.fulldayRateEdittext.text.toString().trim()

        if (bindingObj.halfdayRateCheckbox.isChecked)
            halfday_rate = "Available on request"
        else
            halfday_rate = bindingObj.halfdayRateEdittext.text.toString().trim()

        if (bindingObj.hourlyRateCheckbox.isChecked)
            hourly_rate = "Available on request"
        else
            hourly_rate = bindingObj.hourlyRateEdittext.text.toString().trim()


        if (bindingObj.rateTFPBtn1.isChecked)
            will_test = "1"
        else if(bindingObj.rateTFPBtn2.isChecked)
            will_test = "0"
        else if(bindingObj.rateTFPBtn3.isChecked)
            will_test = "2"

        if (bindingObj.rateTravelBtn1.isChecked)
            will_travel = "1"
        else if(bindingObj.rateTravelBtn2.isChecked)
            will_travel = "0"

        if (bindingObj.ratePassportBtn1.isChecked)
            ispassport_ready = "1"
        else if(bindingObj.ratePassportBtn2.isChecked)
            ispassport_ready = "0"
        else if(bindingObj.ratePassportBtn3.isChecked)
            ispassport_ready = "2"

        payment_options =bindingObj.ratePaymentSpinner.selectedItemsAsString
    }

    override fun onGetProfileSuccess(model: ProfilePojo)
    {
        profilePojo = model
        populateProfileData()
        populatePaymentSpinnerData()
        checkboxSettings()
    }


    override fun onSuccess()
    {
       Toast.makeText(this@EditRatesAndTravelActivity , "Rates and travels has been updated successfully" ,Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onProfilePicUploaded(url: String) {
        //--- Not in use
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


    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.leftbtn ->  finish()

            R.id.rateSubmitBtn->
            {

                populateRadioBtnsData()


                if(isNetworkActiveWithMessage())
                {

                    if(bindingObj.rateFacebookEdittext.text.trim().isNotEmpty() && !Patterns.WEB_URL.matcher(bindingObj.rateFacebookEdittext.text.trim()).matches())
                    {
                        showSnackbar("Please enter a valid facebook url")
                    }

                    else if(bindingObj.rateTwitterEdittext.text.trim().isNotEmpty() && !Patterns.WEB_URL.matcher(bindingObj.rateTwitterEdittext.text.trim()).matches())
                    {
                        showSnackbar("Please enter a valid twitter url")
                    }

                    else if(bindingObj.rateInstagramEdittext.text.trim().isNotEmpty() && !Patterns.WEB_URL.matcher(bindingObj.rateInstagramEdittext.text.trim()).matches())
                    {
                        showSnackbar("Please enter a valid instagram url")
                    }

                    else {
                        mPresenter.updateRatesAndTravel(sharedPreferences.getString(Constants.User_id).toString()
                                , fullday_rate, halfday_rate, hourly_rate, will_test, will_travel, ispassport_ready
                                , bindingObj.rateDistanceEdittext.text.toString().trim()
                                , payment_options
                                , availability
                                , bindingObj.rateFacebookEdittext.text.toString().trim()
                                , bindingObj.rateTwitterEdittext.text.toString().trim()
                                , bindingObj.rateInstagramEdittext.text.toString().trim())
                    }
                }
            }

            R.id.rateAvailabilityBtn-> populateAvailabilitySpinnerData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.cancelRetrofitRequest()
    }

}
