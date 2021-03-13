package com.dazzlerr_usa.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityUserInActiveBinding
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.activities.contactus.ContactUsActivity
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import javax.inject.Inject

class UserInActiveActivity : AppCompatActivity() , View.OnClickListener {

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObj : ActivityUserInActiveBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_user_in_active)
        initializations()
        onClickListeners()
    }

    fun initializations()
    {

    (application as MyApplication).myComponent.inject(this)
    bindingObj.titleTxt.text = intent.extras?.getString("title" , "")
    bindingObj.messageTxt.text = intent.extras?.getString("message" , "")
    sharedPreferences.clear()
    }

    fun onClickListeners()
    {
        bindingObj.OkBtn.setOnClickListener(this)
        bindingObj.contactusBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?)
    {

        when(v?.id)
        {
         R.id.OkBtn->
         {
            onBackPressed()
         }

            R.id.contactusBtn->
            {

                startActivity(Intent(this@UserInActiveActivity, ContactUsActivity::class.java))
            }
        }
    }

    override fun onBackPressed() {

        val bundle = Bundle()
        bundle.putString("ShouldOpenLogin"  , "true")
        val newIntent = Intent(this@UserInActiveActivity, MainActivity::class.java)
        newIntent.putExtras(bundle)
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(newIntent)

    }
}
