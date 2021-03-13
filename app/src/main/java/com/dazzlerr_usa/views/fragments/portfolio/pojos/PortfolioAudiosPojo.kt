package com.dazzlerr_usa.views.fragments.portfolio.pojos

class PortfolioAudiosPojo {

    var status: String? = null
    var success: Boolean = false
    var data: MutableList<DataBean>? = null

    class DataBean {

        var audio_id: Int = 0
        var user_id: Int = 0
        var audio_url: String? = null
        var title: String? = null
        var created_on: String? = null
        var updated_on: String? = null
    }
}
