package com.dazzlerr_usa.views.activities.influencers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.InfluencersItemLayoutBinding
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.influencerdetails.InfluencerDetailsActivity


class InfluencersAdapter(internal var context: Context?, var mEliteList: MutableList<InfluencersPojo.ResultBean>) : androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            val binderObject = DataBindingUtil.inflate<InfluencersItemLayoutBinding>(LayoutInflater.from(parent
                    .context), R.layout.influencers_item_layout, parent, false)

            return ViewHolder(binderObject)
        }


    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int)
    {


        if(viewHolder is ViewHolder)
        {
        animateView(i , viewHolder)

        viewHolder.bindingObj.modelBinder = mEliteList[i]
        viewHolder.bindingObj.executePendingBindings()

            if(mEliteList.get(i).is_featured.equals("1"))
            {
                viewHolder.bindingObj.featuredIcon.visibility = View.VISIBLE
            }
            else
                viewHolder.bindingObj.featuredIcon.visibility = View.GONE

        viewHolder.itemView.setOnClickListener {

            if(mEliteList.get(i).is_profile_complete.equals("true",ignoreCase = true)) {
                val bundle = Bundle()
                bundle.putString("profile_id", "" + mEliteList.get(i).user_id)
                context?.startActivity(Intent(context, InfluencerDetailsActivity::class.java).putExtras(bundle))
            }
            else
            {
                val dialog = CustomDialog(context)
                dialog.setTitle("Coming Soon!")
                dialog.setMessage("This feature is coming soon. Stay connected.")
                dialog.setPositiveButton("Ok" , DialogListenerInterface.onPositiveClickListener {

                    dialog.dismiss()
                })
                dialog.show()
            }
        }
        }
    }

    fun animateView(position: Int , viewHolder: RecyclerView.ViewHolder )
    {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(context,
                    if (position > lastPosition)
                    {
                        if(position%2==0)
                            R.anim.enter_from_left

                        else
                            R.anim.enter_from_right
                    }
                    else
                        R.anim.exit_to_bottom)
            viewHolder.itemView.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun onViewDetachedFromWindow(viewHolder: RecyclerView.ViewHolder)
    {
        super.onViewDetachedFromWindow(viewHolder)
        viewHolder.itemView.clearAnimation()
    }


    fun add(response: InfluencersPojo.ResultBean)
    {
        mEliteList.add(response)
        notifyItemInserted(mEliteList.size - 1)

    }

    fun addAll(Items: MutableList<InfluencersPojo.ResultBean>)
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
        add(InfluencersPojo.ResultBean())
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


    fun getItem(position: Int): InfluencersPojo.ResultBean? {
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

     inner class ViewHolder(var bindingObj: InfluencersItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
     inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}
