package com.dazzlerr_usa.views.activities.blogs.activities

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivitySearchBlogsBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.views.activities.blogs.adapters.CommonBlogsAdapter
import com.dazzlerr_usa.views.activities.blogs.models.AllCategoryBlogsModel
import com.dazzlerr_usa.views.activities.blogs.pojos.AllCategoryBlogsPojo
import com.dazzlerr_usa.views.activities.blogs.pojos.BlogCategoriesPojo
import com.dazzlerr_usa.views.activities.blogs.presenter.AllCategoryBlogsPresenter
import com.dazzlerr_usa.views.activities.blogs.views.AllCategoryBlogsView
import com.google.android.material.snackbar.Snackbar

class SearchBlogsActivity : AppCompatActivity(), AllCategoryBlogsView, View.OnClickListener
{

    lateinit var bindingObj: ActivitySearchBlogsBinding

    lateinit var mPresenter: AllCategoryBlogsPresenter
    lateinit var mAdapter : CommonBlogsAdapter

    val mBlogsList: MutableList<AllCategoryBlogsPojo.Result> ?= ArrayList()
    val PAGE_START = 1
    var currentPage = PAGE_START
    var isLoading = false
    var SearchStr = ""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj= DataBindingUtil.setContentView(this , R.layout.activity_search_blogs)
        initializations()
        clickListeners()
        blogsRecyclerSettings()
        searchView()
        pagination()
    }

    fun initializations()
    {
        mPresenter  = AllCategoryBlogsModel(this , this)
    }

    private fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.titleLayout.searchBtn.setOnClickListener(this)
    }

    fun blogsRecyclerSettings()
    {
        mAdapter = CommonBlogsAdapter(this , mBlogsList!!)
        val gManager = LinearLayoutManager(this)
        bindingObj.searchBlogsRecyclerView.layoutManager = gManager
        bindingObj.searchBlogsRecyclerView.adapter = mAdapter
    }

    private fun searchView()
    {
        val searchIcon = bindingObj.titleLayout.searchView.findViewById(androidx.appcompat.R.id.search_mag_icon) as ImageView
        searchIcon.visibility = View.GONE

        bindingObj.titleLayout.searchView.queryHint = "Search for blogs..."
        bindingObj.titleLayout.searchView.setQuery(SearchStr , false)
        // listening to search query text change

        bindingObj.titleLayout.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(query: String): Boolean
            {
                SearchStr = query
                currentPage = PAGE_START
                apiCalling(currentPage)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean
            {
                return false
            }
        })

    }

    private fun pagination()
    {
        bindingObj.searchBlogsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener()
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

    fun apiCalling(page:Int)
    {
        if(isNetworkActive())
        {
            mPresenter.getAllCategoryBlogs("" ,"",SearchStr,page.toString())
        }

        else
        {

            AlertDialog.Builder(this)
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

            bindingObj.emptyCartLayout.visibility = View.GONE
            bindingObj.searchBlogsRecyclerView.visibility = View.VISIBLE
        }
        else
        {
            if(currentPage!=1)
                mAdapter.removeLoading()

            else if(currentPage==1)
            {
                bindingObj.emptyCartLayout.visibility = View.VISIBLE
                bindingObj.searchBlogsRecyclerView.visibility = View.GONE

            }
        }
    }

    override fun onFailed(message: String) {
        isLoading =false
        showSnackbar(message)
    }


    override fun onGetCategories(model: BlogCategoriesPojo)
    {
        // Not in use
    }



    override fun showProgress(visibility: Boolean)
    {
        if (visibility)
            startProgressBarAnim()
        else
            stopProgressBarAnim()
    }

    fun startProgressBarAnim()
    {
        bindingObj.aviProgressBar.visibility = View.VISIBLE
    }

    fun stopProgressBarAnim()
    {
        bindingObj.aviProgressBar.visibility = View.GONE
    }

    fun showSnackbar(message: String)
    {
        try {
            val parentLayout = findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout!!, message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {

        when(v?.id)
        {
            R.id.leftbtn ->
            {
                onBackPressed()
            }

            R.id.searchBtn->
            {
                hideSoftKeyboard()
                SearchStr = bindingObj.titleLayout.searchView.query.toString()
                currentPage = PAGE_START
                apiCalling(currentPage)
            }

        }
    }


    private fun hideSoftKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive) {
            if (this@SearchBlogsActivity.currentFocus != null) {
                inputMethodManager.hideSoftInputFromWindow(this@SearchBlogsActivity.currentFocus!!.windowToken, 0)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        hideSoftKeyboard()
        finish()
    }
    override fun onDestroy() {
        super.onDestroy()
        mPresenter.cancelRetrofitRequest()
    }
}
