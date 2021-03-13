package com.dazzlerr_usa.views.activities.events

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
import com.dazzlerr_usa.views.activities.eventdetails.EventDetailsActivity
import com.dazzlerr_usa.views.activities.events.activities.EventsActivity
import com.dazzlerr_usa.views.activities.events.pojos.EventsPojo
import java.text.SimpleDateFormat


class EventsAdapter(internal var context: Context, var mEventList: MutableList<EventsPojo.ResultBean>) : androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            val binderObject = DataBindingUtil.inflate<EventsItemLayoutBinding>(LayoutInflater.from(parent
                    .context), R.layout.events_item_layout, parent, false)

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
                .load(mEventList[i].event_image)
                .apply(RequestOptions().centerCrop().fitCenter())
                .thumbnail(0.3f)
                .placeholder(R.drawable.placeholder)
                .into(viewHolder.bindingObj.eventImage)

            dateFormat(viewHolder.bindingObj.eventDateTxt , mEventList[i].start_date.toString())

        viewHolder.itemView.setOnClickListener {

            (context as EventsActivity).hideSearchLayout()
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

    fun add(response: EventsPojo.ResultBean)
    {
        mEventList.add(response)
        notifyItemInserted(mEventList.size - 1)

    }

    fun addAll(Items: MutableList<EventsPojo.ResultBean>, eventimagelist: List<EventsPojo.EventImagesBean>, eventVenuelist: List<EventsPojo.EventVenueBean>)
    {
        for (response in Items)
        {
            for(i in eventimagelist.indices)
            {
                if(eventimagelist.get(i).ID.toString().equals(response._thumbnail_id)) {
                    response.event_image = eventimagelist.get(i).guid
                }
            }

            for(i in eventVenuelist.indices)
            {
                if(eventVenuelist.get(i).post_id.toString().equals(response._EventVenueID)) {
                    response.venue = eventVenuelist.get(i).event_city
                }
            }

            add(response)
        }
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
        add(EventsPojo.ResultBean())
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


    fun getItem(position: Int): EventsPojo.ResultBean? {
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

     inner class ViewHolder(var bindingObj: EventsItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
     inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}
