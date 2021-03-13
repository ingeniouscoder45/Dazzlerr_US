package com.dazzlerr_usa.views.activities.events.activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityEventListBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.activities.events.adapters.EventsListAdapter
import com.dazzlerr_usa.views.activities.events.models.EventsListModel
import com.dazzlerr_usa.views.activities.events.pojos.EventsListPojo
import com.dazzlerr_usa.views.activities.events.presenters.EventsListPresenter
import com.dazzlerr_usa.views.activities.events.views.EventsListView
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject
import kotlin.collections.ArrayList

class EventListActivity : AppCompatActivity() , View.OnClickListener , EventsListView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var bindingObj:ActivityEventListBinding
    lateinit var mPresenter: EventsListPresenter
    var mFeaturedList: MutableList<EventsListPojo.ResultBean>? = ArrayList()
    var mPopularList: MutableList<EventsListPojo.ResultBean>? = ArrayList()
    var mRecentList: MutableList<EventsListPojo.ResultBean>? = ArrayList()

    override fun onResume() {
        super.onResume()

        apiCalling(1)//type 1 for featured events
        apiCalling(2)//type 1 for popular events
        apiCalling(3)//type 1 for recent events
    }
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_event_list)
        initializations()
        clickListeners()
    }

    fun initializations()
    {

        (application as MyApplication).myComponent.inject(this)
        showAds()

        bindingObj.titleLayout.titletxt.text = "Events"
        mPresenter = EventsListModel(this ,this)

        bindingObj.titleLayout.righttxt.visibility = View.VISIBLE
        bindingObj.titleLayout.righttxt.text = "Browse All"

        bindingObj.featuredEventsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        bindingObj.popularEventsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        bindingObj.recentEventsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    fun showAds()
    {

        //Initializing Google ads
/*
        val testDeviceIds: List<String> = Arrays.asList("A86FED701CDC6E77D341DEBA488306E3")
        val configuration: RequestConfiguration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
*/


        if(sharedPreferences.getString(Constants.Membership_id).equals("")
                ||sharedPreferences.getString(Constants.Membership_id).equals("0")
                ||sharedPreferences.getString(Constants.Membership_id).equals("1")
                ||sharedPreferences.getString(Constants.Membership_id).equals("6"))
        {
            //MobileAds.initialize(this) {}
            val adRequest = AdRequest.Builder().build()
            bindingObj.adView1.loadAd(adRequest)
            bindingObj.adView1.visibility = View.VISIBLE

            val adRequest1 = AdRequest.Builder().build()
            bindingObj.adView2.loadAd(adRequest1)
            bindingObj.adView2.visibility = View.VISIBLE

            val adRequest2 = AdRequest.Builder().build()
            bindingObj.adView3.loadAd(adRequest2)
            bindingObj.adView3.visibility = View.VISIBLE
            //--------------------------
        }
    }
    private fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.titleLayout.righttxt.setOnClickListener(this)
        bindingObj.browseAllEventsBtn.setOnClickListener(this)
        bindingObj.featuredEventsBtn.setOnClickListener(this)
        bindingObj.popularEventsBtn.setOnClickListener(this)
        bindingObj.recentEventsBtn.setOnClickListener(this)
        bindingObj.promoteEventImage.setOnClickListener(this)
    }

    @Synchronized
    private fun apiCalling(type:Int)
    {

        bindingObj.mainLayout.visibility = View.GONE
        if(isNetworkActive()!!)
        {
            mPresenter?.getEventslist(type, "1")
        }

        else
        {

            AlertDialog.Builder(this@EventListActivity)
                    .setTitle("No internet Connection!")
                    .setMessage("Please connect to Mobile network or WiFi.")
                    .setPositiveButton("Retry", DialogInterface.OnClickListener { dialog, which ->
                        apiCalling(type)
                    })

                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    })
                    .show()

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

            R.id.righttxt->
            {
                startActivity(Intent(this@EventListActivity , EventsActivity::class.java))
            }

            R.id.promoteEventImage->
            {
                startActivity(Intent(this@EventListActivity , PromoteEventActivity::class.java))
            }

            R.id.browseAllEventsBtn->
            {
                startActivity(Intent(this@EventListActivity , EventsActivity::class.java))
            }

            R.id.featuredEventsBtn->
            {
                val bundle = Bundle()
                bundle.putString("type" , "Featured")
                startActivity(Intent(this@EventListActivity , AllOtherEventsActivity::class.java).putExtras(bundle))
            }

            R.id.popularEventsBtn->
            {
                val bundle = Bundle()
                bundle.putString("type" , "Popular")
                startActivity(Intent(this@EventListActivity , AllOtherEventsActivity::class.java).putExtras(bundle))
            }

            R.id.recentEventsBtn->
            {
                val bundle = Bundle()
                bundle.putString("type" , "Latest")
                startActivity(Intent(this@EventListActivity , AllOtherEventsActivity::class.java).putExtras(bundle))
            }
        }
    }


    override fun onSuccess(model: EventsListPojo, type:Int)
    {
        bindingObj.mainLayout.visibility = View.VISIBLE
        if(type==1) {

            Glide.with(this@EventListActivity)
                    .load("https://www.dazzlerr.com/API/"+model.promotion_banner)
                    .apply(RequestOptions().centerCrop().fitCenter())
                    .thumbnail(0.3f)
                    .into(bindingObj.promoteEventImage)

            if(model.result?.size==0) {
                bindingObj.featuredLayout.visibility = View.GONE
                bindingObj.adView1.visibility = View.GONE

            }

            else {
                bindingObj.featuredLayout.visibility = View.VISIBLE
                if(sharedPreferences.getString(Constants.Membership_id).equals("")
                        ||sharedPreferences.getString(Constants.Membership_id).equals("0")
                        ||sharedPreferences.getString(Constants.Membership_id).equals("1")
                        ||sharedPreferences.getString(Constants.Membership_id).equals("6")
                ) {
                    bindingObj.adView1.visibility = View.VISIBLE
                }
            }

            mFeaturedList = model.result
            populateDataAdapters(1)
        }
        else if(type==2) {
            mPopularList = model.result
            populateDataAdapters(2)

            if (model.result?.size == 0) {
                bindingObj.popularLayout.visibility = View.GONE
                bindingObj.adView2.visibility = View.GONE
            } else {
                bindingObj.popularLayout.visibility = View.VISIBLE
                if(sharedPreferences.getString(Constants.Membership_id).equals("")
                        ||sharedPreferences.getString(Constants.Membership_id).equals("0")
                        ||sharedPreferences.getString(Constants.Membership_id).equals("1")
                        ||sharedPreferences.getString(Constants.Membership_id).equals("6")
                ) {
                    bindingObj.adView2.visibility = View.VISIBLE
                }
            }
        }

        else if(type==3)
        {
            mRecentList = model.result
            populateDataAdapters(3)

            if(model.result?.size==0) {
                bindingObj.latestLayout.visibility = View.GONE
                bindingObj.adView3.visibility = View.GONE
            }

            else {
                bindingObj.latestLayout.visibility = View.VISIBLE

                if(sharedPreferences.getString(Constants.Membership_id).equals("")
                        ||sharedPreferences.getString(Constants.Membership_id).equals("0")
                        ||sharedPreferences.getString(Constants.Membership_id).equals("1")
                        ||sharedPreferences.getString(Constants.Membership_id).equals("6")
                ) {
                    bindingObj.adView3.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }


    override fun showProgress(visiblity: Boolean)
    {
        if (visiblity)
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

    fun populateDataAdapters( type: Int)
    {

        if(type==1)
        {
            bindingObj.featuredEventsRecycler.adapter = EventsListAdapter(this , mFeaturedList!! , "featured")
        }

        else if(type==2)
        {
            bindingObj.popularEventsRecycler.adapter = EventsListAdapter(this , mPopularList!! , "popular")
        }

        else if(type==3)
        {
            bindingObj.recentEventsRecycler.adapter = EventsListAdapter(this , mRecentList!! , "recent")
        }

    }
}
