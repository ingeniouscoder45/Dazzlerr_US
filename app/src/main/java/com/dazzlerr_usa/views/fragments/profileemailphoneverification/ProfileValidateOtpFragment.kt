package com.dazzlerr_usa.views.fragments.profileemailphoneverification


import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentValidateOtpBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.views.activities.accountverification.AccountVerification
import com.dazzlerr_usa.views.fragments.forgotpassword.models.ForgotPasswordModel
import com.dazzlerr_usa.views.fragments.forgotpassword.pojos.ForgotPasswordByQuestionPojo
import com.dazzlerr_usa.views.fragments.forgotpassword.presenters.ForgotPasswordPresenter
import com.dazzlerr_usa.views.fragments.forgotpassword.views.ForgotPasswordView
import com.dazzlerr_usa.views.fragments.modelaccountverification.AccountVerificationModel
import com.dazzlerr_usa.views.fragments.modelaccountverification.AccountVerificationPresenter
import com.dazzlerr_usa.views.fragments.modelaccountverification.AccountVerificationView
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 */
class ProfileValidateOtpFragment : Fragment() , View.OnClickListener  , ForgotPasswordView , AccountVerificationView
{

    lateinit var mResendOtpPresenter: ForgotPasswordPresenter
    lateinit var mPresenter: AccountVerificationPresenter
    internal lateinit  var bindingObject: FragmentValidateOtpBinding
    internal var recoveryType: String? = ""
    internal var registerType: String? = ""
    internal var Message: String? = ""
    var email = ""
    var phone = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        bindingObject =  DataBindingUtil.inflate(inflater , R.layout.fragment_validate_otp, container, false)
        initializations()
        countdownTimer()
        return bindingObject.root
    }

    private fun initializations()
    {

        recoveryType = arguments!!.getString("RecoveryType")
        if((activity as AccountVerification).sharedPreferences.getString(Constants.User_Role).toString().equals("casting" , ignoreCase = true))
        registerType = "casting"

        else
            registerType = "Model"

        if(recoveryType.equals("email" , ignoreCase = true))
            email = arguments!!.getString("Message").toString()
        else
            phone = arguments!!.getString("Message").toString()


        Message = arguments!!.getString("Message")

        mResendOtpPresenter = ForgotPasswordModel(activity as Activity , this)
        mPresenter = AccountVerificationModel(activity as Activity , this)

        bindingObject.recoveryType.text = "We've sent an OTP to " + Message!!
        bindingObject.validateOtpBtn.setOnClickListener(this)
        bindingObject.resendOTP.setOnClickListener(this)
        bindingObject.titleLayout.leftbtn.setOnClickListener(this)
        bindingObject.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        bindingObject.titleLayout.rightbtn.visibility = View.GONE
        bindingObject.resendOTP.isEnabled = false
    }

    private fun countdownTimer()
    {
        object : CountDownTimer(165000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val Minutes = millisUntilFinished / (60 * 1000) % 60
                val Seconds = millisUntilFinished / 1000 % 60

                bindingObject.otpTxt.text = "Waiting for OTP... " + String.format("%02d", Minutes) + ":" + String.format("%02d", Seconds)

            }

            override fun onFinish() {
                bindingObject.resendOTP.isEnabled = true
            }

        }.start()
    }

    override fun onValidOtp()
    {

        if (recoveryType!!.equals("email", ignoreCase = true))
        {
            mPresenter.ForgotPasswordByEmail(activity as Activity , email ,  bindingObject.pinview.value)
        }

        else if(recoveryType!!.equals("mobile", ignoreCase = true))
        {
            mPresenter.ForgotPasswordByPhone(activity as Activity , phone ,  bindingObject.pinview.value ,"")
        }

    }

    override fun onOtpValidateSuccess()
    {

        showSnackbar("Your account has been verified successfully")

        if (recoveryType!!.equals("email", ignoreCase = true))
        {
            (activity as AccountVerification).sharedPreferences.saveString(Constants.Is_Email_Verified , "1")// 0 or 1 format
        }

        else if(recoveryType!!.equals("mobile", ignoreCase = true))
        {
            (activity as AccountVerification).sharedPreferences.saveString(Constants.Is_Phone_Verified , "1")// 0 or 1 format
        }


  //      if(registerType.equals("Model" , ignoreCase = true))
    //    {
          activity?.finish()
/*        }

        else
        {
            if((activity as AccountVerification).sharedPreferences.getString(Constants.Is_Documement_Verified).toString().equals("1" , ignoreCase = true)
                    &&(activity as AccountVerification).sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("1" , ignoreCase = true))
            {
              activity?.finish()
            }

            else if((activity as AccountVerification).sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("0" , ignoreCase = true) || (activity as AccountVerification).sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("" , ignoreCase = true) ||(activity as AccountVerification).sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("null" , ignoreCase = true))
            {
                val fragment = CastingProfleImageProofFragment()
                (activity as AccountVerification).loadFirstFragment(fragment)
            }

            else if((activity as AccountVerification).sharedPreferences.getString(Constants.Is_Documement_Verified).toString().equals("0" , ignoreCase = true)
                    &&(activity as AccountVerification).sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("1" , ignoreCase = true))
            {
                AlertDialog.Builder(activity as Activity)
                        .setMessage("Your documents verification is under process. We will inform your once they will be verified.")
                        .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->

                            activity?.finish()
                        })

                        .show()
            }
        }*/
    }


    //-------Resend Otp validation
    override fun onValidate()
    {

        if (recoveryType!!.equals("email", ignoreCase = true))
        {
            mResendOtpPresenter.ForgotPasswordByPhoneOrEmail(activity as Activity , email , "" , "")
        }

        else if(recoveryType!!.equals("mobile", ignoreCase = true))
        {
            mResendOtpPresenter.ForgotPasswordByPhoneOrEmail(activity as Activity , "", phone , "")
        }


    }

    //----- Resend otp success
    override fun onOtpSendSuccess()
    {
        showSnackbar("Otp has been resent successfully")
        countdownTimer()
    }

    //-------- Not in use
    override fun onGetQuestionSuccess(model: ForgotPasswordByQuestionPojo)
    {
    }

    override fun showProgress(showProgress: Boolean)
    {
        if(showProgress)
            (activity as AccountVerification).startProgressBarAnim()

        else
            (activity as AccountVerification).stopProgressBarAnim()
    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    fun showSnackbar(message : String)
    {
        Snackbar.make((activity as Activity).findViewById(android.R.id.content) , message , Snackbar.LENGTH_SHORT ).show()
    }

    override fun onClick(v: View)
    {

        when (v.id)
        {
            R.id.leftbtn -> (activity as AccountVerification).finish()

            R.id.resendOTP->
            {
                if(recoveryType.equals("email",ignoreCase = true))
                    mResendOtpPresenter.verifyAccountValidate(activity as Activity, email, recoveryType.toString())

                else
                    mResendOtpPresenter.verifyAccountValidate(activity as Activity, phone, recoveryType.toString())

            }

            R.id.validateOtpBtn->
            {

            mPresenter.isOtpValidate(activity as Activity, bindingObject.pinview.value)

            }

        }


    }

}
