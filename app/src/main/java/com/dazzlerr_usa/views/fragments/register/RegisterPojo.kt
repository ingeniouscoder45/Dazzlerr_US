package com.dazzlerr_usa.views.fragments.register

class RegisterPojo {

    var status: String? = null
    var success: Boolean = false
    var data: MutableList<DataBean>? = null

    class DataBean
    {
        var user_id: Int = 0
        var employer_id: Int = 0
        var profile_pic: String ?= null
    }
}
