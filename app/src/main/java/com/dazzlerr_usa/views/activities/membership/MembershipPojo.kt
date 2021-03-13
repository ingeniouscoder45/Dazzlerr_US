package com.dazzlerr_usa.views.activities.membership

data class MembershipPojo(
        var features: MutableList<Feature> = mutableListOf(),
        var result: MutableList<Result> = mutableListOf(),
        var status: Boolean = false,
        var success: String = ""
) {
    data class Feature(
            var feature_name: String = "",
            var feature_slug: String = "",
            var feature_value: MutableList<String> = mutableListOf(),
            var membership_id: String = ""
    )

    data class Result(
            var membership_id: String = "",
            var post_title: String = "",
            var product_id: String = "",
            var regular_price: String = "",
            var sale_price:String = "",
            var discount:String = ""
    )
}