package com.dazzlerr_usa.views.fragments.forgotpassword.localfragments


import android.app.Activity
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentResetPasswordBinding
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.fragments.ParentFragment
import com.dazzlerr_usa.views.fragments.forgotpassword.models.ResetPasswordModel
import com.dazzlerr_usa.views.fragments.forgotpassword.presenters.ResetPasswordPresenter
import com.dazzlerr_usa.views.fragments.forgotpassword.views.ResetPasswordView
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 */
class ResetPasswordFragment : ParentFragment(), View.OnClickListener , ResetPasswordView
{

    lateinit var mPresenter: ResetPasswordPresenter
    internal lateinit var bindingObject: FragmentResetPasswordBinding
    var user_id = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        bindingObject = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password, container, false)
        initializations()
        return bindingObject.root
    }

    private fun initializations()
    {
        user_id = arguments!!.getString("user_id").toString()

        mPresenter = ResetPasswordModel(activity as Activity , this)
        bindingObject.titleLayout.leftbtn.setOnClickListener(this)
        bindingObject.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        bindingObject.titleLayout.rightbtn.visibility = View.GONE
        bindingObject.submitBtn.setOnClickListener(this)
    }

    override fun onResetPasswordSuccess()
    {
        Toast.makeText(activity as Activity , "Password has been updated successfully", Toast.LENGTH_SHORT).show()
        activity?.onBackPressed()
        activity?.onBackPressed()
    }

    override fun onValidate()
    {
        mPresenter.resetPassword(activity as Activity, user_id , bindingObject.userConfirmPassword.text.toString().trim())
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

            R.id.leftbtn -> activity?.onBackPressed()

            R.id.submitBtn -> {

                mPresenter.restPasswordValidate(activity as Activity, bindingObject.userPassword.text.toString().trim() , bindingObject.userConfirmPassword.text.toString().trim())

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mPresenter.cancelRetrofitRequest()
    }

}
