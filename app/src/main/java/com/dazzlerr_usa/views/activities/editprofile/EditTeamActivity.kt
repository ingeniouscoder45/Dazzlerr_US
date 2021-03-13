package com.dazzlerr_usa.views.activities.editprofile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityEditTeamBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.GridSpacingItemDecoration
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.editprofile.adapters.EditTeamAdapter
import com.dazzlerr_usa.views.activities.profileteam.*
import com.dazzlerr_usa.views.activities.profileteam.models.ProfileTeamModel
import com.dazzlerr_usa.views.activities.profileteam.pojos.GetTeamPojo
import com.dazzlerr_usa.views.activities.profileteam.presenter.ProfileTeamPresenter
import com.dazzlerr_usa.views.activities.profileteam.views.ProfileTeamView
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

class EditTeamActivity : AppCompatActivity(), View.OnClickListener, ProfileTeamView
{
    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var bindingObj : ActivityEditTeamBinding
    lateinit var mTeamAdapter: EditTeamAdapter
    lateinit var mPresenter: ProfileTeamPresenter

    val PAGE_START = 1
    private var currentPage = PAGE_START
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_edit_team)
        intializations()
        talentRecyclerSettings()
        clicklisteners()
        pagination()
        apiCalling(currentPage.toString() , true)
    }


    fun intializations()
    {
        (application as MyApplication).myComponent.inject(this)
        bindingObj.titleLayout.titletxt.text = "My Team"
        mPresenter = ProfileTeamModel(this, this)

        bindingObj.titleLayout.righttxt.visibility =View.VISIBLE
        bindingObj.titleLayout.righttxt.text  = "Add"
        bindingObj.titleLayout.rightbtn.visibility =View.VISIBLE
        bindingObj.titleLayout.rightbtn.setImageResource(R.drawable.ic_add)
    }
    fun clicklisteners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.titleLayout.rightLayout.setOnClickListener(this)
    }



    fun talentRecyclerSettings()
    {
        mTeamAdapter = EditTeamAdapter(this , ArrayList())
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

        bindingObj.editTeamRecycler.layoutManager = gManager
        val spanCount = 2 // 3 columns
        val spacing = 25 // 50px
        val includeEdge = true
        bindingObj.editTeamRecycler.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
        bindingObj.editTeamRecycler.adapter = mTeamAdapter
    }

    private fun pagination()
    {
        bindingObj.editTeamRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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


    private fun apiCalling(page: String ,isShowProgressbar  :Boolean)
    {

        if(this@EditTeamActivity?.isNetworkActive()!!)
        {
            mPresenter.getTeamMembers(sharedPreferences.getString(Constants.User_id).toString() ,page ,isShowProgressbar)
        }

        else
        {

            val dialog = CustomDialog(this@EditTeamActivity)
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

            R.id.rightLayout->
            {
                val intent = Intent(this@EditTeamActivity, AddTeamMemberActivity::class.java)
                startActivityForResult(intent ,100)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode)
        {
            100->
            {
                currentPage =PAGE_START
                apiCalling(currentPage.toString() , true)
            }
        }

        }
}
