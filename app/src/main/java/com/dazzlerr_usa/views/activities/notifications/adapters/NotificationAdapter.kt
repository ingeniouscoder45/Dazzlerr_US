package com.dazzlerr_usa.views.activities.notifications.adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.*
import com.dazzlerr_usa.utilities.imageslider.EmojiRainLayout
import com.dazzlerr_usa.views.activities.OthersProfileActivity
import com.dazzlerr_usa.views.activities.eventdetails.EventDetailsActivity
import com.dazzlerr_usa.views.activities.jobsdetails.JobsDetailsActivity
import com.dazzlerr_usa.views.activities.notifications.NotificationsPojo
import com.dazzlerr_usa.views.activities.recommendedevents.RecommendedEventsActivity
import com.dazzlerr_usa.views.activities.recommendedjobs.RecommendedJobsActivity
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationAdapter(val mContext:Context, var mNotificationList: MutableList<NotificationsPojo.Data>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    var VIEW_TYPE_LOADING = 0
    var VIEW_TYPE_JOB = 1
    var VIEW_TYPE_TEXT = 2
    var VIEW_TYPE_IMAGE = 3
    var VIEW_TYPE_EVENT = 4
    private var isLoaderVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {

        when (viewType) {
            VIEW_TYPE_LOADING -> {
                val bindingObj : PaginationLoadingLayoutBinding =
                        DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.pagination_loading_layout, parent, false)

                return LoadingViewHolder(bindingObj)
            }
            VIEW_TYPE_JOB -> {

                val inflater: LayoutInflater = LayoutInflater.from(mContext)
                val bindingObj = DataBindingUtil.inflate<NotificationsJobItemLayoutBinding>(inflater, R.layout.notifications_job_item_layout, parent, false)

                return JobViewHolder(bindingObj)

            }
            VIEW_TYPE_EVENT -> {

                val inflater: LayoutInflater = LayoutInflater.from(mContext)
                val bindingObj = DataBindingUtil.inflate<NotificationsEventItemLayoutBinding>(inflater, R.layout.notifications_event_item_layout, parent, false)

                return EventViewHolder(bindingObj)

            }
            VIEW_TYPE_IMAGE -> {

                val inflater: LayoutInflater = LayoutInflater.from(mContext)
                val bindingObj = DataBindingUtil.inflate<NotificationsImageItemLayoutBinding>(inflater, R.layout.notifications_image_item_layout, parent, false)

                return ImageViewHolder(bindingObj)

            }
            else -> {

                val inflater: LayoutInflater = LayoutInflater.from(mContext)
                val bindingObj = DataBindingUtil.inflate<NotificationsTextItemLayoutBinding>(inflater, R.layout.notifications_text_item_layout, parent, false)

                return TextViewHolder(bindingObj)
            }
        }

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        when (holder) {
            is TextViewHolder -> {
                simpledateFormat(holder.bindingObj.notificaionTime , mNotificationList[position].created_on)

                holder.bindingObj.notificationTitle.text = mNotificationList[position].title
                holder.bindingObj.notificationMessage.text = mNotificationList[position].message

                if(mNotificationList[position].type.equals("occasional",ignoreCase = true))
                {
                    holder.bindingObj.notificationProfileImage.visibility = View.VISIBLE
                    holder.bindingObj.notificationProfileImage.setImageResource(R.drawable.occasional_confetti)
                }
                else
                    holder.bindingObj.notificationProfileImage.visibility = View.GONE
            }
            is EventViewHolder -> {
                simpledateFormat(holder.bindingObj.notificaionTime , mNotificationList[position].created_on)


                holder.bindingObj.notificationTitle.text = mNotificationList[position].title
                holder.bindingObj.notificationMessage.text = mNotificationList[position].message
            }
            is ImageViewHolder -> {
                simpledateFormat(holder.bindingObj.notificaionTime , mNotificationList[position].created_on)
                holder.bindingObj.notificationMessage.text = mNotificationList[position].message
                holder.bindingObj.notificationTitle.text = mNotificationList[position].title

                Glide.with(mContext)
                        .load("https://www.dazzlerr.com/API/"+mNotificationList[position].profile_pic).apply(RequestOptions().centerCrop())
                        .thumbnail(.2f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.profile_placeholder)
                        .into(holder.bindingObj.notificationProfileImage)
            }
            is JobViewHolder -> {
                simpledateFormat(holder.bindingObj.notificaionTime , mNotificationList[position].created_on)
                holder.bindingObj.notificationTitle.text = mNotificationList[position].title
                holder.bindingObj.notificationMessage.text = mNotificationList[position].message
            }
        }


        holder.itemView.setOnClickListener {

            if(mNotificationList[position].type.equals("job",ignoreCase = true)
            || mNotificationList[position].type.equals("new_job",ignoreCase = true)
            || mNotificationList[position].type.equals("application_viewed",ignoreCase = true)
            || mNotificationList[position].type.equals("proposal_shortlisted",ignoreCase = true)
            || mNotificationList[position].type.equals("proposal_rejected",ignoreCase = true)
            || mNotificationList[position].type.equals("proposal_hired",ignoreCase = true)
            )
            {
                var bundle = Bundle()
                bundle.putString("call_id" , ""+mNotificationList[position].id)
                var intent = Intent(mContext, JobsDetailsActivity::class.java)
                intent.putExtras(bundle)
                mContext.startActivity(intent)
            }

            else if(mNotificationList[position].type.equals("job_added",ignoreCase = true))
            {
                //var bundle = Bundle()
                //bundle.putString("event_id" , ""+mNotificationList[position].id)
                var intent = Intent(mContext, RecommendedJobsActivity::class.java)
                //intent.putExtras(bundle)
                mContext.startActivity(intent)
            }
            else if(mNotificationList[position].type.equals("event",ignoreCase = true))
            {
                var bundle = Bundle()
                bundle.putString("event_id" , ""+mNotificationList[position].id)
                var intent = Intent(mContext, EventDetailsActivity::class.java)
                intent.putExtras(bundle)
                mContext.startActivity(intent)
            }
            else if(mNotificationList[position].type.equals("event_added",ignoreCase = true))
            {
                //var bundle = Bundle()
                //bundle.putString("event_id" , ""+mNotificationList[position].id)
                 var intent = Intent(mContext, RecommendedEventsActivity::class.java)
                //intent.putExtras(bundle)
                mContext.startActivity(intent)
            }

            else if(mNotificationList[position].type.equals("like",ignoreCase = true)
                    ||mNotificationList[position].type.equals("follow",ignoreCase = true)
                    ||mNotificationList[position].type.equals("photo_liked",ignoreCase = true)
                    ||mNotificationList[position].type.equals("portfolio_viewed",ignoreCase = true)
                    ||mNotificationList[position].type.equals("contact_viewed",ignoreCase = true))
            {

                if(mNotificationList[position].id.isNotEmpty() && !mNotificationList[position].id.equals("null" ,ignoreCase = true)) {
                    var bundle = Bundle()
                    bundle.putString("user_id", "" + mNotificationList[position].id)
                    bundle.putString("user_role", "" + mNotificationList[position].user_role)
                    bundle.putString("is_featured", "" + mNotificationList[position].is_featured)
                    var intent = Intent(mContext, OthersProfileActivity::class.java)
                    intent.putExtras(bundle)
                    mContext.startActivity(intent)
                }
            }

            else if(mNotificationList[position].type.equals("shortlist",ignoreCase = true))
            {
                var bundle = Bundle()

                bundle.putString("user_id" ,""+mNotificationList[position].id)
                bundle.putString("user_role" , "casting")
                bundle.putString("is_featured" , "0")
                var intent = Intent(mContext, OthersProfileActivity::class.java)
                intent.putExtras(bundle)
                mContext.startActivity(intent)
            }


            else if (mNotificationList[position].type.equals("occasional",ignoreCase = true))
            {
                    greetingDialog(mNotificationList[position].title , mNotificationList[position].image_url)
            }

            else if (mNotificationList[position].type.equals("others",ignoreCase = true))
            {

                if(!mNotificationList[position].image_url.equals("") && mNotificationList[position].image_url!=null)
                {
                }
                else
                {
                }

            }



        }

    }

    fun greetingDialog(greetingMessageTxt: String , greetingImageTxt: String)
    {
        var mDialog = Dialog(mContext, R.style.NewDialogTheme /*android.R.style.Theme_Black_NoTitleBar_Fullscreen*/)
        mDialog.setContentView(R.layout.occasional_greeting_dialog)
        mDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT)

        var greetingTitle = mDialog.findViewById<TextView>(R.id.greetingTitle)
        var greetingMessage = mDialog.findViewById<TextView>(R.id.greetingMessage)
        var greetingImage = mDialog.findViewById<ImageView>(R.id.greetingImage)
        var greetingCloseBtn = mDialog.findViewById<ImageView>(R.id.greetingCloseBtn)
        var greetingCommonBackgroundImage = mDialog.findViewById<ImageView>(R.id.greetingCommonBackgroundImage)
        var group_emoji_container = mDialog.findViewById<EmojiRainLayout>(R.id.group_emoji_container)

        val text = "<b><font color=#000000>Dazzlerr</font></b> <font color=#000000> wishing you very</font>"
        greetingTitle.text = Html.fromHtml(text)
        greetingMessage.text = greetingMessageTxt

        mDialog.show()

        if(greetingMessageTxt.toLowerCase()?.contains("birthday") || greetingMessageTxt.toLowerCase()?.contains("anniversary"))
        {

            group_emoji_container?.addEmoji(R.drawable.occasional_cake)
            group_emoji_container.addEmoji(R.drawable.occasional_confetti)
            group_emoji_container.addEmoji(R.drawable.occasional_balloons)
        }

        else
        {
            group_emoji_container.addEmoji(R.drawable.occasional_confetti)
            group_emoji_container.addEmoji(R.drawable.occasional_balloons)
        }

        try{
        group_emoji_container.startDropping()
        }
        catch(e:Exception)
        {
            e.printStackTrace()
        }

        if(greetingImageTxt.isNotEmpty())
        {
            Glide.with(mContext)
                    .load("https://www.dazzlerr.com/API/" + greetingImageTxt).apply(RequestOptions().centerCrop())
                    .placeholder(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(greetingImage)
        }

        else
        {
            greetingImage.visibility = View.GONE
            greetingCommonBackgroundImage.visibility  = View.VISIBLE
        }
        greetingCloseBtn.setOnClickListener {
            mDialog.dismiss()
        }

    }
    fun simpledateFormat(view: TextView, date : String)
    {
        try
        {
            //val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
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
    fun add(response: NotificationsPojo.Data)
    {
        mNotificationList.add(response)
        notifyItemInserted(mNotificationList.size - 1)

    }

    fun addAll(Items: MutableList<NotificationsPojo.Data>)
    {
        for (response in Items)
        {
            add(response)
        }
    }

    fun removeAll()
    {

        mNotificationList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int
    {
        return mNotificationList.size
    }


    fun addLoading()
    {
        isLoaderVisible = true
        add(NotificationsPojo.Data())
    }

    fun removeLoading()
    {
        isLoaderVisible = false

        val position = mNotificationList.size - 1
        val result = getItem(position)

        if (result != null) {
            mNotificationList.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    fun getItem(position: Int): NotificationsPojo.Data? {
        return mNotificationList.get(position)
    }

    override fun getItemId(position: Int): Long {

        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {

        return if (isLoaderVisible && position == mNotificationList.size - 1)
        {
            VIEW_TYPE_LOADING
        }
        else if(mNotificationList[position].type.equals("job_added",ignoreCase = true))
        {
            VIEW_TYPE_JOB
        }
        else if(mNotificationList[position].type.equals("event_added",ignoreCase = true))
        {
            VIEW_TYPE_EVENT
        }
        else if(mNotificationList[position].type.equals("like",ignoreCase = true)
                ||mNotificationList[position].type.equals("follow",ignoreCase = true)
                ||mNotificationList[position].type.equals("shortlist",ignoreCase = true)
                ||mNotificationList[position].type.equals("photo_liked",ignoreCase = true)
                ||mNotificationList[position].type.equals("portfolio_viewed",ignoreCase = true)
                ||mNotificationList[position].type.equals("contact_viewed",ignoreCase = true)
                ||mNotificationList[position].type.equals("application_viewed",ignoreCase = true)
                ||mNotificationList[position].type.equals("proposal_hired",ignoreCase = true)
                ||mNotificationList[position].type.equals("proposal_rejected",ignoreCase = true)
                ||mNotificationList[position].type.equals("proposal_shortlisted",ignoreCase = true)
                ||mNotificationList[position].type.equals("job",ignoreCase = true)
                ||mNotificationList[position].type.equals("event",ignoreCase = true)
        )
        {
          //  Timber.e("Profile pic type")
            VIEW_TYPE_IMAGE
        }

        else
        {
           // Timber.e("Text type")
           VIEW_TYPE_TEXT
        }
    }

    inner class JobViewHolder(var bindingObj: NotificationsJobItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
    inner class TextViewHolder(var bindingObj: NotificationsTextItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
    inner class ImageViewHolder(var bindingObj: NotificationsImageItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
    inner class EventViewHolder(var bindingObj: NotificationsEventItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
    inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}