package com.dazzlerr_usa.views.activities.changepassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityChangePasswordBinding
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class ChangePasswordActivity : AppCompatActivity() , View.OnClickListener , ChangePasswordView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var mPresenter:ChangePasswordPresenter
    lateinit var bindingObj : ActivityChangePasswordBinding


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_change_password)
        initializations()
        clickListners()
    }

    private fun initializations()
    {
        //--------Injecting the activity to dagger component-----
        (application as MyApplication).myComponent.inject(this)
        bindingObj.titleLayout.titletxt.text = "Change Password"
        mPresenter = ChangePasswordModel(this , this)
    }

    private fun clickListners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.changePasswordBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?)
    {
        when(v?.id)
        {
            R.id.leftbtn->finish()

            R.id.changePasswordBtn-> {
                mPresenter.validate(
                        bindingObj.oldPasswordEdittext.text.toString().trim(),
                        bindingObj.newPasswordEdittext.text.toString().trim()
                        , bindingObj.confirmPasswordEdittext.text.toString().trim())
            }
        }


    }

    override fun onChangePasswordSuccess()
    {

        val dialog  = CustomDialog(this)
        dialog.setTitle("Alert!")
        dialog.setMessage("Password has been changed successfully.")

        dialog.setPositiveButton("Ok" , DialogListenerInterface.onPositiveClickListener {
            dialog.dismiss()
            finish()
        })

        dialog.show()
    }

    override fun onChangePasswordFailed(message: String)
    {
        showSnackbar(message)
    }

    override fun showProgress(showProgress: Boolean)
    {
        if (showProgress)
            startProgressBarAnim()
        else
            stopProgressBarAnim()

    }

    override fun isValidate(isValid: Boolean) {

        if (isValid && isNetworkActiveWithMessage()) {

            mPresenter.changePassword(bindingObj.oldPasswordEdittext.text.toString().trim(),
                    sharedPreferences.getString(Constants.User_id).toString() , bindingObj.newPasswordEdittext.text.toString().trim())
        }
    }

    fun startProgressBarAnim() {

        bindingObj.aviProgressBar.visibility = View.VISIBLE
    }

    fun stopProgressBarAnim() {

        bindingObj.aviProgressBar.visibility = View.GONE
    }


    fun showSnackbar(message: String)
    {
        var snackbar:Snackbar
        val parentLayout = findViewById<View>(android.R.id.content)
        snackbar  = Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT)

        snackbar.show()
    }


}
