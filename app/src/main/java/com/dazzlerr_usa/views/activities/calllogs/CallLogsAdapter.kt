package com.dazzlerr_usa.views.activities.calllogs

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.CallLogsItemsLayoutBinding
import com.dazzlerr_usa.databinding.RecommendedEventsItemLayoutBinding
import java.text.SimpleDateFormat
import java.util.*


class CallLogsAdapter(internal var context: Context, var mCallLogList: MutableList<CallLogsPojo.Data>) : androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {


            val binderObject = DataBindingUtil.inflate<CallLogsItemsLayoutBinding>(LayoutInflater.from(parent
                    .context), R.layout.call_logs_items_layout, parent, false)

            return ViewHolder(binderObject)



    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int)
    {

        if(viewHolder is ViewHolder)
        {

        Glide.with(context)
                .load(mCallLogList[i].profile_pic)
                .apply(RequestOptions().centerCrop().fitCenter())
                .thumbnail(0.3f)
                .placeholder(R.drawable.placeholder)
                .into(viewHolder.bindingObj.callLogProfilePic)

            viewHolder.bindingObj.callLogDateTimeTxt.text  = dateFormat(  mCallLogList[i].date_time.toString())

            if(mCallLogList[i].is_caller.equals("true"))
            {
                if (mCallLogList.get(i).call_type == "DENIED")
                    viewHolder.bindingObj.callLogTypeImage.setImageResource(R.drawable.ic_baseline_call_missed_outgoing_24)
                else if (mCallLogList.get(i).call_type == "CANCELED")
                    viewHolder.bindingObj.callLogTypeImage.setImageResource(R.drawable.ic_baseline_call_made_24)
                else if (mCallLogList.get(i).call_type == "NO_ANSWER")
                    viewHolder.bindingObj.callLogTypeImage.setImageResource(R.drawable.ic_baseline_call_missed_outgoing_24)
                else
                    viewHolder.bindingObj.callLogTypeImage.setImageResource(R.drawable.ic_baseline_call_made_24)

            }

            else
            {
                if (mCallLogList.get(i).call_type == "DENIED")
                    viewHolder.bindingObj.callLogTypeImage.setImageResource(R.drawable.ic_baseline_call_missed_24)
                else if (mCallLogList.get(i).call_type == "CANCELED")
                    viewHolder.bindingObj.callLogTypeImage.setImageResource(R.drawable.ic_baseline_call_missed_24)
                else if (mCallLogList.get(i).call_type == "NO_ANSWER")
                    viewHolder.bindingObj.callLogTypeImage.setImageResource(R.drawable.ic_baseline_call_missed_24)
                else
                    viewHolder.bindingObj.callLogTypeImage.setImageResource(R.drawable.ic_baseline_call_received_24)

            }
        }
    }

    fun dateFormat(date:String) :String
    {
        if(date.isNotEmpty() && !date.equals("null") ) {
            val dateFormat = SimpleDateFormat("dd MMM, yyyy hh:mm:ss a")
            val datetime = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed
            //val formatter = SimpleDateFormat("dd MMM, yyyy hh:mm a") //If you need time just put specific format for time like 'HH:mm:ss'
            val formatter = SimpleDateFormat("dd MMM, yyyy hh:mm:ss a") //If you need time just put specific format for time like 'HH:mm:ss'
            var dateStr = ""

            val calendar = Calendar.getInstance()
            calendar.time = datetime
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DATE, -1)
            val timeFormatter = SimpleDateFormat("hh:mma")

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                dateStr = /*"Today " +*/ timeFormatter.format(datetime)
            } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
                dateStr = "Yesterday " + timeFormatter.format(datetime)
            } else {
                dateStr = formatter.format(datetime)
            }
           return dateStr
        }
        else
            return ""
    }


    override fun getItemCount(): Int
    {
        return mCallLogList.size
    }



     inner class ViewHolder(var bindingObj: CallLogsItemsLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)

}
