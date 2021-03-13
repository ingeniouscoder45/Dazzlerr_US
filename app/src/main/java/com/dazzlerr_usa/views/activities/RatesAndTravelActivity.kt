package com.dazzlerr_usa.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.views.fragments.profile.ProfilePojo
import com.google.gson.Gson
import timber.log.Timber

class RatesAndTravelActivity : AppCompatActivity() , View.OnClickListener {

    lateinit var bindingObj : com.dazzlerr_usa.databinding.ActivityRatesAndTravelBinding
    var jsonData= ""
    lateinit var profilePojo:ProfilePojo

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_rates_and_travel)
        initializations()

        if(profilePojo!=null)
        populateData()
    }

    internal fun initializations()
    {
        jsonData = intent.extras?.getString("data").toString()

        val gson  = Gson()
         profilePojo = gson.fromJson(jsonData , ProfilePojo::class.java)

        bindingObj.titleLayout.rightbtn.visibility = View.GONE
        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        bindingObj.titleLayout.titletxt.text = "Rates & Travel"
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
    }

    fun populateData()
    {
        bindingObj.travelBinder = profilePojo.data?.get(0)
        bindingObj.executePendingBindings()

        Timber.e(profilePojo.data?.get(0)?.will_fly)
        if(profilePojo.data?.get(0)?.will_fly?.equals("1")!!)
            bindingObj.travel.text = "Yes"
        else
            bindingObj.travel.text = "No"


        if(profilePojo.data?.get(0)?.test?.equals("1")!!)
            bindingObj.test.text = "Yes"

        else if(profilePojo.data?.get(0)?.test?.equals("0")!!)
            bindingObj.test.text = "No"

        else
        bindingObj.test.text = "Maybe"


        if(profilePojo.data?.get(0)?.passport_ready?.equals("1")!!)
            bindingObj.isPassportReady.text = "Yes"
        else if(profilePojo.data?.get(0)?.passport_ready?.equals("0")!!)
            bindingObj.isPassportReady.text = "No"

        else
            bindingObj.isPassportReady.text = "Applied"

    }

    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.leftbtn ->
            {
                finish()
            }
        }
    }
}
