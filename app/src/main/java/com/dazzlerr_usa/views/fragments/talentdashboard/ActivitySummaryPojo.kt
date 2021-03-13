package com.dazzlerr_usa.views.fragments.talentdashboard

data class ActivitySummaryPojo(
        var data: MutableList<Data> = mutableListOf(),
        var message: String = "",
        var monitor: String = "",
        var activity_level_title: String = "",
        var activity_level_suggestion: String = "",
        var status: Boolean = false
) {
    data class Data(
        var application_count :String = "",
        var contact_count:String = "",
        var job_count: String = "",
        var portfolio_count: String = ""
    )
}