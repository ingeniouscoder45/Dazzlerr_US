package com.dazzlerr_usa.views.fragments


import android.app.Activity
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.dazzlerr_usa.BuildConfig

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentSplashBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.fragments.intro.IntroFragment
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.customdialogs.AppUpdateDialog
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.views.activities.contactus.ContactUsActivity
import com.dazzlerr_usa.views.activities.mainactivity.*
import com.dazzlerr_usa.views.fragments.login.LoginFragment
import com.dazzlerr_usa.views.activities.weblinks.WebLinksActivity
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class SplashFragment : androidx.fragment.app.Fragment(), MainActivityView
{
    internal lateinit var bindingObject: FragmentSplashBinding
    lateinit var mPresenter: MainActivityPresenter
    private val SPLASH_TIME_OUT = 100
    var handler : Handler = Handler()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        bindingObject = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)

        initializations()
        animations()
        return bindingObject.getRoot()
    }

    fun initializations()
    {
        mPresenter = MainActivityModel(activity as Activity ,this)
    }


    private fun animations()
    {

        val anim = AnimationUtils.loadAnimation(activity, R.anim.sp_anim1)
        bindingObject.logoIcon.startAnimation(anim)

        val anim1 = AnimationUtils.loadAnimation(activity, R.anim.app_name1)
        bindingObject.titleTxt.startAnimation(anim1)

        val anim2 = AnimationUtils.loadAnimation(activity, R.anim.app_name2)
        bindingObject.taglineTxt.startAnimation(anim2)

        anim.setAnimationListener(object : Animation.AnimationListener
        {
            override fun onAnimationStart(animation: Animation)
            {

            }

            override fun onAnimationEnd(animation: Animation)
            {
                handler.postDelayed({

                    if((activity as Activity).isNetworkActiveWithMessage())
                    {
                        if((activity as Activity).isNetworkActiveWithMessage())
                            mPresenter.getConstants((activity as MainActivity).sharedPreferences.getString(Constants.User_id).toString())

                    }


                }, SPLASH_TIME_OUT.toLong())

            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

    }

    override fun onSuccess(model: MonetizeApiPojo)
    {

        Constants.CURRENT_CURRENCY = model.current_currency

        if(model.is_user_delete.equals("true",ignoreCase = true))
        {

            (activity as MainActivity).sharedPreferences.clear()

            val mDialog = CustomDialog(activity)
                mDialog.setTitle("Account Deleted!")

            mDialog.setMessage("Your account has been deleted due to some suspicious activity found on your account.")
            mDialog.setPositiveButton("Contact Support") {

                startActivity(Intent(activity , ContactUsActivity::class.java))

                mDialog.dismiss()
            }

            mDialog.setNegativeButton("Cancel") {

                (activity as MainActivity).loadFirstFragment(LoginFragment())
                mDialog.dismiss()

            }
            mDialog.show()

        }

        else {


            (activity as MainActivity).sharedPreferences.saveString(Constants.Is_Monetize, model.is_monitize!!)

            if (model.app_version?.isNotEmpty()!! && BuildConfig.VERSION_CODE < model.app_version?.toInt()!!) {
                //New Version is available.
                AppUpdateDialog(activity as Activity)
            } else {

                if (model.shut_down.equals("enabled", ignoreCase = true)) {

                    val bundle = Bundle()
                    bundle.putString("type", "shutdown")
                    bundle.putString("url", model.shut_down_link)

                    val intent =
                            Intent(activity as Activity, WebLinksActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                    (activity as MainActivity).finish()

                } else {



                    //------------Hiding the title bar----------
                    (activity as MainActivity).titleVisibility(false)
                    if ((activity as MainActivity).sharedPreferences.getString(Constants.User_type).equals(""))
                        (activity as MainActivity).loadFirstFragment(IntroFragment())
                    else {

                        if ((activity as Activity).isNetworkActive()) {
                            startActivity(Intent(activity, HomeActivity::class.java))
                            activity!!.finish()
                        }
                    }

                }
            }
        }
    }

    override fun onFailed(message: String) {
        try {
            Toast.makeText(activity , message , Toast.LENGTH_SHORT).show()
        }

        catch (e:Exception)
        {
            e.printStackTrace()
        }

        //Snackbar.make(activity?.findViewById(android.R.id.content)!!, message, Snackbar.LENGTH_SHORT).show()
    }

}