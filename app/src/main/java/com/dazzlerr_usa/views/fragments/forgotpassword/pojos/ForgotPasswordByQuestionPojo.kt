package com.dazzlerr_usa.views.fragments.forgotpassword.pojos

class ForgotPasswordByQuestionPojo {

    var status: Boolean = false
    var message: String? = null
    var result: List<ResultBean>? = null

    class ResultBean {

        var question_id: String? = null
        var question: String? = null
    }
}
