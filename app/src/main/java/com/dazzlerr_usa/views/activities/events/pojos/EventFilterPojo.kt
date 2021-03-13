package com.dazzlerr_usa.views.activities.events.pojos

data class EventFilterPojo(
    var cat: MutableList<Cat>,
    var city: ArrayList<String>,
    var message: String?,
    var organizer: MutableList<String>,
    var status: Boolean?,
    var venue: ArrayList<String>
) {
    data class Cat(
        var cat_id: Int?,
        var name: String?
    )
}