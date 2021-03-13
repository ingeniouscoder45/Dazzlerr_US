package com.dazzlerr_usa.views.fragments.forgotpassword.localfragments


import android.app.Activity
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.os.CountDownTimer
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentVerifyOtpBinding
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.fragments.ParentFragment
import com.dazzlerr_usa.views.fragments.forgotpassword.models.ForgotPasswordModel
import com.dazzlerr_usa.views.fragments.forgotpassword.models.ValidateOtpModel
import com.dazzlerr_usa.views.fragments.forgotpassword.pojos.ForgotPasswordByQuestionPojo
import com.dazzlerr_usa.views.fragments.forgotpassword.presenters.ForgotPasswordPresenter
import com.dazzlerr_usa.views.fragments.forgotpassword.presenters.ValidateOtpPresenter
import com.dazzlerr_usa.views.fragments.forgotpassword.views.ForgotPasswordView
import com.dazzlerr_usa.views.fragments.forgotpassword.views.ValidateOtpView

/**
 * A simple [Fragment] subclass.
 */
class VerifyOtpFragment : ParentFragment(), View.OnClickListener ,ValidateOtpView  , ForgotPasswordView {

    lateinit var mResendOtpPresenter: ForgotPasswordPresenter
    lateinit var mPresenter : ValidateOtpPresenter
    internal lateinit  var bindingObject: FragmentVerifyOtpBinding
    internal var recoveryType: String? = ""
    internal var Message: String? = ""
    var email = ""
    var phone = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        bindingObject = DataBindingUtil.inflate(inflater, R.layout.fragment_verify_otp, container, false)
        initializations()
        countdownTimer()
        return bindingObject.root
    }

    private fun initializations() {
        recoveryType = arguments!!.getString("RecoveryType")

        if(recoveryType.equals("email" , ignoreCase = true))
            email = arguments!!.getString("Message").toString()
        else
            phone = arguments!!.getString("Message").toString()

        mPresenter = ValidateOtpModel(activity as Activity  ,this)
        mResendOtpPresenter = ForgotPasswordModel(activity as Activity , this)
        Message = arguments!!.getString("Message")
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
                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {
                bindingObject.resendOTP.isEnabled = true
            }

        }.start()
    }

    override fun onOtpValidate()
    {

        if (recoveryType!!.equals("email", ignoreCase = true))
        {
            mPresenter.validateForgotPasswordOtp(activity as Activity , email  ,bindingObject.pinview.value)
        }

        else if(recoveryType!!.equals("mobile", ignoreCase = true))
        {
            mPresenter.validateForgotPasswordOtp(activity as Activity  , phone ,bindingObject.pinview.value)
        }


    }

    override fun onOtpValidateSuccess(user_id : String)
    {
        val fragment = ResetPasswordFragment()
        val args = Bundle()
        args.putString("user_id",user_id)
        fragment.arguments = args
        (activity as MainActivity).LoadFragment(fragment)
    }

    //-------Resend Otp validation
    override fun onValidate()
    {

        if (recoveryType!!.equals("email", ignoreCase = true))
        {
            mResendOtpPresenter.ForgotPasswordByPhoneOrEmail(activity as Activity , email , "" ,"forgotpassword")
        }

        else if(recoveryType!!.equals("mobile", ignoreCase = true))
        {
            mResendOtpPresenter.ForgotPasswordByPhoneOrEmail(activity as Activity , "", phone,"forgotpassword")
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
            (activity as MainActivity).startProgressBarAnim()

        else
            (activity as MainActivity).stopProgressBarAnim()
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
            R.id.leftbtn -> (activity as MainActivity).backpress()

            R.id.validateOtpBtn ->
               mPresenter.isOtpValidate(activity as Activity , bindingObject.pinview.value)

            R.id.resendOTP->
            {
                if(recoveryType.equals("email",ignoreCase = true))
                mResendOtpPresenter.verifyAccountValidate(activity as Activity, email, recoveryType.toString())

               else
                    mResendOtpPresenter.verifyAccountValidate(activity as Activity, phone, recoveryType.toString())

            }
        }
    }

    override fun onDestroy()
    {
        super.onDestroy()
        mPresenter.cancelRetrofitRequest()
    }


}
