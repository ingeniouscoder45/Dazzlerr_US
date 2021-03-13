package com.dazzlerr_usa.views.activities.interestedtalents

class InterestedTalentsPojo {
    var status = false
    var success: String? = null
    var data: MutableList<DataBean>? = null

    class DataBean {

        var user_id = 0
        var call_id = 0
        var name: String? = null
        var profile_pic: String? = null
        var user_role: String? = null
        var secondary_role: String? = null
        var title: String? = null
        var applied_on: String? = null

    }
}