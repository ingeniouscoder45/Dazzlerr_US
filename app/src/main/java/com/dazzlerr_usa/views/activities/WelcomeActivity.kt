package com.dazzlerr_usa.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityWelcomeBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.activities.editprofile.EditProfileActivity
import javax.inject.Inject

class WelcomeActivity : AppCompatActivity() ,View.OnClickListener
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObj : ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_welcome)
        initializations()
        clickListeners()
    }

    fun initializations()
    {
        this.setFinishOnTouchOutside(false)
        (application as MyApplication).myComponent.inject(this)
    }

    fun clickListeners()
    {
        bindingObj.completeProfileBtn.setOnClickListener(this)
        bindingObj.gotToDashboardBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?)
    {
        when(v?.id)
        {
            R.id.gotToDashboardBtn-> finish()

            R.id.completeProfileBtn->
            {
                val bundle = Bundle()
                bundle.putString("from", "dashboard")
                bundle.putString("type", "register")
                bundle.putString("user_id", sharedPreferences.getString(Constants.User_id))

                val intent = Intent(this@WelcomeActivity, EditProfileActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onBackPressed()
    {

    }




}

