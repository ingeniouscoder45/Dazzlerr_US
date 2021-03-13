package com.dazzlerr_usa.utilities.sinchcalling

import android.content.*
import android.os.Bundle
import android.os.IBinder
import timber.log.Timber


class MyCallNotificationActionReceiver : BroadcastReceiver()
{
    var mContext: Context? = null
    var mCallId =""
    var mUserPic = ""

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

            action = intent.getStringExtra("ACTION_TYPE")
            if (action != null && !action.equals("", ignoreCase = true))
            {
                Timber.e("Broadcast called")
                performClickAction(context, action)
            }

            // Close the notification after the click action is performed.
            val iclose = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            context.sendBroadcast(iclose)
            context.stopService(Intent(context, OngoingCallNotificationService::class.java))
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

                             if (action.equals("DIALOG_MYCALL", ignoreCase = true))
                            {

                            }

                            else if (action.equals("CANCEL_MYCALL", ignoreCase = true))
                            {

                                val call = service?.getCall(mCallId)
                                call?.hangup()
                                context.stopService(Intent(context, OngoingCallNotificationService::class.java))
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