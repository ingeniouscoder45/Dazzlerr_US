package com.dazzlerr_usa.views.fragments.profile.childfragments


import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentProfileServicesBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.editprofile.models.ProfileServicesModel
import com.dazzlerr_usa.views.activities.editprofile.pojos.ProfileServicesPojo
import com.dazzlerr_usa.views.activities.editprofile.presenters.ProfileServicesPresenter
import com.dazzlerr_usa.views.activities.editprofile.views.ProfileServicesView
import com.dazzlerr_usa.views.fragments.profile.adapters.ProfileServicesAdapter
import com.dazzlerr_usa.views.fragments.profile.ProfilePojo
import com.dazzlerr_usa.views.fragments.profile.adapters.InterestAdapter
import com.google.android.flexbox.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ProfileServicesFragment : Fragment()  , ProfileServicesView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var bindingObj : FragmentProfileServicesBinding
    var user_role = ""
    var jsonData= ""
    lateinit var profilePojo: ProfilePojo
    lateinit var mServicesPresenter: ProfileServicesPresenter

    lateinit var mServicesAdapter: ProfileServicesAdapter
    var mServicesList: MutableList<ProfileServicesPojo.RoleServicesBean> = java.util.ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        bindingObj = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_services, container, false)
        initializations()
        clicklisteners()
        apiCalling()

        return bindingObj.root
    }

    fun initializations()
    {
        (activity!!.application as MyApplication).myComponent.inject(this)
        mServicesPresenter = ProfileServicesModel(activity as Activity, this)
        jsonData = arguments!!.getString("data").toString()

        val gson  = Gson()
        profilePojo = gson.fromJson(jsonData , ProfilePojo::class.java)

        user_role= profilePojo.data?.get(0)?.user_role!!

        var mServicesList : MutableList<String> = ArrayList()

        if(profilePojo.data?.get(0)?.user_services!=null && !profilePojo.data?.get(0)?.user_services.equals(""))
            mServicesList  = profilePojo.data?.get(0)?.user_services?.split(",") as MutableList<String>

        var mServicesAdapter = InterestAdapter(activity as Activity ,mServicesList!!)

        val servicesLayoutManager = FlexboxLayoutManager(activity)
        servicesLayoutManager.flexDirection = FlexDirection.ROW
        servicesLayoutManager.justifyContent = JustifyContent.FLEX_START
        //layoutManager.alignContent = AlignContent.STRETCH
        servicesLayoutManager.alignItems = AlignItems.STRETCH
        servicesLayoutManager.flexWrap = FlexWrap.WRAP
        bindingObj.servicesRecycler.setLayoutManager(servicesLayoutManager)
        bindingObj.servicesRecycler.adapter = mServicesAdapter

        if (mServicesList.size == 0)
            bindingObj.emptyLayout.visibility = View.VISIBLE
        else
            bindingObj.emptyLayout.visibility = View.GONE
    }




    fun clicklisteners()
    {

    }

    private fun apiCalling()
    {

        if((activity as Activity)?.isNetworkActive()!!)
        {
            mServicesPresenter.getServices("" ,arguments!!.getString("user_id").toString())        }

        else
        {

            val dialog = CustomDialog(activity as Activity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                apiCalling()
            })
            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                dialog.dismiss()
            })
            dialog.show()
        }
    }

    //----- On get services success
    override fun onSuccess(model: ProfileServicesPojo)
    {


        if(model?.role_services?.size!=0)
        {

            // mServicesList = model?.role_services!!
            var previousCategoryName = ""
            for(mBean in model?.role_services!!)
            {

                if (!mBean.cat_name.equals(previousCategoryName) && mBean.is_added)
                {
                    previousCategoryName = mBean.cat_name!!
                    //mBean.shouldChangeCategory = true

                    var locaBean = ProfileServicesPojo.RoleServicesBean()
                    locaBean.shouldChangeCategory = true
                    locaBean.cat_name = mBean.cat_name!!
                    mServicesList.add(locaBean)
                } else {
                    mBean.shouldChangeCategory = false
                }

                if(mBean.is_added==true) {
                    mServicesList.add(mBean)
                }
            }

            mServicesAdapter = ProfileServicesAdapter(activity as Activity ,mServicesList)

            val servicesLayoutManager = FlexboxLayoutManager(activity)
            servicesLayoutManager.flexDirection = FlexDirection.ROW
            servicesLayoutManager.justifyContent = JustifyContent.FLEX_START
            //layoutManager.alignContent = AlignContent.STRETCH
            servicesLayoutManager.alignItems = AlignItems.STRETCH
            servicesLayoutManager.flexWrap = FlexWrap.WRAP
            bindingObj.servicesRecycler.setLayoutManager(servicesLayoutManager)
            bindingObj.servicesRecycler.adapter = mServicesAdapter

            if (mServicesList.size == 0)
                bindingObj.servicesLayout.visibility = View.VISIBLE
            else
                bindingObj.servicesLayout.visibility = View.GONE


        }
    }

    override fun onUpdateServiceSuccess()
    {
        //Not in use
    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    override fun showProgress(visiblity: Boolean)
    {


    }

    fun startProgressBarAnim() {

        // bindingObj.aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {
        // bindingObj.aviProgressBar.setVisibility(View.GONE)
    }

    fun showSnackbar(message: String)
    {
        try {
            val parentLayout = activity!!.findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }
}
