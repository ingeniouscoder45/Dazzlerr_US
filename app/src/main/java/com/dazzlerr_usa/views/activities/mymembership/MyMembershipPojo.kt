package com.dazzlerr_usa.views.activities.mymembership

data class MyMembershipPojo(
        var features: MutableList<Feature> = mutableListOf(),
        var result: List<Result> = listOf(),
        var status: Boolean = false,
        var success: String = ""
) {
    data class Feature(
        var feature_name: String = "",
        var feature_slug: String = "",
        var feature_value: List<String> = listOf()
    )

    data class Result(
        var membership_end_date: String = "",
        var membership_id: Int = 0,
        var my_plan: String = ""
    )
}