package com.dazzlerr_usa.utilities.sinchcalling

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dazzlerr_usa.R
import com.dazzlerr_usa.views.activities.synchcalling.CallScreenActivity
import com.dazzlerr_usa.views.activities.synchcalling.IncomingCallScreenActivity
import com.sinch.android.rtc.PushPair
import com.sinch.android.rtc.calling.Call
import com.sinch.android.rtc.calling.CallListener
import timber.log.Timber
import java.util.*


class OngoingCallNotificationService : Service()
{
    private val CHANNEL_ID = "DazzlerrCallChannel"
    private val CHANNEL_NAME = "DazzlerrCall Channel"
    var callerName = ""
    var mAudioPlayer:AudioPlayer?=null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int
    {

        var data: Bundle? = null
        val NOTIFICATION_ID = 121

        if (intent != null && intent.extras != null) {
            data = intent.extras
        }
        Timber.e("Service Started")


        object : ServiceConnection
        {

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

                    val sinchService: SinchService.SinchServiceInterface? = service as SinchService.SinchServiceInterface?
                    if (sinchService != null)
                    {

                        val call = service?.getCall(data?.getString(SinchService.CALL_ID))
                        if (call != null)
                        {
                            call.addCallListener(SinchCallListener())
                            callerName = data?.getString(SinchService.USERNAME).toString()
                            val cancelCallAction = Intent(this@OngoingCallNotificationService, MyCallNotificationActionReceiver::class.java)
                            cancelCallAction.putExtra("ConstantApp.CALL_RESPONSE_ACTION_KEY", "ConstantApp.MYCALL_CANCEL_ACTION")
                            cancelCallAction.putExtra("ACTION_TYPE", "CANCEL_MYCALL")
                            cancelCallAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID)
                            data?.let { cancelCallAction.putExtras(it) }
                            cancelCallAction.action = "CANCEL_MYCALL"

                            val callDialogintent = Intent(this@OngoingCallNotificationService, CallScreenActivity::class.java)
                            callDialogintent.putExtra(SinchService.CALL_ID, data?.getString(SinchService.CALL_ID))
                            callDialogintent.putExtra(SinchService.USERPIC, data?.getString(SinchService.USERPIC))
                            callDialogintent.putExtra(SinchService.USERNAME, data?.getString(SinchService.USERNAME))
                            callDialogintent.putExtra("isPrimaryCaller", true)
                            callDialogintent.addFlags( Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_CLEAR_TOP)

                            val cancelCallPendingIntent = PendingIntent.getBroadcast(this@OngoingCallNotificationService, 1205, cancelCallAction, PendingIntent.FLAG_UPDATE_CURRENT)
                            val pendingIntent = PendingIntent.getActivity(this@OngoingCallNotificationService, 1206, callDialogintent, PendingIntent.FLAG_UPDATE_CURRENT)

                            createChannel()
                            var notificationBuilder: NotificationCompat.Builder? = null
                            if (data != null) {
                                // Uri ringUri= Settings.System.DEFAULT_RINGTONE_URI;
                                notificationBuilder = NotificationCompat.Builder(this@OngoingCallNotificationService, CHANNEL_ID)
                                        .setContentTitle(callerName.capitalize())
                                        .setContentText("Outgoing voice call")
                                        .setSmallIcon(R.drawable.ic_dazzlerr)
                                        .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
                                        .setVibrate(null)
                                        .addAction(R.drawable.ic_baseline_call_end_24, "Hang up", cancelCallPendingIntent)
                                        .setAutoCancel(true) //.setSound(ringUri)
                                        .setContentIntent(pendingIntent)
                            }

                            var incomingCallNotification: Notification? = null
                            if (notificationBuilder != null) {
                                incomingCallNotification = notificationBuilder.build()
                            }
                            startForeground(NOTIFICATION_ID, incomingCallNotification)
                        }
                    }

            }

            override fun onServiceDisconnected(name: ComponentName?) {}
            fun relayMessageData()
            {
                applicationContext.bindService(Intent(applicationContext, SinchService::class.java), this, Context.BIND_AUTO_CREATE)
            }
        }.relayMessageData()


        return START_STICKY
    }

    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {

                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
                channel.description = "Call Notifications"
                channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                channel.setSound(null, null)
                /* channel.setSound(ringUri,
                    new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setLegacyStreamType(AudioManager.STREAM_RING)
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION).build());*/Objects.requireNonNull(this@OngoingCallNotificationService.getSystemService(NotificationManager::class.java)).createNotificationChannel(channel)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy() // release your media player here
    }

    private inner class SinchCallListener : CallListener
    {
        override fun onCallEnded(call: Call) {
            val cause = call.details.endCause
            Log.e(IncomingCallScreenActivity.TAG, "Call ended, cause: $cause")

            // Close the notification after the click action is performed.
            val iclose = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            sendBroadcast(iclose)
            stopService(Intent(applicationContext, OngoingCallNotificationService::class.java))


        }

        override fun onCallEstablished(call: Call) {
            Log.d(IncomingCallScreenActivity.TAG, "Call established")
        }

        override fun onCallProgressing(call: Call) {
            Log.d(IncomingCallScreenActivity.TAG, "Call progressing")
        }

        override fun onShouldSendPushNotification(call: Call, pushPairs: List<PushPair>) {
            // Send a push through your push provider here, e.g. GCM
        }
    }


}