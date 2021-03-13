package com.dazzlerr_usa.views.activities.institute

data class InstitutePojo(
        var `data`: MutableList<Data> = mutableListOf(),
        var filter_cat: MutableList<FilterCat> = mutableListOf(),
        var filter_city: MutableList<FilterCity> = mutableListOf(),
        var status: String = "",
        var success: Boolean = false
) {
    data class Data(
        var category_id: Int = 0,
        var institute_id:String = "",
        var category_name: String = "",
        var city: String = "",
        var institute_image: String = "",
        var institute_name: String = "",
        var views: Int = 0,
        var viewType : String = ""
    )

    data class FilterCat(
        var category_id: Int = 0,
        var category_name: String = ""
    )

    data class FilterCity(
        var city: String = ""
    )
}