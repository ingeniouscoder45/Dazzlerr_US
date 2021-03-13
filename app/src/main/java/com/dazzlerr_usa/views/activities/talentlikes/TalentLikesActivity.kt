package com.dazzlerr_usa.views.activities.talentlikes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityTalentLikesBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.GridSpacingItemDecoration
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.fragments.home.pojos.ModelsPojo
import com.dazzlerr_usa.views.fragments.talent.adapters.TalentsAdapter
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

class TalentLikesActivity : AppCompatActivity() , TalentLikesView , View.OnClickListener
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObj : ActivityTalentLikesBinding

    lateinit var talentsAdapter: TalentsAdapter

    lateinit var talentPresenter: TalentLikesPresenter
    val PAGE_START = 1
    private var currentPage = PAGE_START
    private var isLoading = false

    override fun onResume() {
        super.onResume()
        currentPage = PAGE_START
        apiCalling(sharedPreferences.getString(Constants.User_id).toString() , currentPage.toString() ,true)

    }
    override fun onCreate(savedInstanceState: Bundle?)
    {
       super.onCreate(savedInstanceState)
       bindingObj =  DataBindingUtil.setContentView(this , R.layout.activity_talent_likes)
        initializations()
        clickListeners()
        talentRecyclerSettings()
        pagination()

    }

    fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)
        bindingObj.titleLayout.titletxt.text = "Likes"
        talentPresenter = TalentLikesModel(this, this)
    }

    fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
    }

    fun talentRecyclerSettings()
    {
        talentsAdapter = TalentsAdapter(this , ArrayList())
        val gManager = GridLayoutManager(this, 2)

        gManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup()
        {
            override fun getSpanSize(position: Int): Int {
                when (talentsAdapter!!.getItemViewType(position))
                {
                    talentsAdapter?.VIEW_TYPE_LOADING -> return 2
                    talentsAdapter?.VIEW_TYPE_NORMAL -> return 1
                    else -> return -1
                }
            }
        }

        bindingObj.talentRecycler.layoutManager = gManager
        val spanCount = 2 // 3 columns
        val spacing = 25 // 50px
        val includeEdge = true
        bindingObj.talentRecycler.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
        bindingObj.talentRecycler.adapter = talentsAdapter

    }

    private fun pagination()
    {
        bindingObj.talentRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleThreshold = 2
                val layoutManager = recyclerView.layoutManager as GridLayoutManager?
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
            talentPresenter?.getlikedTalents(user_id , page ,isShowProgressbar)
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

    fun addItem( list : MutableList<ModelsPojo.DataBean>)
    {
        if (currentPage != PAGE_START)
            talentsAdapter?.removeLoading()

        talentsAdapter?.addAll(list)
        talentsAdapter?.addLoading()
    }

    override fun onGetLikesSuccess(model: ModelsPojo) {

        if (model.data?.size!=0)
        {


            if(currentPage==PAGE_START)
            {
                Timber.e("current Page"+currentPage)
                talentsAdapter?.removeAll()
                talentsAdapter?.addAll(model.data!!)
                talentsAdapter?.addLoading()
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
                talentsAdapter?.removeLoading()

            else if(currentPage==PAGE_START)
            {
                bindingObj.emptyLayout.visibility  = View.VISIBLE
                //bindingObj.filterBtn.hide()
                talentsAdapter?.removeAll()
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
