package com.dazzlerr_usa.views.fragments.profileemailphoneverification


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentCastingVerificationSuccessfullBinding
import com.dazzlerr_usa.views.activities.home.HomeActivity

/**
 * A simple [Fragment] subclass.
 */
class CastingProfileVerificationSuccessfullFragment : Fragment(), View.OnClickListener
{

    lateinit var bindingObj:FragmentCastingVerificationSuccessfullBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        bindingObj = DataBindingUtil.inflate(inflater, R.layout.fragment_casting_verification_successfull, container, false)
        clickListeners()
        return bindingObj.root
    }

    fun clickListeners()
    {
        bindingObj.browseDashboardBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when(v?.id)
        {
            R.id.browseDashboardBtn ->
            {
                val intent = Intent(activity, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                activity!!.finish()
            }
        }
    }

}
