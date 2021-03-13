package com.dazzlerr_usa.views.activities.editprofile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityEditServicesBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.editprofile.adapters.EditServicesAdapter
import com.dazzlerr_usa.views.activities.editprofile.models.ProfileServicesModel
import com.dazzlerr_usa.views.activities.editprofile.pojos.ProfileServicesPojo
import com.dazzlerr_usa.views.activities.editprofile.presenters.ProfileServicesPresenter
import com.dazzlerr_usa.views.activities.editprofile.views.ProfileServicesView
import com.dazzlerr_usa.views.activities.editprofile.adapters.SkillsAndInterestsAdapter
import com.google.android.flexbox.*
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import java.util.ArrayList
import javax.inject.Inject

class EditServicesActivity : AppCompatActivity(), View.OnClickListener , ProfileServicesView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObj : ActivityEditServicesBinding
    lateinit var mPresenter: ProfileServicesPresenter
    var filter_services_list  : ArrayList<String> = ArrayList()
    var mCustomServiceList : MutableList<String> = ArrayList()
    var mCustomServiceAdapter: SkillsAndInterestsAdapter?=null
    lateinit var mServicesAdapter:EditServicesAdapter
    var mServicesList: MutableList<ProfileServicesPojo.RoleServicesBean> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?)
    {

        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_edit_services)
        initializations()
        clicklisteners()
        apiCalling()

    }

    fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)
        bindingObj.titleLayout.titletxt.text = "Services"
        mPresenter = ProfileServicesModel(this, this)

        val interestLayoutManager = FlexboxLayoutManager(this)
        interestLayoutManager.flexDirection = FlexDirection.ROW
        interestLayoutManager.justifyContent = JustifyContent.FLEX_START
        interestLayoutManager.alignItems= AlignItems.STRETCH
        interestLayoutManager.flexWrap = FlexWrap.WRAP
        bindingObj.customServiceRecycler.setLayoutManager(interestLayoutManager)
        mCustomServiceAdapter = SkillsAndInterestsAdapter(this, mCustomServiceList)
        bindingObj.customServiceRecycler.adapter = mCustomServiceAdapter

    }

    fun clicklisteners()
    {

        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.addServicesBtn.setOnClickListener(this)
        bindingObj.submitServicesBtn.setOnClickListener(this)

    }

    private fun apiCalling()
    {

        if(this@EditServicesActivity?.isNetworkActive()!!)
        {
            mPresenter.getServices(sharedPreferences.getString(Constants.User_Role).toString() ,sharedPreferences.getString(Constants.User_id).toString())
        }

        else
        {

            val dialog = CustomDialog(this@EditServicesActivity)
                    dialog.setTitle(resources.getString(R.string.no_internet_txt))
                    dialog.setMessage(resources.getString(R.string.connection_txt))
                    dialog.setPositiveButton("Retry",DialogListenerInterface.onPositiveClickListener {
                        apiCalling()
                    })
                    dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })
                    dialog.show()
        }
    }

    override fun onUpdateServiceSuccess()
    {
        Toast.makeText(this , "Services has been updated successfully!" , Toast.LENGTH_SHORT).show()
        finish()
    }
    override fun onSuccess(model: ProfileServicesPojo)
    {

        if(model?.role_services?.size!=0)
        {

           // mServicesList = model?.role_services!!
            var previousCategoryName = ""
            for(mBean in model?.role_services!!)
            {
               if(!mBean.cat_name.equals(previousCategoryName))
               {

                   previousCategoryName = mBean.cat_name!!
                   //mBean.shouldChangeCategory = true

                   var locaBean = ProfileServicesPojo.RoleServicesBean()
                   locaBean.shouldChangeCategory =true
                   locaBean.cat_name =mBean.cat_name!!
                   mServicesList.add(locaBean)
               }
                else
               {
                   mBean.shouldChangeCategory = false
               }

                mServicesList.add(mBean)
            }

            mServicesAdapter = EditServicesAdapter(this@EditServicesActivity ,mServicesList)
            val gManager = GridLayoutManager(this, 2)


            gManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup()
            {
                override fun getSpanSize(position: Int): Int
                {
                    when (mServicesAdapter!!.getItemViewType(position))
                    {
                        mServicesAdapter?.VIEW_TYPE_NORMAL -> return 1
                        mServicesAdapter?.VIEW_TYPE_CATEGORY -> return 2
                        else -> return -1
                    }
                }
            }

            bindingObj.servicesRecyclerView.layoutManager = gManager
            bindingObj.servicesRecyclerView.adapter = mServicesAdapter

        }
    }

    override fun onFailed(message: String)
    {

        showSnackbar(message)
    }

    override fun showProgress(visiblity: Boolean) {

        if(visiblity)
            startProgressBarAnim()
        else
            stopProgressBarAnim()
    }

    fun startProgressBarAnim() {

        bindingObj.aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {
        bindingObj.aviProgressBar.setVisibility(View.GONE)
    }

    fun showSnackbar(message: String)
    {
        try {
            val parentLayout = findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?)
    {

        when(v?.id)
        {

            R.id.leftbtn->
            {
                finish()
            }

            R.id.addServicesBtn->
            {
                if(bindingObj.customServiceEdittext.text.length!=0)
                {
                    mCustomServiceList.add(bindingObj.customServiceEdittext.text.toString())
                    mCustomServiceAdapter?.notifyDataSetChanged()
                    bindingObj.customServiceEdittext.text.clear()
                }
            }

             R.id.submitServicesBtn->
            {
               if(isNetworkActiveWithMessage())
               {
                  /* Timber.e("Services "+filter_services_list.toString().replace("[", "").replace("]","") )
                   Timber.e("Custom Services "+mCustomServiceList.toString().replace("[", "").replace("]","") )
*/
                   mPresenter.updateServices(sharedPreferences.getString(Constants.User_id).toString()
                           , filter_services_list.toString().replace("[", "").replace("]","")
                           , mCustomServiceList.toString().replace("[", "").replace("]","")
                            )
               }
            }

        }
    }
}
