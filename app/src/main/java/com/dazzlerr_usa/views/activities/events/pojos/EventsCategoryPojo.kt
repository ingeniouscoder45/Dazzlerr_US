package com.dazzlerr_usa.views.activities.events.pojos

data class EventsCategoryPojo(
    val message: String = "",
    val result: List<Result> = listOf(),
    val status: Boolean = false
) {
    data class Result(
        val cat_id: Int = 0,
        val name: String = ""
    )
}