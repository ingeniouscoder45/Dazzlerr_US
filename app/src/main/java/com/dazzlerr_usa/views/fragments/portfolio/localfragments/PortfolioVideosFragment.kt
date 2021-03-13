package com.dazzlerr_usa.views.fragments.portfolio.localfragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.dazzlerr_usa.R
import com.dazzlerr_usa.utilities.GridSpacingItemDecoration
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.portfolio.PortfolioActivity
import com.dazzlerr_usa.views.fragments.portfolio.adapters.VideosAdapter
import com.dazzlerr_usa.views.fragments.portfolio.models.PortfolioVideosModel
import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioVideosPojo
import com.dazzlerr_usa.views.fragments.portfolio.presenters.PortfolioVideosPresenter
import com.dazzlerr_usa.views.fragments.portfolio.views.PortfolioVideosView
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActive


/**
 * A simple [Fragment] subclass.
 */
class PortfolioVideosFragment : androidx.fragment.app.Fragment() , PortfolioVideosView
{
    lateinit var  bindingObj: com.dazzlerr_usa.databinding.FragmentPortfolioVideosBinding
    var adapter: VideosAdapter? = null

    lateinit var mPresenter: PortfolioVideosPresenter
    val PAGE_START = 1
    private var currentPage = PAGE_START
    private var isLoading = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        bindingObj =  DataBindingUtil.inflate(inflater , R.layout.fragment_portfolio_videos, container, false)
        initializations()
        apiCalling( (activity as PortfolioActivity).user_id ,currentPage.toString())
        pagination()
        return bindingObj.root
    }

    fun initializations()
    {
        mPresenter = PortfolioVideosModel(activity as Activity  , this)
        adapter = VideosAdapter(activity as Activity , ArrayList() ,this)
        val gManager = GridLayoutManager(activity, 2)
        gManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup()
        {
            override fun getSpanSize(position: Int): Int {
                when (adapter!!.getItemViewType(position))
                {
                    adapter?.VIEW_TYPE_LOADING -> return 2
                    adapter?.VIEW_TYPE_NORMAL -> return 1
                    else -> return -1
                }
            }
        }

        bindingObj.videoRecyclerview.layoutManager = gManager

        val spanCount = 2 // 3 columns
        val spacing = 2 // 4px
        val includeEdge = true
        bindingObj.videoRecyclerview.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
        bindingObj.videoRecyclerview.adapter = adapter
    }

    private fun pagination()
    {
        bindingObj.videoRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleThreshold = 2

                val layoutManager = recyclerView.layoutManager as GridLayoutManager?
                val lastItem = layoutManager!!.findLastCompletelyVisibleItemPosition()
                val currentTotalCount = layoutManager.itemCount

                if (currentTotalCount <= lastItem + visibleThreshold && !isLoading)
                {
                    isLoading = true
                    apiCalling((activity as PortfolioActivity).user_id  , currentPage.toString())
                }
            }
        })
    }

    private fun apiCalling(user_id: String , page:String)
    {

        if(activity?.isNetworkActive()!!)
        {
            mPresenter?.getPortfolioVideos(user_id , page)
        }

        else
        {

            val dialog  = CustomDialog(activity as Activity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
            apiCalling(user_id ,page)
                    })
                    dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })
                    dialog.show()
        }
    }


    fun addItem( list : MutableList<PortfolioVideosPojo.DataBean>)
    {
        if (currentPage != PAGE_START)
            adapter?.removeLoading()

        adapter?.addAll(list)
        adapter?.addLoading()
    }

    fun deleteProject(video_id:String , position:Int)
    {
        mPresenter.deleteVideo((activity as PortfolioActivity).user_id ,video_id ,position)
    }

    override fun onVideoDelete(position: Int) {

        adapter?.removeItem(position)
        showSnackbar("Video has been deleted successfully.")
    }


    override fun onSuccess(model: PortfolioVideosPojo)
    {
        if (model.data?.size!=0)
        {




            if(currentPage==PAGE_START)
            {
                adapter?.addAll(model.data!!)
                adapter?.addLoading()
            }
            else
                addItem(model.data!!)

            currentPage++
            isLoading = false
            bindingObj.emptyLayout.visibility  = View.GONE
        }
        else
        {
            if(currentPage!=PAGE_START)
                adapter?.removeLoading()

            else if(currentPage==PAGE_START)
            {
                bindingObj.emptyLayout.visibility  = View.VISIBLE
            }

        }
    }


    override fun onFailed(message: String) {
        isLoading =false
        showSnackbar(message)
    }

    override fun showProgress(visiblity: Boolean) {

        if(currentPage==PAGE_START && activity!=null) {
            if (visiblity) {
                (activity as PortfolioActivity).startProgressBarAnim()
            } else {
                (activity as PortfolioActivity).stopProgressBarAnim()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {

        if(requestCode==101)
        {
            if(data?.extras != null && data.extras!!.containsKey("extras"))
            {
                currentPage =1
                adapter?.removeAll()
                apiCalling( (activity as PortfolioActivity).user_id ,currentPage.toString())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.cancelRetrofitRequest()
    }

}// Required empty public constructor
