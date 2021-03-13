package com.dazzlerr_usa.views.fragments.castingdashboard.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import android.view.animation.AnimationUtils
import com.dazzlerr_usa.databinding.DashboardActiveJobsItemLayoutBinding
import com.dazzlerr_usa.databinding.DashboardFeaturedTalentsItemLayoutBinding
import com.dazzlerr_usa.databinding.DashboardFeaturedjobsItemLayoutBinding
import com.dazzlerr_usa.databinding.InterestedTalentsItemLayoutBinding
import com.dazzlerr_usa.views.activities.OthersProfileActivity
import com.dazzlerr_usa.views.fragments.castingdashboard.CastingDashboardPojo


class FeaturedTalentsAdapter(val mContext:Context, val mList: MutableList<CastingDashboardPojo.FeaturedTalentBean>): RecyclerView.Adapter<FeaturedTalentsAdapter.MyViewHolder>()
{

    private var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val bindingObj: DashboardFeaturedTalentsItemLayoutBinding
        bindingObj = DataBindingUtil.inflate(LayoutInflater.from(parent.context) , R.layout.dashboard_featured_talents_item_layout , parent ,false )

        return MyViewHolder(bindingObj)
    }

    override fun getItemCount(): Int
    {
        return mList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        holder.bindingObj.bindingObj = mList.get(position)
        holder.bindingObj.executePendingBindings()

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("user_id" , ""+mList.get(position).user_id)
            //bundle.putString("user_role" , ""+mList.get(i).user_role)
            bundle.putString("is_featured" , "1")

            mContext?.startActivity(Intent(mContext, OthersProfileActivity::class.java).putExtras(bundle))
        }
    }

    override fun getItemId(position: Int): Long {
        return mList.get(position).user_id.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onViewDetachedFromWindow(holder: MyViewHolder)
    {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    private fun setAnimation(holder: MyViewHolder, position: Int)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            val animation = AnimationUtils.loadAnimation(mContext,
                    if (position > lastPosition)
                        R.anim.enter_from_bottom
                    else
                        R.anim.exit_to_bottom)
            holder.itemView.startAnimation(animation)
            lastPosition = position
        }

    }

    inner class MyViewHolder(var bindingObj: DashboardFeaturedTalentsItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)

}