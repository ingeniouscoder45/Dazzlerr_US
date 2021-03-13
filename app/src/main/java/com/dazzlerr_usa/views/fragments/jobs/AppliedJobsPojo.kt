package com.dazzlerr_usa.views.fragments.jobs

class AppliedJobsPojo
{
    var status: String? = null
    var success: Boolean = false
    var data: MutableList<DataBean>? = null

    class DataBean {

        var creply_id: Int = 0
        var name: String? = null
        var user_role: String? = null
        var role_img: String? = null
        var call_id: Int = 0
        var user_id: Int = 0
        var title: String? = null
        var start_date: String? = null
        var city: String? = null
        var status: String? = null
        var request_sent: String? = null
        var casting_name: String? = null
        var views =""
        var is_featured =""
        var job_uid = ""

    }
}
