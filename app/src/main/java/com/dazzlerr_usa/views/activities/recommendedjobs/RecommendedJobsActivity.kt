package com.dazzlerr_usa.views.activities.recommendedjobs

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityRecommendedJobsBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.fragments.jobs.*
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class RecommendedJobsActivity : AppCompatActivity() , View.OnClickListener ,ReccomendedJobsView
{
    lateinit var bindingObj: ActivityRecommendedJobsBinding
    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    val PAGE_START = 1
    private var currentPage = PAGE_START
    private var isLoading = false
    lateinit var mPresenter: RecommendedJobsPresenter
    lateinit var mAdapter: RecommendedJobsAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this ,R.layout.activity_recommended_jobs)

        initializations()
        clickListeners()
        recommendedJobsRecyclerSettings()
        apiCalling(sharedPreferences.getString(Constants.User_id).toString() , ""+currentPage , false)
    }


    internal fun initializations()
    {
        (application as MyApplication).myComponent.inject(this@RecommendedJobsActivity)

        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        bindingObj.titleLayout.titletxt.text = "Recommended Jobs"
        bindingObj.titleLayout.righttxt.text = "View All"

        mPresenter = RecommendedJobsModel(this , this)
    }


    fun clickListeners()
    {
        bindingObj.titleLayout.rightbtn.visibility = View.VISIBLE
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.titleLayout.rightLayout.setOnClickListener(this)
    }

    fun recommendedJobsRecyclerSettings()
    {
        mAdapter = RecommendedJobsAdapter(this@RecommendedJobsActivity, ArrayList())
        val gManager = LinearLayoutManager(this@RecommendedJobsActivity)

        bindingObj.recommendedJosRecycler.layoutManager = gManager
        bindingObj.recommendedJosRecycler.addItemDecoration( DividerItemDecoration(bindingObj.recommendedJosRecycler.getContext(), DividerItemDecoration.VERTICAL))
        bindingObj.recommendedJosRecycler.adapter = mAdapter
    }


    private fun apiCalling(user_id :String, page : String ,  isShowProgressbar:Boolean)
    {

        if(this@RecommendedJobsActivity.isNetworkActive())
        {
            mPresenter.getRecommendedJobs(user_id  ,page ,  isShowProgressbar)
        }

        else
        {

            val dialog  = CustomDialog(this@RecommendedJobsActivity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                apiCalling(user_id , page , true)
            })

            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                dialog.dismiss()
            })
            dialog.show()

        }
    }



    override fun onSuccess(model: JobsPojo) {

        if (model.data?.size!=0)
        {

            bindingObj.emptyLayout.visibility = View.GONE
            bindingObj.recommendedJosRecycler.visibility = View.VISIBLE

            mAdapter?.addAll(model.data!!)

        }

        else
        {

                bindingObj.emptyLayout.visibility = View.VISIBLE
                bindingObj.recommendedJosRecycler.visibility = View.GONE


        }
    }


    override fun onFailed(message: String) {
        showSnackbar(message)
    }


    override fun showProgress(visiblity: Boolean, isShowProgressbar: Boolean) {

        if(isShowProgressbar)
        {
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
            R.id.leftbtn ->
            {
               onBackPressed()
            }

            R.id.rightLayout->
            {
                var bundle = Bundle()
                bundle.putString("openJobs" , "true")

                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed()
    {

        if(isTaskRoot)
        {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        else
        {
            super.onBackPressed()
        }
    }
}