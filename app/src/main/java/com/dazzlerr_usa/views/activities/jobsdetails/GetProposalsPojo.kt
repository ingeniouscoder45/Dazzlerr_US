package com.dazzlerr_usa.views.activities.jobsdetails

class GetProposalsPojo
{

    var status: String? = null
    var success: Boolean = false
    var data: MutableList<DataBean>? = null

    class DataBean
    {

        var creply_id: String? = null
        var call_id: String? = null
        var user_id: String? = null
        var msg: String? = null
        var request_sent: String? = null
        var request_message: String? = null
        var accepted: String? = null
        var accepted_message: String? = null
        var is_read: String? = null
        var status: String? = null
        var review_bycasting: String? = null
        var hidden_review_bycasting: String? = null
        var review_byuser: String? = null
        var hidden_review_byuser: String? = null
        var rating: String? = null
        var created_on: String? = null
        var updated_on: String? = null
        var deleted_on: String? = null
        var name: String? = null
        var phone: String? = null
        var location: String? = null
        var age: String? = null
        var profile_pic: String? = null
        var email: String? = null
        var user_role: String? = null
        var is_featured: String = "0"
    }
}
