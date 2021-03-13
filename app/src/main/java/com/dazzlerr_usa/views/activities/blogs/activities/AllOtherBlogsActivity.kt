package com.dazzlerr_usa.views.activities.blogs.activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityAllOtherBlogsBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.views.activities.blogs.adapters.OtherBlogsAdapter
import com.dazzlerr_usa.views.activities.blogs.models.BlogsListModel
import com.dazzlerr_usa.views.activities.blogs.pojos.BlogListPojo
import com.dazzlerr_usa.views.activities.blogs.presenter.BlogsListPresenter
import com.dazzlerr_usa.views.activities.blogs.views.BlogListView
import com.google.android.material.snackbar.Snackbar

class AllOtherBlogsActivity : AppCompatActivity(), View.OnClickListener , BlogListView
{

    lateinit var bindingObj: ActivityAllOtherBlogsBinding
    lateinit var mPresenter: BlogsListPresenter
    var type:String = ""
    var cat_id =""
    lateinit var mAdapter:OtherBlogsAdapter
    val PAGE_START = 1
    var currentPage = PAGE_START
    var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_all_other_blogs)
        initializations()
        clickListeners()
        blogsRecyclerSettings()
        pagination()
        apiCalling(currentPage)
    }

    fun initializations()
    {
        type = intent.extras?.getString("type").toString()

        if(intent.extras?.containsKey("cat_id")!!)
        cat_id = intent.extras?.getString("cat_id").toString()

        bindingObj.titleLayout.titletxt.text = type+" Posts"
        bindingObj.titleLayout.rightbtn.visibility = View.VISIBLE
        bindingObj.titleLayout.rightbtn.setImageResource(R.drawable.ic_search)

        mPresenter = BlogsListModel(this , this)
    }

    fun blogsRecyclerSettings()
    {
        mAdapter = OtherBlogsAdapter(this , ArrayList())
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
            mPresenter?.getBlogslist(1, "" ,page.toString())

            else if(type.equals("Popular",ignoreCase = true))
                        mPresenter?.getBlogslist(2, "" ,page.toString())

            else if(type.equals("Recent",ignoreCase = true))
                        mPresenter?.getBlogslist(3, "" ,page.toString())

            else if(type.equals("Similar",ignoreCase = true))
                        mPresenter?.getBlogslist(4, cat_id ,page.toString())

        }

        else
        {

            AlertDialog.Builder(this@AllOtherBlogsActivity)
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

    fun addItem( list : MutableList< BlogListPojo.ResultBean>)
    {
        if (currentPage != PAGE_START)
            mAdapter.removeLoading()

        mAdapter.addAll(list)
        mAdapter.addLoading()

    }

    override fun onSuccess(model: BlogListPojo, type: Int)
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

            R.id.rightbtn -> {
                startActivity(Intent(this@AllOtherBlogsActivity, SearchBlogsActivity::class.java))
            }

        }
    }

}
