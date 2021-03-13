package com.dazzlerr_usa.views.activities.calllogs

data class CallLogsPojo(
        var data: MutableList<Data> = mutableListOf(),
        var status: Boolean = false,
        var success: String = ""
) {
    data class Data(
        var call_duration: String = "",
        var call_type: String = "",
        var caller_id: String = "",
        var caller_name: String = "",
        var date_time: String = "",
        var user_id: String = "",
        var profile_pic: String = "",
        var is_caller: String = ""
    )
}