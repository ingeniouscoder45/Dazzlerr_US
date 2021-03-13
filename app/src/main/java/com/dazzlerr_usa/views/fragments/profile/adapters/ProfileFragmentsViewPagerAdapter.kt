package com.dazzlerr_usa.views.fragments.profile.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter

import com.dazzlerr_usa.views.fragments.profile.childfragments.*

class ProfileFragmentsViewPagerAdapter(fm: androidx.fragment.app.FragmentManager , val fragmentsCount: Int , val profile_id : String , val profileDataJson : String , val from : String ,val user_role : String) : FragmentStatePagerAdapter(fm)
{

    override fun getItem(position: Int): Fragment
    {

        var fragment  = Fragment()

        if (position == 0)
        {
            fragment = ProfileImagesFragment()
            val bundle = Bundle()
            bundle.putString("profile_id",  profile_id)
            bundle.putString("from",  from)
            fragment.arguments = bundle
        }

        else if (position == 1)
        {
            fragment = ProfileAboutFragment()
            val bundle = Bundle()
            bundle.putString("user_id",  profile_id)
            bundle.putString("data" , profileDataJson)
            fragment.arguments = bundle
        }

        else if (position == 2)
        {
            fragment = ProfileStatsFragment()
            val bundle = Bundle()
            bundle.putString("user_id",  profile_id)
            bundle.putString("data" , profileDataJson)
            fragment.arguments = bundle
        }
        else if (position == 3)
        {
            fragment = ProfileRatesAndTravelFragment()
            val bundle = Bundle()
            bundle.putString("user_id",  profile_id)
            bundle.putString("data" , profileDataJson)
            fragment.arguments = bundle
        }
        else if (position == 4)
        {
            fragment = ProfileServicesFragment()
            val bundle = Bundle()
            bundle.putString("user_id",  profile_id)
            bundle.putString("data" , profileDataJson)
            fragment.arguments = bundle
        }

        return fragment
    }

    override fun getCount(): Int
    {
        return fragmentsCount
    }

    override fun getPageTitle(position: Int): CharSequence?
    {

        var title: String? = null

        if (position == 0)
        {
            title = "PORTFOLIO"
        }

        else if (position == 1)
        {
            title = "ABOUT"
        }

        else if (position == 2)
        {
            title = "STATS"
        }

        else if (position == 3)
        {
            title = "RATES AND TRAVEL"
        }

        else if (position == 4)
        {
            if(user_role.equals("Makeup Artist" , ignoreCase = true))
            title = "PRODUCTS"
            else
                title = "SERVICES"
        }

        return title
    }
}
