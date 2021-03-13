package com.dazzlerr_usa.views.fragments.messages

interface MessagesListPresenter
{

    fun getTrashMessages(user_id:String ,page:String)
    fun getChatList(user_id:String)
    fun deleteMessages(thread_id:String ,position:Int , messageType:Int)
    fun restoreMessages(thread_id:String ,position:Int)
    fun cancelRetrofitRequest()
}