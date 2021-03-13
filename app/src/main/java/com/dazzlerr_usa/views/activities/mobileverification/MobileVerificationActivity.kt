package com.dazzlerr_usa.views.activities.mobileverification

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityAccountVerificationBinding
import com.dazzlerr_usa.databinding.ActivityMobileEmailVerificationBinding
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.activities.emailverification.EmailVerificationActivity
import com.dazzlerr_usa.views.activities.membership.MembershipActivity
import com.dazzlerr_usa.views.activities.setprimarydevice.SetPrimaryDeviceActivity
import com.dazzlerr_usa.views.activities.setprimarydevice.SetPrimaryDeviceModel
import com.dazzlerr_usa.views.activities.setprimarydevice.SetPrimaryDevicePresenter
import com.dazzlerr_usa.views.activities.setprimarydevice.SetPrimaryDeviceView
import com.dazzlerr_usa.views.fragments.forgotpassword.models.ForgotPasswordModel
import com.dazzlerr_usa.views.fragments.forgotpassword.pojos.ForgotPasswordByQuestionPojo
import com.dazzlerr_usa.views.fragments.forgotpassword.presenters.ForgotPasswordPresenter
import com.dazzlerr_usa.views.fragments.forgotpassword.views.ForgotPasswordView
import com.dazzlerr_usa.views.fragments.modelaccountverification.AccountVerificationModel
import com.dazzlerr_usa.views.fragments.modelaccountverification.AccountVerificationPresenter
import com.dazzlerr_usa.views.fragments.modelaccountverification.AccountVerificationView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import timber.log.Timber
import javax.inject.Inject

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class MobileVerificationActivity : AppCompatActivity() , View.OnClickListener,  ForgotPasswordView, AccountVerificationView , SetPrimaryDeviceView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObject: ActivityMobileEmailVerificationBinding
    lateinit var mResendOtpPresenter: ForgotPasswordPresenter
    lateinit var mPresenter: AccountVerificationPresenter
    lateinit var mDeviceVerification: SetPrimaryDevicePresenter
    var email = ""
    var apiSuccess = false
    var apiType = ""
    var navigateFrom= ""
    var device_token =""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObject = DataBindingUtil.setContentView(this, R.layout.activity_mobile_email_verification)
        initializations()
        listeners()
        countdownTimer(false)
        getDeviceInfo()
    }

    private fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)
        mResendOtpPresenter = ForgotPasswordModel(this , this)
        mPresenter = AccountVerificationModel(this , this)
        mDeviceVerification = SetPrimaryDeviceModel(this , this)

        if(intent?.extras!=null && intent?.extras!!.containsKey("navigateFrom"))
        {
            navigateFrom = intent?.extras?.getString("navigateFrom")!!
            bindingObject.verifyEmailBtn.visibility = View.GONE
        }

        email = sharedPreferences.getString(Constants.User_Email).toString()
        bindingObject.recoveryType.text = "We've sent an OTP to " + email
        bindingObject.validateOtpBtn.setOnClickListener(this)
        bindingObject.resendOTP.isEnabled = false
    }


    fun getDeviceToken()
    {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful)
                    {
                        Timber.e( "getInstanceId failed "+ task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result!!.token

                    // Log and toast
                    //val msg = getString(R.string.msg_token_fmt, token)
                    Timber.e(token)
                    sharedPreferences.saveString(Constants.device_id , token)
                    device_token =token
                    //  Toast.makeText(this@MainActivity, token, Toast.LENGTH_SHORT).show()
                }
                )
    }

    fun getDeviceInfo()
    {
        try
        {
            sharedPreferences.saveString(Constants.DEVICE_MODEL , Build.MODEL)
            sharedPreferences.saveString(Constants.DEVICE_BRAND , Build.BRAND)
            sharedPreferences.saveString(Constants.DEVICE_VERSION , Build.VERSION_CODES::class.java.fields.get(
                    Build.VERSION_CODES::class.java.fields.size-1).getName())

            Log.i("TAG","MODEL: " + sharedPreferences.getString(Constants.DEVICE_MODEL))
            Log.i("TAG","brand: " + Build.BRAND)
            Log.i("TAG","Version Code: " + Build.VERSION_CODES::class.java.fields.get(Build.VERSION_CODES::class.java.fields.size-1).getName())

        }

        catch (e:Exception)
        {
            e.printStackTrace()
        }

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
                /*if(showEmailVerification && !navigateFrom.equals("login",ignoreCase = true))
                bindingObject.verifyEmailBtn.visibility = View.VISIBLE*/

            }

        }.start()
    }

    // Device verification success after login from new device
    override fun onSuccess() {

        apiSuccess =true
        startActivity(Intent(this@MobileVerificationActivity , SetPrimaryDeviceActivity::class.java))

    }

    override fun onValidOtp()
    {
        if(navigateFrom.equals("Login", ignoreCase = true))
        {
            mDeviceVerification.verifyDeviceOtp(this@MobileVerificationActivity, sharedPreferences.getString(Constants.User_id).toString() , bindingObject.pinview.value)
        }
        else
        {
            mPresenter.ForgotPasswordByEmail(this, email, bindingObject.pinview.value)
        }
    }

    override fun onOtpValidateSuccess()
    {

        apiSuccess = true
/*        sharedPreferences.saveString(Constants.Is_Phone_Verified , "1")// 0 or 1 format

        sharedPreferences.saveString(Constants.User_type , "MainUser")
        val intent = Intent(this@MobileVerificationActivity, HomeActivity::class.java)
        sharedPreferences.saveString(Constants.Latitude, "28.65381")//Delhi Location
        sharedPreferences.saveString(Constants.Longitude, "77.22897")
        sharedPreferences.saveString(Constants.CurrentAddress, "Delhi")

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)


            val bundle = Bundle()
            bundle.putString("from", "Register")
            intent.putExtras(bundle)
            startActivity(intent)
            finish()*/

        var bundle = Bundle()
        bundle.putString("membership_type" , "register")

        startActivity(Intent(this@MobileVerificationActivity  , MembershipActivity::class.java).putExtras(bundle))

    }


    //-------Resend Otp validation
    override fun onValidate()
    {
            //Not in use
    }

    //----- Resend otp success
    override fun onOtpSendSuccess()
    {
        if(apiType.equals("resend",ignoreCase = true))
        {
            bindingObject.resendOTP.isEnabled = false
            showSnackbar("Otp has been resent successfully")
            countdownTimer(true)
        }

        else
        {
            apiSuccess = true

            val bundle = Bundle()
            bundle.putString("navigateFrom", navigateFrom)
            intent.putExtras(bundle)

            startActivity(Intent(this , EmailVerificationActivity::class.java))
        }
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

                    apiType = "resend"
                    if (isNetworkActiveWithMessage())
                    {
                        if (navigateFrom.equals("Login", ignoreCase = true))
                        {
                            mResendOtpPresenter.ForgotPasswordByPhoneOrEmail(this, "", sharedPreferences.getString(Constants.User_Phone).toString(), "device_changed")
                        }
                        else
                            mResendOtpPresenter.ForgotPasswordByPhoneOrEmail(this, "", sharedPreferences.getString(Constants.User_Phone).toString(), "")

                    }

            }

            R.id.validateOtpBtn->
            {
                    mPresenter.isOtpValidate(this@MobileVerificationActivity, bindingObject.pinview.value)
            }

            R.id.verifyEmailBtn->
            {
                    apiType = "send email"
                    if (isNetworkActiveWithMessage())
                        mResendOtpPresenter.ForgotPasswordByPhoneOrEmail(this, sharedPreferences.getString(Constants.User_Email).toString(), "", "")

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

    override fun onResume() {
        super.onResume()
        getDeviceToken()
        // checkNewAppVersionState()
    }
}

