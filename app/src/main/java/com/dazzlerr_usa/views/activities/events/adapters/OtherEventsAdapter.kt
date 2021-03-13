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
import com.dazzlerr_usa.databinding.OtherEventlistItemLayoutBinding
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.views.activities.eventdetails.EventDetailsActivity
import com.dazzlerr_usa.views.activities.events.pojos.EventsListPojo
import java.text.SimpleDateFormat


class OtherEventsAdapter(val mContext:Context, val mList: MutableList<EventsListPojo.ResultBean>): RecyclerView.Adapter< RecyclerView.ViewHolder>()
{

    private var lastPosition = -1
    var VIEW_TYPE_LOADING = 0
    var VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {

        if (viewType == VIEW_TYPE_LOADING)
        {
            val bindingObj : PaginationLoadingLayoutBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.pagination_loading_layout, parent, false)

            return LoadingViewHolder(bindingObj)
        }

        else
        {
            val bindingObj: OtherEventlistItemLayoutBinding
            bindingObj = DataBindingUtil.inflate(LayoutInflater.from(parent.context) , R.layout.other_eventlist_item_layout , parent ,false )

            return MyViewHolder(bindingObj)
        }

    }

    override fun getItemCount(): Int
    {
        return mList.size
    }

    override fun onBindViewHolder(holder:  RecyclerView.ViewHolder, position: Int)
    {
        if(holder is MyViewHolder)
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

        }



    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder)
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
    fun add(response: EventsListPojo.ResultBean)
    {
        mList.add(response)
        notifyItemInserted(mList.size - 1)

    }

    fun addAll(Items: MutableList<EventsListPojo.ResultBean>)
    {
        for (response in Items)
        {
            add(response)
        }
    }

    fun removeAll()
    {

        mList.clear()
        notifyDataSetChanged()
    }

    fun removeAllItems()
    {
        mList.clear();
        notifyDataSetChanged();

    }

    fun addLoading()
    {
        isLoaderVisible = true
        add(EventsListPojo.ResultBean())
    }

    fun removeLoading()
    {
        isLoaderVisible = false

        val position = mList.size - 1
        val result = getItem(position)

        if (result != null) {
            mList.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    fun getItem(position: Int): EventsListPojo.ResultBean? {
        return mList.get(position)
    }

    override fun getItemId(position: Int): Long {
        val product = mList.get(position)
        return product.event_id.toLong()
    }

    override fun getItemViewType(position: Int): Int
    {

        return if (isLoaderVisible)
        {
            if (position == mList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        }
        else
        {
            VIEW_TYPE_NORMAL
        }
    }
    inner class MyViewHolder(var bindingObj: OtherEventlistItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
    inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)
}