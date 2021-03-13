package com.dazzlerr_usa.views.fragments.portfolio.pojos

class UploadAudioPojo
{
    var status: Boolean = false
    var success: String? = null
    var data:DataBean? = null

    class DataBean
    {

        var audio_id: Int = 0
        var user_id: Int = 0
        var audio_url: String? = null
        var title: String? = null
        var status: Int = 0
        var created_on: String? = null
        var updated_on: String? = null
    }
}
