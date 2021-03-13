package com.dazzlerr_usa.views.activities.featuredccavenue

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityStatusBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.google.android.material.snackbar.Snackbar

class StatusActivity : AppCompatActivity(), View.OnClickListener
{

    lateinit var bindingObj: ActivityStatusBinding
    public override fun onCreate(bundle: Bundle?)
    {
        super.onCreate(bundle)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_status)
        initializations()
        clickListeners()

    }

    fun  initializations()
    {
        bindingObj.goToDashboardBtn.visibility = View.VISIBLE

    }
    fun clickListeners()
    {
        bindingObj.goToDashboardBtn.setOnClickListener(this)
    }

    fun showSnackbar(message: String)
    {
        try
        {
            val parentLayout = findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
        }

        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.goToDashboardBtn->
            {
               onBackPressed()
            }
        }

    }

    override fun onBackPressed()
    {
        val intent = Intent(applicationContext, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

}