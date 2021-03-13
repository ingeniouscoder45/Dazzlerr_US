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
import com.dazzlerr_usa.databinding.ActivityBlogsByTagsBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.views.activities.blogs.adapters.CommonBlogsAdapter
import com.dazzlerr_usa.views.activities.blogs.models.AllCategoryBlogsModel
import com.dazzlerr_usa.views.activities.blogs.pojos.AllCategoryBlogsPojo
import com.dazzlerr_usa.views.activities.blogs.pojos.BlogCategoriesPojo
import com.dazzlerr_usa.views.activities.blogs.presenter.AllCategoryBlogsPresenter
import com.dazzlerr_usa.views.activities.blogs.views.AllCategoryBlogsView
import com.google.android.material.snackbar.Snackbar

class BlogsByTagsActivity : AppCompatActivity() , AllCategoryBlogsView , View.OnClickListener{


    lateinit var bindingObj: ActivityBlogsByTagsBinding
    lateinit var mPresenter: AllCategoryBlogsPresenter
    lateinit var mAdapter : CommonBlogsAdapter

    val mBlogsList: MutableList<AllCategoryBlogsPojo.Result> ?= ArrayList()
    val PAGE_START = 1
    var currentPage = PAGE_START
    var isLoading = false
    var TAG_ID = ""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this ,R.layout.activity_blogs_by_tags)
        initializations()
        clickListeners()
        blogsRecyclerSettings()
        pagination()
        apiCalling(currentPage)
    }

    fun initializations()
    {
        bindingObj.titleLayout.titletxt.text = intent.extras?.getString("tag_name")!!.toUpperCase()
        bindingObj.titleLayout.rightbtn.visibility = View.VISIBLE
        bindingObj.titleLayout.rightbtn.setImageResource(R.drawable.ic_search)

        if(intent.extras?.containsKey("tag_id")!!)
            TAG_ID = intent.extras?.getString("tag_id")!!

        mPresenter  = AllCategoryBlogsModel(this@BlogsByTagsActivity, this)
    }

    fun blogsRecyclerSettings()
    {
        mAdapter = CommonBlogsAdapter(this , ArrayList())
        val gManager = LinearLayoutManager(this)
        bindingObj.tagsBlogsRecyclerVIew.layoutManager = gManager
        bindingObj.tagsBlogsRecyclerVIew.adapter = mAdapter
    }

    private fun pagination()
    {
        bindingObj.tagsBlogsRecyclerVIew.addOnScrollListener(object : RecyclerView.OnScrollListener()
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


                mPresenter?.getAllCategoryBlogs("", TAG_ID,"" ,page.toString())

        }

        else
        {

            AlertDialog.Builder(this@BlogsByTagsActivity)
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

    fun addItem( list : MutableList< AllCategoryBlogsPojo.Result>)
    {
        if (currentPage != PAGE_START)
            mAdapter.removeLoading()

        mAdapter.addAll(list)
        mAdapter.addLoading()

    }

    override fun onSuccess(model: AllCategoryBlogsPojo)
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

    override fun onGetCategories(model: BlogCategoriesPojo) {
        //Not in use
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
                startActivity(Intent(this@BlogsByTagsActivity, SearchBlogsActivity::class.java))
            }

        }
    }

}
