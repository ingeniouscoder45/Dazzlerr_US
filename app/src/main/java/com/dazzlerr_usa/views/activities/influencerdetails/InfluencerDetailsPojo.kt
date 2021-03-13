package com.dazzlerr_usa.views.activities.influencerdetails

class InfluencerDetailsPojo
{
    var status: Boolean = false
    var success: String? = null
    var result: MutableList<ResultBean>? = null
    var portfolio: MutableList<PortfolioBean>? = null

    class ResultBean
    {
        var profile_id: Int = 0
        var name: String? = null
        var username: String? = null
        var city: String? = null
        var about: String? = null
        var profile_pic: String? = null
        var views: Int = 0
        var desktop_banner: String? = null
        var sub_roles: String? = null
        var niche: String? = null
        var fb_followers: String? = null
        var insta_followers: String? = null
        var tw_followers: String? = null
        var yt_followers: String? = null
        var tik_followers: String? = null
        var snp_follewers: String? = null
        var gallery_banner: String? = null
        var is_featured: String ? = null
        var like_status: String ? = null
        var likes: String? = null
        var social_banner: String? = null
    }

    class PortfolioBean {
        /**
         * image : assets/images/6629/268677_1565163962.jpeg
         */

        var image: String? = null
    }
}
