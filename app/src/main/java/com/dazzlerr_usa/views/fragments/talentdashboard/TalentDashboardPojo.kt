package com.dazzlerr_usa.views.fragments.talentdashboard

class TalentDashboardPojo
{
    var status: String? = null
    var success = false
    var data: List<DataBean>? = null
    var featured_jobs: MutableList<FeaturedJobsBean>? = null
    var recommended_jobs: MutableList<RecommendedJobsBean>? = null
    var applied_jobs:  MutableList<AppliedJobsBean>? = null
    var activity_log:  MutableList<ActivityLogBean>? = null

    class DataBean
    {
        var profile_id:Int = 0
        var name: String? = null
        var current_city: String? = null
        var current_state: String? = null
        var role: String? = null
        var age: String? = null
        var gender: String? = null
        var views  : String = "0"
        var member_type: String? = null
        //var account_type: String? = null
        var membership_id: String? = null
        var likes  : String = "0"
        var followers : String = "0"
        var is_active : String = ""
        var messages : String = "0"
        var notification_count : String = "0"
        var profile_complete : String = "0"
        var become_featured : String = ""
        var is_featured : String = ""
        var is_published : String = ""
        var is_email_verified : String = ""
        var secondary_role : String = ""
        var featured_valid_till : String = ""
        var is_notified: String = ""
        var personal_occasion_image: String = ""
        var personal_occasion: String = ""
        var festival_occasion_image: String = ""
        var festival_occasion: String = ""
        var membership_days_left: String = ""
        var membership_start_date: String? = null
        var membership_end_date: String? = null

    }

    class FeaturedJobsBean {

        var call_id = 0
        var title: String? = null
        var name: String? = null
        var user_role: String? = null
        var created_on: String? = null

    }


    class RecommendedJobsBean {

        var name: String? = null
        var casting_name: String? = null
        var user_role: String? = null
        var role_img: String? = null
        var call_id: Int = 0
        var created_on: String? = null
        var city: String? = null
        var title: String? = null
        var views = "0"
        var is_featured = ""
    }

    class AppliedJobsBean {

        var call_id = 0
        var title: String? = null
        var name: String? = null
        var user_role: String? = null
        var created_on: String? = null

    }

    class ActivityLogBean {

        var activity_desc: String? = null
        var activity_date: String? = null

    }
}