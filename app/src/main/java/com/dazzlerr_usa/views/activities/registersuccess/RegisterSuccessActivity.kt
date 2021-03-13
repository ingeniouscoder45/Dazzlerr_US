package com.dazzlerr_usa.views.activities.registersuccess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityRegisterSuccessBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.activities.home.HomeActivity
import javax.inject.Inject

class RegisterSuccessActivity : AppCompatActivity() , View.OnClickListener
{


    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences


    lateinit var bindingObj: ActivityRegisterSuccessBinding
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_register_success)
        initializations()
        clickListeners()
    }

    fun initializations()
    {

        (application as MyApplication).myComponent.inject(this)
    }

    fun clickListeners()
    {

        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.registerSuccessDashboardBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when(v?.id)
        {
            R.id.leftLayout->
            {
                onBackPressed()
            }

            R.id.registerSuccessDashboardBtn->
            {

                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {

        sharedPreferences.saveString(Constants.User_type , "MainUser")
        val intent = Intent(this, HomeActivity::class.java)
        sharedPreferences.saveString(Constants.Latitude, "28.65381")//Delhi Location
        sharedPreferences.saveString(Constants.Longitude, "77.22897")
        sharedPreferences.saveString(Constants.CurrentAddress, "Delhi")

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val bundle = Bundle()
        bundle.putString("from", "Register")
        intent.putExtras(bundle)
        startActivity(intent)
        finish()

    }
}