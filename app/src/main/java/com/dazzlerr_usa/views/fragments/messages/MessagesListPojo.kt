package com.dazzlerr_usa.views.fragments.messages

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*



@BindingAdapter("set_formatted_text")
fun dateFormat(view: TextView, date : String)
{
    try
    {
        if(date.isNotEmpty() && !date.equals("null") ) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            val datetime = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed
            //val formatter = SimpleDateFormat("dd MMM, yyyy hh:mm a") //If you need time just put specific format for time like 'HH:mm:ss'
            val formatter = SimpleDateFormat("dd MMM, yyyy") //If you need time just put specific format for time like 'HH:mm:ss'
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
            view.text = dateStr
        }
    }

    catch (e:Exception)
    {
        e.printStackTrace()
        view.text = date
    }

}

class MessagesListPojo
{

    var status: String? = null
    var user_id: String? = null
    var success: Boolean = false
    var data: MutableList<DataBean>? = null

    class DataBean
    {
        var thread_id: String  = ""
        var name: String? = null
        var subject: String? = null
        var created_on: String? = null
        var sender_id: String? = null
        var receiver_id: String? = null
        var sender_pic: String? = null
        var deleted_from: String? = null
    }
}
