package com.dazzlerr_usa.views.fragments.portfolio.adapters

import androidx.fragment.app.Fragment

import com.dazzlerr_usa.views.fragments.portfolio.localfragments.PortfolioAudiosFragment
import com.dazzlerr_usa.views.fragments.portfolio.localfragments.PortfolioImagesFragment
import com.dazzlerr_usa.views.fragments.portfolio.localfragments.PortfolioProjectsFragment
import com.dazzlerr_usa.views.fragments.portfolio.localfragments.PortfolioVideosFragment

class ViewPagerAdapter(fm: androidx.fragment.app.FragmentManager ,var shouldShowProjects:String ) : androidx.fragment.app.FragmentPagerAdapter(fm)
{

    override fun getItem(position: Int): Fragment
    {
        var fragment  = Fragment()
        if (position == 0)
        {
            fragment = PortfolioImagesFragment()
        }
        else if (position == 1)
        {
            fragment = PortfolioVideosFragment()
        }
        else if (position == 2)
        {
            fragment = PortfolioAudiosFragment()
        }
        else if (position == 3)
        {
            fragment = PortfolioProjectsFragment()
        }

        return fragment
    }

    override fun getCount(): Int
    {
        if(shouldShowProjects.equals("true"))
        return 4
        else
            return 3
    }

    override fun getPageTitle(position: Int): CharSequence?
    {
        var title: String? = null
        if (position == 0)
        {
            title = "   IMAGES  "
        } else if (position == 1)
        {
            title = "  VIDEOS  "
        } else if (position == 2)
        {
            title = "  AUDIOS  "
        } else if (position == 3)
        {
            title = "  PROJECTS  "
        }

        return title
    }
}
