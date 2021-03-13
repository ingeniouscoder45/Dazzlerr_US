package com.dazzlerr_usa.views.activities.eventdetails

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat


@BindingAdapter("fullDateFormat")
fun fullDateFormat(view: TextView, date : String)
{
    try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val date = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed
        val formatter = SimpleDateFormat("MMMM dd, yyyy @ hh:mm a") //If you need time just put specific format for time like 'HH:mm:ss'
        val dateStr = formatter.format(date)
        view.text = dateStr
    }
    catch (e:Exception)
    {
        e.printStackTrace()
        view.text = date
    }
}


class EventDetailsPojo
{

    var status: Boolean = false
    var message: String? = null
    var result: List<ResultBean>? = null

    class ResultBean
    {

        var event_title: String? = null
        var event_slug: String? = null
        var description: String? = null
        var category: String? = null
        var _thumbnail_id: String? = null
        var _EventStartDate: String? = null
        var _EventVenueID: String? = null
        var _EventOrganizerID: String? = null
        var _post_views: String? = null
        var event_image: String? = null
        var venue: String? = null
        var location: String? = null
        var full_address: String? = null
        var contact_name: String? = null
        var contact_email: String? = null
        var contact_phone: String? = null
        var contact_website: String? = null
        var likes: String ="0"
        var price: String = ""
        var like_status: String ? = null
        var event_url: String ? = null
    }
}
