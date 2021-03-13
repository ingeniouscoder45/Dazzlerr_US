package com.dazzlerr_usa.views.fragments.home.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.HomeModelsLayoutBinding
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.views.activities.OthersProfileActivity
import com.dazzlerr_usa.views.fragments.home.pojos.ModelsPojo

class FeaturedAdapter(internal var context: Context?, internal var mModelList: MutableList<ModelsPojo.DataBean>) : androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            val binderObject: HomeModelsLayoutBinding
            binderObject = DataBindingUtil.inflate(LayoutInflater.from(parent
                    .context), R.layout.home_models_layout, parent, false)

            return ViewHolder(binderObject)

        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int)
    {

        if(viewHolder is ViewHolder)
        {
            viewHolder.bindingObj.modelBinder = mModelList[i]
            viewHolder.bindingObj.executePendingBindings()

            viewHolder.bindingObj.isFeaturedOrTopRated.visibility = View.VISIBLE
            viewHolder.bindingObj.isFeaturedOrTopRated.setBackgroundResource(R.drawable.ic_featured)

            viewHolder.itemView.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("user_id" , ""+mModelList.get(i).user_id)
                bundle.putString("user_role" , ""+mModelList.get(i).user_role)
                bundle.putString("is_featured" , "1")
                context?.startActivity(Intent(context, OthersProfileActivity::class.java).putExtras(bundle))
            }
        }
    }


    fun add(response: ModelsPojo.DataBean)
    {
        mModelList.add(response)
        notifyItemInserted(mModelList.size - 1)
    }

    fun addAll(Items: List<ModelsPojo.DataBean>)
    {
        for (response in Items)
        {
            add(response)
        }
    }

    override fun getItemCount(): Int
    {
        return mModelList.size
    }


    fun addLoading()
    {
        isLoaderVisible = true
        add(ModelsPojo.DataBean())
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


    fun getItem(position: Int): ModelsPojo.DataBean? {
        return mModelList.get(position)
    }


    override fun getItemId(position: Int): Long {
        return mModelList.get(position).user_id.toLong()
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


     inner class ViewHolder(var bindingObj: HomeModelsLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
     inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)


}
