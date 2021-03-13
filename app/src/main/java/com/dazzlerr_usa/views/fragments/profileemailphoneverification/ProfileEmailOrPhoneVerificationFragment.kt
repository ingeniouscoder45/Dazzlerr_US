package com.dazzlerr_usa.views.fragments.profileemailphoneverification


import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentAccountVerificationBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.views.activities.accountverification.AccountVerification
import com.dazzlerr_usa.views.fragments.forgotpassword.models.ForgotPasswordModel
import com.dazzlerr_usa.views.fragments.forgotpassword.pojos.ForgotPasswordByQuestionPojo
import com.dazzlerr_usa.views.fragments.forgotpassword.presenters.ForgotPasswordPresenter
import com.dazzlerr_usa.views.fragments.forgotpassword.views.ForgotPasswordView
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */

class ProfileEmailOrPhoneVerificationFragment : Fragment() , View.OnClickListener , ForgotPasswordView
{

    lateinit var mPresenter: ForgotPasswordPresenter
    lateinit var bindingObj: FragmentAccountVerificationBinding
    var recoveryType = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {

        // Inflate the layout for this fragment
        bindingObj =  DataBindingUtil.inflate(inflater , R.layout.fragment_account_verification, container, false)
        initializations()
        clickListeners()
        return bindingObj.root
    }

    fun initializations()
    {

        mPresenter = ForgotPasswordModel(activity as Activity , this)

        if((activity as AccountVerification).sharedPreferences.getString(Constants.Is_Email_Verified).equals("0"))
            bindingObj.emailLayout.visibility = View.VISIBLE
        else
            bindingObj.emailLayout.visibility = View.GONE

        if((activity as AccountVerification).sharedPreferences.getString(Constants.Is_Phone_Verified).equals("0"))
            bindingObj.phoneLayout.visibility = View.VISIBLE
        else
            bindingObj.phoneLayout.visibility = View.GONE

        bindingObj.verifyEmailText.text = (activity as AccountVerification).sharedPreferences.getString(Constants.User_Email)
        bindingObj.verifyPhoneText.text = (activity as AccountVerification).sharedPreferences.getString(Constants.User_Phone)
    }

    fun clickListeners()
    {
        bindingObj.verifyPhoneButton.setOnClickListener(this)
        bindingObj.verifyEmailButton.setOnClickListener(this)
    }

    override fun onValidate()
    {

        if (recoveryType!!.equals("email", ignoreCase = true))
        {
            mPresenter.ForgotPasswordByPhoneOrEmail(activity as Activity , bindingObj.verifyEmailText.text.toString()  , "" ,"")
        }

        else if(recoveryType!!.equals("mobile", ignoreCase = true))
        {
            mPresenter.ForgotPasswordByPhoneOrEmail(activity as Activity , "",  bindingObj.verifyPhoneText.text.toString() ,"")
        }

    }

    override fun onOtpSendSuccess()
    {

        val fragment = ProfileValidateOtpFragment()
        val args = Bundle()

        if(recoveryType!!.equals("email", ignoreCase = true))
        args.putString("Message",  bindingObj.verifyEmailText.text.toString())

        else
            args.putString("Message", bindingObj.verifyPhoneText.text.toString())

        args.putString("RecoveryType", recoveryType)
        fragment.arguments = args
        (activity as AccountVerification).loadFirstFragment(fragment)
    }

    //-------- Not in use
    override fun onGetQuestionSuccess(model: ForgotPasswordByQuestionPojo)
    {

    }

    override fun showProgress(showProgress: Boolean)
    {
        try {
            if (showProgress)
                (activity as AccountVerification).startProgressBarAnim()
            else
                (activity as AccountVerification).stopProgressBarAnim()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
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

    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.verifyEmailButton->
            {
                recoveryType = "email"
                mPresenter.verifyAccountValidate(activity as Activity, bindingObj.verifyEmailText.text.toString(), recoveryType)
            }

            R.id.verifyPhoneButton->
            {
                recoveryType = "mobile"
                mPresenter.verifyAccountValidate(activity as Activity, bindingObj.verifyPhoneText.text.toString(), recoveryType)
            }
        }
    }

}
