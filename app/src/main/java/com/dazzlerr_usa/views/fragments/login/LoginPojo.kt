package com.dazzlerr_usa.views.fragments.login

class LoginPojo {

    /**
     * status : true
     * message : Login Successfully
     * data : {"user_id":3,"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjoiOWRtVk5nOGciLCJpYXQiOjE1NjIyMzU1MDQsImV4cCI6MTU2MjIzNjEwNH0.5X8vMbHrjy2P0GuTPnEgq8SkCXFLBH69AdaRT1w1rCM"}
     */

    var status: Boolean = false
    var message: String? = null
    var data: DataBean? = null
    var status_type: String ?= null

    class DataBean {
        /**
         * user_id : 3
         * token : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjoiOWRtVk5nOGciLCJpYXQiOjE1NjIyMzU1MDQsImV4cCI6MTU2MjIzNjEwNH0.5X8vMbHrjy2P0GuTPnEgq8SkCXFLBH69AdaRT1w1rCM
         */

        var user_id: Int = 0
        var employer_id: Int = 0
        var token: String? = null
        var user_role: String? = null
        var exp_type: String? = null
        var current_city: String? = null
        var state_name: String? = null
        var name: String? = null
        var profile_pic: String? = null
        var email: String? = null
        var phone: String? = null
        var email_isverified: String? = null
        var phone_isverified: String? = null
        var is_document_submitted: String? = null
        var is_document_verified: String? = null
        var account_type: String? = null
        var membership_id: String? = null
        var is_published: String? = null
        var identity_proof: String? = null
        var identity_video: String? = null
    }
}
