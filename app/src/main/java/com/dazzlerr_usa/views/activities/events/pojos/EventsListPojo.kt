package com.dazzlerr_usa.views.activities.events.pojos

class EventsListPojo {

    var status = false
    var message: String? = null
    var promotion_banner: String? = null
    var result: MutableList<ResultBean>? = null

    class ResultBean
    {

        var event_id = 0
        var event_title: String? = null
        var event_location: String? = null
        var _EventStartDate: String? = null
        var image_url: String? = null
        var _post_views: String? = null
    }
}