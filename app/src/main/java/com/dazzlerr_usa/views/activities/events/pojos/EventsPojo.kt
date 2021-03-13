package com.dazzlerr_usa.views.activities.events.pojos

class EventsPojo
{

    var status: Boolean = false
    var message: String? = null
    var result: MutableList<ResultBean>? = null
    var event_images: MutableList<MutableList<EventImagesBean>>? = null
    var event_venue: MutableList<EventVenueBean>? = null

    class ResultBean
    {
        var event_id: Int = 0
        var event_title: String? = null
        var start_date: String? = null
        var _thumbnail_id: String? = null
        var _EventVenueID: String? = null
        var event_image:String ?= null
        var venue:String ?= null
        var _post_views: String? = null
    }

    class EventImagesBean {
        var ID: Int = 0
        var guid: String? = null
    }

    class EventVenueBean {
        var post_id: Int = 0
        var event_city: String? = null
    }
}
