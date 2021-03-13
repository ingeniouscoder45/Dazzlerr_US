package com.dazzlerr_usa.views.activities.profileteam.pojos

class GetTeamPojo
{

    var status: String? = null
    var success: Boolean = false
    var data: MutableList<DataBean>? = null

    class DataBean {
        var id: Int = 0
        var name: String? = null
        var team_img: String? = null
        var description: String? = null
        var user_role: String? = null
        var isSelected: Boolean= false
    }
}
