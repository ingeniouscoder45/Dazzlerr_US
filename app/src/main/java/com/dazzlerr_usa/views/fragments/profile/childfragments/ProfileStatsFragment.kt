package com.dazzlerr_usa.views.fragments.profile.childfragments


import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentProfileStatsBinding
import com.dazzlerr_usa.views.fragments.profile.ProfilePojo
import com.google.android.flexbox.*
import com.google.gson.Gson

/**
 * A simple [Fragment] subclass.
 */

class ProfileStatsFragment : Fragment() {

    lateinit var bindingObj:FragmentProfileStatsBinding
    var jsonData= ""
    lateinit var profilePojo: ProfilePojo
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        bindingObj =  DataBindingUtil.inflate(inflater , R.layout.fragment_profile_stats, container, false)
        initializations()
        return  bindingObj.root
    }

    fun initializations()
    {
        jsonData = arguments!!.getString("data").toString()

        val gson  = Gson()
        profilePojo = gson.fromJson(jsonData , ProfilePojo::class.java)

        //--------Hiding appearance tab for some roles
            if(profilePojo?.data?.get(0)?.profile_tabs?.contains("Appearance" ,ignoreCase = true)!!)
            {
                bindingObj.physicalAppearanceLayout.physicalAppearanceLayout.visibility = View.VISIBLE
            }
            else
                bindingObj.physicalAppearanceLayout.physicalAppearanceLayout.visibility = View.GONE

        bindingObj.physicalAppearanceLayout.physicalBinder = profilePojo.data?.get(0)
        bindingObj.physicalAppearanceLayout.executePendingBindings()

        bindingObj.generalInformationLayout.informationBinder = profilePojo.data?.get(0)
        bindingObj.generalInformationLayout.executePendingBindings()

        //------Available For Start
        var mAvailableForList : MutableList<String> = ArrayList()
        val skillsLayoutManager = FlexboxLayoutManager(activity)
        skillsLayoutManager.flexDirection = FlexDirection.ROW
        skillsLayoutManager.justifyContent = JustifyContent.FLEX_START
        //layoutManager.alignContent = AlignContent.STRETCH
        skillsLayoutManager.alignItems= AlignItems.STRETCH
        skillsLayoutManager.flexWrap = FlexWrap.WRAP
        bindingObj.availableForRecyclerView.setLayoutManager(skillsLayoutManager)

        if(profilePojo.data?.get(0)?.available_for!=null && !profilePojo.data?.get(0)?.available_for.equals(""))
            mAvailableForList  = profilePojo.data?.get(0)?.available_for?.split(",") as MutableList<String>

        if(mAvailableForList.size!=0)
            bindingObj.availableForRecyclerView.adapter  = com.dazzlerr_usa.views.fragments.profile.adapters.AvailableForAdapter(activity as Activity, mAvailableForList!!)

        else
            bindingObj.availableLayout.visibility = View.GONE

        //------Available For End

        if (profilePojo.data?.get(0)?.gender.equals("female",ignoreCase = true))
            bindingObj.physicalAppearanceLayout.biceps.text = "N/A"


        if (profilePojo.data?.get(0)?.agency_signed.equals("1"))
            bindingObj.generalInformationLayout.agencySigned.text = "Yes"
        else
            bindingObj.generalInformationLayout.agencySigned.text = "No"

        if (profilePojo.data?.get(0)?.marital_status.equals("1"))
            bindingObj.generalInformationLayout.maritalStatus.text = "Married"
        else if(profilePojo.data?.get(0)?.marital_status.equals("2"))
            bindingObj.generalInformationLayout.maritalStatus.text = "Divorced"
        else
            bindingObj.generalInformationLayout.maritalStatus.text = "Single"
    }
}
