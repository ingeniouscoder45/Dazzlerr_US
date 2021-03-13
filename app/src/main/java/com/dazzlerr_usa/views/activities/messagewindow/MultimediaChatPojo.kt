package com.dazzlerr_usa.views.activities.messagewindow

class MultimediaChatPojo
{

    var status: String? = null
    var success: Boolean = false
    var data: DataBean? = null

    class DataBean
    {

        var ReadState =""
        var message: String =""
        var sender_id: String? = null
        var sender_pic: String? = null
        var contact_id: String? = null
        var read_status: String? = null
        var message_type: String? = null
        var msg_id: String? = null
        var msg_image: String = ""
    }
}