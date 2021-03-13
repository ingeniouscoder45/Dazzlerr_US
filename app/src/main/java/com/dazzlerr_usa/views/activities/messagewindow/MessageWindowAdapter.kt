package com.dazzlerr_usa.views.activities.messagewindow

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.LeftMessageItemLayoutBinding
import com.dazzlerr_usa.databinding.MessageWindowDateLayoutBinding
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.databinding.RightMessageItemLayoutBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.imageslider.ImagePreviewActivity
import com.dazzlerr_usa.utilities.imageslider.PreviewFile
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MessageWindowAdapter(internal var context: Context?, var mMessageList: MutableList<MessageWindowPojo.DataBean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    var VIEW_TYPE_LOADING = 0
    var VIEW_TYPE_NORMAL = 1
    var VIEW_TYPE_OTHERUSER = 2
    var VIEW_TYPE_DATE = 3
    private var isLoaderVisible = false
    private var hasDateChanged = true
    var PreviousDate = ""
    var shouldReadMsg = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {

        if (viewType == VIEW_TYPE_LOADING)
        {
            val bindingObj : PaginationLoadingLayoutBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pagination_loading_layout, parent, false)

            return LoadingViewHolder(bindingObj)
        }

        else if(viewType==VIEW_TYPE_DATE)
        {
            val binderObject = DataBindingUtil.inflate<MessageWindowDateLayoutBinding>(LayoutInflater.from(parent
                    .context), R.layout.message_window_date_layout, parent, false)

            return DateViewHolder(binderObject)
        }
        else if(viewType==VIEW_TYPE_NORMAL)
        {
            val binderObject = DataBindingUtil.inflate<RightMessageItemLayoutBinding>(LayoutInflater.from(parent
                    .context), R.layout.right_message_item_layout, parent, false)

            return RightViewHolder(binderObject)
        }

        else
        {
            val binderObject = DataBindingUtil.inflate<LeftMessageItemLayoutBinding>(LayoutInflater.from(parent
                    .context), R.layout.left_message_item_layout, parent, false)

            return LeftViewHolder(binderObject)
        }

    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int)
    {
/*
        if(i==mMessageList.size-1 && shouldReadMsg *//*&& !mMessageList.get(i).sender_id.equals((context as MessageWindowActivity).sharedPreferences.getString(Constants.User_id))*//*)
        {
            Timber.e("Last message")
            shouldReadMsg = false
            (context as MessageWindowActivity).readMessage(mMessageList.get(i).contact_id.toString())
        }*/

        if(viewHolder is LeftViewHolder)
        {

        viewHolder.bindingObj.itemBinder = mMessageList[i]
        viewHolder.bindingObj.executePendingBindings()

            if(mMessageList.get(i).ReadState!=null)
        dateFormat(viewHolder.bindingObj.datetime , mMessageList.get(i).ReadState)

            if(mMessageList.get(i).message_type.equals("image",ignoreCase = true))
            {
                viewHolder.bindingObj.imageLayout.visibility  = View.VISIBLE
                viewHolder.bindingObj.textLayout.visibility  = View.GONE

                Glide.with(context!!)
                        .load("https://www.dazzlerr.com/API/"+mMessageList.get(i).msg_image).apply(RequestOptions().centerCrop())
                        //.placeholder(R.color.colorLightGrey)
                        .error(R.color.colorLightGrey)
                        .into(viewHolder.bindingObj.chatImage)
            }

            else
            {
                viewHolder.bindingObj.imageLayout.visibility  = View.GONE
                viewHolder.bindingObj.textLayout.visibility  = View.VISIBLE
            }

            viewHolder.itemView.setOnClickListener {

                if(mMessageList.get(i).message_type.equals("image",ignoreCase = true))
                {
                 openImage(mMessageList.get(i).msg_image)
                }
            }
        }
        else if(viewHolder is RightViewHolder)
        {
            viewHolder.bindingObj.itemBinder = mMessageList[i]
            viewHolder.bindingObj.executePendingBindings()
            if(mMessageList.get(i).ReadState!=null)
            dateFormat(viewHolder.bindingObj.datetime , mMessageList.get(i).ReadState)

            if(mMessageList.get(i).read_status.equals("0") ||mMessageList.get(i).read_status.equals("null")  ||mMessageList.get(i).read_status?.isEmpty()!!)
            {
                viewHolder.bindingObj.messageStatusIcon.setImageResource(R.drawable.ic_baseline_sent_icon)
            }

           else if(mMessageList.get(i).read_status.equals("1"))
            {
                viewHolder.bindingObj.messageStatusIcon.setImageResource(R.drawable.ic_baseline_delivered_icon)
            }

            else if(mMessageList.get(i).read_status.equals("2"))
            {
                viewHolder.bindingObj.messageStatusIcon.setImageResource(R.drawable.ic_baseline_read_icon)
            }


            if(mMessageList.get(i).message_type.equals("image",ignoreCase = true))
            {
                viewHolder.bindingObj.imageLayout.visibility  = View.VISIBLE
                viewHolder.bindingObj.textLayout.visibility  = View.GONE

                Glide.with(context!!)
                        .load("https://www.dazzlerr.com/API/"+mMessageList.get(i).msg_image).apply(RequestOptions().centerCrop())
                        //.placeholder(R.color.colorLightGrey)
                        .error(R.color.colorLightGrey)
                        .into(viewHolder.bindingObj.chatImage)
            }

            else
            {
                viewHolder.bindingObj.imageLayout.visibility  = View.GONE
                viewHolder.bindingObj.textLayout.visibility  = View.VISIBLE
            }

            viewHolder.itemView.setOnClickListener {

                if(mMessageList.get(i).message_type.equals("image",ignoreCase = true))
                {
                    openImage(mMessageList.get(i).msg_image)
                }
            }

        }


        else if(viewHolder is DateViewHolder)
        {
            viewHolder.bindingObj.chatDateTxt.text = dateFormat2(mMessageList.get(i).ReadState)
        }
    }

    fun openImage(image:String)
    {

        var imageList : ArrayList<PreviewFile> = ArrayList()

        imageList.add(PreviewFile(image,false ,true, "" ,"" , "", 0,0 ,"" ,""))

        if(imageList.size!=0)
        {
            val intent = Intent(context,
                    ImagePreviewActivity::class.java)

            intent.putExtra(ImagePreviewActivity.IMAGE_LIST, imageList)
            intent.putExtra(ImagePreviewActivity.CURRENT_ITEM, 0)
            context?.startActivity(intent)
        }
    }
    fun dateFormat(view: TextView, date : String)
    {
        try
        {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            val datetime = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed
            val formatter = SimpleDateFormat("hh:mm a") //If you need time just put specific format for time like 'HH:mm:ss'
            var dateStr = ""

            val calendar = Calendar.getInstance()
            calendar.time = datetime
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DATE, -1)
            val timeFormatter = SimpleDateFormat("hh:mma")

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                dateStr = /*"Today " +*/ timeFormatter.format(datetime)
            }
            else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
                dateStr =/* "Yesterday " +*/ timeFormatter.format(datetime)
            } else
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

    fun dateFormat2(date : String): String
    {
        try
        {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            val datetime = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed
            val formatter = SimpleDateFormat("dd MMM, yyyy") //If you need time just put specific format for time like 'HH:mm:ss'
            var dateStr = ""

            val calendar = Calendar.getInstance()
            calendar.time = datetime!!
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DATE, -1)
            val timeFormatter = SimpleDateFormat("hh:mma")

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                dateStr = "Today "
            }
            else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
                dateStr = "Yesterday "
            } else
            {
                dateStr = formatter.format(datetime!!)
            }
           return dateStr
        }

        catch (e:Exception)
        {
            e.printStackTrace()
            return date
        }

    }

    fun add(response: MessageWindowPojo.DataBean)
    {
        if(dateFormat2(response.ReadState).equals(PreviousDate))
        {
            response.hasDateChanged = false
            mMessageList.add(response)
        }
        else
        {

            //mMessageList.get(i).hasDateChanged = true
            PreviousDate = dateFormat2(response.ReadState)

            var modelObj = MessageWindowPojo.DataBean()
            modelObj.hasDateChanged =  true
            modelObj.ReadState =  response.ReadState

            mMessageList.add(modelObj)

            response.hasDateChanged = false
            mMessageList.add(response)
        }

        notifyItemInserted(mMessageList.size - 1)

    }

    fun addAll(Items: MutableList<MessageWindowPojo.DataBean>)
    {
        for (response in Items)
        {
            add(response)
        }
    }

    fun removeAll()
    {

        mMessageList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int
    {
        return mMessageList.size
    }




    fun addLoading()
    {
        isLoaderVisible = true
        add(MessageWindowPojo.DataBean())
    }

    fun removeLoading()
    {
        isLoaderVisible = false

        val position = mMessageList.size - 1
        val result = getItem(position)

        if (result != null) {
            mMessageList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    
    fun getItem(position: Int): MessageWindowPojo.DataBean? {
        return mMessageList.get(position)
    }

    override fun getItemViewType(position: Int): Int {

        return if (isLoaderVisible)
        {
            if (position == mMessageList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        }

        else if(mMessageList.get(position).hasDateChanged)
        {
            VIEW_TYPE_DATE
        }
        else
        {
            if(mMessageList.get(position).sender_id.equals((context as MessageWindowActivity).sharedPreferences.getString(Constants.User_id)))
            {
                VIEW_TYPE_NORMAL
            }
            else
            {
                VIEW_TYPE_OTHERUSER
            }

        }
    }

     inner class RightViewHolder(var bindingObj: RightMessageItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
     inner class LeftViewHolder(var bindingObj: LeftMessageItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
     inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)
     inner class DateViewHolder(val bindingObj: MessageWindowDateLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}
