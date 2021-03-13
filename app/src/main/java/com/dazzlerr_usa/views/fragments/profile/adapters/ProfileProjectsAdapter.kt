package com.dazzlerr_usa.views.fragments.profile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.databinding.ProjectsItemLayoutBinding
import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioProjectsPojo

class ProfileProjectsAdapter(val mContext: Context, var mModelList: MutableList<PortfolioProjectsPojo.DataBean>, val mFragment :Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    var VIEW_TYPE_LOADING = 0
    var VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        if (viewType == VIEW_TYPE_LOADING)
        {
            val bindingObj : PaginationLoadingLayoutBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.pagination_loading_layout, parent, false)

            return LoadingViewHolder(bindingObj)
        }

        else
        {
            val bindingObj = DataBindingUtil.inflate<ProjectsItemLayoutBinding>(LayoutInflater.from(mContext), R.layout.projects_item_layout, parent, false)
            return MyViewHolder(bindingObj)
        }
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {

        if(holder is MyViewHolder)
        {
            holder.bindingObj.projectsBinder = mModelList.get(position)
            holder.bindingObj.executePendingBindings()

            holder.bindingObj.modifyLayout.visibility = View.GONE
        }
    }



    fun add(response: PortfolioProjectsPojo.DataBean)
    {

        mModelList.add(response)
        notifyItemInserted(mModelList.size - 1)
    }

    fun addAll(Items: MutableList<PortfolioProjectsPojo.DataBean>)
    {
        for (response in Items)
        {
            add(response)

        }
    }

    fun removeAll()
    {
        val size = mModelList.size
        mModelList.clear();
        notifyItemRangeRemoved(0, size);
    }

    override fun getItemCount(): Int
    {
        return mModelList.size
    }

    fun removeItem(position: Int)
    {
        mModelList.removeAt(position)
        notifyDataSetChanged()
    }


    fun addLoading()
    {
        isLoaderVisible = true
        add(PortfolioProjectsPojo.DataBean())
    }

    fun removeLoading()
    {
        isLoaderVisible = false

        val position = mModelList.size - 1
        val result = getItem(position)

        if (result != null) {
            mModelList.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    fun getItem(position: Int): PortfolioProjectsPojo.DataBean? {
        return mModelList.get(position)
    }


    override fun getItemViewType(position: Int): Int {

        return if (isLoaderVisible)
        {
            if (position == mModelList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        }
        else
        {
            VIEW_TYPE_NORMAL
        }
    }


    inner class MyViewHolder(var bindingObj: ProjectsItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
    inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}