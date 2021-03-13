package com.dazzlerr_usa.views.activities.jobsdetails

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat


@BindingAdapter("date_format")
fun dateFormat(view: TextView, date : String)
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
class JobDetailsPojo {

    var status: Boolean = false
    var message: String? = null
    var data: MutableList<DataBean>? = null

    class DataBean
    {

        var call_id: Int = 0
        var is_featured:String? = null
        var can_apply:String? = null
        var profile_complete:String? = null
        var casting_id :String? = null
        var title: String? = null
        var description: String? = null
        var job_role: String? = null
        var gender: String? = null
        var age_range: String? = null
        var vacancies: String? = null
        var audition: String? = null
        var job_expiry: String = ""
        var budget: String? = null
        var budget_amount: String =""
        var budget_duration: String? = null
        var address: String? = null
        var city: String? = null
        var state_name: String? = null
        var country_name: String? = null
        var start_date: String = ""
        var end_date: String =""
        var tags: String? = null
        var views: String? = null
        var email: String? = null
        var phone: String? = null
        var contact_mobile: String? = null
        var contact_person: String? = null
        var contact_email: String? = null
        var contact_whatsapp: String = ""
        var name: String? = null
        var profile_pic: String? = null
        var representer: String? = null
        var profile_msg: String? = null
        var casting_reply: String =""
        var casting_msg: String = ""
        var proposal_shortlist_status: String = ""
        var is_applied: String? = null
        var job_apply_count: String? = null
        var is_job_shortlisted: String? = null
        var job_link: String = ""
        var applied_date: String = ""
        var job_uid = ""
    }
}
