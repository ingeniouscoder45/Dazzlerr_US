package com.dazzlerr_usa.views.activities.events.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.databinding.ActivityEventsBinding
import java.util.*
import com.dazzlerr_usa.R
import com.dazzlerr_usa.utilities.GridSpacingItemDecoration
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.views.activities.events.EventsAdapter
import com.dazzlerr_usa.views.activities.events.models.EventsModel
import com.dazzlerr_usa.views.activities.events.pojos.EventsPojo
import com.dazzlerr_usa.views.activities.events.presenters.EventsPresenter
import com.dazzlerr_usa.views.activities.events.views.EventsView
import timber.log.Timber
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList


class EventsActivity : AppCompatActivity() , View.OnClickListener , EventsView
{

    lateinit var mEventAdapter: EventsAdapter
    lateinit var mPresenter: EventsPresenter
    lateinit var bindingObj: ActivityEventsBinding
    var calendar : Calendar ?= null
    var df : SimpleDateFormat ? = null
    var formattedDate =""
    var currentMonth = 0
    var currentYear = 0
    val PAGE_START = 1
    private var currentPage = PAGE_START
    private var isLoading = false
    var eventImageList: MutableList<EventsPojo.EventImagesBean> = ArrayList()
    var eventVenueList: MutableList<EventsPojo.EventVenueBean> = ArrayList()

    var enterAnimation: Animation? = null
    var exitAnimation: Animation? = null
    var searchVisibilityFlag = false

    var mSearchStr = ""
    var mCategoryId =""
    var mCity = ""
    var mOrganizer = ""
    var mVenue = ""
    var FilterREQUESTCODE = 1001


    override fun onResume() {
        super.onResume()
        apiCalling(currentPage.toString() ,true)
    }
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_events)
        initializations()
        searchView()
        eventRecyclerSettings()
        clickListeners()
        pagination()

    }

    fun initializations()
    {
        bindingObj.titleLayout.titletxt.text = "All Events"
        bindingObj.titleLayout.rightbtn.visibility = View.VISIBLE

        bindingObj.titleLayout.rightbtn.setImageResource(R.drawable.ic_search)

        mPresenter = EventsModel(this, this)

        calendar = Calendar.getInstance()
        currentMonth = calendar?.get(Calendar.MONTH)!!+1
        currentYear = calendar?.get(Calendar.YEAR) as Int
        df = SimpleDateFormat("MMMM yyyy")
        formattedDate = df?.format(calendar?.getTime()).toString()
        bindingObj.currentMonthTxt.setText(formattedDate)
        enterAnimation = AnimationUtils.loadAnimation(this@EventsActivity,R.anim.search_edit_anim);
        exitAnimation = AnimationUtils.loadAnimation(this@EventsActivity, R.anim.search_edit_anim_exit);
    }

    fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.titleLayout.rightbtn.setOnClickListener(this)
        bindingObj.previousMonthTxt.setOnClickListener(this)
        bindingObj.nextMonthTxt.setOnClickListener(this)
        bindingObj.filterBtn.setOnClickListener(this)
        bindingObj.searchBtn.setOnClickListener(this)
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
        bindingObj.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(query: String): Boolean
            {
                currentPage=PAGE_START
                mSearchStr = query
                apiCalling(currentPage.toString() , true)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean
            {

                if(!mSearchStr.equals("")&&query.equals(""))
                {
                    currentPage = PAGE_START
                    mSearchStr = query
                    apiCalling(currentPage.toString() , false)

                }

                return false
            }
        })

    }

    fun eventRecyclerSettings()
    {
        mEventAdapter = EventsAdapter(this@EventsActivity, ArrayList())
        val gManager = LinearLayoutManager(this@EventsActivity)

        bindingObj.eventsRecyclerView.layoutManager = gManager
        val spanCount = 2 // 3 columns
        val spacing = 25 // 50px
        val includeEdge = true
        bindingObj.eventsRecyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
        bindingObj.eventsRecyclerView.adapter = mEventAdapter
    }

    private fun pagination()
    {
        bindingObj.eventsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleThreshold = 2

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val lastItem = layoutManager!!.findLastCompletelyVisibleItemPosition()
                val currentTotalCount = layoutManager.itemCount

                if (currentTotalCount <= lastItem + visibleThreshold && !isLoading)
                {
                    isLoading = true
                    apiCalling( currentPage.toString() ,false)
                }
            }
        })
    }

    private fun apiCalling(page:String, isShowProgressbar:Boolean)
    {

        if(this@EventsActivity.isNetworkActive())
        {
            mPresenter.getEvents(page, ""+currentMonth , ""+currentYear ,mSearchStr ,mCategoryId.toString() , mCity , mVenue , mOrganizer, isShowProgressbar)
        }

        else
        {

            val dialog  = CustomDialog(this@EventsActivity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                        apiCalling(page, true)
                    })

                    dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })
                    dialog.show()

        }
    }

    fun addItem(list : MutableList<EventsPojo.ResultBean>, eventImageList: MutableList<EventsPojo.EventImagesBean>, eventVenueList: MutableList<EventsPojo.EventVenueBean>)
    {
        if (currentPage != PAGE_START)
            mEventAdapter?.removeLoading()

        mEventAdapter?.addAll(list ,eventImageList ,eventVenueList)
        mEventAdapter?.addLoading() }


    override fun onSuccess(model: EventsPojo)
    {
        if (model.result?.size!=0)
        {

            hideSoftKeyboard()
            bindingObj.emptyLayout.visibility = View.GONE
            bindingObj.eventsRecyclerView.visibility = View.VISIBLE
            this.eventImageList.clear()
            this.eventVenueList.clear()
            this.eventImageList = model?.event_images?.get(0)!!
            this.eventVenueList = model?.event_venue!!

            if(currentPage==PAGE_START)
            {
                Timber.e("current Page"+currentPage)
                mEventAdapter?.removeAll()
                mEventAdapter?.addAll(model.result!! ,eventImageList ,eventVenueList)
                mEventAdapter?.addLoading()
            }
            else
                addItem(model.result!! ,eventImageList ,eventVenueList)

            currentPage++
            isLoading = false
        }

        else
        {
            if(currentPage!=PAGE_START)
                mEventAdapter?.removeLoading()

            else if(currentPage==PAGE_START)
            {
                bindingObj.emptyLayout.visibility = View.VISIBLE
                bindingObj.eventsRecyclerView.visibility = View.GONE
                mEventAdapter?.removeAll()
            }

        }
    }

    override fun onFailed(message: String) {
        isLoading =false
        showSnackbar(message)
    }


    override fun showProgress(visiblity: Boolean , isShowProgressbar: Boolean) {

        if(isShowProgressbar) {
            if (visiblity)
                startProgressBarAnim()
            else
                stopProgressBarAnim()
        }
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

            R.id.leftbtn-> finish()

            R.id.previousMonthTxt->
            {
                calendar?.add(Calendar.MONTH, -1)

                formattedDate = df?.format(calendar?.getTime()).toString()

                currentMonth = calendar?.get(Calendar.MONTH)!!+1
                currentYear = calendar?.get(Calendar.YEAR) as Int
              //  Toast.makeText(this@EventsActivity , "Month "+currentMonth+" Year "+ currentYear, Toast.LENGTH_SHORT).show()
                bindingObj.currentMonthTxt.setText(formattedDate)

                currentPage =1
                apiCalling(currentPage.toString() ,true)
            }

            R.id.nextMonthTxt->
            {
                calendar?.add(Calendar.MONTH, 1)
                formattedDate = df?.format(calendar?.getTime()).toString()
                currentMonth = calendar?.get(Calendar.MONTH)!!+1
                currentYear = calendar?.get(Calendar.YEAR) as Int
              //  Toast.makeText(this@EventsActivity , "Month "+currentMonth+" Year "+currentYear, Toast.LENGTH_SHORT).show()
                bindingObj.currentMonthTxt.setText(formattedDate)
                currentPage =1
                apiCalling(currentPage.toString() ,true)
            }

            R.id.rightbtn->
            {

                if(!searchVisibilityFlag)
                {
                    bindingObj.searchCardView.visibility = View.VISIBLE
                    bindingObj.searchCardView.startAnimation(enterAnimation)
                    searchVisibilityFlag = true
                }
            }

            R.id.filterBtn->
            {

                var bundle = Bundle()
                bundle.putString("mCategoryId",mCategoryId)
                bundle.putString("mVenue",mVenue)
                bundle.putString("mCity",mCity)
                bundle.putString("mOrganizer",mOrganizer)
                startActivityForResult(Intent(this@EventsActivity , EventsFilterActivity::class.java).putExtras(bundle) ,FilterREQUESTCODE)
            }

            R.id.searchBtn->
            {
                if(!bindingObj.searchView.query.toString().equals(""))
                {
                    currentPage = PAGE_START
                    mSearchStr = bindingObj.searchView.query.toString()
                    apiCalling(currentPage.toString(), true)
                }
            }
        }

    }

    private fun hideSoftKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive) {
            if (this@EventsActivity.currentFocus != null) {
                inputMethodManager.hideSoftInputFromWindow(this@EventsActivity.currentFocus!!.windowToken, 0)
            }
        }
    }

    fun hideSearchLayout()
    {
        bindingObj.searchCardView.startAnimation(exitAnimation)
        bindingObj.searchCardView.visibility = View.GONE
        searchVisibilityFlag = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==FilterREQUESTCODE)
        {
            if( data?.extras!=null)
            {
                var bundle: Bundle = data?.extras!!
                mCategoryId = bundle.getString("mCategoryId")!!
                mCity = bundle.getString("mCity")!!
                mVenue = bundle.getString("mVenue")!!
                mOrganizer = bundle.getString("mOrganizer")!!

                currentPage =  PAGE_START
                mSearchStr = ""
                bindingObj.searchView.setQuery("", false)
                bindingObj.searchView.clearFocus()
                apiCalling(currentPage.toString() ,true)
            }
        }
    }
}
