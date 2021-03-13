package com.dazzlerr_usa.utilities.sinchcalling

import android.content.*
import android.os.Bundle
import android.os.IBinder
import com.dazzlerr_usa.views.activities.synchcalling.CallScreenActivity
import com.dazzlerr_usa.views.activities.synchcalling.IncomingCallScreenActivity


class CallNotificationActionReceiver : BroadcastReceiver()
{
    var mContext: Context? = null
    var mCallId =""
    var mUserPic = ""
    var mUserName = ""

    override fun onReceive(context: Context, intent: Intent)
    {
        mContext = context
        if (intent != null && intent.extras != null)
        {
            var bundle = Bundle()
            bundle = intent.extras!!

            var action: String? = ""
            mCallId = bundle?.getString(SinchService.CALL_ID).toString()
            mUserPic = bundle?.getString(SinchService.USERPIC).toString()
            mUserName = bundle?.getString(SinchService.USERNAME).toString()

            action = intent.getStringExtra("ACTION_TYPE")
            if (action != null && !action.equals("", ignoreCase = true))
            {
                performClickAction(context, action)
            }

            // Close the notification after the click action is performed.
            val iclose = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            context.sendBroadcast(iclose)
            context.stopService(Intent(context, HeadsUpNotificationService::class.java))
        }
    }

    private fun performClickAction(context: Context, action: String) {

        object : ServiceConnection
            {

                override fun onServiceConnected(name: ComponentName?, service: IBinder?)
                {

                        val sinchService: SinchService.SinchServiceInterface? = service as SinchService.SinchServiceInterface?

                        if(sinchService!=null)
                        {

                            if (action.equals("RECEIVE_CALL", ignoreCase = true))
                            {

                                val call = service?.getCall(mCallId)
                                if (call != null) {
                                    call.answer()
                                    val intent = Intent(context, CallScreenActivity::class.java)
                                    intent.putExtra(SinchService.CALL_ID, mCallId)
                                    intent.putExtra(SinchService.USERPIC, mUserPic)
                                    intent.putExtra(SinchService.USERNAME, mUserName)
                                    intent.addFlags( Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    mContext?.startActivity(intent)
                                }

                            } else if (action.equals("DIALOG_CALL", ignoreCase = true))
                            {

                                val intent = Intent(context, IncomingCallScreenActivity::class.java)
                                intent.putExtra(SinchService.CALL_ID, mCallId)
                                intent.putExtra(SinchService.USERPIC, mUserPic)
                                intent.putExtra(SinchService.USERNAME, mUserName)
                                intent.putExtra("play_ringtone", "true")
                                intent.addFlags( Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NEW_TASK)
                                mContext?.startActivity(intent)

                            }
                            else
                            {

                                val call = service?.getCall(mCallId)
                                call?.hangup()
                                context.stopService(Intent(context, HeadsUpNotificationService::class.java))
                                val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                                context.sendBroadcast(it)


                            }

                        }


                }

                override fun onServiceDisconnected(name: ComponentName?) {}
                fun relayMessageData()
                {
                    context.applicationContext.bindService(Intent(context, SinchService::class.java), this, Context.BIND_AUTO_CREATE)
                }
            }.relayMessageData()


    }




}