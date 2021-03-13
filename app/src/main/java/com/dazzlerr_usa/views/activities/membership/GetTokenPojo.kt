package com.dazzlerr_usa.views.activities.membership

data class GetTokenPojo(
    var client_token: String = "",
    var message: String = "",
    var status: Boolean = false
)