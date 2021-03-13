package com.dazzlerr_usa.views.fragments.home


import android.app.Activity
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentHomeBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.GridSpacingItemDecoration
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.activities.home.LocationUpdate
import com.dazzlerr_usa.views.activities.location.LocationActivity
import com.dazzlerr_usa.views.fragments.home.adapters.*
import com.dazzlerr_usa.views.fragments.home.pojos.HomeCategoryPojo
import com.dazzlerr_usa.views.fragments.home.pojos.ModelsPojo
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActive

import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : androidx.fragment.app.Fragment(), View.OnClickListener , HomeView , LocationUpdate
{

    val LOCATIONREQUESTCODE = 100
    var featureAdapter: FeaturedAdapter? = null
    var topRatedAdapter: TopRatedAdapter? = null
    var castingcallAdapter: CastingCallAdapter? = null
    lateinit var categoryAdapter: CategoryAdapter
    lateinit var homePresenter: HomePresenter
    val PAGE_START = 1
    private var currentPageForFeatured = PAGE_START
    private var currentPageForTopRated = PAGE_START
    private var currentPageForCasting = PAGE_START
    private var isFeaturedLoading = false
    private var isTopratedLoading = false
    private var isCastingLoading = false
    var currentTab = 1 //  1 for featured , 2 for Top rated and 3 for trending

    internal lateinit var bindingObj: FragmentHomeBinding
    var categoryList: MutableList<HomeCategoryPojo.DataBean> = ArrayList()

    var mBannerList: List<HomeCategoryPojo.BannersBean> = ArrayList()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {

        // Inflate the layout for this fragment
        bindingObj = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        initializations()
        clickListeners()
        modelsRecyclerSettings()
        pagination()

        getCategoriesApi()
        tabManager()
        return bindingObj.root

    }

    private fun initializations()
    {
        homePresenter = HomeModel(activity as Activity, this)
        (activity as HomeActivity).titleSettings(resources.getColor(R.color.colorWhite), true, resources.getColor(R.color.colorBlack), resources.getColor(R.color.colorBlack), resources.getColor(R.color.colorBlack), "", (activity as HomeActivity).currentAddress!!, R.drawable.ic_location)
        (activity as HomeActivity).setBottomNavigationMenu(false, false, false, true, false)

        featureAdapter = FeaturedAdapter(activity, ArrayList())
        topRatedAdapter = TopRatedAdapter(activity, ArrayList())
        castingcallAdapter = CastingCallAdapter(activity, ArrayList())
        categoryAdapter = CategoryAdapter(activity, ArrayList())

        val spanCount = 2 // 3 columns
        val spacing = 5 // 10px
        val includeEdge = true
        bindingObj.recyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
        bindingObj.recyclerView.setItemAnimator(DefaultItemAnimator())

        bindingObj.categoryRecycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

    }

    private fun clickListeners()
    {
        bindingObj.exploreBtn.setOnClickListener(this)
    }

    fun modelsRecyclerSettings()
    {
        var gManager = GridLayoutManager(activity, 2)
        gManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup()
        {
            override fun getSpanSize(position: Int): Int
            {

                if(currentTab==1) {
                    when (featureAdapter!!.getItemViewType(position)) {
                        featureAdapter?.VIEW_TYPE_LOADING -> return 2
                        featureAdapter?.VIEW_TYPE_NORMAL -> return 1
                        else -> return -1
                    }
                }

                else if(currentTab==2) {
                    when (topRatedAdapter!!.getItemViewType(position)) {
                        topRatedAdapter?.VIEW_TYPE_LOADING -> return 2
                        topRatedAdapter?.VIEW_TYPE_NORMAL -> return 1
                        else -> return -1
                    }
                }

                else{
                    when (castingcallAdapter!!.getItemViewType(position)) {
                        castingcallAdapter?.VIEW_TYPE_LOADING -> return 2
                        castingcallAdapter?.VIEW_TYPE_NORMAL -> return 1
                        else -> return -1
                    }
                }
            }
        }

        bindingObj.recyclerView.layoutManager = gManager

    }

    private fun pagination()
    {
        bindingObj.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
            {
                super.onScrolled(recyclerView, dx, dy)

                val visibleThreshold = 2
                val layoutManager = recyclerView.layoutManager as GridLayoutManager?
                val lastItem = layoutManager!!.findLastCompletelyVisibleItemPosition()
                val currentTotalCount = layoutManager.itemCount

                if (currentTotalCount <= lastItem + visibleThreshold)
                {
                    if(!isFeaturedLoading && currentTab==1)
                    {
                        isFeaturedLoading = true
                        apiCalling("featured", (activity as HomeActivity).sharedPreferences.getString(Constants.CurrentAddress)!!, currentPageForFeatured.toString())
                    }
                    else if(!isTopratedLoading && currentTab==2)
                    {
                        isTopratedLoading = true
                        apiCalling("toprated" , (activity as HomeActivity).sharedPreferences.getString(Constants.CurrentAddress)!!, currentPageForTopRated.toString() )
                    }
                    else if(!isCastingLoading && currentTab==3)
                    {
                        isCastingLoading= true
                        apiCalling("trending" , (activity as HomeActivity).sharedPreferences.getString(Constants.CurrentAddress)!!, currentPageForCasting.toString() )
                    }
                }
            }
        })
    }

    private fun getCategoriesApi()
    {
        if(activity?.isNetworkActive()!!)
        {
            bindingObj.mainHomeLayout.visibility = View.GONE
            homePresenter?.getCategoriesProducts()

        }

        else
        {

            val dialog = CustomDialog(activity as Activity )
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {

               try {
                   getCategoriesApi()
               }
               catch (e:Exception)
               {
                   e.printStackTrace()
               }
            })

            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                dialog.dismiss()
            })
            dialog.show()
        }
    }
    private fun apiCalling(type : String, city: String , page:String)
    {

        if(activity?.isNetworkActive()!!)
        {

            if((activity as HomeActivity).sharedPreferences.getString(Constants.Is_Monetize).equals("true"))
            homePresenter?.getHomeModels((activity as HomeActivity).sharedPreferences.getString(Constants.User_id).toString()  , type ,city , page)
            else
                homePresenter?.getHomeModels((activity as HomeActivity).sharedPreferences.getString(Constants.User_id).toString() ,type ,"NA" , page)

        }

        else
        {

            val dialog = CustomDialog(activity as Activity )
                    dialog.setTitle(resources.getString(R.string.no_internet_txt))
                    dialog.setMessage(resources.getString(R.string.connection_txt))
                    dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                        apiCalling(type ,city ,page)
                    })

                    dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })
                    dialog.show()
        }
    }


    private fun tabManager()
    {
        bindingObj.tabLayout.addTab(bindingObj.tabLayout.newTab().setText("FEATURED"))
        bindingObj.tabLayout.addTab(bindingObj.tabLayout.newTab().setText("TOP RATED"))
        bindingObj.tabLayout.addTab(bindingObj.tabLayout.newTab().setText("TRENDING"))

        bindingObj.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener
        {

            override fun onTabSelected(tab: TabLayout.Tab)
            {

                if (tab.text!!.toString().equals("Featured", ignoreCase = true))
                {
                    bindingObj.emptyLayout.visibility = View.GONE
                    bindingObj.recyclerView.visibility = View.VISIBLE
                    currentTab = 1
                    modelsRecyclerSettings()
                    if (featureAdapter?.itemCount==0)
                    {
                        bindingObj.recyclerView.adapter = featureAdapter
                        apiCalling("featured", (activity as HomeActivity).sharedPreferences.getString(Constants.CurrentAddress)!!, currentPageForFeatured.toString())
                    }
                    else
                        bindingObj.recyclerView.adapter = featureAdapter

                }

                else if (tab.text!!.toString().equals("TOP RATED", ignoreCase = true))
                {
                    bindingObj.emptyLayout.visibility = View.GONE
                    bindingObj.recyclerView.visibility = View.VISIBLE
                    currentTab = 2
                    modelsRecyclerSettings()
                    if (topRatedAdapter?.itemCount==0)
                    {
                        bindingObj.recyclerView.adapter = topRatedAdapter
                        apiCalling("toprated" , (activity as HomeActivity).sharedPreferences.getString(Constants.CurrentAddress)!!, currentPageForTopRated.toString())
                    }
                    else
                        bindingObj.recyclerView.adapter = topRatedAdapter


                }

                else if (tab.text!!.toString().equals("TRENDING", ignoreCase = true))
                {
                    bindingObj.emptyLayout.visibility = View.GONE
                    bindingObj.recyclerView.visibility = View.VISIBLE
                    currentTab = 3
                    modelsRecyclerSettings()
                    if (castingcallAdapter?.itemCount==0)
                    {
                        bindingObj.recyclerView.adapter = castingcallAdapter
                        apiCalling("trending" , (activity as HomeActivity).sharedPreferences.getString(Constants.CurrentAddress)!!, currentPageForCasting.toString())

                    }
                    else
                        bindingObj.recyclerView.adapter = castingcallAdapter
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    fun addItem(type:String ,model: ModelsPojo)
    {
        if(type.equals("featured" ,ignoreCase = true))
        {
            if(currentPageForFeatured!=PAGE_START)
                featureAdapter?.removeLoading()

            featureAdapter?.addAll(model.data!!)
            featureAdapter?.addLoading()
            if(currentPageForFeatured==1)
            bindingObj.recyclerView.adapter = featureAdapter

            currentPageForFeatured +=1
            isFeaturedLoading = false
        }

        else if(type.equals("toprated" ,ignoreCase = true))
        {
            if(currentPageForTopRated!=PAGE_START)
                topRatedAdapter?.removeLoading()

            topRatedAdapter?.addAll(model.data!!)
            topRatedAdapter?.addLoading()

            if(currentPageForTopRated==1)
            bindingObj.recyclerView.adapter = topRatedAdapter

            currentPageForTopRated+=1
            isTopratedLoading =false
        }

        else if(type.equals("trending" ,ignoreCase = true))
        {
            if(currentPageForCasting!=PAGE_START)
                castingcallAdapter?.removeLoading()

            castingcallAdapter?.addAll(model.data!!)
            castingcallAdapter?.addLoading()

            if(currentPageForCasting==1)
            bindingObj.recyclerView.adapter = castingcallAdapter

            currentPageForCasting+=1
            isCastingLoading = false

        }
    }
    override fun onModelsSuccess(model: ModelsPojo, type: String)
    {

        if(model.data?.size!=0)
        {

            addItem(type , model)

        }
        else
        {

            if(type.equals("featured"))
            {
                if(currentPageForFeatured==PAGE_START)
                {
                    bindingObj.errorText.text = "No featured models found to explore!"
                    bindingObj.emptyLayout.visibility = View.VISIBLE
                    bindingObj.recyclerView.visibility = View.GONE
                }

                else
                featureAdapter?.removeLoading()
                }

            else if(type.equals("toprated"))
            {
                if(currentPageForTopRated==PAGE_START)
                {
                    bindingObj.errorText.text = "No top models found to explore!"
                    bindingObj.emptyLayout.visibility = View.VISIBLE
                    bindingObj.recyclerView.visibility = View.GONE
                }
           else
                topRatedAdapter?.removeLoading()
            }

            else if(type.equals("trending"))
                    {
                        if (currentPageForCasting == PAGE_START)
                        {
                            bindingObj.errorText.text = "No Trending models found to explore!"
                            bindingObj.emptyLayout.visibility = View.VISIBLE
                            bindingObj.recyclerView.visibility = View.GONE
                        }
                        else
                            castingcallAdapter?.removeLoading()
                    }
        }

    }

    override fun onGetCategory(model: HomeCategoryPojo) {


        bindingObj.mainHomeLayout.visibility = View.VISIBLE
        if(model.data?.size!=0)
        categoryList = model.data!!

        categoryAdapter = CategoryAdapter(activity, categoryList)
        bindingObj.categoryRecycler.adapter = categoryAdapter

        mBannerList = model.banners!!
        bindingObj.bannerSlider.setInterval(3000)
        bindingObj.bannerSlider.selectedSlidePosition = 1
        bindingObj.bannerSlider.setAdapter(MainSliderAdapter(activity as? Activity , mBannerList))

        apiCalling("featured" , (activity as HomeActivity).sharedPreferences.getString(Constants.CurrentAddress)!!, currentPageForFeatured.toString() )
    }

    override fun onFailed(message: String)
    {
        isFeaturedLoading = false
        isCastingLoading =false
        isTopratedLoading =false
        showSnackbar(message)
    }

    override fun showProgress(visiblity: Boolean ,page:String)
    {

        if(activity !=null)
        {
            if (visiblity && page.equals("1")) {
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


    override fun onClick(v: View) {

        when(v?.id)
        {
            R.id.exploreBtn->
            {
                (activity as HomeActivity).startActivityForResult(Intent(activity as HomeActivity , LocationActivity::class.java ) ,LOCATIONREQUESTCODE)
            }
        }
    }

    override fun onCurrentLocationUpdate()
    {
        currentPageForFeatured =PAGE_START
        currentPageForCasting = PAGE_START
        currentPageForTopRated = PAGE_START
        isTopratedLoading =false
        isCastingLoading =false
        isFeaturedLoading = false
        featureAdapter = FeaturedAdapter(activity, ArrayList())
        topRatedAdapter = TopRatedAdapter(activity, ArrayList())
        castingcallAdapter = CastingCallAdapter(activity, ArrayList())
        bindingObj.tabLayout.getTabAt(0)!!.select()
        bindingObj.recyclerView.adapter = featureAdapter
        bindingObj.emptyLayout.visibility = View.GONE
        bindingObj.recyclerView.visibility = View.VISIBLE
        apiCalling("featured" , (activity as HomeActivity).sharedPreferences.getString(Constants.CurrentAddress)!!, currentPageForFeatured.toString() )
    }

    override fun onDestroy()
    {
        super.onDestroy()
        homePresenter.cancelRetrofitRequest()
    }
}
