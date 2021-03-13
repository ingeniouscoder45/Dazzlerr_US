package com.dazzlerr_usa.views.activities.emailverification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityAccountVerificationBinding
import com.dazzlerr_usa.databinding.ActivityMobileEmailVerificationBinding
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class IndividualEmailVerificationActivity : AppCompatActivity() , View.OnClickListener,  EmailVerificationView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObject:ActivityMobileEmailVerificationBinding
    lateinit var mPresenter: EmailVerificationPresenter
    var email = ""


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObject = DataBindingUtil.setContentView(this, R.layout.activity_mobile_email_verification)
        initializations()
        listeners()
        countdownTimer(false)
    }

    private fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)
        mPresenter = EmailVerificationModel(this , this)


        email = intent?.extras?.getString("email")!!
        bindingObject.recoveryType.text = "We've sent an OTP to " + email
        bindingObject.validateOtpBtn.setOnClickListener(this)
        bindingObject.resendOTP.isEnabled = false
        bindingObject.verifyEmailBtn.visibility = View.GONE
    }

    private fun listeners()
    {
        bindingObject.resendOTP.setOnClickListener(this)
        bindingObject.validateOtpBtn.setOnClickListener(this)
        bindingObject.verifyEmailBtn.setOnClickListener(this)
        bindingObject.titleLayout.leftbtn.setOnClickListener(this)
        bindingObject.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
    }

    fun startProgressBarAnim()
    {
        bindingObject.aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {
        bindingObject.aviProgressBar.setVisibility(View.GONE)
    }

    private fun countdownTimer( showEmailVerification: Boolean)
    {
        object : CountDownTimer(165000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val Minutes = millisUntilFinished / (60 * 1000) % 60
                val Seconds = millisUntilFinished / 1000 % 60

                bindingObject.otpTxt.text = "Waiting for OTP... " + String.format("%02d", Minutes) + ":" + String.format("%02d", Seconds)

            }

            override fun onFinish()
            {
                bindingObject.resendOTP.isEnabled = true

            }

        }.start()
    }

    override fun onValidOtp()
    {
       mPresenter.verifyEmailOtp(sharedPreferences.getString(Constants.User_id).toString(), email ,  bindingObject.pinview.value)
    }

    override fun onOtpValidateSuccess()
    {
        //showSnackbar("Your mobile number has been verified successfully")
        sharedPreferences.saveString(Constants.Is_Email_Verified , "1")// 0 or 1 format
        finish()
    }

    override fun onValidEmail(email: String) {
        //Not in use
    }

    override fun onOtpSentSuccess(email: String)
    {
        bindingObject.resendOTP.isEnabled = false
        showSnackbar("Otp has been resent successfully")
        countdownTimer(true)
    }

    override fun showProgress(visibility: Boolean)
    {
        if(visibility)
            startProgressBarAnim()

        else
            stopProgressBarAnim()
    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    fun showSnackbar(message : String)
    {
        Snackbar.make(findViewById(android.R.id.content) , message , Snackbar.LENGTH_SHORT ).show()
    }

    override fun onClick(v: View)
    {

        when (v.id)
        {
            R.id.leftbtn -> finish()

            R.id.resendOTP->
            {
                if(isNetworkActiveWithMessage())
                mPresenter.sendEmailOtp(sharedPreferences.getString(Constants.User_id).toString() ,email)
            }

            R.id.validateOtpBtn->
            {
                mPresenter.isOtpValidate(bindingObject.pinview.value)
            }

        }


    }

}
