package com.dazzlerr_usa.views.activities.recommendedjobs

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
import com.dazzlerr_usa.views.activities.jobsdetails.JobsDetailsActivity
import com.dazzlerr_usa.views.fragments.jobs.JobsForAdapter
import com.dazzlerr_usa.views.fragments.jobs.JobsPojo
import com.dazzlerr_usa.views.fragments.jobs.dateFormat
import com.google.android.flexbox.*


class RecommendedJobsAdapter(internal var context: Context?, var mJobsList: MutableList<JobsPojo.DataBean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
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

            }
        }
    }

    fun add(response: JobsPojo.DataBean)
    {
        mJobsList.add(response)
        notifyItemInserted(mJobsList.size - 1)

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
            if (position == mJobsList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        }
        else
        {
            VIEW_TYPE_NORMAL
        }
    }

     inner class ViewHolder(var bindingObj: JobsItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
     inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}
