package com.dazzlerr_usa.views.activities.myJobs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityMyJobsBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.fragments.castingprofile.pojo.CastingMyJobPojo
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.views.activities.addjob.AddOrEditJobActivity
import javax.inject.Inject

class MyJobsActivity : AppCompatActivity()  , CastingMyJobsView, View.OnClickListener {

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObj:ActivityMyJobsBinding

    var adapter:  MyJobsAdapter? = null

    lateinit var mPresenter: CastingMyJobsPresenter
    val PAGE_START = 1
    private var currentPage = PAGE_START
    private var isLoading = false
    var user_id = ""
    var currentTab = ""


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_my_jobs)

        initializations()
        clickListeners()
        tabManager()
        apiCalling(user_id ,currentPage.toString() ,currentTab)
        pagination()

    }

    fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)

        user_id = sharedPreferences.getString(Constants.User_id).toString()
        bindingObj.titleLayout.titletxt.text  = "My Jobs"
        bindingObj.titleLayout.rightbtn.visibility = View.VISIBLE
        bindingObj.titleLayout.righttxt.text = "Add Job"
        bindingObj.titleLayout.rightbtn.setImageResource(R.drawable.ic_add)
        bindingObj.titleLayout.rightbtn.setPadding(10,10,5,10)
        mPresenter = CastingMyJobModel(this, this)
        adapter = MyJobsAdapter(this, ArrayList() )
        val gManager = LinearLayoutManager(this)
        bindingObj.myJobsRecyclerview.layoutManager = gManager
        bindingObj.myJobsRecyclerview.addItemDecoration( DividerItemDecoration(bindingObj.myJobsRecyclerview.getContext(), DividerItemDecoration.VERTICAL))
        bindingObj.myJobsRecyclerview.adapter = adapter
    }

    fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.titleLayout.rightLayout.setOnClickListener(this)
    }
    private fun pagination()
    {
        bindingObj.myJobsRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleThreshold = 2
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val lastItem = layoutManager!!.findLastCompletelyVisibleItemPosition()
                val currentTotalCount = layoutManager.itemCount
                if (currentTotalCount <= lastItem + visibleThreshold && !isLoading)
                {
                    isLoading = true
                    apiCalling(user_id  , currentPage.toString() ,currentTab)
                }
            }
        })
    }

    private fun apiCalling(user_id: String , page:String ,status : String)
    {

        if(isNetworkActive()!!)
        {
            mPresenter?.getMyJobs(user_id , page , status)
        }
        else
        {

            val dialog = CustomDialog(this)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))

            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                apiCalling(user_id ,page ,status)
            })

            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
            dialog.dismiss()
            })
            dialog.show()
        }
    }

    fun updateJobStatus(call_id: String, status: String , position: Int)
    {
        if(isNetworkActiveWithMessage())
        mPresenter.updateJobStatus(sharedPreferences.getString(Constants.User_id).toString() ,call_id , status , position )
    }

    fun addItem( list : MutableList<CastingMyJobPojo.DataBean>)
    {
        if (currentPage != PAGE_START)
            adapter?.removeLoading()

        adapter?.addAll(list)
        adapter?.addLoading()
    }

    override fun onSuccess(model: CastingMyJobPojo)
    {
        if (model.data?.size!=0)
        {
            if(currentPage==PAGE_START)
            {
                adapter?.removeAll()
                adapter?.addAll(model.data!!)
                adapter?.addLoading()
            }
            else
                addItem(model.data!!)

            currentPage++
            isLoading = false
            bindingObj.emptyLayout.visibility  = View.GONE
            bindingObj.myJobsRecyclerview.visibility  = View.VISIBLE
        }
        else
        {
            if(currentPage!=PAGE_START)
                adapter?.removeLoading()

            else if(currentPage==PAGE_START)
            {
                bindingObj.emptyLayout.visibility  = View.VISIBLE
                bindingObj.myJobsRecyclerview.visibility  = View.GONE
            }

        }
    }

    override fun onUpdateJobStatus(status: String, position: Int)
    {
        try
        {
            if(adapter?.mModelList?.size!!>position)
                adapter?.mModelList?.get(position)?.status = status
                adapter?.notifyItemChanged(position)
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }

    }


    override fun onFailed(message: String)
    {
        isLoading =false
        showSnackbar(message)

    }

    override fun onStatusFailed(message: String) {

        val mDialog = CustomDialog(this@MyJobsActivity)
        mDialog.setTitle("Alert!")
        mDialog.setMessage(message)
        mDialog.setPositiveButton("Ok" , DialogListenerInterface.onPositiveClickListener {
            mDialog.dismiss()
        })

        mDialog.show()
    }
    override fun showProgress(visiblity: Boolean)
    {

        if(currentPage==PAGE_START && visiblity)
        {
            startProgressBarAnim()
        }
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
        try
        {
            val parentLayout = findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode)
        {

            100 ->
            {
                currentPage= PAGE_START
                adapter?.removeAll()
                apiCalling(user_id, currentPage.toString(),currentTab)
            }

            101 ->
            {
                currentPage= PAGE_START
                adapter?.removeAll()
                apiCalling(user_id, currentPage.toString(),currentTab)
            }
        }

    }

    override fun onDestroy()
    {
        super.onDestroy()
        mPresenter.cancelRetrofitRequest()
    }

    private fun tabManager()
    {

        bindingObj.tabLayout.addTab(bindingObj.tabLayout.newTab().setText("   ALL   "))
        bindingObj.tabLayout.addTab(bindingObj.tabLayout.newTab().setText("   ACTIVE   "))
        bindingObj.tabLayout.addTab(bindingObj.tabLayout.newTab().setText("   PAUSED   "))
        bindingObj.tabLayout.addTab(bindingObj.tabLayout.newTab().setText("   COMPLETED   "))

        //----- Coming from casting dashboard screen when click on view active jobs button
        if(intent.extras != null && intent.extras!!.containsKey("selectedTab"))
        {
            if(intent.extras!!.getString("selectedTab", "").equals("active" , ignoreCase = true) && bindingObj.tabLayout.getTabAt(1)!=null)
                bindingObj.tabLayout.getTabAt(1)!!.select()

            if(intent.extras!!.getString("selectedTab", "").equals("completed" , ignoreCase = true) && bindingObj.tabLayout.getTabAt(3)!=null)
                bindingObj.tabLayout.getTabAt(3)!!.select()
        }

        bindingObj.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener
        {
            override fun onTabSelected(tab: TabLayout.Tab)
            {
                if(tab.text.toString().trim().equals("All", ignoreCase = true))
                {
                    currentTab = ""
                    currentPage = PAGE_START
                    adapter?.removeAll()
                    apiCalling(user_id , currentPage.toString() ,currentTab)
                }

                else if(tab.text.toString().trim().equals("Paused", ignoreCase = true))
                {
                    currentTab = "0"
                    currentPage = PAGE_START
                    adapter?.removeAll()
                    apiCalling(user_id , currentPage.toString() ,currentTab)
                }

                else if(tab.text.toString().trim().equals("Active", ignoreCase = true))
                {
                    currentTab = "1"
                    currentPage = PAGE_START

                    adapter?.removeAll()
                    apiCalling(user_id , currentPage.toString() ,currentTab)
                }

                else if(tab.text.toString().trim().equals("Completed", ignoreCase = true))
                {
                    currentTab = "2"
                    currentPage = PAGE_START

                    adapter?.removeAll()
                    apiCalling(user_id , currentPage.toString() ,currentTab)
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab)
            {

            }

            override fun onTabReselected(tab: TabLayout.Tab)
            {

            }
        })
    }

    override fun onClick(v: View?) {

        when(v?.id)
        {
            R.id.leftbtn-> finish()

            R.id.rightLayout->
            {
                val bundle = Bundle()
                bundle.putString("type", "Add")
                bundle.putString("call_id", "")
                val intent = Intent(this, AddOrEditJobActivity::class.java)
                intent.putExtras(bundle)
                startActivityForResult(intent, 101)

/*                if (sharedPreferences.getString(Constants.IsProfile_published).equals("1")) {

                    if (sharedPreferences.getString(Constants.Is_Phone_Verified).equals("1") || sharedPreferences.getString(Constants.Is_Email_Verified).equals("1")) {

                        if (sharedPreferences.getString(Constants.Is_Documement_Verified).toString().equals("1", ignoreCase = true)
                                && sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("1", ignoreCase = true)) {
                            val bundle = Bundle()
                            bundle.putString("type", "Add")
                            bundle.putString("call_id", "")
                            val intent = Intent(this, AddOrEditJobActivity::class.java)
                            intent.putExtras(bundle)
                            startActivityForResult(intent, 101)
                        } else if (sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("0", ignoreCase = true)
                                || sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("", ignoreCase = true)
                                || sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("null", ignoreCase = true)) {

                            val dialog = CustomDialog(this@MyJobsActivity)
                            dialog.setMessage("Your identity is not verified. It is very important to help protect our community and ensure that Dazzlerr stays safe and secure. We verify identity in several ways:\n" +
                                    "\n" +
                                    "- Government ID Verification.\n" +
                                    "- Video Verification.")
                            dialog.setPositiveButton("Verify now", DialogListenerInterface.onPositiveClickListener {

                                dialog.dismiss()
                                val bundle = Bundle()
                                bundle.putString("type", "documentVerification")
                                val newIntent = Intent(this, AccountVerification::class.java)
                                newIntent.putExtras(bundle)
                                startActivity(newIntent)

                            })
                            dialog.setNegativeButton("Later", DialogListenerInterface.onNegetiveClickListener {
                                dialog.dismiss()
                            })

                            dialog.show()


                        } else if (sharedPreferences.getString(Constants.Is_Documement_Verified).toString().equals("0", ignoreCase = true)
                                && sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("1", ignoreCase = true)) {


                            val dialog = CustomDialog(this@MyJobsActivity)
                            dialog.setMessage("Your identity verification is under process. It will take 24-48 hours.")
                            dialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {
                                dialog.dismiss()
                            })

                            dialog.show()
                        }
                    } else {
                        val dialog = CustomDialog(this@MyJobsActivity)
                        dialog.setMessage("Your email and mobile number is not verified. Please verify your details to proceed.")
                        dialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {

                            val bundle = Bundle()
                            bundle.putString("type", "emailOrPhoneVerification")
                            val newIntent = Intent(this, AccountVerification::class.java)
                            newIntent.putExtras(bundle)
                            startActivity(newIntent)
                        })

                        dialog.setNegativeButton("Later", DialogListenerInterface.onNegetiveClickListener {
                            dialog.dismiss()
                        })
                        dialog.show()
                    }

                }
                else
                {
                    val dialog = CustomDialog(this@MyJobsActivity)
                    dialog.setTitle(this@MyJobsActivity?.resources?.getString(R.string.published_error_title))
                    dialog.setMessage(this@MyJobsActivity?.resources?.getString(R.string.published_error_message))
                    dialog.setPositiveButton("SETTINGS", DialogListenerInterface.onPositiveClickListener {

                        val newIntent = Intent(this@MyJobsActivity, SettingsActivity::class.java)
                        startActivity(newIntent)
                    })

                    dialog.setNegativeButton("Later", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })

                    dialog.show()
                }*/
            }
        }
    }

}
