package com.dazzlerr_usa.views.fragments.portfolio.pojos

class PortfolioVideosPojo
{
    var status: String? = null
    var success: Boolean = false
    var data: MutableList<DataBean>? = null

    class DataBean
    {
        var rows: Any? = null
        var video_id: Int = 0
        var user_id: Int = 0
        var title: String? = null
        var description: String? = null
        var video_url: String? = null
        var status: Int = 0
        var created_on: String? = null
        var updated_on: String? = null
    }
}
