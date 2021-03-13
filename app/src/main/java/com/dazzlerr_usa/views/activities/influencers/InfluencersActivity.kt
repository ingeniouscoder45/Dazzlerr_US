package com.dazzlerr_usa.views.activities.influencers


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityInfluencersBinding
import com.dazzlerr_usa.utilities.GridSpacingItemDecoration
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActive
import java.util.ArrayList


class InfluencersActivity : AppCompatActivity(), View.OnClickListener , InfluencersView
{
    lateinit var mPresenter: InfluencersPresenter
    lateinit var bindingObj:ActivityInfluencersBinding

    lateinit var mEliteAdapter: InfluencersAdapter
    val PAGE_START = 1
    private var currentPage = PAGE_START
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_influencers)
        initializations()
        talentRecyclerSettings()
        clickListeners()
        pagination()
        apiCalling(currentPage.toString() ,true)
    }


    fun initializations()
    {
        mPresenter = InfluencersModel(this  , this )
    }
    fun clickListeners()
    {
        bindingObj.backBtn.setOnClickListener(this)
    }

    fun talentRecyclerSettings()
    {
        mEliteAdapter = InfluencersAdapter(this@InfluencersActivity , ArrayList())
        val gManager = GridLayoutManager(this@InfluencersActivity, 2)

        gManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup()
        {
            override fun getSpanSize(position: Int): Int {
                when (mEliteAdapter!!.getItemViewType(position))
                {
                    mEliteAdapter?.VIEW_TYPE_LOADING -> return 2
                    mEliteAdapter?.VIEW_TYPE_NORMAL -> return 1
                    else -> return -1
                }
            }
        }

        bindingObj.recyclerView.layoutManager = gManager
        val spanCount = 2 // 3 columns
        val spacing = 10 // 50px
        val includeEdge = true
        bindingObj.recyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
        bindingObj.recyclerView.adapter = mEliteAdapter
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

        if(this@InfluencersActivity?.isNetworkActive()!!)
        {
            mPresenter?.getInfluencers(page, isShowProgressbar)
        }

        else
        {

            val dialog = CustomDialog(this@InfluencersActivity)
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

    fun addItem( list : MutableList<InfluencersPojo.ResultBean>)
    {
        if (currentPage != PAGE_START)
            mEliteAdapter?.removeLoading()

        mEliteAdapter?.addAll(list)
        mEliteAdapter?.addLoading()
    }

    override fun onSuccess(model: InfluencersPojo)
    {

        Glide.with(this@InfluencersActivity).load("https://www.dazzlerr.com/API/"+model.banner).placeholder(R.drawable.placeholder2).into(bindingObj.influencerBannerImg)

        if (model.result?.size!=0)
        {
            if(currentPage==PAGE_START)
            {
                mEliteAdapter.removeAll()
                mEliteAdapter.addAll(model.result!!)
                mEliteAdapter.addLoading()
            }
            else
                addItem(model.result!!)

            currentPage++
            isLoading = false
        }
        else
        {
            if(currentPage!=PAGE_START)
                mEliteAdapter?.removeLoading()

            else if(currentPage==PAGE_START)
            {

                mEliteAdapter?.removeAll()
            }

        }
    }


    override fun onFailed(message: String)
    {
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
            R.id.backBtn-> finish()
        }
    }
}
