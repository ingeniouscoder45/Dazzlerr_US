package com.dazzlerr_usa.views.fragments.register.localfragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentChooseRegisterBinding
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.fragments.ParentFragment

/**
 * A simple [Fragment] subclass.
 */
class ChooseRegisterFragment : ParentFragment() ,View.OnClickListener
{

    lateinit var bindingObj:FragmentChooseRegisterBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        bindingObj =  DataBindingUtil.inflate(inflater , R.layout.fragment_choose_register, container, false)
        initializations()
        clickListeners()
        return bindingObj.root
    }

     fun initializations()
    {
        (activity as MainActivity).showStatusBar()
        bindingObj.titleLayout.rightbtn.visibility = View.GONE
        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
    }

    fun clickListeners()
    {
        bindingObj.registerAsCastingBtn.setOnClickListener(this)
        bindingObj.registerAsModelBtn.setOnClickListener(this)
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
    }
    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.registerAsCastingBtn->
            {

                val fragment = RegisterCastingsFragment()
                (activity as MainActivity).LoadFragment(fragment)

            }
            R.id.registerAsModelBtn->
            {
                val fragment = RegisterModelFragment()
                (activity as MainActivity).LoadFragment(fragment)

               /* var bundle = Bundle()
                bundle.putString("membership_type" , "register")

               startActivity(Intent(activity  , MembershipActivity::class.java).putExtras(bundle))*/
            }

            R.id.leftbtn -> (activity as MainActivity).backpress()

        }

    }

}
