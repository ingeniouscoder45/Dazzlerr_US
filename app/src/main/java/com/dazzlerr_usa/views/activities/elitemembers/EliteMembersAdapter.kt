package com.dazzlerr_usa.views.activities.elitemembers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.EliteItemLayoutBinding
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.views.activities.elitememberdetails.EliteMemberDetailsActivity
import timber.log.Timber


class EliteMembersAdapter(internal var context: Context?, var mEliteList: MutableList<EliteMembersPojo.ResultBean>) : androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

        else {
            val binderObject = DataBindingUtil.inflate<EliteItemLayoutBinding>(LayoutInflater.from(parent
                    .context), R.layout.elite_item_layout, parent, false)

            return ViewHolder(binderObject)
        }


    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {


        if(viewHolder is ViewHolder)
        {
        viewHolder.bindingObj.modelBinder = mEliteList[i]
        viewHolder.bindingObj.executePendingBindings()

            Timber.e(""+mEliteList.get(i).is_featured)


            if(mEliteList.get(i).is_featured.equals("1"))
            {
                viewHolder.bindingObj.featuredIcon.visibility = View.VISIBLE
            }
            else
                viewHolder.bindingObj.featuredIcon.visibility = View.GONE

        viewHolder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("profile_id" , ""+mEliteList.get(i).user_id)
            context?.startActivity(Intent(context, EliteMemberDetailsActivity::class.java).putExtras(bundle))
        }
        }
    }

    fun add(response: EliteMembersPojo.ResultBean)
    {
        mEliteList.add(response)
        notifyItemInserted(mEliteList.size - 1)

    }

    fun addAll(Items: MutableList<EliteMembersPojo.ResultBean>)
    {
        for (response in Items)
        {
            add(response)
        }
    }

    fun removeAll()
    {

        mEliteList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int
    {
        return mEliteList.size
    }

    fun addLoading()
    {
        isLoaderVisible = true
        add(EliteMembersPojo.ResultBean())
    }

    fun removeLoading()
    {
        isLoaderVisible = false

        val position = mEliteList.size - 1
        val result = getItem(position)

        if (result != null) {
            mEliteList.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    fun getItem(position: Int): EliteMembersPojo.ResultBean? {
        return mEliteList.get(position)
    }

    override fun getItemId(position: Int): Long {
        val product = mEliteList.get(position)
        return product.user_id.toLong()
    }

    override fun getItemViewType(position: Int): Int {

        return if (isLoaderVisible)
        {
            if (position == mEliteList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        }
        else
        {
            VIEW_TYPE_NORMAL
        }
    }

     inner class ViewHolder(var bindingObj: EliteItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
     inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}
