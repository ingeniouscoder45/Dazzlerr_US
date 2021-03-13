package com.dazzlerr_usa.views.activities.blogs.pojos

data class BlogCategoriesPojo(
    val message: String = "",
    val result: List<Result> = listOf(),
    val status: Boolean = false
) {
    data class Result(
            var cat_id: String = "",
            var name: String = ""
    )
}