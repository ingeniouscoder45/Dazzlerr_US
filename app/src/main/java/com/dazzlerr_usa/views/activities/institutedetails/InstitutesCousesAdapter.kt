package com.dazzlerr_usa.views.activities.institutedetails

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.CoursesOfferedItemLayoutBinding
import com.dazzlerr_usa.databinding.InstituteItemLayoutBinding
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding


class InstitutesCousesAdapter(internal var context: Context?, var mInstituteList: MutableList<InstituteDetailsPojo.Course>) : androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var VIEW_TYPE_LOADING = 0
    var VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false
    private var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {

        if (viewType == VIEW_TYPE_LOADING)
        {
            val bindingObj : PaginationLoadingLayoutBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pagination_loading_layout, parent, false)

            return LoadingViewHolder(bindingObj)
        }

        else {
            val binderObject = DataBindingUtil.inflate<CoursesOfferedItemLayoutBinding>(LayoutInflater.from(parent
                    .context), R.layout.courses_offered_item_layout, parent, false)

            return ViewHolder(binderObject)
        }


    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int)
    {


        if(viewHolder is ViewHolder)
        {

        viewHolder.bindingObj.binderObj = mInstituteList[i]
        viewHolder.bindingObj.executePendingBindings()

        }
    }



    override fun onViewDetachedFromWindow(viewHolder: RecyclerView.ViewHolder)
    {
        super.onViewDetachedFromWindow(viewHolder)
        viewHolder.itemView.clearAnimation()
    }


    fun add(response: InstituteDetailsPojo.Course)
    {
        mInstituteList.add(response)
        notifyItemInserted(mInstituteList.size - 1)

    }

    fun addAll(Items: MutableList<InstituteDetailsPojo.Course>)
    {
        for (response in Items)
        {
            add(response)
        }
    }

    fun removeAll()
    {

        mInstituteList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int
    {
        return mInstituteList.size
    }


    fun addLoading()
    {
        isLoaderVisible = true
        add(InstituteDetailsPojo.Course())
    }

    fun removeLoading()
    {
        isLoaderVisible = false

        val position = mInstituteList.size - 1
        val result = getItem(position)

        if (result != null) {
            mInstituteList.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    fun getItem(position: Int): InstituteDetailsPojo.Course? {
        return mInstituteList.get(position)
    }

    override fun getItemId(position: Int): Long {

        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {

        return if (isLoaderVisible)
        {
            if (position == mInstituteList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        }
        else
        {
            VIEW_TYPE_NORMAL
        }
    }

     inner class ViewHolder(var bindingObj: CoursesOfferedItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
     inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}
