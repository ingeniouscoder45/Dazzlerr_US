package com.dazzlerr_usa.views.activities.setprimarydevice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dazzlerr_usa.R
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_set_primary_device.*
import timber.log.Timber
import javax.inject.Inject

class SetPrimaryDeviceActivity : AppCompatActivity(), View.OnClickListener , SetPrimaryDeviceView
{

    @Inject
    lateinit var sharedPreferences : HelperSharedPreferences
    lateinit var mPresenter: SetPrimaryDevicePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_primary_device)
        initializations()
        listeners()
    }

    fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)
        mPresenter = SetPrimaryDeviceModel(this@SetPrimaryDeviceActivity , this)
    }

    fun listeners()
    {
        primaryDeviceSkipBtn.setOnClickListener(this)
        primaryDeviceYesBtn.setOnClickListener(this)
    }



    override fun onClick(v: View?) {

        when(v?.id)
        {

            R.id.primaryDeviceSkipBtn->
            {
                onBackPressed()
            }

            R.id.primaryDeviceYesBtn->
            {

                if(isNetworkActiveWithMessage())
                {
                    Timber.e("Device id: "+ sharedPreferences.getString(Constants.device_id).toString())
                    if(isNetworkActiveWithMessage())
                    mPresenter.setPrimaryDevice(sharedPreferences.getString(Constants.User_id).toString() ,sharedPreferences.getString(Constants.device_id).toString() ,sharedPreferences.getString(Constants.DEVICE_BRAND).toString() , sharedPreferences.getString(Constants.DEVICE_MODEL).toString() ,"Android", sharedPreferences.getString(Constants.DEVICE_VERSION).toString() )
                }
            }
        }

    }

    override fun onBackPressed()
    {
//        super.onBackPressed()

        val intent = Intent(this@SetPrimaryDeviceActivity, HomeActivity::class.java)
        sharedPreferences.saveString(Constants.User_type , "MainUser")
        sharedPreferences.saveString(Constants.Latitude, "28.65381")//Delhi Location
        sharedPreferences.saveString(Constants.Longitude, "77.22897")
        sharedPreferences.saveString(Constants.CurrentAddress, "Delhi")

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onSuccess() {

        onBackPressed()
    }


    override fun onValidOtp() {
        // Not in use
    }

    override fun showProgress(visibility: Boolean)
    {

        if(visibility)
            startProgressBarAnim()
        else
            stopProgressBarAnim()
    }
    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }


    fun startProgressBarAnim() {

        aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {
        aviProgressBar.setVisibility(View.GONE)
    }

    fun showSnackbar(message: String)
    {
        try {
            val parentLayout = findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

}
