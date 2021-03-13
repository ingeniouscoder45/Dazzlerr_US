package com.dazzlerr_usa.utilities.firebasemessaging

import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
import android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.dazzlerr_usa.R
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.Utils
import com.dazzlerr_usa.utilities.ccavenueutility.ServiceUtility
import com.dazzlerr_usa.utilities.sinchcalling.SinchService
import com.dazzlerr_usa.views.activities.OthersProfileActivity
import com.dazzlerr_usa.views.activities.UserInActiveActivity
import com.dazzlerr_usa.views.activities.eventdetails.EventDetailsActivity
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.activities.jobsdetails.JobsDetailsActivity
import com.dazzlerr_usa.views.activities.messagewindow.MessageWindowActivity
import com.dazzlerr_usa.views.activities.recommendedevents.RecommendedEventsActivity
import com.dazzlerr_usa.views.activities.recommendedjobs.RecommendedJobsActivity
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sinch.android.rtc.NotificationResult
import com.sinch.android.rtc.SinchHelpers
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject


class MyFirebaseMessagingService : FirebaseMessagingService()
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    private val TAG = "MyFirebaseMsgService"
    lateinit var bigImage: Bitmap
     var mSocket : Socket?=null
    var lastMessage_id = ""
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]

    override fun onMessageReceived(remoteMessage: RemoteMessage)
    {
        //--------Injecting the activity to dagger component-----
        (application as MyApplication).myComponent.inject(this)

        Timber.e("Notifications from: " + remoteMessage.from)


        val data: Map<String , String> = remoteMessage.data
        Timber.e("Remote Message: " + remoteMessage.data)
        if (SinchHelpers.isSinchPushPayload(data))
        {
            if(foregrounded())
            {
                return
            }

            object : ServiceConnection {
                private var payload: Map<String , String>? = null

                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

                    if (payload != null)
                    {

                        val sinchService: SinchService.SinchServiceInterface? = service as SinchService.SinchServiceInterface?
                        if (sinchService != null)
                        {
                            val dataResult = SinchHelpers.queryPushNotificationPayload(applicationContext, remoteMessage.data)

                            Constants.SINCH_CALLER_PIC = dataResult?.callResult?.headers?.get("user_pic").toString()
                            Constants.SINCH_CALLER_NAME = dataResult?.callResult?.headers?.get("user_name").toString()
                            Timber.e("Header: "+Constants.SINCH_CALLER_PIC)

                            val result: NotificationResult? = sinchService.relayRemotePushNotificationPayload(remoteMessage.data)
                        }
                    }
                    payload = null
                }

                override fun onServiceDisconnected(name: ComponentName?)
                {

                }

                fun relayMessageData(data: Map<String, String>?) {
                    payload = data
                    applicationContext.bindService(Intent(applicationContext, SinchService::class.java), this, Context.BIND_AUTO_CREATE)
                }
            }.relayMessageData(data)
        }
        else {
            // Checking if app notifications are enable or disabled
            if (sharedPreferences.getBoolean(Constants.isShowNotifications) && !sharedPreferences.getString(Constants.User_id).equals("")) {
                // Check if message contains a data payload.
                if (remoteMessage.data.size > 0) {
                    Timber.e("Message data payload: " + remoteMessage.data)

                    handleDataPayload(remoteMessage)
                }

                // Check if message contains a notification payload.
                if (remoteMessage.notification != null) {
                    if (remoteMessage.notification!!.body != null) {
                        Timber.e("Message Notification Body: " + remoteMessage.notification!!.body!!)
                        handleNotificationPayload(remoteMessage.notification!!.body)
                    }
                }
            }
        }

    }

    ///To check if the app is in foreground ///
    fun foregrounded(): Boolean
    {
        val appProcessInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return (appProcessInfo.importance === IMPORTANCE_FOREGROUND
                || appProcessInfo.importance === IMPORTANCE_VISIBLE)
    }
    override fun onNewToken(token: String)
    {
        Timber.e("Refreshed token: $token")
//        sendRegistrationToServer(token)

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
                .setContentTitle("Dazzlerr")
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


    private fun handleDataPayload(remoteMessage: RemoteMessage)
    {
        var intent : Intent? = null
        val bundle = Bundle()
        var title= remoteMessage.data["title"]
        var message=  remoteMessage.data["message"]

        if(remoteMessage.data.get("type").equals("job",ignoreCase = true))
        {
            bundle.putString("call_id" , ""+remoteMessage.data.get("id"))
            intent = Intent(this, JobsDetailsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtras(bundle)

            prepareNotification(title , message , intent ,3)
        }

        else if(remoteMessage.data.get("type").equals("proposal_hired",ignoreCase = true)||
                remoteMessage.data.get("type").equals("proposal_rejected",ignoreCase = true)||
                remoteMessage.data.get("type").equals("proposal_shortlisted",ignoreCase = true)
                || remoteMessage.data.get("type").equals("application_viewed"))
        {
            bundle.putString("call_id" , ""+remoteMessage.data.get("id"))
            intent = Intent(this, JobsDetailsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtras(bundle)

            prepareNotification(title , message , intent ,3)
        }

        else if(remoteMessage.data.get("type").equals("job_added",ignoreCase = true))
        {

            intent = Intent(this, RecommendedJobsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtras(bundle)

            prepareNotification(title , message , intent ,3)
        }

        else if(remoteMessage.data.get("type").equals("event_added",ignoreCase = true))
        {

            intent = Intent(this, RecommendedEventsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtras(bundle)

            prepareNotification(title , message , intent ,3)
        }

        else if(remoteMessage.data.get("type").equals("event",ignoreCase = true))
        {
            bundle.putString("event_id" , ""+remoteMessage.data.get("id"))
            intent = Intent(this, EventDetailsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtras(bundle)

            prepareNotification(title , message , intent ,3)
        }

        else if(remoteMessage.data.get("type").equals("login",ignoreCase = true))
        {
            intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            prepareNotification(title , message , intent ,3)
        }

        else if(remoteMessage.data.get("type").equals("contact",ignoreCase = true))
        {

            //If user is in chat list screen then we do not have need to show notifications
            if(!Constants.IN_CHATLIST_SCREEN)
            {
                if(!Constants.CURRENT_THREAD_ID.equals(""+remoteMessage.data.get("id")) )
                {

                    bundle.putString("Username",  "" + remoteMessage.data.get("title"))
                    bundle.putString("thread_id", "" + remoteMessage.data.get("id"))
                    bundle.putString("sender_id", "" + remoteMessage.data.get("sender_id"))

                    intent = Intent(this, MessageWindowActivity::class.java)
                    //intent.addFlags(Intent.FLAG_ACTIVITY_)
                    intent.putExtras(bundle)

                    if (remoteMessage.data.containsKey("msg_image") && !remoteMessage.data.get("msg_image").equals("") && remoteMessage.data.get("msg_image") != null) {

                        getBitmapAsyncAndDoWork("https://www.dazzlerr.com/API/" + remoteMessage.data.get("msg_image")!!, ""+remoteMessage.data.get("title"), message!!, intent)

                    } else {
                        prepareNotification(title, message, intent, 2)
                    }
                }
            }

            mSocket = (applicationContext as MyApplication).getSocket()!!


            var jsonObject  = JSONObject()
            jsonObject.put("thread_id" , remoteMessage.data.get("id"))
            jsonObject.put("sender_id" ,sharedPreferences.getString(Constants.User_id))
            jsonObject.put("read_status" ,"1")
            jsonObject.put("msg_id" , remoteMessage.data.get("msg_id"))

            lastMessage_id = remoteMessage.data.get("msg_id").toString()

            mSocket?.on("onConnect", Emitter.Listener { args ->

                if(lastMessage_id.isNotEmpty()) {
                    Timber.e("Connected on notification service")
                    mSocket?.emit("on_message_delivered", jsonObject)
                    //mSocket?.disconnect()
                    lastMessage_id = ""
                    //mSocket?.off("on_message_delivered" )


                }

            } )

            if(mSocket!!.connected())
            {
                Timber.e("Socket Already Connected on notification service")
                mSocket?.emit("on_message_delivered", jsonObject )

                //mSocket?.disconnect()
                lastMessage_id = ""
                //mSocket?.off("on_message_delivered" )
            }

            else
            {
                mSocket?.connect()
            }

        }

       else if(remoteMessage.data.get("type").equals("in_active",ignoreCase = true) && !Utils.isAppInBackground(this))
        {

            bundle.putString("title" , ""+remoteMessage.data.get("title"))
            bundle.putString("message" , ""+remoteMessage.data.get("message"))
            intent = Intent(this@MyFirebaseMessagingService, UserInActiveActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtras(bundle)
            startActivity(intent)
        }

        else if(remoteMessage.data.get("type").equals("in_active",ignoreCase = true) && Utils.isAppInBackground(this))
        {
            bundle.putString("title" , remoteMessage.data.get("title"))
            bundle.putString("message" , ""+remoteMessage.data.get("message"))
            intent = Intent(this@MyFirebaseMessagingService, UserInActiveActivity::class.java)
            intent.putExtras(bundle)
            prepareNotification(title , message , intent ,3)//3 For BigTextStyleNotification9871844686
        }


        else if(remoteMessage.data.get("type").equals("like",ignoreCase = true)
                || remoteMessage.data.get("type").equals("follow",ignoreCase = true)
                || remoteMessage.data.get("type").equals("shortlist")
                || remoteMessage.data.get("type").equals("contact_viewed")
                || remoteMessage.data.get("type").equals("portfolio_viewed")
                || remoteMessage.data.get("type").equals("photo_liked")
        )
        {
            bundle.putString("user_id" ,""+remoteMessage.data.get("id"))
            bundle.putString("user_role" , ""+remoteMessage.data.get("user_role"))
            bundle.putString("is_featured" , ""+remoteMessage.data.get("is_featured"))

            intent = Intent(this, OthersProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtras(bundle)

            prepareNotification(title , message , intent ,3)
        }

/*        else if(remoteMessage.data.get("type").equals("shortlist",ignoreCase = true))
        {
            bundle.putString("user_id" ,""+remoteMessage.data.get("id"))
            bundle.putString("user_role" , "casting")
            bundle.putString("is_featured" , "0")

            intent = Intent(this, OthersProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtras(bundle)

            prepareNotification(title , message , intent ,3)
        }*/


        else if (remoteMessage.data.get("type").equals("others"))
        {

        if(remoteMessage.data.containsKey("image") && !remoteMessage.data.get("image").equals("") && remoteMessage.data.get("image")!=null)
        {

            intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            getBitmapAsyncAndDoWork(remoteMessage.data.get("image")!! ,title!! , message!! , intent)

        }
        else
        {

            intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            prepareNotification(title , message , intent ,3) //3 For BigTextStyleNotification

        }

        }

        else if (remoteMessage.data.get("type").equals("occasional"))
        {

                    if(remoteMessage.data.containsKey("image") && !remoteMessage.data.get("image").equals("") && remoteMessage.data.get("image")!=null)
                    {

                        intent = Intent(this, HomeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        getBitmapAsyncAndDoWork(remoteMessage.data.get("image")!! ,title!! , message!! , intent)

                    }
                    else
                    {

                        intent = Intent(this, HomeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        prepareNotification(title , message , intent ,3) //3 For BigTextStyleNotification

                    }
                }
        else
        {
            intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            prepareNotification(title , message , intent,3) // 3 For BigTextStyleNotification


        }
    }

    fun prepareNotification( title: String? ,  message : String? ,  intent: Intent, type: Int)
    {

        val pendingIntent = PendingIntent.getActivity(this,  ServiceUtility.randInt(0, 9999),
                intent, PendingIntent.FLAG_ONE_SHOT)

        val CHANNEL_ID = "my_channel_01"
        val name = "Android"

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mBuilder = NotificationCompat.Builder(this , CHANNEL_ID)

        mBuilder
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_dazzlerr)
                .setColor(resources.getColor(R.color.appColor))
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
                .setChannelId(CHANNEL_ID)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)

        if(type==1)  //For BigPictureStyleNotification
            mBuilder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bigImage))

        else if(type ==2)//For InboxStyleNotification
            mBuilder.setStyle(NotificationCompat.InboxStyle().addLine(message).setBigContentTitle(title))

        else if(type==3)//For BigTextStyleNotification
            mBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(message))

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(CHANNEL_ID, name, importance)
            assert(mNotificationManager != null)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }

        assert(mNotificationManager != null)
        mNotificationManager.notify(System.currentTimeMillis().toInt(),
                mBuilder.build())

    }

    // Load bitmap from image url on background thread and display image notification
    private fun getBitmapAsyncAndDoWork(imageUrl: String ,title:String , message: String ,intent : Intent)
    {

        Glide.with(applicationContext)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap?>()
                {
                    override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?)
                    {
                        bigImage = resource
                        prepareNotification(title ,message , intent ,1)// 1 For BigPictureStyleNotification
                    }

                    override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
                })

    }

    override fun onDestroy() {
        super.onDestroy()

        if(mSocket!=null)
        {
            mSocket?.disconnect()
         mSocket?.off("on_message_delivered" )
        }
    }


}