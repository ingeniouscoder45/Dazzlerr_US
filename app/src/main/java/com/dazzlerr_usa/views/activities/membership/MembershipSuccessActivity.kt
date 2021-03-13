package com.dazzlerr_usa.views.activities.membership

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityMembershipSuccessBinding
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.fragments.forgotpassword.models.ForgotPasswordModel
import com.dazzlerr_usa.views.fragments.forgotpassword.pojos.ForgotPasswordByQuestionPojo
import com.dazzlerr_usa.views.fragments.forgotpassword.presenters.ForgotPasswordPresenter
import com.dazzlerr_usa.views.fragments.forgotpassword.views.ForgotPasswordView
import com.dazzlerr_usa.views.fragments.jobs.dateFormat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_membership_success.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class MembershipSuccessActivity : AppCompatActivity(),View.OnClickListener, ForgotPasswordView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var mPresenter: ForgotPasswordPresenter
    lateinit var bindingObj: ActivityMembershipSuccessBinding
    var order_id=  ""
    var payment_mode=  ""
    var total_amount = ""
    var membership_id = ""
    var membership_plan_type = ""
    var membership_type = ""
    var apiSuccess = false
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment
        bindingObj =  DataBindingUtil.setContentView(this , R.layout.activity_membership_success)
        initializations()
        listeners()
    }

    fun initializations()
    {
        mPresenter = ForgotPasswordModel(this , this)

        (application as MyApplication).myComponent.inject(this)
        order_id = intent?.extras?.getString("order_id")!!
        payment_mode = intent?.extras?.getString("payment_mode")!!
        total_amount = intent?.extras?.getString("total_amount")!!
        membership_id = intent?.extras?.getString("membership_id")!!
        membership_plan_type = intent?.extras?.getString("membership_plan_type")!!
        membership_type = intent?.extras?.getString("membership_type")!!

        if(membership_type.equals("register" ,ignoreCase = true))
        {
            membershipSuccessDashboardBtn.visibility = View.VISIBLE
           // membershipSuccessVerificationLayout.visibility = View.VISIBLE
            membershipSuccessVerificationLayout.visibility = View.GONE
        }
        else
        {
            membershipSuccessDashboardBtn.visibility = View.VISIBLE
            membershipSuccessVerificationLayout.visibility = View.GONE
        }

        sharedPreferences.saveString(Constants.Account_type , membership_plan_type)
        bindingObj.membershipSuccessOrderIdTxt.text = order_id
        bindingObj.membershipPaymentMethod.text = payment_mode
        bindingObj.membershipSuccessPlanTypeTxt.text = membership_plan_type
        bindingObj.membershipSuccessAmountTxt.text = Constants.RupeesSign+total_amount
        bindingObj.membershipSuccessEmailTxt.text = sharedPreferences.getString(Constants.User_Email).toString()

        val currentDate: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        dateFormat(bindingObj.membershipSuccessDateTxt , currentDate)


    }

    fun listeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.membershipSuccessVerifyNowBtn.setOnClickListener(this)
        bindingObj.membershipSuccessDashboardBtn.setOnClickListener(this)

    }

    fun apiCalling()
    {
        if(isNetworkActiveWithMessage()!!)
        mPresenter.ForgotPasswordByPhoneOrEmail(this , "", sharedPreferences.getString(Constants.User_Phone).toString() ,"")
    }

    override fun onValidate()
    {
        //Not in use
    }

    override fun onOtpSendSuccess()
    {
        apiSuccess = true
       // startActivity(Intent(this , MobileVerificationActivity::class.java))
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

    fun startProgressBarAnim() {

        bindingObj.aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {
        bindingObj.aviProgressBar.setVisibility(View.GONE)
    }


    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    fun showSnackbar(message : String)
    {
        Snackbar.make(findViewById(android.R.id.content) , message , Snackbar.LENGTH_SHORT ).show()
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.leftbtn-> onBackPressed()

            R.id.membershipSuccessVerifyNowBtn->
            {
                apiCalling()
            }

            R.id.membershipSuccessDashboardBtn-> {

                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {

        sharedPreferences.saveString(Constants.User_type, "MainUser")
        val intent = Intent(this, HomeActivity::class.java)

        if (membership_type.equals("register", ignoreCase = true))
        {
            val bundle = Bundle()
            bundle.putString("from", "Register")
            intent.putExtras(bundle)
        }

        sharedPreferences.saveString(Constants.Latitude, "28.65381")//Delhi Location
        sharedPreferences.saveString(Constants.Longitude, "77.22897")
        sharedPreferences.saveString(Constants.CurrentAddress, "Delhi")


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(intent)
        finish()
    }
/*
    override fun onDestroy()
    {
        super.onDestroy()

        if(!apiSuccess && membership_type.equals("register", ignoreCase = true))
        {
            sharedPreferences.clear()
        }
    }
*/

}
