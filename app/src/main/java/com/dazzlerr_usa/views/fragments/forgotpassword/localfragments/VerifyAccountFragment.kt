package com.dazzlerr_usa.views.fragments.forgotpassword.localfragments


import android.app.Activity
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentVerifyAccountBinding
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.fragments.ParentFragment
import com.dazzlerr_usa.views.fragments.forgotpassword.models.ForgotPasswordModel
import com.dazzlerr_usa.views.fragments.forgotpassword.pojos.ForgotPasswordByQuestionPojo
import com.dazzlerr_usa.views.fragments.forgotpassword.presenters.ForgotPasswordPresenter
import com.dazzlerr_usa.views.fragments.forgotpassword.views.ForgotPasswordView
import com.google.android.material.snackbar.Snackbar
import android.text.InputFilter
import com.google.android.gms.ads.AdRequest
import java.util.regex.Pattern


/**
 * A simple [Fragment] subclass.
 */
class VerifyAccountFragment : ParentFragment(), View.OnClickListener, ForgotPasswordView
{

    lateinit var mPresenter: ForgotPasswordPresenter
    lateinit var bindingObject: FragmentVerifyAccountBinding
    internal var recoveryType: String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        bindingObject = DataBindingUtil.inflate(inflater, R.layout.fragment_verify_account, container, false)
        initializations()
        setdata()
        return bindingObject.root
    }

    private fun initializations()
    {
        if (arguments?.containsKey("showAds")!!)
        {
            //Initializing Google ads
/*
            val testDeviceIds: List<String> = Arrays.asList("A86FED701CDC6E77D341DEBA488306E3")
            val configuration: RequestConfiguration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
            MobileAds.setRequestConfiguration(configuration)
*/

            //MobileAds.initialize(this) {}
            val adRequest = AdRequest.Builder().build()
            bindingObject.adView.visibility = View.VISIBLE
            bindingObject.adView.loadAd(adRequest)
            //--------------------------
        }
        recoveryType = arguments!!.getString("RecoveryType")
        mPresenter = ForgotPasswordModel(activity as Activity , this)
        bindingObject.submitBtn.setOnClickListener(this)
        bindingObject.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        bindingObject.titleLayout.rightbtn.visibility = View.GONE
        bindingObject.titleLayout.leftbtn.setOnClickListener(this)
    }


    private fun setdata()
    {
        if (recoveryType == "mobile")
        {
            bindingObject.recoveryType.text = "To get OTP enter your Mobile Number"
            bindingObject.recoveryTypeTitle.text = "Enter your Mobile Number"
            bindingObject.recoveryTypeEdittxt.hint = "eg: 9******810"
            bindingObject.recoveryTypeEdittxt.inputType = InputType.TYPE_CLASS_PHONE

            val filter = InputFilter { source, start, end, dest, dstart, dend ->
                for (i in start until end) {
                    if (!Pattern.compile("[1234567890]*").matcher(source[i].toString()).matches()) {
                        return@InputFilter ""
                    }
                }

                null
            }
            bindingObject.recoveryTypeEdittxt.filters = arrayOf(filter, InputFilter.LengthFilter(10))


        }
        else if (recoveryType == "email") {
            bindingObject.recoveryType.text = "To get OTP enter your Email Id"
            bindingObject.recoveryTypeTitle.text = "Enter your Email Id"
            bindingObject.recoveryTypeEdittxt.hint = "eg: example@gmail.com"
            bindingObject.recoveryTypeEdittxt.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        else if (recoveryType == "securityQuestion") {
            bindingObject.recoveryType.text = "To get security question enter your Email Id"
            bindingObject.recoveryTypeTitle.text = "Enter your Email Id"
            bindingObject.recoveryTypeEdittxt.hint = "eg: example@gmail.com"
            bindingObject.recoveryTypeEdittxt.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
    }

    override fun onValidate()
    {

        if (recoveryType!!.equals("email", ignoreCase = true))
        {
            mPresenter.ForgotPasswordByPhoneOrEmail(activity as Activity , bindingObject.recoveryTypeEdittxt.text.toString().trim() , "" ,"forgotpassword")
        }

        else if(recoveryType!!.equals("mobile", ignoreCase = true))
        {
            mPresenter.ForgotPasswordByPhoneOrEmail(activity as Activity , "", bindingObject.recoveryTypeEdittxt.text.toString().trim() ,"forgotpassword")
        }

        else if(recoveryType!!.equals("securityQuestion", ignoreCase = true))
        {
            mPresenter.ForgotPasswordByQuestion(activity as Activity , bindingObject.recoveryTypeEdittxt.text.toString().trim())
        }

    }
    override fun onOtpSendSuccess()
    {
        val fragment = VerifyOtpFragment()
        val args = Bundle()
        args.putString("Message", bindingObject.recoveryTypeEdittxt.text.toString().trim())
        args.putString("RecoveryType", recoveryType)
        fragment.arguments = args
        (activity as MainActivity).LoadFragment(fragment)
    }

    override fun onGetQuestionSuccess(model: ForgotPasswordByQuestionPojo)
    {

        val fragment = VerifySecurityQuestionFragment()
        val args = Bundle()
        args.putString("Question", model?.result?.get(0)?.question)
        args.putString("QuestionID", model?.result?.get(0)?.question_id)
        args.putString("email", bindingObject.recoveryTypeEdittxt.text.toString().trim())
        fragment.arguments = args
        (activity as MainActivity).LoadFragment(fragment)

    }
    override fun showProgress(showProgress: Boolean)
    {
        if(showProgress) {
            if(activity!=null)
                (activity as MainActivity).startProgressBarAnim()
        }

        else {
            if(activity !=null)
                (activity as MainActivity).stopProgressBarAnim()
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

    override fun onClick(v: View) {

        when (v.id) {
            R.id.submitBtn -> {

                mPresenter.verifyAccountValidate(activity as Activity, bindingObject.recoveryTypeEdittxt.text.toString().trim(), recoveryType.toString())

            }

            R.id.leftbtn -> (activity as MainActivity).backpress()
        }
    }

    override fun onDestroy()
    {
        super.onDestroy()
        mPresenter.cancelRetrofitRequest()
    }

}// Required empty public constructor
