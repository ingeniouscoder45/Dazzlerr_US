package com.dazzlerr_usa.views.fragments.castingdashboard.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.DashboardInterestedTalentsItemLayoutBinding
import com.dazzlerr_usa.views.activities.jobsdetails.JobsDetailsActivity
import com.dazzlerr_usa.views.fragments.castingdashboard.CastingDashboardPojo
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class DashboardInterestedTalentsAdapter(val mContext:Context, val mList: MutableList<CastingDashboardPojo.InterstedTalentBean>): RecyclerView.Adapter<DashboardInterestedTalentsAdapter.MyViewHolder>()
{

    private var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val bindingObj: DashboardInterestedTalentsItemLayoutBinding
        bindingObj = DataBindingUtil.inflate(LayoutInflater.from(parent.context) , R.layout.dashboard_interested_talents_item_layout , parent ,false )

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

        Glide.with(mContext)
                .load("https://www.dazzlerr.com/API/"+mList.get(position).profile_pic).apply(RequestOptions().centerCrop())
                .thumbnail(.2f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.model_placeholder)
                .into(holder.bindingObj.talentProfilePic)

        simpledateFormat(holder.bindingObj.interestedTalentAppliedDate , mList.get(position).applied_on.toString())
        setAnimation(holder ,position)

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("call_id" , ""+mList.get(position).call_id)
            bundle.putString("selectedModel" , ""+mList.get(position).user_id)
            mContext?.startActivity(Intent(mContext , JobsDetailsActivity::class.java).putExtras(bundle))
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
                //dateStr = "Today " + timeFormatter.format(datetime)

                val now = Date()
                val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(now.time - datetime.time)
                val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(now.time - datetime.time)
                val hours: Long = TimeUnit.MILLISECONDS.toHours(now.time - datetime.time)
                val days: Long = TimeUnit.MILLISECONDS.toDays(now.time - datetime.time)

                if(seconds<60)
                {
                    dateStr  = ""+ seconds +" seconds ago"
                }
                else if(minutes<60)
                {
                    dateStr = ""+ minutes+" minutes ago"
                }
                else if(hours<24)
                {
                    dateStr = ""+hours+" hours ago"
                }
                else
                {
                    dateStr =  ""+days+" days ago"
                }
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

    inner class MyViewHolder(var bindingObj: DashboardInterestedTalentsItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)

}