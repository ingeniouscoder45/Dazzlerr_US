package com.dazzlerr_usa.views.activities.events.activities

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityAllOtherEventsBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.views.activities.events.adapters.OtherEventsAdapter
import com.dazzlerr_usa.views.activities.events.models.EventsListModel
import com.dazzlerr_usa.views.activities.events.pojos.EventsListPojo
import com.dazzlerr_usa.views.activities.events.presenters.EventsListPresenter
import com.dazzlerr_usa.views.activities.events.views.EventsListView
import com.google.android.material.snackbar.Snackbar

class AllOtherEventsActivity : AppCompatActivity() ,View.OnClickListener  , EventsListView{

    lateinit var bindingObj:ActivityAllOtherEventsBinding
    lateinit var mPresenter: EventsListPresenter
    var type:String = ""
    var cat_id =""
    lateinit var mAdapter: OtherEventsAdapter
    val PAGE_START = 1
    var currentPage = PAGE_START
    var isLoading = false


    override fun onResume() {
        super.onResume()

        apiCalling(currentPage)
    }
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this ,R.layout.activity_all_other_events)
        initializations()
        clickListeners()
        blogsRecyclerSettings()
        pagination()

    }


    fun initializations()
    {
        type =intent.extras?.getString("type")!!

        if(intent.extras?.containsKey("cat_id")!!)
            cat_id = intent.extras?.getString("cat_id")!!

        bindingObj.titleLayout.titletxt.text = type+" Events"

        mPresenter = EventsListModel(this , this)
    }

    fun blogsRecyclerSettings()
    {
        mAdapter = OtherEventsAdapter(this , ArrayList())
        val gManager = LinearLayoutManager(this)
        bindingObj.otherBlogsRecyclerVIew.layoutManager = gManager
        bindingObj.otherBlogsRecyclerVIew.adapter = mAdapter
    }

    private fun pagination()
    {
        bindingObj.otherBlogsRecyclerVIew.addOnScrollListener(object : RecyclerView.OnScrollListener()
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
                    apiCalling(currentPage)
                }
            }
        })
    }

    private fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.titleLayout.rightbtn.setOnClickListener(this)
    }

    private fun apiCalling(page:Int)
    {

        if(isNetworkActive()!!)
        {

            if(type.equals("Featured",ignoreCase = true))
                mPresenter?.getEventslist(1,  page.toString())

            else if(type.equals("Popular",ignoreCase = true))
                mPresenter?.getEventslist(2 ,page.toString())

            else if(type.equals("Latest",ignoreCase = true))
                mPresenter?.getEventslist(3, page.toString())

        }

        else
        {

            AlertDialog.Builder(this@AllOtherEventsActivity)
                    .setTitle("No internet Connection!")
                    .setMessage("Please connect to Mobile network or WiFi.")
                    .setPositiveButton("Retry", DialogInterface.OnClickListener { dialog, which ->
                        apiCalling(page)
                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    })
                    .show()
        }
    }

    fun addItem( list : MutableList< EventsListPojo.ResultBean>)
    {
        if (currentPage != PAGE_START)
            mAdapter.removeLoading()

        mAdapter.addAll(list)
        mAdapter.addLoading()

    }
    override fun onSuccess(model: EventsListPojo, type: Int)
    {

        if (model.result?.size!=0)
        {

            if(currentPage==PAGE_START)
            {
                mAdapter.removeAllItems()
                mAdapter.addAll(model.result!!)
                mAdapter.addLoading()
            }
            else
                addItem(model.result!!)

            currentPage++
            isLoading = false

        }
        else
        {
            if(currentPage!=1)
                mAdapter.removeLoading()

        }
    }

    override fun onFailed(message: String)
    {
        isLoading =false
        showSnackbar(message)
    }

    override fun showProgress(visiblity: Boolean)
    {
        if(currentPage==1)
        {
            if (visiblity)
                startProgressBarAnim()
            else
                stopProgressBarAnim()
        }
    }

    fun startProgressBarAnim()
    {
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

        when (v?.id) {
            R.id.leftbtn -> {
                finish()
            }


        }
    }

}
