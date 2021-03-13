package com.dazzlerr_usa.views.fragments.modelaccountverification


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
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.fragments.ParentFragment
import com.dazzlerr_usa.views.fragments.forgotpassword.models.ForgotPasswordModel
import com.dazzlerr_usa.views.fragments.forgotpassword.pojos.ForgotPasswordByQuestionPojo
import com.dazzlerr_usa.views.fragments.forgotpassword.presenters.ForgotPasswordPresenter
import com.dazzlerr_usa.views.fragments.forgotpassword.views.ForgotPasswordView
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 */
class AccountVerificationFragment : ParentFragment() , View.OnClickListener , ForgotPasswordView
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

        bindingObj.verifyEmailText.text = (activity as MainActivity).sharedPreferences.getString(Constants.User_Email)
        bindingObj.verifyPhoneText.text = (activity as MainActivity).sharedPreferences.getString(Constants.User_Phone)
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
            if(arguments!!.getString("registerType").equals("casting",ignoreCase = true))
            mPresenter.ForgotPasswordByPhoneOrEmail(activity as Activity , bindingObj.verifyEmailText.text.toString()  , "" ,"Casting")

            else   mPresenter.ForgotPasswordByPhoneOrEmail(activity as Activity , bindingObj.verifyEmailText.text.toString()  , "" ,"")
        }

        //Hiding phone verification for now 12/14/2020
/*
        else if(recoveryType!!.equals("mobile", ignoreCase = true))
        {
            if(arguments!!.getString("registerType").equals("casting",ignoreCase = true))
            mPresenter.ForgotPasswordByPhoneOrEmail(activity as Activity , "",  bindingObj.verifyPhoneText.text.toString(),"Casting")

          else
                mPresenter.ForgotPasswordByPhoneOrEmail(activity as Activity , "",  bindingObj.verifyPhoneText.text.toString(),"")
        }*/
    }

    override fun onOtpSendSuccess()
    {
        val fragment = ValidateOtpFragment()
        val args = Bundle()

        if(recoveryType!!.equals("email", ignoreCase = true))
        args.putString("Message",  bindingObj.verifyEmailText.text.toString())

        else
            args.putString("Message", bindingObj.verifyPhoneText.text.toString())

        args.putString("RecoveryType", recoveryType)
        args.putString("registerType", arguments!!.getString("registerType"))
        fragment.arguments = args
        (activity as MainActivity).LoadFragment(fragment)
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
