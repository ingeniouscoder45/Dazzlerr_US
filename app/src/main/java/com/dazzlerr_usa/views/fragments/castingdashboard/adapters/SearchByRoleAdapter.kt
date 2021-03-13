package com.dazzlerr_usa.views.fragments.castingdashboard.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.databinding.CastingDashboardRoleItemLayoutBinding
import com.dazzlerr_usa.databinding.DashboardActiveJobsItemLayoutBinding
import com.dazzlerr_usa.databinding.DashboardFeaturedjobsItemLayoutBinding
import com.dazzlerr_usa.databinding.InterestedTalentsItemLayoutBinding
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.fragments.castingdashboard.CastingDashboardPojo
import com.dazzlerr_usa.views.fragments.talent.TalentFragment
import java.text.SimpleDateFormat
import java.util.*


class SearchByRoleAdapter(val mContext:Context, val mList: MutableList<CastingDashboardPojo.CategoryRolesBean>): RecyclerView.Adapter<SearchByRoleAdapter.MyViewHolder>()
{

    private var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val bindingObj: CastingDashboardRoleItemLayoutBinding
        bindingObj = DataBindingUtil.inflate(LayoutInflater.from(parent.context) , R.layout.casting_dashboard_role_item_layout , parent ,false )

        return MyViewHolder(bindingObj)
    }

    override fun getItemCount(): Int
    {
        return mList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        holder.bindingObj.rolesBinder = mList.get(position)
        holder.bindingObj.executePendingBindings()
        Glide.with(mContext!!).load("https://www.dazzlerr.com/API/"+mList.get(position).role_img).apply(RequestOptions().centerCrop()).into(holder.bindingObj.categoryImage)

        holder.itemView.setOnClickListener {
            val bundle  = Bundle()
            bundle.putString("category_id" , mList.get(position).user_role_id)

            val fragment = TalentFragment()
            fragment.arguments = bundle
            (mContext as HomeActivity).loadFirstFragment(fragment)
        }

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
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

    fun simpledateFormat(view: TextView, date : String)
    {
        try
        {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            val datetime = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed
            val formatter = SimpleDateFormat("MMM dd, yyyy") //If you need time just put specific format for time like 'HH:mm:ss'
            var dateStr = ""

            val calendar = Calendar.getInstance()
            calendar.time = datetime
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DATE, -1)
            val timeFormatter = SimpleDateFormat("hh:mma")

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
            {
                dateStr = "Today " + timeFormatter.format(datetime)
            }
            else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR))
            {
                dateStr = "Yesterday " + timeFormatter.format(datetime)
            }
            else
            {
                dateStr = formatter.format(datetime)
            }
            view.text = dateStr
        }

        catch (e:Exception)
        {
            e.printStackTrace()
            view.text = date
        }

    }

    inner class MyViewHolder(var bindingObj: CastingDashboardRoleItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)

}