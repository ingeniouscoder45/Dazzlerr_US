package com.dazzlerr_usa.views.fragments.forgotpassword.localfragments


import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils

import com.dazzlerr_usa.R
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.databinding.FragmentForgotPasswordBinding
import com.dazzlerr_usa.views.fragments.ParentFragment
import com.google.android.gms.ads.AdRequest

/**
 * A simple [Fragment] subclass.
 */
class ForgotPasswordFragment : ParentFragment(), View.OnClickListener {

    internal lateinit var bindingObj: FragmentForgotPasswordBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        bindingObj = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password, container, false)

        animationSet()
        initializations()
        return bindingObj.root
    }

    private fun initializations()
    {
        //Initializing Google ads
/*
        val testDeviceIds: List<String> = Arrays.asList("A86FED701CDC6E77D341DEBA488306E3")
        val configuration: RequestConfiguration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
*/

        //MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        bindingObj.adView.visibility = View.VISIBLE
        bindingObj.adView.loadAd(adRequest)
        //--------------------------

        bindingObj.titleLayout.titletxt.text = "FORGOT PASSWORD"
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        bindingObj.titleLayout.rightbtn.visibility = View.GONE
        bindingObj.recoverMobileBtn.setOnClickListener(this)
        bindingObj.recoverEmailBtn.setOnClickListener(this)
        bindingObj.recoverQuestionBtn.setOnClickListener(this)
    }

    private fun animationSet() {
        val anim1 = AnimationUtils.loadAnimation(activity, R.anim.enter_from_leftslow)
        bindingObj.recoverMobileBtn.startAnimation(anim1)

        val anim = AnimationUtils.loadAnimation(activity, R.anim.enter_from_rightslow)
        bindingObj.recoverEmailBtn.startAnimation(anim)

        val anim2 = AnimationUtils.loadAnimation(activity, R.anim.enter_from_leftslow)
        bindingObj.recoverQuestionBtn.startAnimation(anim2)

    }


    override fun onClick(v: View) {
        when (v.id) {


            R.id.recoverMobileBtn -> {
                //Put the value
                val fragment = VerifyAccountFragment()
                val args = Bundle()
                args.putString("RecoveryType", "mobile")
                args.putString("showAds", "true")
                fragment.arguments = args
                (activity as MainActivity).LoadFragment(fragment)
            }

            R.id.recoverEmailBtn -> {
                //Put the value
                val fragment1 = VerifyAccountFragment()
                val args1 = Bundle()
                args1.putString("RecoveryType", "email")
                args1.putString("showAds", "true")
                fragment1.arguments = args1
                (activity as MainActivity).LoadFragment(fragment1)
            }

            R.id.recoverQuestionBtn -> {
                //Put the value
                val fragment3 = VerifyAccountFragment()
                val args3 = Bundle()
                args3.putString("RecoveryType", "securityQuestion")
                args3.putString("showAds", "true")
                fragment3.arguments = args3
                (activity as MainActivity).LoadFragment(fragment3)
            }

            R.id.leftbtn -> (activity as MainActivity).backpress()
        }
    }
}// Required empty public constructor
