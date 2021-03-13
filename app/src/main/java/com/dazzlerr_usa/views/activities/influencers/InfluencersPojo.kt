package com.dazzlerr_usa.views.activities.influencers

class InfluencersPojo
{

    var status: Boolean = false
    var success: String? = null
    var banner: String? = null
    var result: MutableList<ResultBean>? = null

    class ResultBean
    {
        var user_id: Int = 0
        var name: String? = null
        var profile_pic: String? = null
        var followers: String? = null
        var is_featured: String? = ""
        var is_profile_complete: String? = ""
    }
}
