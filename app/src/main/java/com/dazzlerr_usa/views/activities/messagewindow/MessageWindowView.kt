package com.dazzlerr_usa.views.activities.messagewindow

interface MessageWindowView
{
    fun OnGetMessagesSuccess(model: MessageWindowPojo)
    fun onThreadIdGenrated(model: GenerateThraedPojo)
    fun onChatReplySuccess(model : MultimediaChatPojo)
    fun onUserBlockedSuccess()
    fun onFailed(message : String)
    fun showProgress(visibility:Boolean)
}