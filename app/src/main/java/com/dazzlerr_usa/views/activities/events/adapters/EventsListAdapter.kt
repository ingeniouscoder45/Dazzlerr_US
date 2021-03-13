package com.dazzlerr_usa.views.activities.events.adapters

import android.content.Context
import android.content.Intent
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
import com.dazzlerr_usa.databinding.BloglistAdapterItemLayoutBinding
import com.dazzlerr_usa.databinding.EventlistAdapterItemLayoutBinding
import com.dazzlerr_usa.views.activities.eventdetails.EventDetailsActivity
import com.dazzlerr_usa.views.activities.events.pojos.EventsListPojo
import java.text.SimpleDateFormat


class EventsListAdapter(val mContext:Context, val mList: MutableList<EventsListPojo.ResultBean>, val adapterType: String): RecyclerView.Adapter<EventsListAdapter.MyViewHolder>()
{

    private var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val bindingObj: EventlistAdapterItemLayoutBinding
        bindingObj = DataBindingUtil.inflate(LayoutInflater.from(parent.context) , R.layout.eventlist_adapter_item_layout , parent ,false )

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

        simpledateFormat(holder.bindingObj.eventDateMonthTxt ,holder.bindingObj.eventDateYearTxt , mList.get(position)._EventStartDate.toString())
        //setAnimation(holder ,position)

        Glide.with(mContext)
                .load(mList.get(position).image_url)
                .apply(RequestOptions().centerCrop().fitCenter())
                .thumbnail(0.3f)
                .placeholder(R.drawable.event_placeholder)
                .into(holder.bindingObj.eventImage)

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("event_id" , ""+mList.get(position).event_id)
            mContext?.startActivity(Intent(mContext, EventDetailsActivity::class.java).putExtras(bundle))
        }
    }

    override fun getItemId(position: Int): Long {
        return mList.get(position).event_id.toLong()
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

    fun simpledateFormat(month: TextView, year:TextView, date : String)
    {
        try
        {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val datetime = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed
            val formatter = SimpleDateFormat("dd MMM-yyyy") //If you need time just put specific format for time like 'HH:mm:ss'
            var dateStr = ""

            dateStr = formatter.format(datetime)

            if(dateStr.split("-").size==2)
            {
                month.text = dateStr.split("-").get(0)
                year.text = dateStr.split("-").get(1)
            }
        }

        catch (e:Exception)
        {
            e.printStackTrace()
            month.text = ""
        }

    }

    inner class MyViewHolder(var bindingObj: EventlistAdapterItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)

}