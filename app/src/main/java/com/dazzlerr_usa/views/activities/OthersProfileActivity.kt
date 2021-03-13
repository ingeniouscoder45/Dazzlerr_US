package com.dazzlerr_usa.views.activities

import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.View

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityOthersProfileBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.fragments.castingprofile.localfragments.CastingProfileFragment
import com.dazzlerr_usa.views.fragments.profile.FeaturedProfileFragment
import com.dazzlerr_usa.views.fragments.profile.ProfileFragment
import javax.inject.Inject

class OthersProfileActivity : ContainerActivity(), View.OnClickListener
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    internal lateinit var bindingObject: ActivityOthersProfileBinding
    internal lateinit var fragment_manager: androidx.fragment.app.FragmentManager
    internal lateinit var fragment_transaction: androidx.fragment.app.FragmentTransaction
    var CUST_ID  = ""
    lateinit var fragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObject = DataBindingUtil.setContentView(this, R.layout.activity_others_profile)
        initializations()
        clickListeners()
        fragmentCalling()
    }

    override fun onLocationfetched(latitude: Double, longitude: Double, address: String?) {

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
        val bundle = Bundle()
        bundle.putString("from", "talentFragment")
        bundle.putString("user_id", intent.extras!!.getString("user_id",""))
        bundle.putString("user_role", intent.extras!!.getString("user_role",""))

        if(intent.extras!!.getString("user_role","").equals("casting",ignoreCase = true))
         fragment= CastingProfileFragment()

        else
        {

            if(intent.extras!!.getString("is_featured","").equals("1",ignoreCase = true))
            fragment = FeaturedProfileFragment()

            else
                fragment = ProfileFragment()
        }
        fragment.arguments = bundle
        loadFirstFragment(fragment)
    }

    private fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)

        bindingObject.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        bindingObject.titleLayout.rightbtn.setBackgroundResource(R.drawable.ic_share)
        bindingObject.titleLayout.leftbtn.setOnClickListener(this)
        bindingObject.titleLayout.titletxt.text = ""
        bindingObject.titleLayout.rightbtn.visibility = View.VISIBLE
    }

    fun clickListeners()
    {
        bindingObject.titleLayout.leftbtn.setOnClickListener(this)
        bindingObject.titleLayout.rightLayout.setOnClickListener(this)
    }


    override fun onClick(v: View) {

        when (v.id)
        {
            R.id.leftbtn -> onBackPressed()
            R.id.rightLayout ->
            {

                if(isUserLogined() && !CUST_ID.equals(""))
                {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "https://www.dazzlerr.com/profile/"+CUST_ID)

                        // (Optional) Here we're setting the title of the content
                        putExtra(Intent.EXTRA_TITLE, "Dazzlerr")
                        putExtra(android.content.Intent.EXTRA_SUBJECT, "Dazzlerr - Connecting Talent");
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }
            }
        }
    }

    fun startProgressBarAnim() {

        bindingObject.aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {
        bindingObject.aviProgressBar.setVisibility(View.GONE)
    }

    fun isUserLogined():Boolean
    {
        if(sharedPreferences.getString(Constants.User_id).equals(""))
        {

            val dialog = CustomDialog(this)
            dialog.setMessage(resources.getString(R.string.signin_txt))
                    dialog.setPositiveButton("Continue", DialogListenerInterface.onPositiveClickListener{

                        val bundle = Bundle()
                        bundle.putString("ShouldOpenLogin"  , "true")
                        val newIntent = Intent(this@OthersProfileActivity, MainActivity::class.java)
                        newIntent.putExtras(bundle)
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(newIntent)
                    })

                    dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener{
                        dialog.dismiss()
                    })
                    dialog.show()

            return false
        }

        else
        {
            return true
        }
    }

}
