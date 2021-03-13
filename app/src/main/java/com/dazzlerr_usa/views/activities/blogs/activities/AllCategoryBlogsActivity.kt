package com.dazzlerr_usa.views.activities.blogs.activities

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityAllCategoryBlogsBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.views.activities.blogs.adapters.CategoryBlogsPagerAdapter
import com.dazzlerr_usa.views.activities.blogs.models.AllCategoryBlogsModel
import com.dazzlerr_usa.views.activities.blogs.pojos.AllCategoryBlogsPojo
import com.dazzlerr_usa.views.activities.blogs.presenter.AllCategoryBlogsPresenter
import com.dazzlerr_usa.views.activities.blogs.views.AllCategoryBlogsView
import com.google.android.material.snackbar.Snackbar
import androidx.viewpager.widget.ViewPager
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.activities.blogs.fragments.AllBlogsFragment
import com.dazzlerr_usa.views.activities.blogs.pojos.BlogCategoriesPojo
import com.google.android.material.tabs.TabLayout
import javax.inject.Inject


class AllCategoryBlogsActivity : AppCompatActivity(), AllCategoryBlogsView, View.OnClickListener
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var bindingObj: ActivityAllCategoryBlogsBinding
    lateinit var mPresenter: AllCategoryBlogsPresenter
    internal lateinit var viewPagerAdapter: CategoryBlogsPagerAdapter
    val mCategoriesList: MutableList<BlogCategoriesPojo.Result> = ArrayList()
    var CAT_ID = ""
    var TAG_ID = ""
    var CURRENT_POS = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj =  DataBindingUtil.setContentView(this ,R.layout.activity_all_category_blogs)
        initializations()
        clickListeners()
        apiCalling()
    }

    private fun initializations()
    {

        (application as MyApplication).myComponent.inject(this)
        bindingObj.titleLayout.titletxt.text = "Blogs"
        bindingObj.titleLayout.rightbtn.visibility = View.VISIBLE
        bindingObj.titleLayout.rightbtn.setImageResource(R.drawable.ic_search)
        mPresenter  = AllCategoryBlogsModel(this , this)

        if(intent.extras!=null)
        {
            if(intent.extras?.containsKey("cat_id")!!)
                CAT_ID = intent.extras?.getString("cat_id")!!

            if(intent.extras?.containsKey("tag_id")!!)
                TAG_ID = intent.extras?.getString("tag_id")!!
        }

        bindingObj.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener
        {
            override fun onTabSelected(tab: TabLayout.Tab)
            {
                val tabLayout = (bindingObj.tabLayout.getChildAt(0) as ViewGroup).getChildAt(tab.position) as LinearLayout
                val tabTextView = tabLayout.getChildAt(1) as TextView
                tabTextView.typeface =null
                tabTextView.setTypeface(tabTextView.typeface, Typeface.BOLD)
            }

            override fun onTabUnselected(tab: TabLayout.Tab)
            {

                val tabLayout = (bindingObj.tabLayout.getChildAt(0) as ViewGroup).getChildAt(tab.position) as LinearLayout
                val tabTextView = tabLayout.getChildAt(1) as TextView
                tabTextView.typeface =null
                tabTextView.setTypeface(tabTextView.typeface, Typeface.NORMAL)
            }

            override fun onTabReselected(tab: TabLayout.Tab)
            {

            }
        })
    }

    private fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.titleLayout.rightbtn.setOnClickListener(this)
    }

    private fun apiCalling()
    {
        if(isNetworkActive()!!)
        {
            mPresenter?.getAllCategories("1")
        }
        else
        {
            AlertDialog.Builder(this@AllCategoryBlogsActivity)
                    .setTitle("No internet Connection!")
                    .setMessage("Please connect to Mobile network or WiFi.")
                    .setPositiveButton("Retry", DialogInterface.OnClickListener { dialog, which ->
                        apiCalling()
                    })

                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    })
                    .show()
        }
    }

    override fun onGetCategories(model: BlogCategoriesPojo)
    {

        val cat  = BlogCategoriesPojo.Result()
        cat.cat_id = ""
        cat.name = "All Posts"

        mCategoriesList.add(cat)
        mCategoriesList.addAll(model.result!!)
        viewPagerAdapter = CategoryBlogsPagerAdapter(supportFragmentManager,mCategoriesList ,TAG_ID)

        bindingObj.viewPager.adapter = viewPagerAdapter
        bindingObj.tabLayout.setupWithViewPager(bindingObj.viewPager)

        for(i:Int in mCategoriesList.indices)
        {
            if(CAT_ID.equals(mCategoriesList.get(i).cat_id)) {
                bindingObj.tabLayout.getTabAt(i)?.select()
                break
            }
        }

        bindingObj.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)
            {

            }

            override fun onPageSelected(position: Int)
            {
                CURRENT_POS =position
                val frag = viewPagerAdapter.fragments[position]
                if (frag != null && frag is AllBlogsFragment)
                {
                    frag.currentPage = 1
                    frag.isLoading = false
                }


            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun onSuccess(model: AllCategoryBlogsPojo)
    {

    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
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
        bindingObj.aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {
        bindingObj.aviProgressBar.setVisibility(View.GONE)
    }

    fun showSnackbar(message: String)
    {

        try
        {
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

            R.id.rightbtn->
            {
                startActivity(Intent(this@AllCategoryBlogsActivity , SearchBlogsActivity::class.java))
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.cancelRetrofitRequest()
    }
}
