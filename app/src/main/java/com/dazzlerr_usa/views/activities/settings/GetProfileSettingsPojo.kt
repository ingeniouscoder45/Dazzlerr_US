package com.dazzlerr_usa.views.activities.settings

class GetProfileSettingsPojo
{

    var status: String? = null
    var success: Boolean = false
    var username: String? = null
    var set_question_id: String? = null
    var set_answer: String? = null
    var data: String? = null
    var secondary_role_id: String? = null
    var secondary_role: String? = null
    var questions: MutableList<QuestionsBean>? = null

    class QuestionsBean
    {
        var question_id: Int = 0
        var question: String? = null
    }
}
