package com.dazzlerr_usa.views.fragments.castingprofile.pojo

class CastingMyJobPojo
{

    var status: String? = null
    var success: Boolean = false
    var data: MutableList<DataBean>? = null

    class DataBean
    {
        var name: String? = null
        var user_role: String? = null
        var call_id: Int = 0
        var employer_id: Int = 0
        var user_id: Int = 0
        var title: String? = null
        var job_slug: String? = null
        var description: String? = null
        var gender: String? = null
        var age_range: String? = null
        var tags: String? = null
        var vacancies: Int = 0
        var audition: Int = 0
        var job_expiry: String? = null
        var budget_duration: String? = null
        var job_role_id: Int = 0
        var start_date: String? = null
        var end_date: String? = null
        var budget: String? = null
        var contact_person: String? = null
        var contact_email: String? = null
        var contact_mobile: Long = 0
        var contact_whatsapp: Long = 0
        var address: String? = null
        var country_id: Int = 0
        var state_id: Int = 0
        var city: String? = null
        var city_id: Int = 0
        var status: String? = null
        var views: Int = 0
        var is_active: Int = 0
        var is_featured: Any? = null
        var created_on: String? = null
        var updated_on: String? = null
        var deleted_on: Any? = null
    }
}
