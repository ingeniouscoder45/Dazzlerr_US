package com.dazzlerr_usa.views.activities.editprofile.pojos

class StatesPojo {

    var status: Boolean = false
    var message: String? = null
    var data: MutableList<DataBean>? = null

    class DataBean
    {
        var state_id: Int = 0
        var state_name: String? = null
    }
}
