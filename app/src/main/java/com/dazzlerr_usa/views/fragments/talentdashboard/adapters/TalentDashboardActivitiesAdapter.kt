package com.dazzlerr_usa.views.fragments.talentdashboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.dazzlerr_usa.databinding.DashboardActivitiesItemLayoutBinding
import com.dazzlerr_usa.views.fragments.talentdashboard.TalentDashboardPojo
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class TalentDashboardActivitiesAdapter(val mContext:Context, val mList: MutableList<TalentDashboardPojo.ActivityLogBean>): RecyclerView.Adapter<TalentDashboardActivitiesAdapter.MyViewHolder>()
{

    private var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val bindingObj: DashboardActivitiesItemLayoutBinding
        bindingObj = DataBindingUtil.inflate(LayoutInflater.from(parent.context) , R.layout.dashboard_activities_item_layout , parent ,false )

        return MyViewHolder(bindingObj)
    }

    override fun getItemCount(): Int
    {
        return mList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        holder.bindingObj.binderObj = mList.get(position)
        holder.bindingObj.executePendingBindings()
        setAnimation(holder ,position)

        simpledateFormat(holder.bindingObj.activityTimeTxt , mList.get(position).activity_date.toString())
    }

    override fun getItemId(position: Int): Long
    {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int
    {
        return position
    }

    override fun onViewDetachedFromWindow(holder: MyViewHolder)
    {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    fun simpledateFormat(view: TextView, date : String)
    {
        try
        {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
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

                val now = Date()
                val days: Long = TimeUnit.MILLISECONDS.toDays(now.time - datetime.time)
                dateStr =  ""+days+" days ago"
                //dateStr = formatter.format(datetime)
            }
            view.text = dateStr
        }

        catch (e:Exception)
        {
            e.printStackTrace()
            view.text = date
        }

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
    inner class MyViewHolder(var bindingObj: DashboardActivitiesItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)

}