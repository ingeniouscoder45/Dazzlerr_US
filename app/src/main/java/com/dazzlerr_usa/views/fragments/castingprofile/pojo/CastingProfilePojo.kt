package com.dazzlerr_usa.views.fragments.castingprofile.pojo

class CastingProfilePojo
{

    var status: String? = null
    var success: Boolean = false
    var data: List<DataBean>? = null

    class DataBean
    {
        var user_id:  String? = null
        var cust_id:  String? = null
        var email: String? = null
        var email_isverified:  String? = null
        var user_role: String? = null
        var name: String? = null
        var company_name: String? = null
        var about: String? = null
        var profile_pic: String? = null
        var representer: String? = null
        var city: String? = null
        var current_city: String? = null
        var website: String? = null
        var facebook: String? = null
        var twitter: String? = null
        var instagram: String? = null
        var zipcode: String? = null
        var phone: String? = null
        var phone_isverified: Int = 0
        var identity_proof: String? = null
        var identity_video:  String? = null
        var whats_app: String? = null
        var views: String? = null
        var state_name: String? = null
        var country: String? = null
        var likes_count: String? = null
        var followers_count: String? = null
        var current_state: String? = null
        var active_jobs: String? = null
        var completed_jobs: String? = null
        var is_document_submitted: String? = null
        var is_document_verified: String? = null
        var is_published: String? = null
        var is_liked: Int = 0
        var is_followed: Int  =0
    }
}
