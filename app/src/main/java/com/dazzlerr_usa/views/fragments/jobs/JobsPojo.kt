package com.dazzlerr_usa.views.fragments.jobs

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.dazzlerr_usa.R
import java.text.SimpleDateFormat

@BindingAdapter("jobsImage")
fun loadJobsImage(view: ImageView, imageUrl: String)
{

    Glide.with(view.getContext())
            .load("https://www.dazzlerr.com/API/"+imageUrl)
            /*.apply(RequestOptions().centerCrop())*/
            .placeholder(R.drawable.placeholder)
            .into(view)
}

@BindingAdapter("format_text")
fun dateFormat(view:TextView , date : String)
{
    try
    {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val date = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed
        val formatter = SimpleDateFormat("MMM dd, yyyy") //If you need time just put specific format for time like 'HH:mm:ss'
        val dateStr = formatter.format(date)
        view.text = dateStr
    }
    catch (e:Exception)
    {
        e.printStackTrace()
        view.text = date
    }

}
class JobsPojo
{
    var status: Boolean = false
    var message: String? = null
    var data: MutableList<DataBean>? = null

    class DataBean {

        var name: String? = null
        var casting_name: String? = null
        var user_role: String? = null
        var role_img: String? = null
        var call_id: Int = 0
        var start_date: String? = null
        var city: String? = null
        var title: String? = null
        var views = "0"
        var is_featured = ""
        var viewType = ""
        var job_uid = ""
    }
}
