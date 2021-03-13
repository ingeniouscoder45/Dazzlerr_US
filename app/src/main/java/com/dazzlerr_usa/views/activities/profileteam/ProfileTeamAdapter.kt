package com.dazzlerr_usa.views.activities.profileteam

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import android.view.animation.AnimationUtils
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.databinding.TeamItemLayoutBinding
import com.dazzlerr_usa.views.activities.profileteam.pojos.GetTeamPojo


class ProfileTeamAdapter(val mContext:Context,  var mModelList: MutableList<GetTeamPojo.DataBean>): androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    var VIEW_TYPE_LOADING = 0
    var VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false
    private var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {

        if (viewType == VIEW_TYPE_LOADING)
        {
            val bindingObj : PaginationLoadingLayoutBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.pagination_loading_layout, parent, false)

            return LoadingViewHolder(bindingObj)
        }

        else {
            val bindingObj: TeamItemLayoutBinding
            bindingObj = DataBindingUtil.inflate(LayoutInflater.from(parent.context) , R.layout.team_item_layout , parent ,false )

            return MyViewHolder(bindingObj)
        }

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        if(holder is MyViewHolder) {
            holder.bindingObj.modelBinder = mModelList[position]
            holder.bindingObj.executePendingBindings()


            if (position > lastPosition) {
                val animation = AnimationUtils.loadAnimation(mContext,
                        if (position > lastPosition)
                            R.anim.enter_from_left
                        else
                            R.anim.exit_to_right)
                holder.itemView.startAnimation(animation)
                lastPosition = position
            }

        }
    }

    override fun onViewDetachedFromWindow(viewHolder: RecyclerView.ViewHolder)
    {
        super.onViewDetachedFromWindow(viewHolder)
        viewHolder.itemView.clearAnimation()
    }


    fun add(response: GetTeamPojo.DataBean)
    {
        mModelList.add(response)
        notifyItemInserted(mModelList.size - 1)

    }

    fun addAll(Items: MutableList<GetTeamPojo.DataBean>)
    {
        for (response in Items)
        {
            add(response)
        }
    }

    fun removeAll()
    {
        mModelList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int
    {
        return mModelList.size
    }


    fun addLoading()
    {
        isLoaderVisible = true
        add(GetTeamPojo.DataBean())
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


    fun getItem(position: Int): GetTeamPojo.DataBean? {
        return mModelList.get(position)
    }

    override fun getItemId(position: Int): Long {
        val product = mModelList.get(position)
        return product.id.toLong()
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

    inner class MyViewHolder(var bindingObj: TeamItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
    inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}