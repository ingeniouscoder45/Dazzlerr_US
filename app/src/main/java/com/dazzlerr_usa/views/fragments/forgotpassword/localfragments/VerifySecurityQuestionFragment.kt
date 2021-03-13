package com.dazzlerr_usa.views.fragments.forgotpassword.localfragments


import android.app.Activity
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentVerifySecurityQuestionBinding
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.fragments.ParentFragment
import com.dazzlerr_usa.views.fragments.forgotpassword.models.ValidateSecurityQuestionModel
import com.dazzlerr_usa.views.fragments.forgotpassword.presenters.ValidateSecurityQuestionPresenter
import com.dazzlerr_usa.views.fragments.forgotpassword.views.ValidateSecurityQuestionView

/**
 * A simple [Fragment] subclass.
 */
class VerifySecurityQuestionFragment : ParentFragment(), View.OnClickListener , ValidateSecurityQuestionView {


    internal lateinit var bindingObject: FragmentVerifySecurityQuestionBinding
    lateinit var mPresenter: ValidateSecurityQuestionPresenter
    internal var Question: String? = ""
    internal var QuestionID: String? = ""
    internal var email: String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        bindingObject = DataBindingUtil.inflate(inflater, R.layout.fragment_verify_security_question, container, false)
        initializations()
        clickListeners()
        return bindingObject.root
    }

    private fun initializations()
    {
        Question = arguments!!.getString("Question")
        QuestionID = arguments!!.getString("QuestionID")
        email = arguments!!.getString("email")

        mPresenter = ValidateSecurityQuestionModel(activity as Activity , this)
        bindingObject.recoveryTypeTitle.text = Question
        bindingObject.titleLayout.leftbtn.setOnClickListener(this)
        bindingObject.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        bindingObject.titleLayout.rightbtn.visibility = View.GONE
    }
    fun clickListeners()
    {
        bindingObject.submitBtn.setOnClickListener(this)
    }

    override fun onValidateSecurityQuestion(user_id : String)
    {
        val fragment = ResetPasswordFragment()
        val args = Bundle()
        args.putString("user_id",user_id)
        fragment.arguments = args
        (activity as MainActivity).LoadFragment(fragment)
    }


    override fun onValidate()
    {

        if(activity?.isNetworkActiveWithMessage()!!)
        mPresenter.validateOtp(activity as Activity ,QuestionID!! , email!! ,   bindingObject.recoveryTypeEdittxt.text.toString().trim())
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

            R.id.submitBtn ->
            {
                mPresenter.isSecurityQuestionValidate(activity as Activity , bindingObject.recoveryTypeEdittxt.text.toString().trim())
            }
        }
    }

    override fun onDestroy()
    {
        super.onDestroy()
        mPresenter.cancelRetrofitRequest()
    }


}// Required empty public constructor
