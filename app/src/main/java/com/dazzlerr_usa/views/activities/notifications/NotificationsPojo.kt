


















































































































































package com.dazzlerr_usa.views.activities.notifications

data class NotificationsPojo(
        var data: MutableList<Data> = mutableListOf(),
        var success: String = "",
        var status: Boolean = false) {

    data class Data(
            var id: String = "",
            var type: String = "",
            var message: String = "",
            var event_location: String = "",
            var image_url: String = "",
            var casting_name: String = "",
            var title: String = "",
            var user_role: String = "",
            var is_featured: String = "",
            var profile_pic: String = "",
            var created_on: String = ""
    )

}