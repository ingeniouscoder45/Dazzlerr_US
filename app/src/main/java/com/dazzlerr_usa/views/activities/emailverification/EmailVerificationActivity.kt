package com.dazzlerr_usa.views.activities.emailverification

import android.content.Intent
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
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.fragments.forgotpassword.models.ForgotPasswordModel
import com.dazzlerr_usa.views.fragments.forgotpassword.pojos.ForgotPasswordByQuestionPojo
import com.dazzlerr_usa.views.fragments.forgotpassword.presenters.ForgotPasswordPresenter
import com.dazzlerr_usa.views.fragments.forgotpassword.views.ForgotPasswordView
import com.dazzlerr_usa.views.fragments.modelaccountverification.AccountVerificationModel
import com.dazzlerr_usa.views.fragments.modelaccountverification.AccountVerificationPresenter
import com.dazzlerr_usa.views.fragments.modelaccountverification.AccountVerificationView
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class EmailVerificationActivity : AppCompatActivity() , View.OnClickListener,  ForgotPasswordView, AccountVerificationView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObject:ActivityMobileEmailVerificationBinding
    lateinit var mResendOtpPresenter: ForgotPasswordPresenter
    lateinit var mPresenter: AccountVerificationPresenter
    var email = ""
    var apiSuccess = false

    var navigateFrom= ""

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
        mResendOtpPresenter = ForgotPasswordModel(this , this)
        mPresenter = AccountVerificationModel(this , this)

        if(intent?.extras!=null && intent?.extras!!.containsKey("navigateFrom"))
        {
            navigateFrom = intent?.extras?.getString("navigateFrom")!!
        }


        email = sharedPreferences.getString(Constants.User_Email).toString()
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

            mPresenter.ForgotPasswordByPhone(this, email ,  bindingObject.pinview.value , "")
    }

    override fun onOtpValidateSuccess()
    {


        //showSnackbar("Your mobile number has been verified successfully")

        apiSuccess = true
        sharedPreferences.saveString(Constants.Is_Email_Verified , "1")// 0 or 1 format

        sharedPreferences.saveString(Constants.User_type , "MainUser")
        val intent = Intent(this@EmailVerificationActivity, HomeActivity::class.java)
        sharedPreferences.saveString(Constants.Latitude, "28.65381")//Delhi Location
        sharedPreferences.saveString(Constants.Longitude, "77.22897")
        sharedPreferences.saveString(Constants.CurrentAddress, "Delhi")

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val bundle = Bundle()
        if(!navigateFrom.equals("Login",ignoreCase = true)) {
            val bundle = Bundle()
            bundle.putString("from", "Register")
            intent.putExtras(bundle)
        }
        finish()
    }


    //-------Resend Otp validation
    override fun onValidate()
    {
            //Not in use
    }

    //----- Resend otp success
    override fun onOtpSendSuccess()
    {
        bindingObject.resendOTP.isEnabled = false
        showSnackbar("Otp has been resent successfully")
        countdownTimer(true)
    }

    //-------- Not in use
    override fun onGetQuestionSuccess(model: ForgotPasswordByQuestionPojo)
    {
    }

    override fun showProgress(showProgress: Boolean)
    {
        if(showProgress)
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
                mResendOtpPresenter.ForgotPasswordByPhoneOrEmail(this , sharedPreferences.getString(Constants.User_Email).toString(), "" ,"")
            }

            R.id.validateOtpBtn->
            {
                mPresenter.isOtpValidate(this@EmailVerificationActivity, bindingObject.pinview.value)
            }

        }


    }

    override fun onDestroy()
    {
        super.onDestroy()

        if(!apiSuccess)
        {
            sharedPreferences.clear()
        }
    }
}
