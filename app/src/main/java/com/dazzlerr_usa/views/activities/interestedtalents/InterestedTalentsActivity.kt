package com.dazzlerr_usa.views.activities.interestedtalents

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityInterestedTalentsBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

class InterestedTalentsActivity : AppCompatActivity() , InterestedTalentsView  , View.OnClickListener {

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObj : ActivityInterestedTalentsBinding

    lateinit var mPresenter : InterestedTalentsPresenter

    lateinit var interestedTalentsAdapter: InterestedTalentsAdapter

    val PAGE_START = 1
    private var currentPage = PAGE_START
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_interested_talents)

        initializations()
        clickListeners()
        talentRecyclerSettings()
        pagination()
        apiCalling(sharedPreferences.getString(Constants.User_id).toString() , currentPage.toString() ,true)
    }

    fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)
        bindingObj.titleLayout.titletxt.text = "Interested Talents"
        mPresenter = InterestedTalentsModel(this, this)
    }

    fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
    }

    fun talentRecyclerSettings()
    {
        interestedTalentsAdapter = InterestedTalentsAdapter(this , ArrayList())
        val gManager = LinearLayoutManager(this)

        bindingObj.talentRecycler.layoutManager = gManager
        bindingObj.talentRecycler.adapter = interestedTalentsAdapter
    }

    private fun pagination()
    {
        bindingObj.talentRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleThreshold = 2
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val lastItem = layoutManager!!.findLastCompletelyVisibleItemPosition()
                val currentTotalCount = layoutManager.itemCount

                if (currentTotalCount <= lastItem + visibleThreshold && !isLoading)
                {
                    isLoading = true
                    apiCalling(sharedPreferences.getString(Constants.User_id).toString()  , currentPage.toString() ,false)
                }
            }
        })
    }

    private fun apiCalling(user_id: String , page:String , isShowProgressbar:Boolean)
    {

        if(isNetworkActive()!!)
        {
            mPresenter?.getInterestedTalents(user_id , page ,isShowProgressbar)
        }

        else
        {

            val dialog = CustomDialog(this)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))

            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                apiCalling(user_id ,page , true)
            })
            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                dialog.dismiss()
            })
            dialog.show()
        }
    }

    fun addItem( list : MutableList<InterestedTalentsPojo.DataBean>)
    {
        if (currentPage != PAGE_START)
            interestedTalentsAdapter?.removeLoading()

        interestedTalentsAdapter?.addAll(list)
        interestedTalentsAdapter?.addLoading()
    }

    override fun onGetInterestedTalentsSuccess(model: InterestedTalentsPojo) {

        if (model.data?.size!=0)
        {
            if(currentPage==PAGE_START)
            {
                Timber.e("current Page"+currentPage)
                interestedTalentsAdapter?.removeAll()
                interestedTalentsAdapter?.addAll(model.data!!)
                interestedTalentsAdapter?.addLoading()
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
                interestedTalentsAdapter?.removeLoading()

            else if(currentPage==PAGE_START)
            {
                bindingObj.emptyLayout.visibility  = View.VISIBLE
                //bindingObj.filterBtn.hide()
                interestedTalentsAdapter?.removeAll()
            }

        }
    }

    override fun onFailed(message: String) {
        isLoading =false
        showSnackbar(message)
    }

    override fun showProgress(visiblity: Boolean , isShowProgressbar: Boolean) {

        if(isShowProgressbar) {
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
