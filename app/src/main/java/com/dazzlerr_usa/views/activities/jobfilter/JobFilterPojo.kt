package com.dazzlerr_usa.views.activities.jobfilter

class JobFilterPojo
{

    var status : Boolean = false
    var message : String?=null
    var data: MutableList<DataBean>? = null

    class DataBean
    {
        var city: String  = ""
    }
}