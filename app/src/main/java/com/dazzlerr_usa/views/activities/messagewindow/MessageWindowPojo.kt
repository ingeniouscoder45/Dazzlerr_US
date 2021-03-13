package com.dazzlerr_usa.views.activities.messagewindow

class MessageWindowPojo
{

    var status: String? = null
    var success: Boolean = false
    var user_blocked: String = ""
    var sender_blocked: String = ""
    var online_status: String = ""
    var data: MutableList<DataBean>? = null

    class DataBean
    {

        var ReadState =""
        var message: String? = null
        var sender_id: String? = null
        var contact_id: String? = null
        var read_status: String? = null
        var message_type: String? = null
        var msg_image: String = ""
        var hasDateChanged: Boolean = true
    }
}