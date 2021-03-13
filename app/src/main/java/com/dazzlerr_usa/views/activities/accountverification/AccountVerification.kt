package com.dazzlerr_usa.views.activities.accountverification

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityAccountVerificationBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.fragments.profileemailphoneverification.CastingProfileVideoProofFragment
import com.dazzlerr_usa.views.fragments.profileemailphoneverification.CastingProfleImageProofFragment
import com.dazzlerr_usa.views.fragments.profileemailphoneverification.ProfileEmailOrPhoneVerificationFragment
import javax.inject.Inject

class AccountVerification : AppCompatActivity() , View.OnClickListener
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObject:ActivityAccountVerificationBinding
    internal lateinit var fragment_manager: androidx.fragment.app.FragmentManager
    internal lateinit var fragment_transaction: androidx.fragment.app.FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObject = DataBindingUtil.setContentView(this, R.layout.activity_account_verification)
        initializations()
        fragmentCalling()
    }

    fun loadFirstFragment(fragment: Fragment)
    {
        fragment_manager = supportFragmentManager
        fragment_transaction = fragment_manager.beginTransaction()
        fragment_transaction.replace(R.id.container, fragment)
        fragment_transaction.commitAllowingStateLoss()
    }

    fun fragmentCalling()
    {

        if(intent.extras!!.getString("type","").equals("documentVerification"))
        {
            if(sharedPreferences.getString(Constants.identity_proof).toString().equals("")||sharedPreferences.getString(Constants.identity_proof).toString().equals("null"))
            {
                val fragment = CastingProfleImageProofFragment()
                //   fragment.arguments = bundle
                loadFirstFragment(fragment)
            }

            else
            {
                val fragment = CastingProfileVideoProofFragment()
                loadFirstFragment(fragment)
            }

        }

        else
        {
            val fragment = ProfileEmailOrPhoneVerificationFragment()
            //   fragment.arguments = bundle
            loadFirstFragment(fragment)
        }
    }

    private fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)
    }

    override fun onClick(v: View) {

        when (v.id)
        {
            R.id.leftbtn -> onBackPressed()
        }
    }

    fun startProgressBarAnim() {

        try {
            bindingObject.aviProgressBar.setVisibility(View.VISIBLE)
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    fun stopProgressBarAnim()
    {
        try {
            bindingObject.aviProgressBar.setVisibility(View.GONE)
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }

    }

    fun isUserLogined():Boolean
    {
        if(sharedPreferences.getString(Constants.User_id)!!.equals(""))
        {

            val dialog  = CustomDialog(this)
                    dialog.setMessage(resources.getString(R.string.signin_txt))
                    dialog.setPositiveButton("Continue", DialogListenerInterface.onPositiveClickListener
                    {

                        val bundle = Bundle()
                        bundle.putString("ShouldOpenLogin"  , "true")
                        val newIntent = Intent(this@AccountVerification, MainActivity::class.java)
                        newIntent.putExtras(bundle)
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(newIntent)
                    })

                    dialog.setNegativeButton("Cancel",  DialogListenerInterface.onNegetiveClickListener {
                    dialog.dismiss() })

                    dialog.show()

            return false
        }

        else
        {
            return true
        }
    }

}
