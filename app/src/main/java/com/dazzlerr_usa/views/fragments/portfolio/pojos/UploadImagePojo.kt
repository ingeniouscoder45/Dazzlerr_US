package com.dazzlerr_usa.views.fragments.portfolio.pojos

class UploadImagePojo {

    var status: Boolean = false
    var success: String? = null
    var data: List<DataBean>? = null

    class DataBean {

        var portfolio_id: Int = 0
        var user_id: Int = 0
        var image: String? = null
        var title: Any? = null
        var description: Any? = null
        var likes: Any? = null
        var is_active: Int = 0
        var created_on: String? = null
        var updated_on: Any? = null
        var deleted_on: Any? = null
    }
}
