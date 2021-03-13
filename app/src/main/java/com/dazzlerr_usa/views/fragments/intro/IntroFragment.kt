package com.dazzlerr_usa.views.fragments.intro

import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.ViewPager

import com.dazzlerr_usa.R
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.databinding.FragmentIntroBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.fragments.ParentFragment

/**
 * A simple [Fragment] subclass.
 */
class IntroFragment : ParentFragment()
{

    private var myadapter: SlideAdapter? = null

    public var mdots: Array<TextView?>? = null
    public var mCureentPage: Int = 0
    internal lateinit var bindingObject: FragmentIntroBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        bindingObject = DataBindingUtil.inflate(inflater, R.layout.fragment_intro, container, false)
        initializations()
        return bindingObject.root
    }

    private fun initializations()
    {

        bindingObject.viewpager.setPageTransformer(true, SildeTransformer())
        myadapter = SlideAdapter(activity!! ,this@IntroFragment)
        bindingObject.viewpager.adapter = myadapter

        bindingObject.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int)
            {
                mCureentPage= position
            }
        })
    }

    fun nextSlide()
    {
        bindingObject.viewpager.currentItem = mCureentPage+1
    }

    fun skipSlide()
    {
        loadFragment()
    }

    private fun loadFragment()
    {

        (activity as MainActivity).sharedPreferences.saveString(Constants.User_type, "GuestUser")
        (activity as MainActivity).sharedPreferences.saveString(Constants.CurrentAddress, "Delhi")
        val intent = Intent(activity, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        activity!!.finish()

    }


}
