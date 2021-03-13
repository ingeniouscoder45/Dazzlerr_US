package com.dazzlerr_usa.views.fragments.register.localfragments


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentRegisterBinding
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.itempicker.ItemPickerDialog
import com.dazzlerr_usa.utilities.itempicker.ItemPickerListener
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.fragments.ParentFragment
import com.dazzlerr_usa.views.activities.editprofile.pojos.CitiesPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.CountryPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.StatesPojo
import com.dazzlerr_usa.views.activities.mobileverification.MobileVerificationActivity
import com.dazzlerr_usa.views.fragments.register.RegisterModel
import com.dazzlerr_usa.views.fragments.register.RegisterPojo
import com.dazzlerr_usa.views.fragments.register.RegisterPresenter
import com.dazzlerr_usa.views.fragments.register.RegisterView
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.views.activities.weblinks.WebLinksActivity
import timber.log.Timber
import java.util.*


class RegisterModelFragment : ParentFragment()  , View.OnClickListener , RegisterView
{

    override fun isRegisterCastingValidate(isValid: Boolean)
    {
        //Not is use
    }

    override fun onGetStates(dataModel: StatesPojo) {
        //Not in use
    }

    override fun onGetCities(dataModel: CitiesPojo) {
       //Not in use
    }

    override fun onGetCountry(dataModel: CountryPojo) {
        //Not in use
    }

    lateinit var bindingObj: FragmentRegisterBinding
    lateinit var mPresenter: RegisterPresenter
    var categoryIdsList : ArrayList<String> =  ArrayList()
    var mRoleList : ArrayList<String> =  ArrayList()
    var role_id  = ""
    var role_name  = ""
    var representerPosition =0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {

        // Inflate the layout for this fragment
        bindingObj =  DataBindingUtil.inflate(inflater , R.layout.fragment_register, container, false)

        populateCategories()
        initializations()
        clickListeners()

        return bindingObj.root
    }

    fun initializations()
    {


        //Initializing Google ads
       /* val testDeviceIds: List<String> = Arrays.asList("A86FED701CDC6E77D341DEBA488306E3")
        val configuration: RequestConfiguration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
*/
        //MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        bindingObj.adView.visibility = View.VISIBLE
        bindingObj.adView.loadAd(adRequest)
        //--------------------------


        bindingObj.titleLayout.titletxt.text = "Register as Talent"

        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)

        mPresenter = RegisterModel(activity as Activity, this)

    }

    fun clickListeners()
    {
        bindingObj.registerSubmitBtn.setOnClickListener(this)
        bindingObj.termsOfUseBtn.setOnClickListener(this)
        bindingObj.registerAsSpinnerBtn.setOnClickListener(this)
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.titleLayout.rightbtn.visibility = View.GONE
    }

    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.leftbtn -> (activity as MainActivity).backpress()
            R.id.registerAsSpinnerBtn ->
            {
                val representerPicker = ItemPickerDialog(activity as Activity, mRoleList, representerPosition)
                representerPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener
                {
                    override fun onItemSelected(position: Int, selectedValue: String)
                    {
                        role_id = categoryIdsList.get(position)
                        role_name = mRoleList.get(position)
                        representerPosition = position
                        bindingObj.registerAsSpinnerTxt.text = role_name
                        Timber.e("RoleName "+ role_name)
                        Timber.e("RoleID "+ role_id)
                        representerPicker.dismiss()
                    }
                })

                representerPicker.show()
            }

            R.id.registerSubmitBtn->
            {

                mPresenter.validateModel(role_id ,bindingObj.registerEmail.text.toString().trim(),bindingObj.registerPassword.text.toString().trim()
                        ,bindingObj.registerConfirmPassword.text.toString().trim()
                        ,bindingObj.registerPhone.text.toString().trim()
                        ,bindingObj.registerFirstname.text.toString().trim()
                        ,bindingObj.registerLastname.text.toString().trim()
                )
            }

            R.id.termsOfUseBtn->
            {

                val bundle = Bundle()
                bundle.putString("type", "Terms")
                val newIntent = Intent(activity, WebLinksActivity::class.java)
                newIntent.putExtras(bundle)
                activity?.startActivity(newIntent)
            }

        }
    }

    override fun onRegisterSuccess(dataModel: MutableList<RegisterPojo.DataBean>)
    {

        if(dataModel.size!=0)
        {
            showSnackbar("Registered successfully")
            (activity as MainActivity).sharedPreferences.saveString(Constants.User_id , ""+dataModel.get(0).user_id)
            (activity as MainActivity).sharedPreferences.saveString(Constants.Token , "")
            (activity as MainActivity).sharedPreferences.saveString(Constants.User_name , bindingObj.registerFirstname.text.toString().trim()+" "+bindingObj.registerLastname.text.toString().trim())
            (activity as MainActivity).sharedPreferences.saveString(Constants.User_Role , role_name)
            (activity as MainActivity).sharedPreferences.saveString(Constants.User_RoleId , role_id)
            (activity as MainActivity).sharedPreferences.saveString(Constants.Exp_type , "")
            (activity as MainActivity).sharedPreferences.saveString(Constants.Current_city , "")
            (activity as MainActivity).sharedPreferences.saveString(Constants.Current_state , "")
            (activity as MainActivity).sharedPreferences.saveString(Constants.User_pic , "")
            (activity as MainActivity).sharedPreferences.saveString(Constants.User_Email , bindingObj.registerEmail.text.toString().trim())
            (activity as MainActivity).sharedPreferences.saveString(Constants.User_Phone ,  bindingObj.registerPhone.text.toString().trim())
            (activity as MainActivity).sharedPreferences.saveString(Constants.Is_Email_Verified , "0")// 0 or 1 format
            (activity as MainActivity).sharedPreferences.saveString(Constants.Is_Phone_Verified ,"0") // 0 or 1 format
            (activity as MainActivity).sharedPreferences.saveString(Constants.IsProfile_published , "1") // 0 or 1 format
            (activity as MainActivity).sharedPreferences.saveString(Constants.Account_type , "Free")
            (activity as MainActivity).sharedPreferences.saveString(Constants.Membership_id , "0")
            //(activity as MainActivity).fragment_manager.popBackStackImmediate()


            startActivity(Intent(activity , MobileVerificationActivity::class.java))

/*            val fragment = ValidateOtpFragment()
            val args = Bundle()

            args.putString("Message", bindingObj.registerPhone.text.toString().trim())
            args.putString("RecoveryType", "mobile")
            args.putString("registerType", "RegisterModel")
            fragment.arguments = args
            (activity as MainActivity).LoadFragment(fragment)*/

/*
            val bundle = Bundle()

            // This is for differentiating Login with register because we have to direct the registered user after verification to membership screen
            bundle.putString("registerType" , "RegisterModel")
            val fragment = AccountVerificationFragment()
            fragment.arguments = bundle
            (activity as MainActivity).LoadFragment(fragment)
*/

            /*var bundle = Bundle()
            bundle.putString("membership_type" , "register")

            startActivity(Intent(activity  , MembershipActivity::class.java).putExtras(bundle))*/
        }
    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    fun showSnackbar(message : String)
    {
        Snackbar.make((activity as Activity).findViewById(android.R.id.content) , message , Snackbar.LENGTH_SHORT ).show()
    }
    override fun showProgress(showProgress: Boolean)
    {
        if(showProgress)
            (activity as MainActivity).startProgressBarAnim()

        else
            (activity as MainActivity).stopProgressBarAnim()
    }

    @SuppressLint("DefaultLocale")
    override fun isRegisterModelValidate(isValid: Boolean)
    {
        (activity as MainActivity).getDeviceInfo()

        if((activity as MainActivity).isNetworkActiveWithMessage()) {
            mPresenter.registerModel(role_id, bindingObj.registerEmail.text.toString().trim().toLowerCase()
                    , bindingObj.registerPassword.text.toString().trim()
                    , bindingObj.registerPhone.text.toString().trim()
                    , bindingObj.registerFirstname.text.toString().trim() + " " + bindingObj.registerLastname.text.toString().trim()
                    , role_name, (activity as MainActivity).sharedPreferences.getString(Constants.device_id).toString(), (activity as MainActivity).sharedPreferences.getString(Constants.DEVICE_BRAND).toString()
                    , (activity as MainActivity).sharedPreferences.getString(Constants.DEVICE_MODEL).toString()
                    , "Android", (activity as MainActivity).sharedPreferences.getString(Constants.DEVICE_VERSION).toString())
        }
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
    }

    override fun onDestroy() {
        super.onDestroy()

        mPresenter.cancelRetrofitRequest()
    }
}
