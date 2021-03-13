package com.dazzlerr_usa.views.fragments.portfolio.pojos

class PortfolioProjectsPojo {

    var status: String? = null
    var success: Boolean = false
    var data: MutableList<DataBean>? = null

    class DataBean {

        var id: Int = 0
        var user_id: Int = 0
        var title: String? = null
        var role: String? = null
        var start_date: String? = null
        var end_date: String? = null
        var description: String? = null
        var is_active: Int = 0
        var created_on: String? = null
        var updated_on: String? = null
        var deleted_on: String? = null
    }
}
