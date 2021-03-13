package com.dazzlerr_usa.views.fragments.profile.childfragments


import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentProfileAboutBinding
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.fragments.profile.ProfilePojo
import com.dazzlerr_usa.views.fragments.profile.adapters.ProfileProjectsAdapter
import com.google.android.flexbox.*
import com.google.gson.Gson
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ProfileAboutFragment : Fragment()
{

    lateinit var bindingObj : FragmentProfileAboutBinding
    var jsonData= ""
    lateinit var profilePojo: ProfilePojo
    var adapter:  ProfileProjectsAdapter? = null
    val PAGE_START = 1
    private var currentPage = PAGE_START
    private var isLoading = false

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        bindingObj =  DataBindingUtil.inflate(inflater , R.layout.fragment_profile_about, container, false)
        initializations()
        populateData()
        return bindingObj.root
    }

    internal fun initializations()
    {
        jsonData = arguments!!.getString("data").toString()
        (activity?.application as MyApplication).myComponent.inject(this@ProfileAboutFragment)
        val gson  = Gson()
        profilePojo = gson.fromJson(jsonData , ProfilePojo::class.java)
        val interestLayoutManager = FlexboxLayoutManager(activity)
        interestLayoutManager.flexDirection = FlexDirection.ROW
        interestLayoutManager.justifyContent = JustifyContent.FLEX_START
        //layoutManager.alignContent = AlignContent.STRETCH
        interestLayoutManager.alignItems= AlignItems.STRETCH
        interestLayoutManager.flexWrap = FlexWrap.WRAP
        bindingObj.interestedRecycler.setLayoutManager(interestLayoutManager)

        val skillsLayoutManager = FlexboxLayoutManager(activity)
        skillsLayoutManager.flexDirection = FlexDirection.ROW
        skillsLayoutManager.justifyContent = JustifyContent.FLEX_START
        //layoutManager.alignContent = AlignContent.STRETCH
        skillsLayoutManager.alignItems= AlignItems.STRETCH
        skillsLayoutManager.flexWrap = FlexWrap.WRAP

        bindingObj.skillsRecycler.setLayoutManager(skillsLayoutManager)

        //mPresenter = PortfolioProjectsModel(activity as Activity  , this)
        //adapter = ProfileProjectsAdapter(activity as Activity , ArrayList() , this)
        val gManager = LinearLayoutManager(activity)
        //bindingObj.projectsRecyclerview.layoutManager = gManager
        //bindingObj.projectsRecyclerview.adapter = adapter

    }


    @SuppressLint("DefaultLocale")
    fun populateData()
    {

        bindingObj.aboutBinder  = profilePojo.data?.get(0)
        bindingObj.executePendingBindings()

        if(profilePojo.data?.get(0)?.about.toString().length!=0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bindingObj.aboutTxt.setText(Html.fromHtml(profilePojo.data?.get(0)?.about.toString().capitalize(), Html.FROM_HTML_MODE_LEGACY))
            } else {
                bindingObj.aboutTxt.setText(Html.fromHtml(profilePojo.data?.get(0)?.about.toString().capitalize()))
            }

        }

        var mSkillsList : MutableList<String> = ArrayList()
        var mInterestList : MutableList<String> = ArrayList()

        if(profilePojo.data?.get(0)?.tags!=null && !profilePojo.data?.get(0)?.tags.equals(""))
            mInterestList  = profilePojo.data?.get(0)?.tags?.split((Regex("\\n|,"))) as MutableList<String>

        if(profilePojo.data?.get(0)?.skills!=null && !profilePojo.data?.get(0)?.skills.equals(""))
            mSkillsList  = profilePojo.data?.get(0)?.skills?.split(",") as MutableList<String>

        if(mInterestList.size!=0)
        bindingObj.interestedRecycler.adapter  = com.dazzlerr_usa.views.fragments.profile.adapters.InterestAdapter(activity as Activity, mInterestList!!)

        else
            bindingObj.interestedLayout.visibility = View.GONE

        if(mSkillsList.size!=0)
        bindingObj.skillsRecycler.adapter  = com.dazzlerr_usa.views.fragments.profile.adapters.InterestAdapter(activity as Activity , mSkillsList!!)

        else
            bindingObj.skillsLayout.visibility = View.GONE
    }



    override fun onDestroy() {
        super.onDestroy()
    }


}
