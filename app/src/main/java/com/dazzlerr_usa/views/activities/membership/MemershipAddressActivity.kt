package com.dazzlerr_usa.views.activities.membership

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityInitialScreenBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.ccavenueutility.AvenuesParams
import com.dazzlerr_usa.utilities.ccavenueutility.ServiceUtility
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.utilities.itempicker.ItemPickerDialog
import com.dazzlerr_usa.utilities.itempicker.ItemPickerListener
import com.dazzlerr_usa.views.activities.editprofile.pojos.CitiesPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.CountryPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.StatesPojo
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.views.activities.featuredccavenue.CCAvenueModel
import com.dazzlerr_usa.views.activities.featuredccavenue.CCAvenuePresenter
import com.dazzlerr_usa.views.activities.featuredccavenue.CCAvenueView
import timber.log.Timber
import javax.inject.Inject

class MemershipAddressActivity : AppCompatActivity(), CCAvenueView, View.OnClickListener
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var bindingObj : ActivityInitialScreenBinding
    lateinit var mPresenter: CCAvenuePresenter
    var mStatesList:ArrayList<String> = ArrayList()
    var mCityList:ArrayList<String> = ArrayList()
    var mStatesIdsList:ArrayList<String> = ArrayList()
    var mCitiesIdList:ArrayList<String> = ArrayList()
    var statePosition = 0
    var cityPosition = 0
    var state_id = ""
    var mCountryIdsList:ArrayList<String> = ArrayList()
    var mCountriesList:ArrayList<String> = ArrayList()
    var country_id = ""
    var countryPosition = 0

    public override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_initial_screen)
        initializations()

        clickListeners()
        apiCalling()
    }

    fun initializations()
    {

        (application as MyApplication).myComponent.inject(this@MemershipAddressActivity)
        bindingObj.titleLayout.titletxt.text = "Checkout"

        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        mPresenter = CCAvenueModel(this, this)


        bindingObj.billingName.setText(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.User_name).toString())
        bindingObj.billingEmail.setText(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.User_Email).toString())
        bindingObj.billingTel.setText(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.User_Phone).toString())
        bindingObj.billingAddress.setText(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.User_Address).toString())
        bindingObj.billingZip.setText(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.User_Zipcode).toString())

        if(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.billing_city).toString().isEmpty())
        {
            bindingObj.checkoutcityTxt.setText(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.Current_city).toString())

        }

        else
        {
            bindingObj.checkoutcityTxt.setText(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.billing_city).toString())

        }

        if(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.billing_state).toString().isEmpty())
        {
            bindingObj.checkoutStateTxt.setText(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.Current_state).toString())
        }

        else
        {
            bindingObj.checkoutStateTxt.setText(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.billing_state).toString())
        }

        if(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.billing_country).toString().isEmpty())
        {
            bindingObj.checkoutCountryTxt.setText(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.Current_country).toString())
        }

        else
        {
            bindingObj.checkoutCountryTxt.setText(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.billing_country).toString())
        }
    }

    private fun apiCalling()
    {

        if(isNetworkActive())
        {
            mPresenter.getCountries()
        }

        else
        {

            val dialog = CustomDialog(this)
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

    fun clickListeners() {


        bindingObj.checkoutButton.setOnClickListener(this)
        bindingObj.checkoutStateBtn.setOnClickListener(this)
        bindingObj.checkoutCityBtn.setOnClickListener(this)
        bindingObj.checkoutCountryBtn.setOnClickListener(this)
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
    }

    fun showToast(msg: String)
    {
        Toast.makeText(this, "Toast: $msg", Toast.LENGTH_LONG).show()
    }


    fun mCountryListener()
    {

        if(mCountriesList.size!=0)
        {

            val mCountryPicker = ItemPickerDialog(this@MemershipAddressActivity, mCountriesList, countryPosition)
            mCountryPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
                override fun onItemSelected(position: Int, selectedValue: String) {

                    countryPosition = position
                    bindingObj.checkoutStateTxt.text = ""
                    state_id = ""
                    //city_id = ""

                    country_id = mCountryIdsList.get(position)
                    Timber.e("Selected Country " + country_id)

                    bindingObj.checkoutCountryTxt.text = mCountriesList.get(position)
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
            val mStatePicker = ItemPickerDialog(this@MemershipAddressActivity, mStatesList, statePosition)
            mStatePicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
                override fun onItemSelected(position: Int, selectedValue: String)
                {

                    statePosition = position
                    bindingObj.checkoutcityTxt.text = ""

                    state_id = mStatesIdsList.get(position)
                    bindingObj.checkoutStateTxt.text = mStatesList.get(position)

                    if (isNetworkActiveWithMessage())
                        mPresenter.getCities(state_id)//1 for city and 2 for current city

                    mStatePicker.dismiss()
                }
            })
            mStatePicker.show()
        }
    }

    fun mCityListener()
    {
        if(mCityList.size!=0) {
            val mCityPicker = ItemPickerDialog(this@MemershipAddressActivity, mCityList, cityPosition)
            mCityPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener {
                override fun onItemSelected(position: Int, selectedValue: String) {

                    cityPosition = position
                    bindingObj.checkoutcityTxt.text = mCityList.get(position)

                    mCityPicker.dismiss()
                }
            })
            mCityPicker.show()
        }
    }

    override fun isValidate()
    {


        sharedPreferences.saveString(com.dazzlerr_usa.utilities.Constants.User_Zipcode,ServiceUtility.chkNull(bindingObj.billingZip!!.text).toString().trim { it <= ' ' })
        sharedPreferences.saveString(com.dazzlerr_usa.utilities.Constants.User_Address,ServiceUtility.chkNull(bindingObj.billingAddress!!.text).toString().trim { it <= ' ' })
        sharedPreferences.saveString(com.dazzlerr_usa.utilities.Constants.billing_city,  ServiceUtility.chkNull(bindingObj.checkoutcityTxt.text.toString()).toString().trim { it <= ' ' })
        sharedPreferences.saveString(com.dazzlerr_usa.utilities.Constants.billing_state, ServiceUtility.chkNull(bindingObj.checkoutStateTxt.text.toString()).toString().trim { it <= ' ' })
        sharedPreferences.saveString(com.dazzlerr_usa.utilities.Constants.billing_country, ServiceUtility.chkNull(bindingObj.checkoutCountryTxt.text.toString()).toString().trim { it <= ' ' })

        var bundle: Bundle = Bundle()
        bundle.putString(AvenuesParams.ORDER_ID , this.intent!!.getStringExtra(AvenuesParams.ORDER_ID))
        var intent = Intent()
        intent.putExtras(bundle)
        setResult(1002 , intent )
        finish()

/*
        //Mandatory parameters. Other parameters can be added if required.
        val vAccessCode = ServiceUtility.chkNull(Constants.ACCESS_CODE).toString().trim { it <= ' ' }
        val vMerchantId = ServiceUtility.chkNull(Constants.MERCHANT_ID).toString().trim { it <= ' ' }
        val vCurrency = ServiceUtility.chkNull(Constants.CURRENCY).toString().trim { it <= ' ' }
        val vAmount = this.intent.extras!!.getString(AvenuesParams.AMOUNT ,"")

        if (vAccessCode != "" && vMerchantId != "" && vCurrency != "" && vAmount != "")
        {

            val intent = Intent(this, MembershipCCEvenuePaymentActivity::class.java)
            intent.putExtra(AvenuesParams.ACCESS_CODE, ServiceUtility.chkNull(Constants.ACCESS_CODE).toString().trim { it <= ' ' })
            intent.putExtra(AvenuesParams.MERCHANT_ID, ServiceUtility.chkNull(Constants.MERCHANT_ID).toString().trim { it <= ' ' })
            intent.putExtra(AvenuesParams.ORDER_ID, this.intent!!.getStringExtra(AvenuesParams.ORDER_ID))
            intent.putExtra(AvenuesParams.CURRENCY, ServiceUtility.chkNull(Constants.CURRENCY).toString().trim { it <= ' ' })
            intent.putExtra(AvenuesParams.AMOUNT, vAmount)
            intent.putExtra(AvenuesParams.REDIRECT_URL, ServiceUtility.chkNull(Constants.REDIRECT_URL).toString().trim { it <= ' ' })
            intent.putExtra(AvenuesParams.CANCEL_URL, ServiceUtility.chkNull(Constants.CANCEL_URL).toString().trim { it <= ' ' })
            intent.putExtra(AvenuesParams.BILLING_NAME, ServiceUtility.chkNull(bindingObj.billingName!!.text).toString().trim { it <= ' ' })

            intent.putExtra(AvenuesParams.BILLING_COUNTRY, ServiceUtility.chkNull("India").toString().trim { it <= ' ' })
            intent.putExtra(AvenuesParams.BILLING_STATE, ServiceUtility.chkNull(bindingObj.checkoutStateTxt.text.toString()).toString().trim { it <= ' ' })
            intent.putExtra(AvenuesParams.BILLING_CITY, ServiceUtility.chkNull(bindingObj.checkoutcityTxt.text.toString()).toString().trim { it <= ' ' })

            intent.putExtra(AvenuesParams.BILLING_TEL, ServiceUtility.chkNull(bindingObj.billingTel!!.text).toString().trim { it <= ' ' })
            intent.putExtra(AvenuesParams.BILLING_EMAIL, ServiceUtility.chkNull(bindingObj.billingEmail!!.text).toString().trim { it <= ' ' })
            intent.putExtra(AvenuesParams.DELIVERY_NAME, ServiceUtility.chkNull(bindingObj.billingName!!.text).toString().trim { it <= ' ' })

            intent.putExtra(AvenuesParams.DELIVERY_COUNTRY, ServiceUtility.chkNull("India").toString().trim { it <= ' ' })
            intent.putExtra(AvenuesParams.DELIVERY_STATE, ServiceUtility.chkNull(bindingObj.checkoutStateTxt.text.toString()).toString().trim { it <= ' ' })
            intent.putExtra(AvenuesParams.DELIVERY_CITY, ServiceUtility.chkNull(bindingObj.checkoutcityTxt.text.toString()).toString().trim { it <= ' ' })
            intent.putExtra(AvenuesParams.DELIVERY_TEL, ServiceUtility.chkNull(bindingObj.billingTel!!.text).toString().trim { it <= ' ' })
            startActivityForResult(intent ,1002)
        }
        else
        {
            showSnackbar("All parameters are mandatory.")
        }*/

    }
/*    override fun onGetStates(dataModel: StatesPojo)
    {
        if(dataModel?.data?.size!=0)
        {
            mStatesList.clear()
            mStatesIdsList.clear()

            mStatesList.add("-Select State-")
            mStatesIdsList.add("0")

            for (i:Int in dataModel?.data!!.indices)
            {
                mStatesList.add(dataModel?.data?.get(i)?.state_name.toString())
                mStatesIdsList.add(dataModel?.data?.get(i)?.state_id.toString())
            }

            myAdapter.notifyDataSetChanged()
            bindingObj.billingStateSpinner.setAdapter(myAdapter)
            billingStatesListeners()
        }
    }*/

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

            if(dataModel?.data?.get(i)?.country_name.toString().equals(bindingObj.checkoutCountryTxt?.text.toString(),ignoreCase = true))
            {
                countryIndex = i
                country_id = mCountryIdsList[i]
                if (isNetworkActiveWithMessage())
                    mPresenter.getStates(country_id)

                bindingObj.checkoutCountryTxt.text = dataModel?.data?.get(i)?.country_name
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


                if(dataModel?.data?.get(i)?.state_name.toString().equals(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.Current_state),ignoreCase = true))
                {
                    stateIndex = i
                    bindingObj.checkoutStateTxt.text = mStatesList[i]
                    state_id = mStatesIdsList[i]
                    mPresenter.getCities(state_id)//1 for city and 2 for current city
                }


            }

            statePosition = stateIndex

        }
    }


    override fun onGetCities(dataModel: CitiesPojo)
    {

        if(dataModel?.data?.size!=0)
        {

                mCityList.clear()
                var cityindex=0

                for (i:Int in dataModel?.data!!.indices)
                {
                    mCityList.add(dataModel?.data?.get(i)?.city_name.toString())
                    if(dataModel?.data?.get(i)?.city_name.toString().equals(sharedPreferences.getString(com.dazzlerr_usa.utilities.Constants.Current_city),ignoreCase = true))
                    {
                        cityindex = i
                        bindingObj.checkoutcityTxt.text = mCityList[i]
                    }
                }

                cityPosition = cityindex
        }

    }

    override fun onSuccess() {
        //Not in use
    }


    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    override fun onGetToken(model: GetTokenPojo) {
        //Not in use
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

        bindingObj.aviProgressBar.visibility = View.VISIBLE
    }

    fun stopProgressBarAnim() {

        bindingObj.aviProgressBar.visibility = View.GONE
    }



    override fun onClick(v: View?)
    {

        when (v?.id) {

            R.id.checkoutButton->
            {


                mPresenter.validate(
                bindingObj.billingName.text.toString().trim(),
                bindingObj.billingAddress.text.toString().trim(),
                bindingObj.checkoutCountryTxt.text.toString(),
                bindingObj.checkoutStateTxt.text.toString(),
                bindingObj.checkoutcityTxt.text.toString(),
                bindingObj.billingZip.text.toString().trim(),
                bindingObj.billingTel.text.toString().trim(),
                bindingObj.billingEmail.text.toString().trim())

            }

            R.id.checkoutCountryBtn->
            {
                mCountryListener()
            }
            R.id.checkoutCityBtn->
                mCityListener()

            R.id.checkoutStateBtn->
                mStatesListener()

            R.id.leftbtn-> finish()
        }
    }
/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode)
        {
            1002->
            {

                if( data?.extras!=null)
                {

                    var bundle: Bundle = data?.extras!!
                    var intent = Intent()
                    intent.putExtras(bundle)
                    setResult(1002 , intent )
                    finish()

                }
            }
        }
    }*/
}