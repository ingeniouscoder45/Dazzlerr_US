package com.dazzlerr_usa.views.fragments.profile.childfragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentProfileRatesAndTravelBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.views.fragments.profile.ProfilePojo
import com.google.gson.Gson
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class ProfileRatesAndTravelFragment : Fragment()
{

    lateinit var bindingObj: FragmentProfileRatesAndTravelBinding
    var jsonData= ""
    lateinit var profilePojo: ProfilePojo

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        bindingObj =  DataBindingUtil.inflate(inflater, R.layout.fragment_profile_rates_and_travel, container, false)
        initializations()
        populateData()
        return bindingObj.root
    }

    internal fun initializations()
    {
        jsonData = arguments!!.getString("data").toString()

        val gson  = Gson()
        profilePojo = gson.fromJson(jsonData , ProfilePojo::class.java)
    }

    fun populateData()
    {
        bindingObj.travelBinder = profilePojo.data?.get(0)

        var CurrentCurrency = if(Constants.CURRENT_CURRENCY.isEmpty() || Constants.CURRENT_CURRENCY.equals("inr",ignoreCase = true))
        {
            Constants.RupeesSign
        } else
        {
            Constants.DollorSign
        }
        bindingObj.travelBinder?.full_day = if(profilePojo.data?.get(0)?.full_day?.isNotEmpty()!! && !profilePojo.data?.get(0)?.full_day.equals("Available on request",ignoreCase = true) ) CurrentCurrency + profilePojo.data?.get(0)!!.full_day else profilePojo.data?.get(0)!!.full_day
        bindingObj.travelBinder?.half_day = if(profilePojo.data?.get(0)?.half_day?.isNotEmpty()!! && !profilePojo.data?.get(0)?.half_day.equals("Available on request",ignoreCase = true) ) CurrentCurrency +profilePojo.data?.get(0)!!.half_day else profilePojo.data?.get(0)!!.half_day
        bindingObj.travelBinder?.hourly = if(profilePojo.data?.get(0)?.hourly?.isNotEmpty()!! && !profilePojo.data?.get(0)?.hourly.equals("Available on request",ignoreCase = true) ) CurrentCurrency +profilePojo.data?.get(0)!!.hourly else profilePojo.data?.get(0)!!.hourly

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

}
