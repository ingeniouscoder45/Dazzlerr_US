package com.dazzlerr_usa.views.activities.institute

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityInstituteListBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.GridSpacingItemDecoration
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class InstituteListActivity : AppCompatActivity() , InstituteView , View.OnClickListener {

    lateinit var bindingObj:ActivityInstituteListBinding
    lateinit var mAdapter: InstitutesAdapter
    val PAGE_START = 1
    private var currentPage = PAGE_START
    private var isLoading = false
    lateinit var mPresenter: InstitutePresenter

    var categoryidsList  : java.util.ArrayList<Int> = java.util.ArrayList()
    var city=""
    var category_id = ""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_institute_list)

        initializations()
        instituteRecyclerSettings()
        clickListeners()
        pagination()

    }


    override fun onResume() {
        super.onResume()


        currentPage = PAGE_START
        apiCalling(city , category_id , currentPage.toString() , true)
    }

    fun initializations()
    {
        //(application as MyApplication).myComponent.inject(this@InstituteListActivity)

        bindingObj.titleLayout.titletxt.text = "Institutes"
        mPresenter = InstituteModel(this , this)
    }

    fun instituteRecyclerSettings()
    {

        mAdapter = InstitutesAdapter(this@InstituteListActivity, ArrayList())
        val gManager = LinearLayoutManager(this@InstituteListActivity)

        bindingObj.instituteRecycler.layoutManager = gManager
        val spanCount = 2 // 3 columns
        val spacing = 25 // 50px
        val includeEdge = true
        bindingObj.instituteRecycler.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
        bindingObj.instituteRecycler.adapter = mAdapter
    }

    private fun pagination()
    {
        bindingObj.instituteRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener()
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
                    apiCalling(city , category_id ,  currentPage.toString() ,false)
                }
            }
        })
    }
    fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.filterBtn.setOnClickListener(this)
    }


    private fun apiCalling(city :String  , category_id : String , page:String, isShowProgressbar:Boolean)
    {

        if(this@InstituteListActivity.isNetworkActive())
        {
            mPresenter.getInstitutez(city  ,category_id  ,page, isShowProgressbar)
        }

        else
        {

            val dialog  = CustomDialog(this@InstituteListActivity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                apiCalling(city , category_id , page, true)
            })

            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                dialog.dismiss()
            })
            dialog.show()

        }
    }

    fun addItem(list : MutableList<InstitutePojo.Data>)
    {
        if (currentPage != PAGE_START)
            mAdapter?.removeLoading()

        mAdapter?.addAll(list)
        mAdapter?.addLoading()
    }


    override fun onSuccess(model: InstitutePojo) {

        if (model.data?.size!=0)
        {
            bindingObj.emptyLayout.visibility = View.GONE
            bindingObj.instituteRecycler.visibility = View.VISIBLE

            if(currentPage==PAGE_START)
            {
                Timber.e("current Page"+currentPage)
                mAdapter?.removeAll()
                mAdapter?.addAll(model.data!! )
                mAdapter?.addLoading()
            }
            else
                addItem(model.data!!)

            currentPage++
            isLoading = false
        }

        else
        {
            if(currentPage!=PAGE_START)
                mAdapter?.removeLoading()

            else if(currentPage==PAGE_START)
            {
                bindingObj.emptyLayout.visibility = View.VISIBLE
                bindingObj.instituteRecycler.visibility = View.GONE
                mAdapter?.removeAll()
            }

        }
    }


    override fun onFailed(message: String) {
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
        when (v?.id) {

            R.id.leftbtn -> finish()

            R.id.filterBtn->
            {
                val bundle = Bundle()

                bundle.putIntegerArrayList("categoryidsList" , categoryidsList)
                bundle.putString("city" , city)
                startActivityForResult(Intent(this@InstituteListActivity, InstituteFilterActivity::class.java ).putExtras(bundle) ,101)

            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==101)
        {
            if( data?.extras!=null)
            {
                var bundle: Bundle = data?.extras!!


                categoryidsList = bundle.getIntegerArrayList("categoryidsList")!!
                city = bundle.getString("city")!!

                //Timber.e(experience_filter_check_list.toString())
                // Timber.e(categoryidsList.toString())

                category_id = categoryidsList.toString()
                        .replace("[","")
                        .replace("]","")
                        .replace(" ","")

                // Timber.e(category_id)
                // Timber.e(gender)
                //  Timber.e(experience_type)

                //currentPage =  PAGE_START


                    //apiCalling(city  ,category_id , currentPage.toString() ,true)

            }
        }
    }


}