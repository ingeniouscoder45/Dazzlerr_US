package com.dazzlerr_usa.views.activities.profileteam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityProfileTeamBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.GridSpacingItemDecoration
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.profileteam.models.ProfileTeamModel
import com.dazzlerr_usa.views.activities.profileteam.pojos.GetTeamPojo
import com.dazzlerr_usa.views.activities.profileteam.presenter.ProfileTeamPresenter
import com.dazzlerr_usa.views.activities.profileteam.views.ProfileTeamView
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

class ProfileTeamActivity : AppCompatActivity(), View.OnClickListener , ProfileTeamView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var bindingObj : ActivityProfileTeamBinding
    lateinit var mTeamAdapter: ProfileTeamAdapter
    lateinit var mPresenter: ProfileTeamPresenter

    val PAGE_START = 1
    private var currentPage = PAGE_START
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_profile_team)
        intializations()
        talentRecyclerSettings()
        clicklisteners()
        pagination()
        apiCalling(currentPage.toString() , true)
    }

    fun intializations()
    {
        (application as MyApplication).myComponent.inject(this)
        bindingObj.titleLayout.titletxt.text = "Team"
        mPresenter = ProfileTeamModel(this, this)
    }

    fun talentRecyclerSettings()
    {
        mTeamAdapter = ProfileTeamAdapter(this , ArrayList())
        val gManager = GridLayoutManager(this, 2)

        gManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup()
        {
            override fun getSpanSize(position: Int): Int {
                when (mTeamAdapter!!.getItemViewType(position))
                {
                    mTeamAdapter?.VIEW_TYPE_LOADING -> return 2
                    mTeamAdapter?.VIEW_TYPE_NORMAL -> return 1
                    else -> return -1
                }
            }
        }

        bindingObj.teamRecycler.layoutManager = gManager
        val spanCount = 2 // 3 columns
        val spacing = 25 // 50px
        val includeEdge = true
        bindingObj.teamRecycler.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
        bindingObj.teamRecycler.adapter = mTeamAdapter
    }

    private fun pagination()
    {
        bindingObj.teamRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleThreshold = 2
                val layoutManager = recyclerView.layoutManager as GridLayoutManager?
                val lastItem = layoutManager!!.findLastCompletelyVisibleItemPosition()
                val currentTotalCount = layoutManager.itemCount

                if (currentTotalCount <= lastItem + visibleThreshold && !isLoading)
                {
                    isLoading = true
                    apiCalling(currentPage.toString() ,false)
                }
            }
        })
    }

    fun clicklisteners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
    }


    private fun apiCalling(page: String ,isShowProgressbar  :Boolean)
    {

        if(this@ProfileTeamActivity?.isNetworkActive()!!)
        {
            mPresenter.getTeamMembers(sharedPreferences.getString(Constants.User_id).toString() ,page ,isShowProgressbar)
        }

        else
        {

            val dialog = CustomDialog(this@ProfileTeamActivity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                apiCalling(page , isShowProgressbar)
            })
            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                dialog.dismiss()
            })
            dialog.show()
        }
    }

    fun addItem( list : MutableList<GetTeamPojo.DataBean>)
    {
        if (currentPage != PAGE_START)
            mTeamAdapter?.removeLoading()

        mTeamAdapter?.addAll(list)
        mTeamAdapter?.addLoading()
    }

    override fun onGetTeamSuccess(model: GetTeamPojo) {

        if (model.data?.size!=0)
        {
            if(currentPage==PAGE_START)
            {
                Timber.e("current Page"+currentPage)
                mTeamAdapter?.removeAll()
                mTeamAdapter?.addAll(model.data!!)
                mTeamAdapter?.addLoading()
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
                mTeamAdapter?.removeLoading()

            else if(currentPage==PAGE_START)
            {
                bindingObj.emptyLayout.visibility  = View.VISIBLE
                //bindingObj.filterBtn.hide()
                mTeamAdapter?.removeAll()
            }

        }

    }


    override fun onFailed(message: String)
    {

        showSnackbar(message)
    }

    override fun showProgress(visiblity: Boolean, isShowProgressbar: Boolean) {
        if(isShowProgressbar)
        {
            if (visiblity) {
                startProgressBarAnim()
            } else {
                stopProgressBarAnim()
            }
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

            R.id.leftbtn->
            {
                finish()
            }
        }
    }

}
