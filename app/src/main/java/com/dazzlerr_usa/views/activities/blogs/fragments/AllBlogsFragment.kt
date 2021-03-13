package com.dazzlerr_usa.views.activities.blogs.fragments


import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentAllBlogsBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.views.activities.blogs.activities.AllCategoryBlogsActivity
import com.dazzlerr_usa.views.activities.blogs.adapters.AllBlogsAdapter
import com.dazzlerr_usa.views.activities.blogs.models.AllCategoryBlogsModel
import com.dazzlerr_usa.views.activities.blogs.pojos.AllCategoryBlogsPojo
import com.dazzlerr_usa.views.activities.blogs.pojos.BlogCategoriesPojo
import com.dazzlerr_usa.views.activities.blogs.presenter.AllCategoryBlogsPresenter
import com.dazzlerr_usa.views.activities.blogs.views.AllCategoryBlogsView
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 */
class AllBlogsFragment : Fragment() , AllCategoryBlogsView
{

    lateinit var bindingObj: FragmentAllBlogsBinding
    lateinit var mPresenter: AllCategoryBlogsPresenter
    lateinit var mAdapter : AllBlogsAdapter

    val mBlogsList: MutableList<AllCategoryBlogsPojo.Result> ?= ArrayList()
    val PAGE_START = 1
    var currentPage = PAGE_START
    var isLoading = false
    var cat_name =""
    var cat_id = ""
    var tag_id = ""
    var search_str = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        bindingObj = DataBindingUtil.inflate(inflater , R.layout.fragment_all_blogs, container, false)
        initializations()
        blogsRecyclerSettings()
        pagination()
        apiCalling(1)
        return bindingObj.root
    }

    fun initializations()
    {
        if(arguments?.containsKey("cat_name")!!)
        cat_name = arguments?.getString("cat_name").toString()

        if(arguments?.containsKey("cat_id")!!)
        cat_id = arguments?.getString("cat_id").toString()

        if(arguments?.containsKey("tag_id")!!)
        tag_id = arguments?.getString("tag_id").toString()

        mPresenter  = AllCategoryBlogsModel(activity as Activity , this)
    }

    fun blogsRecyclerSettings()
    {
        mAdapter = AllBlogsAdapter(activity as Activity , ArrayList())
        val gManager = LinearLayoutManager(activity as Activity)
        bindingObj.allBlogsRecyclerView.layoutManager = gManager
        bindingObj.allBlogsRecyclerView.adapter = mAdapter
    }

    private fun pagination()
    {
        bindingObj.allBlogsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener()
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
                    currentPage += 1
                    apiCalling(currentPage)
                }
            }
        })
    }
    fun apiCalling(page:Int)
    {
        if(activity?.isNetworkActive()!!)
        {
            mPresenter?.getAllCategoryBlogs(cat_id ,tag_id,search_str,page.toString())
        }

        else
        {

            AlertDialog.Builder(activity as Activity)
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


    override fun onSuccess(model: AllCategoryBlogsPojo)
    {
        if(model.result?.size!=0) {
            isLoading =false
            var previousListSize =0
            if(mAdapter.mList?.size!!>0)
             previousListSize= (mAdapter.mList?.size)!! - 1


            //mBlogsList?.addAll(model.result!!)
            mAdapter.addAll(model.result!!)

            if (currentPage > 1)
                bindingObj.allBlogsRecyclerView.smoothScrollToPosition(previousListSize!!)

        }


    }

    override fun onGetCategories(model: BlogCategoriesPojo)
    {

    }


    override fun onFailed(message: String) {
        isLoading =false
        showSnackbar(message)
    }

    override fun showProgress(visibility: Boolean)
    {
        if (visibility)
            (activity as AllCategoryBlogsActivity).startProgressBarAnim()
        else
            (activity as AllCategoryBlogsActivity).stopProgressBarAnim()
    }

    fun showSnackbar(message: String)
    {
        try {
            val parentLayout = activity?.findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout!!, message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.cancelRetrofitRequest()
    }
}
