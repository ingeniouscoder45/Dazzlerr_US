package com.dazzlerr_usa.views.fragments.login


import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast

import com.dazzlerr_usa.R
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.databinding.FragmentLoginBinding
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.fragments.ParentFragment
import com.dazzlerr_usa.views.fragments.forgotpassword.localfragments.ForgotPasswordFragment
import com.dazzlerr_usa.views.fragments.modelaccountverification.AccountVerificationFragment
import com.dazzlerr_usa.views.fragments.register.localfragments.ChooseRegisterFragment
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.PermissionUtils
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.views.activities.contactus.ContactUsActivity
import com.dazzlerr_usa.views.activities.mainactivity.*
import com.dazzlerr_usa.views.activities.mobileverification.MobileVerificationActivity
import com.dazzlerr_usa.views.activities.weblinks.WebLinksActivity
import com.onlinepoundstore.fragments.login.LoginModel
import com.onlinepoundstore.fragments.login.LoginPresenter
import com.onlinepoundstore.fragments.login.LoginView
import permissions.dispatcher.*
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */

@RuntimePermissions
class LoginFragment : ParentFragment(), View.OnClickListener , LoginView, MainActivityView
{

    internal lateinit var bindingObject: FragmentLoginBinding
    var loginPresenter: LoginPresenter?=null
    lateinit var mPresenter: MainActivityPresenter
    var isValid:Boolean?=null
    var isAppDisabled = ""
    var shutdownLink = ""
    var isCallPermissionsGiven = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {

        // Inflate the layout for this fragment
        bindingObject = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
       // (activity as? MainActivity)?.checkForAppUpdate()
        animationSet()
        initializations()
        clickListeners()
        constantApiCalling()
        return bindingObject.root
    }


    //Call related permissions started


    fun askCallPermissions()
    {
        callUserWithPermissionCheck()
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO , Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.READ_PHONE_STATE )
    fun callUser()
    {
        isCallPermissionsGiven =true
        loginPresenter?.validate(bindingObject.userPassword.getText().toString().trim(), bindingObject.userEmail.getText().toString().trim())

    }

    @OnShowRationale(Manifest.permission.RECORD_AUDIO , Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.READ_PHONE_STATE)
    fun callPermissionRationale(request: PermissionRequest)
    {
        PermissionUtils.showRationalDialog(activity as Activity, R.string.permission_rationale_call, request)
    }

    @OnPermissionDenied(Manifest.permission.RECORD_AUDIO , Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.READ_PHONE_STATE)
    fun callPermissionDenied() {
        Toast.makeText(activity as Activity , R.string.permission_denied_call , Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.RECORD_AUDIO , Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.READ_PHONE_STATE)
    fun callPermissionNeverAsk()
    {
        PermissionUtils.showAppSettingsDialog(this, R.string.permission_never_ask_call, Constants.REQ_CODE_APP_SETTINGS)
    }

    private fun initializations()
    {


        //Enabling the notifications by default
        (activity as MainActivity).sharedPreferences.saveBoolean(Constants.isShowNotifications , true)

        (activity as MainActivity).titleVisibility(false)
        (activity as MainActivity).showStatusBar()
        mPresenter = MainActivityModel(activity as Activity ,this)
        loginPresenter = LoginModel(activity as Activity , this)
    }

    fun constantApiCalling()
    {
        if((activity as Activity).isNetworkActiveWithMessage())
        {
            mPresenter.getConstants((activity as MainActivity).sharedPreferences.getString(Constants.User_id).toString())
        }
    }


    private fun clickListeners()
    {

        bindingObject.forgotPasswordBtn.setOnClickListener(this)
        bindingObject.skipBtn.setOnClickListener(this)
        bindingObject.loginBtn.setOnClickListener(this)
        bindingObject.registerBtn.setOnClickListener(this)
    }

    private fun animationSet()
    {
        val anim1 = AnimationUtils.loadAnimation(activity, R.anim.fade1)
        bindingObject.containerLayout.startAnimation(anim1)

        val anim = AnimationUtils.loadAnimation(activity, R.anim.slide_up)
        anim.startOffset = 300
        bindingObject.bottomLayout.startAnimation(anim)

        /*
        ObjectAnimator flip = ObjectAnimator.ofFloat(bindingObject.logo, "rotationY", 0f, 360f);
        flip.setDuration(1600);
        flip.setStartDelay(900);
        flip.start();
        */

    }

    override fun onClick(v: View) {

        when (v.id)
        {

            R.id.loginBtn -> {

                if (activity?.isNetworkActiveWithMessage()!!)
                {
                    if(isAppDisabled.equals("enabled",ignoreCase = true))
                    {

                        val bundle = Bundle()
                        bundle.putString("type", "shutdown")
                        bundle.putString("url", shutdownLink)

                        val intent =
                                Intent(activity as Activity, WebLinksActivity::class.java)
                        intent.putExtras(bundle)
                        startActivity(intent)
                        (activity as MainActivity).finish()
                    }
                    else
                    {

                        if(!isCallPermissionsGiven)
                            askCallPermissions()
                        else
                        loginPresenter?.validate(bindingObject.userPassword.getText().toString().trim(), bindingObject.userEmail.getText().toString().trim())

                    }
                }

            }

            R.id.forgotPasswordBtn -> (activity as MainActivity).LoadFragment(ForgotPasswordFragment())

            R.id.skipBtn ->
            {
                if(isAppDisabled.equals("enabled",ignoreCase = true))
                {

                    val bundle = Bundle()
                    bundle.putString("type", "shutdown")
                    bundle.putString("url", shutdownLink)

                    val intent =
                            Intent(activity as Activity, WebLinksActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                    (activity as MainActivity).finish()
                }
                else {
                    (activity as MainActivity).sharedPreferences.saveString(Constants.Latitude, "28.65381")
                    (activity as MainActivity).sharedPreferences.saveString(Constants.Longitude, "77.22897")
                    (activity as MainActivity).sharedPreferences.saveString(Constants.CurrentAddress, "Delhi")

                    (activity as MainActivity).sharedPreferences.saveString(Constants.User_type, "GuestUser")
                    val intent = Intent(activity, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    activity!!.finish()
                }
            }

            R.id.registerBtn->
            {
                if(isAppDisabled.equals("enabled",ignoreCase = true))
                {

                    val bundle = Bundle()
                    bundle.putString("type", "shutdown")
                    bundle.putString("url", shutdownLink)

                    val intent =
                            Intent(activity as Activity, WebLinksActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                    (activity as MainActivity).finish()
                }
                else {
                    val fragment = ChooseRegisterFragment()
                    (activity as MainActivity).LoadFragment(fragment)
                }
            }
        }
    }

    override fun isLoginValidate(isValid: Boolean)
    {

        (activity as MainActivity).getDeviceInfo()

        if(isValid)
            loginPresenter?.login(bindingObject.userEmail.getText().toString().trim() , bindingObject.userPassword.getText().toString().trim(),(activity as MainActivity).sharedPreferences.getString(Constants.device_id).toString(),(activity as MainActivity).sharedPreferences.getString(Constants.DEVICE_BRAND).toString() , (activity as MainActivity).sharedPreferences.getString(Constants.DEVICE_MODEL).toString() ,"Android", (activity as MainActivity).sharedPreferences.getString(Constants.DEVICE_VERSION).toString()  )
    }

    override fun onLoginSuccess(dataModel: LoginPojo)
    {

        if(dataModel.status_type?.equals("suspended", ignoreCase = true)!! || dataModel.status_type?.equals("deleted", ignoreCase = true)!!  )
        {

            val mDialog = CustomDialog(activity)
            if(dataModel.status_type?.equals("suspended", ignoreCase = true)!!)
                mDialog.setTitle("Account Suspended !")
            else if(dataModel.status_type?.equals("deleted", ignoreCase = true)!!)
                mDialog.setTitle("Account Deleted!")
            else
                mDialog.setTitle("Alert!")

            mDialog.setMessage(dataModel.message)
            mDialog.setPositiveButton("Ok") {
                startActivity(
                        Intent(
                                activity,
                                ContactUsActivity::class.java
                        )
                )
                mDialog.dismiss()
            }

            mDialog.setNegativeButton("Cancel") {
                mDialog.dismiss()
                bindingObject.userEmail.requestFocus()
            }
            mDialog.show()

        }

        else if(dataModel.status_type.equals("success",ignoreCase = true) || dataModel.status_type.equals("device_changed",ignoreCase = true))
        {

            Constants.isComingFromLogin = "true"//This is for checking in home screen that whether we want to show home fragment or dashboard. If coming from login then we have to show home fragment

            (activity as MainActivity).sharedPreferences.saveString(Constants.User_id, "" + dataModel.data?.user_id)
            (activity as MainActivity).sharedPreferences.saveString(Constants.Employer_id, "" + dataModel.data?.employer_id)
            (activity as MainActivity).sharedPreferences.saveString(Constants.Token, "" + dataModel.data?.token)
            (activity as MainActivity).sharedPreferences.saveString(Constants.User_name, "" + dataModel.data?.name)
            (activity as MainActivity).sharedPreferences.saveString(Constants.User_Role, "" + dataModel.data?.user_role)
            (activity as MainActivity).sharedPreferences.saveString(Constants.Exp_type, "" + dataModel.data?.exp_type)
            (activity as MainActivity).sharedPreferences.saveString(Constants.Current_city, "" + dataModel.data?.current_city)
            (activity as MainActivity).sharedPreferences.saveString(Constants.Current_state, "" + dataModel.data?.state_name)
            (activity as MainActivity).sharedPreferences.saveString(Constants.User_pic, "" + dataModel.data?.profile_pic)
            (activity as MainActivity).sharedPreferences.saveString(Constants.User_Email, "" + dataModel.data?.email)
            (activity as MainActivity).sharedPreferences.saveString(Constants.User_Phone, "" + dataModel.data?.phone)
            (activity as MainActivity).sharedPreferences.saveString(Constants.Is_Email_Verified, "" + dataModel.data?.email_isverified)// 0 or 1 format
            (activity as MainActivity).sharedPreferences.saveString(Constants.Is_Phone_Verified, "" + dataModel.data?.phone_isverified) // 0 or 1 format
            (activity as MainActivity).sharedPreferences.saveString(Constants.Is_Documement_Verified, "" + dataModel.data?.is_document_verified) // 0 or 1 format
            (activity as MainActivity).sharedPreferences.saveString(Constants.Is_Documement_Submitted, "" + dataModel.data?.is_document_submitted) // 0 or 1 format
            (activity as MainActivity).sharedPreferences.saveString(Constants.Account_type, "" + dataModel.data?.account_type) // 0 or 1 format
            (activity as MainActivity).sharedPreferences.saveString(Constants.Membership_id, "" + dataModel.data?.membership_id) // 0 or 1 format
            (activity as MainActivity).sharedPreferences.saveString(Constants.IsProfile_published, "" + dataModel.data?.is_published) // 0 or 1 format
            (activity as MainActivity).sharedPreferences.saveString(Constants.identity_proof, "" + dataModel.data?.identity_proof)
            (activity as MainActivity).sharedPreferences.saveString(Constants.identity_video, "" + dataModel.data?.identity_video)

            // if((activity as MainActivity).sharedPreferences.getString(Constants.CurrentAddress).equals(""))
            // {

            if(dataModel.status_type.equals("device_changed",ignoreCase = true)) {

                val bundle = Bundle()
                bundle.putString("navigateFrom" , "Login")
                startActivity(Intent(activity as Activity , MobileVerificationActivity::class.java).putExtras(bundle))
            }

            else
            {
                if ((activity as MainActivity).sharedPreferences.getString(Constants.Is_Phone_Verified).equals("1") || (activity as MainActivity).sharedPreferences.getString(Constants.Is_Email_Verified).equals("1"))
                {
                    val intent = Intent(activity, HomeActivity::class.java)

                    (activity as MainActivity).sharedPreferences.saveString(Constants.User_type, "MainUser")

                    (activity as MainActivity).sharedPreferences.saveString(Constants.Latitude, "28.65381")
                    (activity as MainActivity).sharedPreferences.saveString(Constants.Longitude, "77.22897")
                    (activity as MainActivity).sharedPreferences.saveString(Constants.CurrentAddress, "Delhi")

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    activity!!.finish()
                } else {

                    val bundle = Bundle()
                    if ((activity as MainActivity).sharedPreferences.getString(Constants.User_Role).equals("Casting", ignoreCase = true))
                        bundle.putString("registerType", "Casting")
                    else
                        bundle.putString("registerType", "Model")

                    val fragment = AccountVerificationFragment()
                    fragment.arguments = bundle
                    (activity as MainActivity).LoadFragment(fragment)

                }
            }
        }



     //   }


      //  else
     //   {

       //     val intent = Intent(activity, DashboardActivity::class.java)
        //            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
         //   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
         //   startActivity(intent)
          //  activity!!.finish()
      //  }
    }

    override fun onLoginFailed(message: String)
    {
        try
        {
        Snackbar.make((activity as Activity).findViewById(android.R.id.content) , message , Snackbar.LENGTH_SHORT ).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun showProgress(showProgress: Boolean)
    {

        try {
            if (showProgress)
                (activity as MainActivity).startProgressBarAnim()
            else
                (activity as MainActivity).stopProgressBarAnim()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    private fun loadFragment(fragment: androidx.fragment.app.Fragment) {
        (activity as MainActivity).clearAllFragments()
        (activity as MainActivity).loadFirstFragment(fragment)
    }

    override fun onSuccess(model: MonetizeApiPojo) {

        isAppDisabled  = model.shut_down
        shutdownLink  = model.shut_down_link
    }

    override fun onFailed(message: String) {
        try {
            (activity as MainActivity).showSnackBar(message)
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

}// Required empty public constructor
