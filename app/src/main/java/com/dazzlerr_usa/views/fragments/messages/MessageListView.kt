package com.dazzlerr_usa.views.fragments.messages

interface MessageListView
{

    fun onGetTrashSuccess( mMessageList: MutableList<MessagesListPojo.DataBean>)
    fun onGetChatlistSuccess( mMessageList: MutableList<MessagesListPojo.DataBean>)
    fun onDeleteSuccess( position : Int , messageType:Int)
    fun onRestoreSuccess( position : Int)
    fun onFailed(message : String)
    fun showProgress(visibility:Boolean)
}