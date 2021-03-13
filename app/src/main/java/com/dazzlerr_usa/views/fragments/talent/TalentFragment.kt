package com.dazzlerr_usa.views.fragments.talent


import android.app.Activity
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentTalentBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.GridSpacingItemDecoration
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.activities.home.LocationUpdate
import com.dazzlerr_usa.views.activities.location.LocationActivity
import com.dazzlerr_usa.views.fragments.home.pojos.ModelsPojo
import com.dazzlerr_usa.views.fragments.talent.adapters.TalentsAdapter
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActive

import java.util.ArrayList
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import android.view.animation.Animation
import timber.log.Timber
import android.widget.ImageView
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.talentfilter.TalentFilterActivity


/**
 * A simple [Fragment] subclass.
 */
class TalentFragment : androidx.fragment.app.Fragment() , TalentView , LocationUpdate , View.OnClickListener
{

    lateinit var talentsAdapter: TalentsAdapter
    lateinit var bindingObj: FragmentTalentBinding
    lateinit var talentPresenter: TalentPresenter
    val PAGE_START = 1
    private var currentPage = PAGE_START
    private var isLoading = false
    val LOCATIONREQUESTCODE = 100
    val FilterREQUESTCODE = 101

    var enterAnimation: Animation? = null
    var exitAnimation:Animation? = null
    var searchVisibilityFlag = false
    var city=""
    var default_city=""

    var category_id = ""
    var model_name = ""
    var gender = ""
    var experience_type = ""
    var categoryList : ArrayList<HashMap<String , String>> =  ArrayList()
    var experience_filter_check_list  : ArrayList<String> = ArrayList()
    var gender_check_list  : ArrayList<String> = ArrayList()
    var categoryidsList  : ArrayList<Int> = ArrayList()

    var rangeMinimumAgeVal = ""
    var rangeMinimumHeightVal = ""
    var rangeMaximumAgeVal = ""
    var rangeMaximumHeightVal = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        bindingObj = DataBindingUtil.inflate(inflater, R.layout.fragment_talent, container, false)
        initializations()
        talentRecyclerSettings()
        clickListeners()
        searchView()
        pagination()
        apiCalling(default_city , currentPage.toString() ,model_name , gender , category_id , experience_type ,true)

        return bindingObj.root

    }

    private fun initializations()
    {
        if(arguments!=null)
        {
            category_id=  arguments?.getString("category_id").toString()

            if(category_id.isNotEmpty())
            categoryidsList.add(category_id.toInt())
        }

        default_city = (activity as HomeActivity).sharedPreferences.getString(Constants.CurrentAddress).toString()
        (activity as HomeActivity).titleSettings(resources.getColor(R.color.appColor), true, resources.getColor(R.color.colorWhite), resources.getColor(R.color.colorWhite), resources.getColor(R.color.colorWhite), "Talent", (activity as HomeActivity).currentAddress!!, R.drawable.ic_location)
        (activity as HomeActivity).setBottomNavigationMenu(false, true, false, false, false)
        bindingObj.filterBtn.hide()

        enterAnimation = AnimationUtils.loadAnimation(getActivity(),R.anim.search_edit_anim);
        exitAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.search_edit_anim_exit);
        talentPresenter = TalentModel(activity as Activity, this)

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
                model_name = query


                apiCalling(city  , currentPage.toString() ,model_name , gender , category_id , experience_type ,false)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean
            {
                if(!model_name.equals("")&&query.equals(""))
                {
                    currentPage = PAGE_START
                    model_name = query
                    apiCalling(default_city, currentPage.toString(), model_name, gender, category_id, experience_type, false)
                }

                return false
            }
        })

    }

    fun  talentRecyclerSettings()
    {
        talentsAdapter = TalentsAdapter(activity , ArrayList())
        val gManager = GridLayoutManager(activity, 2)

        gManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup()
        {
            override fun getSpanSize(position: Int): Int {
                when (talentsAdapter!!.getItemViewType(position))
                {
                    talentsAdapter?.VIEW_TYPE_LOADING -> return 2
                    talentsAdapter?.VIEW_TYPE_NORMAL -> return 1
                    else -> return -1
                }
            }
        }

        bindingObj.talentRecycler.layoutManager = gManager
        val spanCount = 2 // 3 columns
        val spacing = 5 // 10px
        val includeEdge = true
        bindingObj.talentRecycler.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
        bindingObj.talentRecycler.adapter = talentsAdapter
    }

    private fun pagination()
    {
        bindingObj.talentRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleThreshold = 2
                val layoutManager = recyclerView.layoutManager as GridLayoutManager?
                val lastItem = layoutManager!!.findLastCompletelyVisibleItemPosition()
                val currentTotalCount = layoutManager.itemCount

                if (currentTotalCount <= lastItem + visibleThreshold && !isLoading) {
                    isLoading = true

                    //This condition is to check for whether is search pagination or normal pagination
                    if (city.isEmpty())
                    {
                        if (model_name.isEmpty())
                            apiCalling(default_city, currentPage.toString(), model_name, gender, category_id, experience_type, false)
                        else
                            apiCalling(city, currentPage.toString(), model_name, gender, category_id, experience_type, false)
                    }
                    else
                    {
                            apiCalling(city, currentPage.toString(), model_name, gender, category_id, experience_type, false)
                    }
                }
            }
        })
    }

    private fun apiCalling(city: String , page:String , model_name: String , gender:String , category_id : String , experience_type:String , isShowProgressbar:Boolean)
    {

        if(activity?.isNetworkActive()!!)
        {
            var ageRange =""
            if(rangeMinimumAgeVal.isNotEmpty())
                ageRange = rangeMinimumAgeVal+"-"+rangeMaximumAgeVal

            var heightRange =""
            if(rangeMinimumHeightVal.isNotEmpty())
                heightRange = rangeMinimumHeightVal+"-"+rangeMaximumHeightVal

            talentPresenter?.getTalentModels(city , page ,model_name , gender , category_id , experience_type ,isShowProgressbar,(activity as HomeActivity).sharedPreferences.getString(Constants.User_id).toString() ,ageRange , heightRange)
        }

        else
        {

            val dialog = CustomDialog(activity as Activity)
                    dialog.setTitle(resources.getString(R.string.no_internet_txt))
                    dialog.setMessage(resources.getString(R.string.connection_txt))
                    dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                        apiCalling(city ,page ,model_name , gender , category_id , experience_type , true)
                    })
                    dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })
                    dialog.show()
        }
    }

    fun addItem( list : MutableList<ModelsPojo.DataBean>)
    {
        if (currentPage != PAGE_START)
            talentsAdapter?.removeLoading()

        talentsAdapter?.addAll(list)
        talentsAdapter?.addLoading()
    }

    override fun onTalentsSuccess(model: ModelsPojo)
    {

        if(model_name.isNotEmpty())
        {

            if(city.isEmpty())
            bindingObj.SearchResultTxt.text = model_name
           else
                bindingObj.SearchResultTxt.text = model_name+" in "+city

            bindingObj.SearchResultTxt.visibility = View.VISIBLE
            bindingObj.SearchResultLayout.visibility = View.VISIBLE
        }

        else
        {
            bindingObj.SearchResultTxt.text =  ""
            bindingObj.SearchResultTxt.visibility = View.GONE
            bindingObj.SearchResultLayout.visibility = View.GONE
        }

        if (model.data?.size!=0)
        {
            //---- Hiding the search bar
            bindingObj.searchCardView.startAnimation(exitAnimation)
            bindingObj.searchCardView.visibility = View.GONE
            searchVisibilityFlag = false

            if(currentPage==PAGE_START)
            {
                Timber.e("current Page"+currentPage)
                talentsAdapter?.removeAll()
                talentsAdapter?.addAll(model.data!!)
                talentsAdapter?.addLoading()
            }
            else
                addItem(model.data!!)

            currentPage++
            isLoading = false
            bindingObj.emptyLayout.visibility  = View.GONE
            bindingObj.filterBtn.show()
        }
        else
        {
            if(currentPage!=PAGE_START)
                talentsAdapter?.removeLoading()

            else if(currentPage==PAGE_START)
            {
             bindingObj.emptyLayout.visibility  = View.VISIBLE
             //bindingObj.filterBtn.hide()
             talentsAdapter?.removeAll()
            }
        }
    }

    override fun onFailed(message: String)
    {
        isLoading =false
        showSnackbar(message)
    }

    override fun showProgress(visiblity: Boolean , isShowProgressbar: Boolean)
    {
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
        try
        {
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
        currentPage =PAGE_START
        isLoading = false
        talentsAdapter = TalentsAdapter(activity , ArrayList())
        bindingObj.talentRecycler.adapter = talentsAdapter


        bindingObj.searchView.setQuery("", false)
        bindingObj.searchView.clearFocus()
        model_name = ""
        gender = ""
        category_id = ""
        experience_type = ""
        city = ""
        default_city = (activity as HomeActivity).sharedPreferences.getString(Constants.CurrentAddress).toString()
        apiCalling(default_city , currentPage.toString()  ,model_name , gender , category_id , experience_type ,true)
    }

    override fun onClick(v: View?)
    {

        when(v?.id)
        {

            R.id.exploreBtn->
            {
                (activity as HomeActivity).startActivityForResult(Intent(activity as HomeActivity, LocationActivity::class.java ) ,LOCATIONREQUESTCODE)
            }

            R.id.filterBtn->
            {

                populateCategories()
                val bundle = Bundle()
                bundle.putStringArrayList("checklist" , experience_filter_check_list)
                bundle.putStringArrayList("gender_check_list" , gender_check_list)
                bundle.putIntegerArrayList("categoryidsList" , categoryidsList)
                bundle.putString("city" , city)
                bundle.putString("minAgeRange" ,rangeMinimumAgeVal)
                bundle.putString("maxAgeRange" , rangeMaximumAgeVal)
                bundle.putString("minHeightRange" , rangeMinimumHeightVal)
                bundle.putString("maxHeightRange" , rangeMaximumHeightVal)

                bundle.putString("fromScreen" , "Talent")
                startActivityForResult(Intent(activity as HomeActivity, TalentFilterActivity::class.java ).putExtras(bundle) ,FilterREQUESTCODE)
            }

            R.id.searchBtn->
            {
                if(!bindingObj.searchView.query.toString().equals(""))
                {
                    currentPage = PAGE_START
                    model_name = bindingObj.searchView.query.toString()
                    apiCalling(city, currentPage.toString(), model_name, gender, category_id, experience_type, true)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
                rangeMinimumAgeVal = bundle.getString("minAgeRange")!!
                rangeMaximumAgeVal = bundle.getString("maxAgeRange")!!
                rangeMinimumHeightVal = bundle.getString("minHeightRange")!!
                rangeMaximumHeightVal = bundle.getString("maxHeightRange")!!

                //Timber.e(experience_filter_check_list.toString())
                //Timber.e(categoryidsList.toString())

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


                Timber.e(category_id)
                Timber.e(gender)
                Timber.e(experience_type)

                currentPage =  PAGE_START
                model_name = ""
                bindingObj.searchView.setQuery("", false)
                bindingObj.searchView.clearFocus()
                bindingObj.SearchResultLayout.visibility = View.GONE
                bindingObj.SearchResultTxt.text = ""

                if(city.isEmpty())
                apiCalling(default_city  , currentPage.toString() ,model_name , gender , category_id , experience_type ,true)

              else
                    apiCalling(city  , currentPage.toString() ,model_name , gender , category_id , experience_type ,true)

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        talentPresenter.cancelRetrofitRequest()
    }

    fun populateCategories()
    {
        var map : HashMap<String ,String>  = HashMap()
        map.put("id" , "2")
        map.put("role" , "Model")
        categoryList.add(map)
        map.put("id" , "3")
        map .put("role" , "Photographer")
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
}
