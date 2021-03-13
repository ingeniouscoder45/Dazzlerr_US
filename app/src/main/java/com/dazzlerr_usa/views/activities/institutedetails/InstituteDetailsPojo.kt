package com.dazzlerr_usa.views.activities.institutedetails

data class InstituteDetailsPojo(
        var courses: MutableList<Course> = mutableListOf(),
        var `data`: List<Data> = listOf(),
        var gallery: MutableList<Gallery> = mutableListOf(),
        var status: String = "",
        var success: Boolean = false
) {
    data class Course(
        var course_duration: String = "",
        var course_name: String = ""
    )

    data class Data(
        var address: String = "",
        var description: String = "",
        var institute_email: String = "",
        var institute_image: String = "",
        var institute_name: String = "",
        var institute_phone: String = "",
        var phone2: String = "",
        var phone3: String = "",
        var website: String = ""
    )

    data class Gallery(
        var image: String = "",
        var title: String = ""
    )
}