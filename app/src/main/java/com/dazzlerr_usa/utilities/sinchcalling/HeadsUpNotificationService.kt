package com.dazzlerr_usa.utilities.sinchcalling

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.dazzlerr_usa.R
import com.dazzlerr_usa.BuildConfig
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.activities.synchcalling.IncomingCallScreenActivity
import com.sinch.android.rtc.PushPair
import com.sinch.android.rtc.calling.Call
import com.sinch.android.rtc.calling.CallListener
import timber.log.Timber

class HeadsUpNotificationService : Service() {
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
        val NOTIFICATION_ID = 120

        val customView = RemoteViews(packageName, R.layout.custom_call_notification)
        if (intent != null && intent.extras != null) {
            data = intent.extras

        }

        object : ServiceConnection {
            private var payload: Map<String , String>? = null

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

                    val sinchService: SinchService.SinchServiceInterface? = service as SinchService.SinchServiceInterface?
                    if (sinchService != null)
                    {

                        val call = service?.getCall(data?.getString(SinchService.CALL_ID))
                        if (call != null) {
                            call.addCallListener(SinchCallListener())
                            callerName = data?.getString(SinchService.USERNAME).toString()
                            customView.setTextViewText(R.id.name , callerName.capitalize())
                        }


                        val receiveCallAction = Intent(this@HeadsUpNotificationService, CallNotificationActionReceiver::class.java)
                        receiveCallAction.putExtra("ConstantApp.CALL_RESPONSE_ACTION_KEY", "ConstantApp.CALL_RECEIVE_ACTION")
                        receiveCallAction.putExtra("ACTION_TYPE", "RECEIVE_CALL")
                        receiveCallAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID)
                        data?.let { receiveCallAction.putExtras(it) }
                        receiveCallAction.action = "RECEIVE_CALL"

                        val cancelCallAction = Intent(this@HeadsUpNotificationService, CallNotificationActionReceiver::class.java)
                        cancelCallAction.putExtra("ConstantApp.CALL_RESPONSE_ACTION_KEY", "ConstantApp.CALL_CANCEL_ACTION")
                        cancelCallAction.putExtra("ACTION_TYPE", "CANCEL_CALL")
                        cancelCallAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID)
                        data?.let { cancelCallAction.putExtras(it) }
                        cancelCallAction.action = "CANCEL_CALL"

                        val callDialogAction = Intent(this@HeadsUpNotificationService, CallNotificationActionReceiver::class.java)
                        callDialogAction.putExtra("ConstantApp.CALL_RESPONSE_ACTION_KEY", "ConstantApp.DIALOG_CALL")
                        callDialogAction.putExtra("ACTION_TYPE", "DIALOG_CALL")
                        callDialogAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID)
                        data?.let { callDialogAction.putExtras(it) }
                        callDialogAction.action = "DIALOG_CALL"


                        val answerPendingIntent = PendingIntent.getBroadcast(this@HeadsUpNotificationService, 1200, receiveCallAction, PendingIntent.FLAG_UPDATE_CURRENT)
                        val cancelCallPendingIntent = PendingIntent.getBroadcast(this@HeadsUpNotificationService, 1201, cancelCallAction, PendingIntent.FLAG_UPDATE_CURRENT)
                        val pendingIntent = PendingIntent.getBroadcast(this@HeadsUpNotificationService, 1202, callDialogAction, PendingIntent.FLAG_UPDATE_CURRENT)

                        customView.setOnClickPendingIntent(R.id.btnAnswer, answerPendingIntent)
                        customView.setOnClickPendingIntent(R.id.btnDecline, cancelCallPendingIntent)


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        {

                            Timber.e("Show notification")

                            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            val notificationChannel = NotificationChannel("IncomingCall",
                                    "IncomingCall", NotificationManager.IMPORTANCE_HIGH)
                            notificationChannel.setSound(null, null)
                            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                            notificationManager.createNotificationChannel(notificationChannel)
                            val notification = NotificationCompat.Builder(this@HeadsUpNotificationService, "IncomingCall")
                            notification.setContentTitle("Dazzlerr")
                            notification.setTicker("Call_STATUS")
                            notification.setContentText("IncomingCall")
                            notification.setSmallIcon(R.drawable.ic_dazzlerr)
                            notification.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
                            notification.setCategory(NotificationCompat.CATEGORY_CALL)
                            notification.setVibrate(null)
                            notification.setOngoing(true)
                            notification.setFullScreenIntent(pendingIntent, true)
                            notification.priority = NotificationCompat.PRIORITY_MAX
                            notification.setStyle(NotificationCompat.DecoratedCustomViewStyle())
                            notification.setCustomContentView(customView)
                            notification.setCustomBigContentView(customView)

                            var incomingCallNotification: Notification? = null
                            if (notification != null) {
                                incomingCallNotification = notification.build()
                            }
                            startForeground(NOTIFICATION_ID, incomingCallNotification)


                        } else {
                            val notification = NotificationCompat.Builder(this@HeadsUpNotificationService)
                            notification.setContentTitle("Dazzlerr")
                            notification.setTicker("Call_STATUS")
                            notification.setContentText("IncomingCall")
                            notification.setSmallIcon(R.drawable.ic_dazzlerr)
                            notification.setLargeIcon(BitmapFactory.decodeResource(this@HeadsUpNotificationService.resources, R.drawable.ic_dazzlerr))
                            notification.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
                            notification.setVibrate(null)
                            notification.setContentIntent(pendingIntent)
                            notification.setOngoing(true)
                            notification.setCategory(NotificationCompat.CATEGORY_CALL)
                            notification.priority = NotificationCompat.PRIORITY_MAX
                            notification.addAction(R.drawable.ic_baseline_call_end_24, "Reject", cancelCallPendingIntent)
                            notification.addAction(R.drawable.ic_baseline_call_24, "Accept", answerPendingIntent)
                            notification.setAutoCancel(true) //.setSound(ringUri)

                            var incomingCallNotification: Notification? = null
                            if (notification != null) {
                                incomingCallNotification = notification.build()
                            }
                            startForeground(NOTIFICATION_ID, incomingCallNotification)

                        }
                    }

            }

            override fun onServiceDisconnected(name: ComponentName?) {}
            fun relayMessageData() {

                applicationContext.bindService(Intent(applicationContext, SinchService::class.java), this, Context.BIND_AUTO_CREATE)
            }
        }.relayMessageData()


        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy() // release your media player here
    }

    private inner class SinchCallListener : CallListener
    {
        override fun onCallEnded(call: Call) {
            val cause = call.details.endCause
            Log.e(IncomingCallScreenActivity.TAG, "Call ended, cause: $cause")

            if(cause.toString().equals("CANCELED",ignoreCase = true))
            handleNotificationPayload("Missed voice call ")

            // Close the notification after the click action is performed.
            val iclose = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            sendBroadcast(iclose)
            stopService(Intent(applicationContext, HeadsUpNotificationService::class.java))


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

    private fun handleNotificationPayload(messageBody: String?)
    {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val channelId = "my_channel_01"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_dazzlerr)
                .setColor(resources.getColor(R.color.appColor))
                .setContentTitle(callerName)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setContentText(messageBody)
                .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channel = NotificationChannel(channelId,
                    "Android",
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

}