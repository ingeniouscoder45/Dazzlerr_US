package com.dazzlerr_usa.views.activities.editprofile.pojos

class CitiesPojo
{
    var status: Boolean = false
    var message: String? = null
    var data: MutableList<DataBean>? = null

    class DataBean
    {
        var city_id: Int = 0
        var city_name: String? = null
    }
}
