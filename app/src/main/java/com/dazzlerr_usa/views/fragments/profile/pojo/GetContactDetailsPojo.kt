package com.dazzlerr_usa.views.fragments.profile.pojo

class GetContactDetailsPojo
{

    var status = false
    var success: String? = null
    var total_contact_count: String =""
    var contact_count_left: String =""
    var data: MutableList<DataBean>? = null

    class DataBean {
        var email: String? = null
        var phone: Long = 0
    }
}

