package com.dazzlerr_usa.views.fragments.castingdashboard

class CastingDashboardPojo
{

    var status: String? = null
    var success = false
    var data: MutableList<DataBean>? = null
    var featured_talent: MutableList<FeaturedTalentBean>? = null
    var active_jobs: MutableList<ActiveJobsBean>? = null
    var intersted_talent: MutableList<InterstedTalentBean>? = null
    var category_roles: MutableList<CategoryRolesBean>? = null

    class DataBean
    {
        var name: String? = null
        var representer: String? = null
        var company_name: String? = null
        var profile_pic: String? = null
        var current_state: String? = null
        var member_type: String? = null
        var my_jobs : String? = ""
        var shortlisted: String? = ""
        var followings : String? = ""
        var messages : String? = "0"
        var notification_count : String? = "0"
        var is_active : String = ""
        var is_published : String = ""
        var is_document_submitted: String = ""
        var is_document_verified: String = ""
        var is_notified: String = ""
        var personal_occasion_image: String = ""
        var personal_occasion: String = ""
        var festival_occasion_image: String = ""
        var festival_occasion: String = ""
    }

    class FeaturedTalentBean {

        var user_id = 0
        var name: String? = null
        var exp_type: String? = null
        var profile_pic: String? = null
        var secondary_role: String = ""
        var user_role: String = ""

    }

    class ActiveJobsBean {

        var call_id = 0
        var title: String? = null
        var name:String? = null
        var user_role: String? = null
        var created_on: String? = null

    }

    class InterstedTalentBean {

        var user_id : String? = ""
        var call_id : String? = ""
        var name: String? = null
        var profile_pic: String? = null
        var user_role: String? = null
        var title: String? = null
        var applied_on: String? = null

    }

    class CategoryRolesBean
    {
        var stat : String? = ""
        var user_role_id  : String? = ""
        var user_role: String? = null
        var role_img: String? = null
    }
}