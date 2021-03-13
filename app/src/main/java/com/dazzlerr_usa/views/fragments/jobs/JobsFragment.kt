package com.dazzlerr_usa.views.fragments.jobs


import android.app.Activity
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentJobsBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.activities.home.LocationUpdate
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.views.activities.jobfilter.JobFilterActivity
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class JobsFragment : androidx.fragment.app.Fragment() , JobsView , View.OnClickListener , LocationUpdate
{

    internal lateinit var bindingObj: FragmentJobsBinding
    var adapter:  JobsAdapter? = null
    var appliedJobAdapter:  AppliedJobsAdapter? = null
    lateinit var mPresenter: JobsPresenter
    val PAGE_START = 1
    private var currentPage = PAGE_START
    private var currentAppliedPage = PAGE_START
    private var isLoading = false
    var city=""
    var default_city=""
    var category_id = ""
    var u_name = ""
    var gender = ""
    var experience_type = ""
    val LOCATIONREQUESTCODE = 100
    val FilterREQUESTCODE = 101
    var currenttab  = 0 // 0 for all, 1 for applied jobs, 2 for shortlisted and 3 for featured
    var categoryList : java.util.ArrayList<HashMap<String, String>> = java.util.ArrayList()
    var experience_filter_check_list  : java.util.ArrayList<String> = java.util.ArrayList()
    var gender_check_list  : java.util.ArrayList<String> = java.util.ArrayList()
    var categoryidsList  : java.util.ArrayList<Int> = java.util.ArrayList()

    var enterAnimation: Animation? = null
    var exitAnimation: Animation? = null
    var searchVisibilityFlag = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        bindingObj = DataBindingUtil.inflate(inflater, R.layout.fragment_jobs, container, false)
        initializations()
        clickListeners()
        searchView()
        tabManager()
        pagination()


        //----- Coming from talent dashboard screen when click on view all applied jobs button
        if(arguments != null && arguments!!.containsKey("selectedTab"))
        {
            if(arguments!!.getString("selectedTab", "").equals("applied" , ignoreCase = true) && bindingObj.tabLayout.getTabAt(2)!=null)
                bindingObj.tabLayout.getTabAt(1)!!.select()

            else if(arguments!!.getString("selectedTab", "").equals("shortlisted" , ignoreCase = true) && bindingObj.tabLayout.getTabAt(3)!=null)
                bindingObj.tabLayout.getTabAt(2)!!.select()

            else if(arguments!!.getString("selectedTab", "").equals("featured" , ignoreCase = true) && bindingObj.tabLayout.getTabAt(1)!=null) {
                bindingObj.tabLayout.addTab(bindingObj.tabLayout.newTab().setText("FEATURED"))
                bindingObj.tabLayout.getTabAt(3)!!.select()
            }


        }
        else
            apiCalling( default_city , currentPage.toString() ,u_name , gender , category_id , experience_type ,"",true)

        return bindingObj.root
    }

    private fun initializations()
    {
        //default_city = (activity as HomeActivity).sharedPreferences.getString(Constants.CurrentAddress).toString()
        default_city = "All"
        (activity as HomeActivity).titleSettings(resources.getColor(R.color.appColor), true, resources.getColor(R.color.colorWhite), resources.getColor(R.color.colorWhite), resources.getColor(R.color.colorWhite), "Jobs",  (activity as HomeActivity).currentAddress!!, R.drawable.ic_location)
        (activity as HomeActivity).setBottomNavigationMenu(false, false, true, false, false)
        mPresenter = JobsModel(activity as HomeActivity , this)
        adapter = JobsAdapter(activity as Activity, ArrayList() ,this)
        appliedJobAdapter = AppliedJobsAdapter(activity as Activity, ArrayList())
        val gManager = LinearLayoutManager(activity)
        bindingObj.jobsRecycler.layoutManager = gManager
        bindingObj.jobsRecycler.addItemDecoration( DividerItemDecoration(bindingObj.jobsRecycler.getContext(), DividerItemDecoration.VERTICAL))
        bindingObj.jobsRecycler.adapter = adapter

        enterAnimation = AnimationUtils.loadAnimation(getActivity(),R.anim.search_edit_anim);
        exitAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.search_edit_anim_exit);

    }

    private fun clickListeners()
    {
        bindingObj.exploreBtn.setOnClickListener(this)
        bindingObj.filterBtn.setOnClickListener(this)
        bindingObj.searchBtn.setOnClickListener(this)
        (activity as HomeActivity).binderObject.titleLayout.titletxt.setOnClickListener(this)
    }

    private fun searchView()
    {
        val searchIcon = bindingObj.searchView.findViewById(androidx.appcompat.R.id.search_mag_icon) as ImageView
        searchIcon.setImageResource(R.drawable.ic_expand_less_black_24dp)

        searchIcon.setOnClickListener {
            if (searchVisibilityFlag)
            {
                bindingObj.searchCardView.startAnimation(exitAnimation)
                bindingObj.searchCardView.visibility = View.GONE
                searchVisibilityFlag = false
            }
        }

        // listening to search query text change
        bindingObj.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean
            {
                currentPage=PAGE_START
                u_name = query
                apiCalling( city , currentPage.toString() ,u_name , gender , category_id , experience_type ,"",true)

                return false
            }

            override fun onQueryTextChange(query: String): Boolean
            {
                if(!u_name.equals("")&&query.equals(""))
                {
                    currentPage = PAGE_START
                    u_name = query
                    apiCalling( city , currentPage.toString() ,u_name , gender , category_id , experience_type ,"",true)
                }

                return false
            }
        })

    }

    private fun tabManager()
    {

        bindingObj.tabLayout.addTab(bindingObj.tabLayout.newTab().setText("ALL JOBS"))

        if((activity as HomeActivity).sharedPreferences.getString(Constants.User_id).toString().equals(""))
        {
            //--- Do your work
            Timber.e("User_id blank")
            bindingObj.tabLayout.visibility  = View.GONE
        }

        else
        {

            if((activity as HomeActivity).sharedPreferences.getString(Constants.User_Role).toString().equals("casting",ignoreCase = true))
            {
                //--- Do your work
                bindingObj.tabLayout.visibility  = View.GONE
            }

            else {
                bindingObj.tabLayout.addTab(bindingObj.tabLayout.newTab().setText("APPLIED JOBS"))
                bindingObj.tabLayout.addTab(bindingObj.tabLayout.newTab().setText("SHORTLISTED"))
            }


        }

        bindingObj.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener
        {
            override fun onTabSelected(tab: TabLayout.Tab)
            {
                mPresenter.cancelRetrofitRequest()

                if(tab.text.toString().trim().equals("All Jobs", ignoreCase = true))
                {

                    bindingObj.filterBtn.show()
                    currenttab =0
                    currentPage = PAGE_START
                    adapter?.removeAll()
                    bindingObj.jobsRecycler.adapter = adapter
                    if(city.isEmpty())
                    apiCalling( default_city , currentPage.toString() ,u_name , gender , category_id , experience_type ,"",true)
                   else
                    apiCalling( city , currentPage.toString() ,u_name , gender , category_id , experience_type ,"",true)
                }

                else if(tab.text.toString().trim().equals("Applied Jobs", ignoreCase = true))
                {
                    bindingObj.filterBtn.hide()
                    currenttab =1
                    currentAppliedPage = PAGE_START
                    appliedJobAdapter?.removeAll()
                    bindingObj.jobsRecycler.adapter = appliedJobAdapter
                    appliedJobsApiCalling((activity as HomeActivity).sharedPreferences.getString(Constants.User_id).toString() , currentAppliedPage.toString() , true)
                }


                if(tab.text.toString().trim().equals("SHORTLISTED", ignoreCase = true))
                {

                    bindingObj.filterBtn.hide()

                    currenttab =2
                    currentPage = PAGE_START
                    adapter?.removeAll()
                    bindingObj.jobsRecycler.adapter = adapter

                    if(city.isEmpty())
                        apiCalling( default_city , currentPage.toString() ,u_name , gender , category_id , experience_type ,"shortlist", true)

                    else
                        apiCalling( city , currentPage.toString() ,u_name , gender , category_id , experience_type ,"shortlist", true)
                }

                else if(tab.text.toString().trim().equals("Featured", ignoreCase = true))
                {
                    bindingObj.filterBtn.hide()

                    currenttab =3
                    currentPage = PAGE_START
                    adapter?.removeAll()
                    bindingObj.jobsRecycler.adapter = adapter

                    if(city.isEmpty())
                        apiCalling( default_city , currentPage.toString() ,u_name , gender , category_id , experience_type ,"featured", true)

                    else
                        apiCalling( city , currentPage.toString() ,u_name , gender , category_id , experience_type ,"featured", true)

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

    private fun pagination()
    {

        bindingObj.jobsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleThreshold = 2

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val lastItem = layoutManager!!.findLastCompletelyVisibleItemPosition()
                val currentTotalCount = layoutManager.itemCount

                if (currentTotalCount <= lastItem + visibleThreshold && !isLoading)
                {
                    //This condition is to check for whether is search pagination or normal pagination
                    if(city.isEmpty())
                    {
                        isLoading = true

                        if(u_name.isEmpty()) {
                            if (currenttab == 0)
                                apiCalling(default_city, currentPage.toString(), u_name, gender, category_id, experience_type, "", false)
                             else if(currenttab==1)
                                appliedJobsApiCalling((activity as HomeActivity).sharedPreferences.getString(Constants.User_id).toString(), currentAppliedPage.toString(), false)
                            else if (currenttab == 2)
                                apiCalling(default_city, currentPage.toString(), u_name, gender, category_id, experience_type, "shortlist", false)
                            else if (currenttab == 3)
                                apiCalling(default_city, currentPage.toString(), u_name, gender, category_id, experience_type, "featured", false)

                        }

                        else
                            apiCalling(city, currentPage.toString(), u_name, gender, category_id, experience_type, "", false)

                    }
                    else
                    {

                        isLoading = true

                        if(u_name.isEmpty()) {

                            if (currenttab == 0)
                                apiCalling(city, currentPage.toString(), u_name, gender, category_id, experience_type, "", false)
                            else if(currenttab==1)
                                appliedJobsApiCalling((activity as HomeActivity).sharedPreferences.getString(Constants.User_id).toString(), currentAppliedPage.toString(), false)
                            else if (currenttab == 2)
                                apiCalling(city, currentPage.toString(), u_name, gender, category_id, experience_type, "shortlist", false)
                            else if (currenttab == 3)
                                apiCalling(city, currentPage.toString(), u_name, gender, category_id, experience_type, "featured", false)

                        }

                        else
                        {
                            apiCalling(city, currentPage.toString(), u_name, gender, category_id, experience_type, "", false)
                        }
                    }

                }
            }
        })
    }

    private fun apiCalling(city: String , page:String , u_name: String , gender:String , category_id : String , experience_type:String ,type : String,  isShowProgressbar:Boolean)
    {

        if(activity?.isNetworkActive()!!)
        {
            mPresenter?.getJobs( (activity as HomeActivity).sharedPreferences.getString(Constants.User_id).toString(), city , page ,u_name , gender , category_id , experience_type ,type  , isShowProgressbar)
        }

        else
        {

            val dialog  = CustomDialog(activity as Activity)
                    dialog.setTitle(resources.getString(R.string.no_internet_txt))
                    dialog.setMessage(resources.getString(R.string.connection_txt))

                    dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                        apiCalling(city ,page ,u_name , gender , category_id , experience_type , type , true)
                    })

                    dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })

                    dialog.show()
        }
    }


    private fun appliedJobsApiCalling(user_id: String , page:String , isShowProgressbar:Boolean)
    {

        if(activity?.isNetworkActive()!!)
        {
            mPresenter.getAppliedJobs( user_id , page ,isShowProgressbar)
        }

        else
        {

            val dialog =CustomDialog(activity as Activity)
                    dialog.setTitle(resources.getString(R.string.no_internet_txt))
                    dialog.setMessage(resources.getString(R.string.connection_txt))
                    dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                        appliedJobsApiCalling( user_id , page ,true)
                    })
                    dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })
                    dialog.show()

        }
    }

    fun removeJobFromShortlisted(position: Int , job_id: String)
    {
        mPresenter.shortList_job((activity as HomeActivity).sharedPreferences.getString(Constants.User_id).toString() , job_id , "0", position)
    }

    fun addItem( list : MutableList<JobsPojo.DataBean>)
    {
        if (currentPage != PAGE_START)
            adapter?.removeLoading()

        adapter?.addAll(list)
        adapter?.addLoading()
    }


    override fun onJobsSuccess(model: JobsPojo)
    {

        if(u_name.isNotEmpty())
        {
            if(city.isEmpty())
                bindingObj.SearchResultTxt.text = u_name
            else
                bindingObj.SearchResultTxt.text = u_name+" in "+city

            bindingObj.tabLayout.visibility = View.GONE
            bindingObj.SearchResultTxt.visibility = View.VISIBLE
            bindingObj.SearchResultLayout.visibility = View.VISIBLE
        }

        else
        {
            if(!(activity as HomeActivity).sharedPreferences.getString(Constants.User_Role).toString().equals("casting",ignoreCase = true)
                    && !(activity as HomeActivity).sharedPreferences.getString(Constants.User_id).toString().isEmpty() )
            bindingObj.tabLayout.visibility = View.VISIBLE

            bindingObj.SearchResultTxt.text =  ""
            bindingObj.SearchResultTxt.visibility = View.GONE
            bindingObj.SearchResultLayout.visibility = View.GONE
        }

        if (model.data?.size!=0)
        {


            //---- Hiding the search bar
            if(u_name.isNotEmpty())
            bindingObj.searchCardView.startAnimation(exitAnimation)

            bindingObj.searchCardView.visibility = View.GONE
            searchVisibilityFlag = false

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

            bindingObj.jobsRecycler.visibility = View.VISIBLE
            bindingObj.emptyLayout.visibility  = View.GONE
        }
        else
        {
            if(currentPage!=PAGE_START)
                adapter?.removeLoading()

            else if(currentPage==PAGE_START)
            {
                bindingObj.emptyLayout.visibility  = View.VISIBLE

                //hiding explore all button for shortlisted jobs
                if(currenttab==2)
                    bindingObj.exploreBtn.visibility = View.GONE
                else
                    bindingObj.exploreBtn.visibility = View.VISIBLE

                bindingObj.jobsRecycler.visibility = View.GONE
            }

        }
    }

    override fun onJobShortlisted(status: String, position: Int) {

        adapter?.remove(position)
    }

    fun addAppliedItem( list : MutableList<AppliedJobsPojo.DataBean>)
    {
        if (currentAppliedPage != PAGE_START)
            appliedJobAdapter?.removeLoading()

        appliedJobAdapter?.addAll(list)
        appliedJobAdapter?.addLoading()
    }

    override fun onAppliedJobsSuccess(model: AppliedJobsPojo) {

        if (model.data?.size!=0)
        {
            if(currentAppliedPage==PAGE_START)
            {
                appliedJobAdapter?.removeAll()
                appliedJobAdapter?.addAll(model.data!!)
                appliedJobAdapter?.addLoading()
            }
            else
                addAppliedItem(model.data!!)

            currentAppliedPage++
            isLoading = false

            bindingObj.jobsRecycler.visibility = View.VISIBLE
            bindingObj.emptyLayout.visibility  = View.GONE
        }
        else
        {
            if(currentAppliedPage!=PAGE_START)
                appliedJobAdapter?.removeLoading()

            else if(currentAppliedPage==PAGE_START)
            {
                bindingObj.emptyLayout.visibility  = View.VISIBLE
                bindingObj.jobsRecycler.visibility = View.GONE
            }

        }

    }

    override fun onFailed(message: String) {
        isLoading =false
        showSnackbar(message)
    }

    override fun showProgress(visiblity: Boolean , isShowProgressbar :Boolean ) {

        if(isShowProgressbar && activity!=null)
        {
            if (visiblity) {
                (activity as HomeActivity).startProgressBarAnim()
            } else {
                (activity as HomeActivity).stopProgressBarAnim()
            }
        }
    }

    fun showSnackbar(message: String)
    {
        try {
            val parentLayout = (activity as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onCurrentLocationUpdate()
    {
        default_city  = (activity as HomeActivity).sharedPreferences.getString(Constants.CurrentAddress).toString()
        bindingObj.searchView.setQuery("", false)
        bindingObj.searchView.clearFocus()
        city = ""
        u_name = ""
        gender = ""
        category_id = ""
        experience_type = ""

        if(currenttab==0)
        {
            if(!(activity as HomeActivity).sharedPreferences.getString(Constants.User_Role).toString().equals("casting",ignoreCase = true))
            bindingObj.tabLayout.visibility = View.VISIBLE
            bindingObj.SearchResultLayout.visibility = View.GONE
            currentPage =PAGE_START
            isLoading = false
            adapter = JobsAdapter(activity , ArrayList() ,this@JobsFragment)
            bindingObj.jobsRecycler.adapter = adapter
            default_city  = (activity as HomeActivity).sharedPreferences.getString(Constants.CurrentAddress).toString()

            city = ""
            u_name = ""
            gender = ""
            category_id = ""
            experience_type = ""
            apiCalling(default_city, currentPage.toString(), u_name, gender, category_id, experience_type, "", true)

        }
        else if(currenttab==2)
        {
            if(!(activity as HomeActivity).sharedPreferences.getString(Constants.User_Role).toString().equals("casting",ignoreCase = true))
            bindingObj.tabLayout.visibility = View.VISIBLE

            bindingObj.SearchResultLayout.visibility = View.GONE

            currentPage =PAGE_START
            isLoading = false
            adapter = JobsAdapter(activity , ArrayList() ,this@JobsFragment)
            bindingObj.jobsRecycler.adapter = adapter
            default_city  = (activity as HomeActivity).sharedPreferences.getString(Constants.CurrentAddress).toString()

            city = ""
            u_name = ""
            gender = ""
            category_id = ""
            experience_type = ""
            apiCalling(default_city  , currentPage.toString()  ,u_name , gender , category_id , experience_type ,"shortlist",true)
        }
        else if(currenttab==3)
        {
            if(!(activity as HomeActivity).sharedPreferences.getString(Constants.User_Role).toString().equals("casting",ignoreCase = true))
            bindingObj.tabLayout.visibility = View.VISIBLE

            bindingObj.SearchResultLayout.visibility = View.GONE

            currentPage =PAGE_START
            isLoading = false
            adapter = JobsAdapter(activity , ArrayList() ,this@JobsFragment)
            bindingObj.jobsRecycler.adapter = adapter
            default_city  = (activity as HomeActivity).sharedPreferences.getString(Constants.CurrentAddress).toString()

            city = ""
            u_name = ""
            gender = ""
            category_id = ""
            experience_type = ""
            apiCalling(default_city  , currentPage.toString()  ,u_name , gender , category_id , experience_type ,"featured",true)
        }

        else
        {
            // Do work
        }

    }

    override fun onClick(v: View?)
    {

        when(v?.id)
        {

            R.id.exploreBtn->
            {
                populateCategories()
                val bundle = Bundle()
                bundle.putStringArrayList("checklist" , experience_filter_check_list)
                bundle.putStringArrayList("gender_check_list" , gender_check_list)
                bundle.putIntegerArrayList("categoryidsList" , categoryidsList)
                bundle.putString("fromScreen" , "Jobs")
                bundle.putString("city" , city)
                startActivityForResult(Intent(activity as HomeActivity, JobFilterActivity::class.java ).putExtras(bundle) ,FilterREQUESTCODE)
            }

            R.id.filterBtn->
            {

                populateCategories()
                val bundle = Bundle()
                bundle.putStringArrayList("checklist" , experience_filter_check_list)
                bundle.putStringArrayList("gender_check_list" , gender_check_list)
                bundle.putIntegerArrayList("categoryidsList" , categoryidsList)
                bundle.putString("fromScreen" , "Jobs")
                bundle.putString("city" , city)
                startActivityForResult(Intent(activity as HomeActivity, JobFilterActivity::class.java ).putExtras(bundle) ,FilterREQUESTCODE)

            }

            R.id.searchBtn->
            {
                if(!bindingObj.searchView.query.toString().equals(""))
                {
                    currentPage = PAGE_START
                    u_name = bindingObj.searchView.query.toString()
                    apiCalling(city  , currentPage.toString()  ,u_name , gender , category_id , experience_type ,"",true)

                }
            }

            R.id.titletxt->
            {
                if(!searchVisibilityFlag)
                {
                    bindingObj.searchCardView.visibility = View.VISIBLE
                    bindingObj.searchCardView.startAnimation(enterAnimation)
                    searchVisibilityFlag = true
                }

            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==FilterREQUESTCODE)
        {
            if( data?.extras!=null)
            {
                var bundle: Bundle = data?.extras!!

                experience_filter_check_list = bundle.getStringArrayList("checklist")!!
                gender_check_list = bundle.getStringArrayList("gender_check_list")!!
                categoryidsList = bundle.getIntegerArrayList("categoryidsList")!!
                city = bundle.getString("city")!!

                //Timber.e(experience_filter_check_list.toString())
               // Timber.e(categoryidsList.toString())

                category_id = categoryidsList.toString()
                        .replace("[","")
                        .replace("]","")
                        .replace(" ","")


                experience_type = experience_filter_check_list.toString()
                        .replace("[","")
                        .replace("]","")
                        .replace(" ","")


                gender = gender_check_list.toString()
                        .replace("[","")
                        .replace("]","")
                        .replace(" ","")


               // Timber.e(category_id)
               // Timber.e(gender)
              //  Timber.e(experience_type)

                u_name= ""
                bindingObj.searchView.setQuery("", false)
                bindingObj.searchView.clearFocus()
                currentPage =  PAGE_START


                if(!(activity as HomeActivity).sharedPreferences.getString(Constants.User_Role).toString().equals("casting",ignoreCase = true))
                bindingObj.tabLayout.visibility = View.VISIBLE

                bindingObj.SearchResultLayout.visibility = View.GONE
                bindingObj.SearchResultTxt.text = ""

                if(city.isEmpty())
                apiCalling(default_city  , currentPage.toString() ,u_name , gender , category_id , experience_type ,"",true)
               else
                    apiCalling(city  , currentPage.toString() ,u_name , gender , category_id , experience_type ,"",true)

            }
        }
    }


    fun populateCategories()
    {
        var map : HashMap<String ,String>  = HashMap()
        map.put("id" , "2")
        map.put("role" , "Model")
        categoryList.add(map)
        map.put("id" , "3")
        map.put("role" , "Photographer")
        categoryList.add(map)
        map.put("id" , "4")
        map.put("role" , "Makeup Artist")
        categoryList.add(map)
        map.put("id" , "5")
        map.put("role" , "Stylist")
        categoryList.add(map)
        map.put("id" , "7")
        map.put("role" , "Hair Stylist")
        categoryList.add(map)
        map.put("id" , "8")
        map.put("role" , "Anchor")
        categoryList.add(map)
        map.put("id" , "9")
        map.put("role" , "Dancer")
        categoryList.add(map)
        map.put("id" , "10")
        map.put("role" , "Fashion Designer")
        categoryList.add(map)
        map.put("id" , "11")
        map.put("role" , "Actor")
        categoryList.add(map)
        map.put("id" , "12")
        map.put("role" , "Modelling Choreographer")
        categoryList.add(map)
        map.put("id" , "13")
        map.put("role" , "Comedian")
        categoryList.add(map)
        map.put("id" , "14")
        map.put("role" , "Singer")
        categoryList.add(map)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.cancelRetrofitRequest()
    }

}
