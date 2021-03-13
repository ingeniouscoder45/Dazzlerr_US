package com.dazzlerr_usa.views.fragments.jobs

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.AppliedJobsItemLayoutBinding
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.views.activities.jobsdetails.JobsDetailsActivity
import com.google.android.flexbox.*


class AppliedJobsAdapter(internal var context: Context?, var mJobsList: MutableList<AppliedJobsPojo.DataBean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
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
            val binderObject = DataBindingUtil.inflate<AppliedJobsItemLayoutBinding>(LayoutInflater.from(parent
                    .context), R.layout.applied_jobs_item_layout, parent, false)

            return ViewHolder(binderObject)
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int)
    {

        if(viewHolder is ViewHolder)
        {
        viewHolder.bindingObj.jobsBinder = mJobsList[i]
            viewHolder.bindingObj?.jobsBinder?.title = mJobsList[i].title?.capitalize()
            viewHolder.bindingObj?.jobsBinder?.casting_name = mJobsList[i].casting_name?.capitalize()
            viewHolder.bindingObj?.jobsBinder?.name = mJobsList[i].name?.capitalize()

            dateFormat(viewHolder.bindingObj?.appliedJobsDateTxt ,mJobsList[i].start_date.toString())

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

/*
            if(mJobsList.get(i).status.equals("0"))
            {
                viewHolder.bindingObj.status.text = "Pause"
                viewHolder.bindingObj.status.setBackgroundColor(context?.resources?.getColor(R.color.colorYellow)!!)
            }
            else if(mJobsList.get(i).status.equals("1"))
            {
                viewHolder.bindingObj.status.text = "Active"
                viewHolder.bindingObj.status.setBackgroundColor(context?.resources?.getColor(R.color.colorGreen)!!)
            }
            else if(mJobsList.get(i).status.equals("2"))
            {
                viewHolder.bindingObj.status.text = "Completed"
                viewHolder.bindingObj.status.setBackgroundColor(context?.resources?.getColor(R.color.appColor)!!)
            }
*/


            viewHolder.itemView.setOnClickListener {

                val bundle = Bundle()
                bundle.putString("call_id" , ""+mJobsList.get(i).call_id)
                context?.startActivity(Intent(context , JobsDetailsActivity::class.java).putExtras(bundle))

            }
        }
    }

    fun add(response: AppliedJobsPojo.DataBean)
    {
        mJobsList.add(response)
        notifyItemInserted(mJobsList.size - 1)

    }

    fun addAll(Items: MutableList<AppliedJobsPojo.DataBean>)
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

    override fun getItemCount(): Int
    {
        return mJobsList.size
    }

    fun addLoading()
    {
        isLoaderVisible = true
        add(AppliedJobsPojo.DataBean())
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


    fun getItem(position: Int): AppliedJobsPojo.DataBean? {
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

     inner class ViewHolder(var bindingObj: AppliedJobsItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
     inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}
