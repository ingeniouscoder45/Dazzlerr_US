package com.dazzlerr_usa.views.fragments.talentdashboard.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.JobsItemLayoutBinding
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.databinding.RecommendedJobsItemLayoutBinding
import com.dazzlerr_usa.views.activities.jobsdetails.JobsDetailsActivity
import com.dazzlerr_usa.views.fragments.jobs.JobsForAdapter
import com.dazzlerr_usa.views.fragments.talentdashboard.TalentDashboardPojo
import com.google.android.flexbox.*
import java.text.SimpleDateFormat


class TalentDashRecommendedJobsAdapter(internal var context: Context?, var mJobsList: MutableList<TalentDashboardPojo.RecommendedJobsBean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    var VIEW_TYPE_LOADING = 0
    var VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {

        if (viewType == VIEW_TYPE_LOADING)
        {
            val bindingObj : PaginationLoadingLayoutBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pagination_loading_layout, parent, false)

            return LoadingViewHolder(bindingObj)
        }

        else
        {
            val binderObject = DataBindingUtil.inflate<RecommendedJobsItemLayoutBinding>(LayoutInflater.from(parent
                    .context), R.layout.recommended_jobs_item_layout, parent, false)

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
            viewHolder.bindingObj?.jobsBinder?.created_on = fullDateFormatter(mJobsList[i].created_on.toString())

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
            //layoutManager.alignCccccccccccccccccccccccccccccccvcvontent = AlignContent.STRETCH
            servicesLayoutManager.alignItems = AlignItems.STRETCH
            servicesLayoutManager.flexWrap = FlexWrap.WRAP
            viewHolder.bindingObj.jobsForRecycler.setLayoutManager(servicesLayoutManager)
            viewHolder.bindingObj.jobsForRecycler.adapter = mServicesAdapter




            viewHolder.bindingObj.executePendingBindings()

            viewHolder.itemView.setOnClickListener {

                val bundle = Bundle()
                bundle.putString("call_id" , ""+mJobsList.get(i).call_id)
                context?.startActivity(Intent(context , JobsDetailsActivity::class.java).putExtras(bundle))

            }
        }
    }

    fun fullDateFormatter(date : String)  :String
    {
        var dateStr = ""
        try {
            if (date!=null && date.isNotEmpty() && !date.equals("null")) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                val datetime = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed
                //val formatter = SimpleDateFormat("dd MMM, yyyy hh:mm a") //If you need time just put specific format for time like 'HH:mm:ss'
                val formatter = SimpleDateFormat("dd MMM, yyyy") //If you need time just put specific format for time like 'HH:mm:ss'
                dateStr = formatter.format(datetime)
                return dateStr
            }

        }
        catch (e: java.lang.Exception)
        {
            e.printStackTrace()
        }
        return dateStr
    }

    fun add(response: TalentDashboardPojo.RecommendedJobsBean)
    {
        mJobsList.add(response)
        notifyItemInserted(mJobsList.size - 1)

    }

    fun addAll(Items: MutableList<TalentDashboardPojo.RecommendedJobsBean>)
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
        add(TalentDashboardPojo.RecommendedJobsBean())
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


    fun getItem(position: Int): TalentDashboardPojo.RecommendedJobsBean? {
        return mJobsList.get(position)
    }

    override fun getItemViewType(position: Int): Int {

        return if (isLoaderVisible)
        {
            if (position == mJobsList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        }
        else
        {
            VIEW_TYPE_NORMAL
        }
    }

     inner class ViewHolder(var bindingObj: RecommendedJobsItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
     inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}
