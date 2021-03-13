package com.dazzlerr_usa.views.activities.interestedtalents

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.InterestedTalentsItemLayoutBinding
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.views.activities.jobsdetails.JobsDetailsActivity
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class InterestedTalentsAdapter(internal var context: Context, var mModelList: MutableList<InterestedTalentsPojo.DataBean>) : androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            val binderObject = DataBindingUtil.inflate<InterestedTalentsItemLayoutBinding>(LayoutInflater.from(parent.context), R.layout.interested_talents_item_layout, parent, false)

            return ViewHolder(binderObject)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        //animateView(i , viewHolder)

        if(holder is ViewHolder)
        {
            holder.bindingObj.bindingObj = mModelList.get(position)
            holder.bindingObj.executePendingBindings()

            Glide.with(context)
                    .load("https://www.dazzlerr.com/API/"+mModelList.get(position).profile_pic).apply(RequestOptions().centerCrop())
                    .thumbnail(.2f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.model_placeholder)
                    .into(holder.bindingObj.talentProfilePic)

            simpledateFormat(holder.bindingObj.interestedTalentAppliedDate , mModelList.get(position).applied_on.toString())
            setAnimation(holder ,position)

            holder.itemView.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("call_id" , ""+mModelList.get(position).call_id)
                bundle.putString("selectedModel" , ""+mModelList.get(position).user_id)
                context?.startActivity(Intent(context , JobsDetailsActivity::class.java).putExtras(bundle))
            }
        }
    }

    private fun setAnimation(holder: RecyclerView.ViewHolder, position: Int)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            val animation = AnimationUtils.loadAnimation(context,
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

    override fun onViewDetachedFromWindow(viewHolder: RecyclerView.ViewHolder)
    {
        super.onViewDetachedFromWindow(viewHolder)
        viewHolder.itemView.clearAnimation()
    }

    fun add(response: InterestedTalentsPojo.DataBean)
    {
        mModelList.add(response)
        notifyItemInserted(mModelList.size - 1)

    }

    fun addAll(Items: MutableList<InterestedTalentsPojo.DataBean>)
    {
        for (response in Items)
        {
            add(response)
        }
    }

    fun removeAll()
    {

        mModelList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int
    {
        return mModelList.size
    }


    fun addLoading()
    {
        isLoaderVisible = true
        add(InterestedTalentsPojo.DataBean())
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


    fun getItem(position: Int): InterestedTalentsPojo.DataBean? {
        return mModelList.get(position)
    }

    override fun getItemId(position: Int): Long {
        val product = mModelList.get(position)
        return product.user_id.toLong()
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

     inner class ViewHolder(var bindingObj: InterestedTalentsItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
     inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}
