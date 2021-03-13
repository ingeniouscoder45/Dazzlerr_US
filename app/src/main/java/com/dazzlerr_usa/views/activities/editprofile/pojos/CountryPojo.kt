package com.dazzlerr_usa.views.activities.editprofile.pojos

class CountryPojo {

    var status: Boolean = false
    var message: String? = null
    var data: MutableList<DataBean>? = null

    class DataBean
    {
        var country_id: Int = 0
        var country_name: String? = null
    }
}
