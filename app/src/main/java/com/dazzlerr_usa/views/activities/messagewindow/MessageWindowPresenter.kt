package com.dazzlerr_usa.views.activities.messagewindow

import java.io.File

interface MessageWindowPresenter
{

    fun generateThread(user_id : String , profile_id:String)
    fun getAllMessages(user_id : String , thread_id: String)
    fun blockUser(user_id : String , thread_id: String ,status : String)
    fun sendMessage(thread_id: String , sender_id:String , name:String , email: String , phone : String  ,message: String)

    fun sendMultimediaMessage(imageFile: File, thread_id: String, sender_id:String, name:String, email: String, phone : String)

    fun cancelRetrofitRequest()
}