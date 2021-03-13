package com.dazzlerr_usa.views.fragments.jobs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.AdItemLayoutBinding
import com.dazzlerr_usa.databinding.JobsItemLayoutBinding
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.activities.jobsdetails.JobsDetailsActivity
import com.google.android.flexbox.*
import com.google.android.gms.ads.AdRequest
import timber.log.Timber
import kotlin.collections.ArrayList


class JobsAdapter(internal var context: Context?, var mJobsList: MutableList<JobsPojo.DataBean> , var fragment : JobsFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    var VIEW_TYPE_LOADING = 0
    var VIEW_TYPE_NORMAL = 1
    var VIEW_TYPE_AD = 2
    private var isLoaderVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {

        if (viewType == VIEW_TYPE_LOADING)
        {
            val bindingObj : PaginationLoadingLayoutBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pagination_loading_layout, parent, false)

            return LoadingViewHolder(bindingObj)
        }

        else if(viewType == VIEW_TYPE_AD)
        {
            val binderObject = DataBindingUtil.inflate<AdItemLayoutBinding>(LayoutInflater.from(parent
                    .context), R.layout.ad_item_layout, parent, false)

            return AdViewHolder(binderObject)
        }

        else
        {
            val binderObject = DataBindingUtil.inflate<JobsItemLayoutBinding>(LayoutInflater.from(parent
                    .context), R.layout.jobs_item_layout, parent, false)

            return ViewHolder(binderObject)
        }


    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int)
    {
        if(viewHolder is ViewHolder)
        {
        viewHolder.bindingObj.jobsBinder = mJobsList[i]
            viewHolder.bindingObj?.jobsBinder?.title = mJobsList[i].title?.capitalize()
            viewHolder.bindingObj?.jobsBinder?.casting_name = mJobsList[i].casting_name?.capitalize()
            viewHolder.bindingObj?.jobsBinder?.name = mJobsList[i].name?.capitalize()

             dateFormat(viewHolder.bindingObj?.jobDateTxt ,mJobsList[i].start_date.toString())

            if(mJobsList[i].is_featured.equals("1"))
            {
                viewHolder.bindingObj.isJobFeaturedTxt.visibility  = View.VISIBLE
            }
            else
                viewHolder.bindingObj.isJobFeaturedTxt.visibility  = View.GONE

            var mServicesList : MutableList<String> = ArrayList()

            if(mJobsList.get(0).user_role!=null && !mJobsList.get(0).user_role.equals(""))
                mServicesList  = mJobsList.get(0).user_role?.split(",") as MutableList<String>

            var mServicesAdapter = JobsForAdapter(context!!,mServicesList!!)

            val servicesLayoutManager = FlexboxLayoutManager(context)
            servicesLayoutManager.flexDirection = FlexDirection.ROW
            servicesLayoutManager.justifyContent = JustifyContent.FLEX_START
            //layoutManager.alignContent = AlignContent.STRETCH
            servicesLayoutManager.alignItems = AlignItems.STRETCH
            servicesLayoutManager.flexWrap = FlexWrap.WRAP
            viewHolder.bindingObj.jobsForRecycler.setLayoutManager(servicesLayoutManager)
            viewHolder.bindingObj.jobsForRecycler.adapter = mServicesAdapter

            viewHolder.bindingObj.executePendingBindings()

            viewHolder.itemView.setOnClickListener {

                val bundle = Bundle()
                bundle.putString("call_id" , ""+mJobsList.get(i).call_id)
                context?.startActivity(Intent(context , JobsDetailsActivity::class.java).putExtras(bundle))
                //context?.startActivity(Intent(context , RecommendedJobsActivity::class.java))

            }

            if(fragment.currenttab==2) {
                viewHolder.itemView.setOnLongClickListener {

                    var mDialog= CustomDialog(context)
                    mDialog.setTitle("Alert!")
                    mDialog.setMessage("Are you sure that you want to remove this job?")

                    mDialog.setPositiveButton("Yes" , DialogListenerInterface.onPositiveClickListener {
                        fragment.removeJobFromShortlisted(i , ""+mJobsList.get(i).call_id)
                    })

                    mDialog.setNegativeButton("No" , DialogListenerInterface.onNegetiveClickListener {

                        mDialog.dismiss()
                    })

                    mDialog.show()

                    return@setOnLongClickListener true

                }
            }


        }

        else if(viewHolder is AdViewHolder)
        {

            //Initializing Google ads
/*
            val testDeviceIds: List<String> = Arrays.asList("A86FED701CDC6E77D341DEBA488306E3")
            val configuration: RequestConfiguration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
            MobileAds.setRequestConfiguration(configuration)
*/

            if((context as HomeActivity).sharedPreferences.getString(Constants.Membership_id).equals("")
                    ||(context as HomeActivity).sharedPreferences.getString(Constants.Membership_id).equals("0")
                    || (context as HomeActivity).sharedPreferences.getString(Constants.Membership_id).equals("1")
                    || (context as HomeActivity).sharedPreferences.getString(Constants.Membership_id).equals("6")
            ) {
                //MobileAds.initialize(this) {}
                val adRequest = AdRequest.Builder().build()
                viewHolder.bindingObj.adView.loadAd(adRequest)
            }
            //-----------------------
        }
    }

    fun add(response: JobsPojo.DataBean)
    {
        mJobsList.add(response)
        notifyItemInserted(mJobsList.size - 1)

        //Ads will visible for only all jobs tab
        if(fragment.currenttab==0 && mJobsList.size==5
                && ((context as HomeActivity).sharedPreferences.getString(Constants.Membership_id).equals("")
                        ||(context as HomeActivity).sharedPreferences.getString(Constants.Membership_id).equals("0")
                        || (context as HomeActivity).sharedPreferences.getString(Constants.Membership_id).equals("1")
                        || (context as HomeActivity).sharedPreferences.getString(Constants.Membership_id).equals("6")
                        ))
        {
            Timber.e("Add Ad")
            var model = JobsPojo.DataBean()
            model.viewType = "Ad"
            mJobsList.add(model)
            notifyItemInserted(mJobsList.size - 1)
        }
    }


    fun addAll(Items: MutableList<JobsPojo.DataBean>)
    {
        for (response in Items)
        {
            add(response)
        }
    }

    fun removeAll()
    {

        mJobsList.clear()
        notifyDataSetChanged()
    }

    fun remove(position: Int)
    {
        mJobsList.removeAt(position)
        notifyItemRemoved(position)
    }
    override fun getItemCount(): Int
    {
        return mJobsList.size
    }


    fun addLoading()
    {
        isLoaderVisible = true
        add(JobsPojo.DataBean())
    }

    fun removeLoading()
    {
        isLoaderVisible = false

        val position = mJobsList.size - 1
        val result = getItem(position)

        if (result != null) {
            mJobsList.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    fun getItem(position: Int): JobsPojo.DataBean? {
        return mJobsList.get(position)
    }

    override fun getItemViewType(position: Int): Int {


        return if (isLoaderVisible)
        {
            if (position == mJobsList.size - 1)
                VIEW_TYPE_LOADING
            else
            {
                if(mJobsList.get(position).viewType.equals("Ad",ignoreCase = true))
                {
                    Timber.e("Type Ad")
                    VIEW_TYPE_AD
                }
                else
                {
                    Timber.e("Type View")
                    VIEW_TYPE_NORMAL
                }
            }
        }

        else
        {
            VIEW_TYPE_NORMAL
        }
    }

     inner class ViewHolder(var bindingObj: JobsItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
     inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)
     inner class AdViewHolder(var bindingObj: AdItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)

}
