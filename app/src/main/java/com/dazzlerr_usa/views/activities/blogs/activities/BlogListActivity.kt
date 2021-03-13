package com.dazzlerr_usa.views.activities.blogs.activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityBlogListBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.activities.blogs.adapters.BlogListAdapter
import com.dazzlerr_usa.views.activities.blogs.models.BlogsListModel
import com.dazzlerr_usa.views.activities.blogs.pojos.BlogListPojo
import com.dazzlerr_usa.views.activities.blogs.presenter.BlogsListPresenter
import com.dazzlerr_usa.views.activities.blogs.views.BlogListView
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject
import kotlin.collections.ArrayList

class BlogListActivity : AppCompatActivity() , View.OnClickListener, BlogListView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var bindingObj: ActivityBlogListBinding
    lateinit var mPresenter: BlogsListPresenter

    var mFeaturedList: MutableList<BlogListPojo.ResultBean>? = ArrayList()
    var mPopularList: MutableList<BlogListPojo.ResultBean>? = ArrayList()
    var mRecentList: MutableList<BlogListPojo.ResultBean>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this ,R.layout.activity_blog_list)
        initializations()
        clickListeners()
        apiCalling(1)//type 1 for featured posts
        apiCalling(2)//type 1 for popular posts
        apiCalling(3)//type 1 for recent posts
    }

    fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)

        //Initializing Google ads
/*
        val testDeviceIds: List<String> = Arrays.asList("A86FED701CDC6E77D341DEBA488306E3")
        val configuration: RequestConfiguration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
*/

        //MobileAds.initialize(this) {}

        if(sharedPreferences.getString(Constants.Membership_id).equals("")
                || sharedPreferences.getString(Constants.Membership_id).equals("0")
                ||sharedPreferences.getString(Constants.Membership_id).equals("1")
                ||sharedPreferences.getString(Constants.Membership_id).equals("6"))
        {
            val adRequest = AdRequest.Builder().build()
            bindingObj.adView1.loadAd(adRequest)

            val adRequest1 = AdRequest.Builder().build()
            bindingObj.adView2.loadAd(adRequest1)

            val adRequest2 = AdRequest.Builder().build()
            bindingObj.adView3.loadAd(adRequest2)

            //--------------------------
        }

        else
        {
            bindingObj.adView1.visibility -View.GONE
            bindingObj.adView2.visibility -View.GONE
            bindingObj.adView3.visibility -View.GONE
        }


        bindingObj.titleLayout.titletxt.text = "Blogs"
        bindingObj.titleLayout.rightbtn.visibility = View.VISIBLE
        bindingObj.titleLayout.rightbtn.setImageResource(R.drawable.ic_search)
        mPresenter = BlogsListModel(this ,this)

        bindingObj.featuredPostsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        bindingObj.popularPostsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        bindingObj.recentPostsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun clickListeners()
    {
       bindingObj.titleLayout.leftbtn.setOnClickListener(this)
       bindingObj.titleLayout.rightbtn.setOnClickListener(this)
       bindingObj.browseAllPostsBtn.setOnClickListener(this)
       bindingObj.featuredBlogsBtn.setOnClickListener(this)
       bindingObj.popularBlogsBtn.setOnClickListener(this)
       bindingObj.recentBlogsBtn.setOnClickListener(this)
    }

    @Synchronized
    private fun apiCalling(type:Int)
    {

        bindingObj.mainLayout.visibility = View.GONE
        if(isNetworkActive()!!)
        {
            mPresenter?.getBlogslist(type, "" , "")
        }

        else
        {
            AlertDialog.Builder(this@BlogListActivity)
                    .setTitle("No internet Connection!")
                    .setMessage("Please connect to Mobile network or WiFi.")
                    .setPositiveButton("Retry", DialogInterface.OnClickListener { dialog, which ->
                        apiCalling(type)
                    })

                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    })
                    .show()

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

            R.id.rightbtn->
            {
                startActivity(Intent(this@BlogListActivity , SearchBlogsActivity::class.java))
            }

            R.id.browseAllPostsBtn->
            {
                startActivity(Intent(this@BlogListActivity , AllCategoryBlogsActivity::class.java))
            }

            R.id.featuredBlogsBtn->
            {
                val bundle = Bundle()
                bundle.putString("type" , "Featured")
                startActivity(Intent(this@BlogListActivity , AllOtherBlogsActivity::class.java).putExtras(bundle))
            }

            R.id.popularBlogsBtn->
            {
                val bundle = Bundle()
                bundle.putString("type" , "Popular")
                startActivity(Intent(this@BlogListActivity , AllOtherBlogsActivity::class.java).putExtras(bundle))
            }

            R.id.recentBlogsBtn->
            {
                val bundle = Bundle()
                bundle.putString("type" , "Recent")
                startActivity(Intent(this@BlogListActivity , AllOtherBlogsActivity::class.java).putExtras(bundle))
            }
        }
    }

    override fun onSuccess(model: BlogListPojo, type:Int)
    {
        bindingObj.mainLayout.visibility = View.VISIBLE
        if(type==1) {
            mFeaturedList = model.result
            populateDataAdapters(1)
        }
        else if(type==2)
        {
            mPopularList = model.result
            populateDataAdapters(2)
        }

        else if(type==3)
        {
            mRecentList = model.result
            populateDataAdapters(3)
        }
    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }


    override fun showProgress(visiblity: Boolean)
    {
            if (visiblity)
                startProgressBarAnim()
            else
                stopProgressBarAnim()
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

    fun populateDataAdapters( type: Int)
    {
        if(type==1)
        {
            bindingObj.featuredPostsRecycler.adapter = BlogListAdapter(this , mFeaturedList!! , "featured")
        }
        else if(type==2)
        {

            bindingObj.popularPostsRecycler.adapter = BlogListAdapter(this , mPopularList!! , "popular")
        }
        else if(type==3)
        {

            bindingObj.recentPostsRecycler.adapter = BlogListAdapter(this , mRecentList!! , "recent")
        }
    }
}
