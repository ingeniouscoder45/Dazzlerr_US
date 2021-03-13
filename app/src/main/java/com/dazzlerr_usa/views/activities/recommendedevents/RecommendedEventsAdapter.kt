package com.dazzlerr_usa.views.activities.recommendedevents

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.EventsItemLayoutBinding
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.databinding.RecommendedEventsItemLayoutBinding
import com.dazzlerr_usa.views.activities.eventdetails.EventDetailsActivity
import com.dazzlerr_usa.views.activities.events.pojos.EventsListPojo
import java.text.SimpleDateFormat


class RecommendedEventsAdapter(internal var context: Context, var mEventList: MutableList<EventsListPojo.ResultBean>) : androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            val binderObject = DataBindingUtil.inflate<RecommendedEventsItemLayoutBinding>(LayoutInflater.from(parent
                    .context), R.layout.recommended_events_item_layout, parent, false)

            return ViewHolder(binderObject)
        }


    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int)
    {

        if(viewHolder is ViewHolder)
        {
        viewHolder.bindingObj.eventsBinder = mEventList[i]
        viewHolder.bindingObj.executePendingBindings()

        Glide.with(context)
                .load(mEventList[i].image_url)
                .apply(RequestOptions().centerCrop().fitCenter())
                .thumbnail(0.3f)
                .placeholder(R.drawable.placeholder)
                .into(viewHolder.bindingObj.eventImage)

            dateFormat(viewHolder.bindingObj.eventDateTxt , mEventList[i]._EventStartDate.toString())

        viewHolder.itemView.setOnClickListener {

            val bundle = Bundle()
            bundle.putString("event_id" , ""+mEventList.get(i).event_id)
            context?.startActivity(Intent(context, EventDetailsActivity::class.java).putExtras(bundle))
        }
        }
    }

    fun dateFormat(view: TextView, date : String)
    {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val mDate = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed
            val formatter = SimpleDateFormat("MMMM dd, yyyy @ hh:mm a") //If you need time just put specific format for time like 'HH:mm:ss'
            val dateStr = formatter.format(mDate)
            view.text = dateStr
        }
        catch (e:Exception)
        {
            e.printStackTrace()
            view.text = date
        }
    }

    fun add(response: EventsListPojo.ResultBean)
    {
        mEventList.add(response)
        notifyItemInserted(mEventList.size - 1)

    }


    fun removeAll()
    {

        mEventList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int
    {
        return mEventList.size
    }


    fun addLoading()
    {
        isLoaderVisible = true
        add(EventsListPojo.ResultBean())
    }

    fun removeLoading()
    {
        isLoaderVisible = false

        val position = mEventList.size - 1
        val result = getItem(position)

        if (result != null) {
            mEventList.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    fun getItem(position: Int): EventsListPojo.ResultBean? {
        return mEventList.get(position)
    }

    override fun getItemId(position: Int): Long
    {
        val product = mEventList.get(position)
        return product.event_id.toLong()
    }

    override fun getItemViewType(position: Int): Int {

        return if (isLoaderVisible)
        {
            if (position == mEventList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        }
        else
        {
            VIEW_TYPE_NORMAL
        }
    }

     inner class ViewHolder(var bindingObj: RecommendedEventsItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
     inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}
