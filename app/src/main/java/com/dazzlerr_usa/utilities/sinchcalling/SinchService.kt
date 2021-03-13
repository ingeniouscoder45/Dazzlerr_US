package com.dazzlerr_usa.utilities.sinchcalling

import android.app.ActivityManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.activities.synchcalling.IncomingCallScreenActivity
import com.sinch.android.rtc.*
import com.sinch.android.rtc.calling.Call
import com.sinch.android.rtc.calling.CallClient
import com.sinch.android.rtc.calling.CallClientListener
import com.sinch.android.rtc.calling.CallListener
import timber.log.Timber
import javax.inject.Inject


class SinchService : Service() {

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    private val mSinchServiceInterface = SinchServiceInterface()
    private var mSinchClient: SinchClient? = null
    var userName: String? = null
    var mAudioPlayer : AudioPlayer ? =null
    private var mListener: StartFailedListener? = null

    override fun onCreate()
    {
        super.onCreate()
        (application as MyApplication).myComponent.inject(this)
    }

    override fun onDestroy() {
        if (mSinchClient != null && mSinchClient!!.isStarted) {
            mSinchClient!!.terminate()
        }
        super.onDestroy()
    }

    private fun start(userName: String)
    {
        if (mSinchClient == null) {
            this.userName = userName
            mSinchClient = Sinch.getSinchClientBuilder().context(applicationContext).userId(userName)
                    .applicationKey(APP_KEY)
                    .applicationSecret(APP_SECRET)
                    .environmentHost(ENVIRONMENT).build()

            mSinchClient?.setSupportCalling(true)
            mSinchClient?.setSupportActiveConnectionInBackground(true)
            mSinchClient?.setSupportPushNotifications(true)
            mSinchClient?.startListeningOnActiveConnection()
            mSinchClient?.addSinchClientListener(MySinchClientListener())
            mSinchClient?.getCallClient()?.addCallClientListener(SinchCallClientListener())
            mSinchClient?.setSupportManagedPush(true)
            mSinchClient?.start()
        }
    }

    private fun stop() {
        if (mSinchClient != null) {
            mSinchClient!!.terminate()
            mSinchClient = null
        }
    }

    private val isStarted: Boolean
        private get() = mSinchClient != null && mSinchClient!!.isStarted

    override fun onBind(intent: Intent): IBinder? {
        return mSinchServiceInterface
    }

    inner class SinchServiceInterface : Binder()
    {

        fun getSinchClient(): SinchClient?
        {
            return mSinchClient
        }

        fun getManagedPush(username: String?): ManagedPush? {
            /*// create client, but you don't need to start it
            initClient(username)*/
            // retrieve ManagedPush
            return Beta.createManagedPush(mSinchClient)
        }

        fun relayRemotePushNotificationPayload(payload: Map<String, String>): NotificationResult?
        {
            if (mSinchClient == null && sharedPreferences!!.getString(Constants.User_name).toString()!!.isNotEmpty())
            {
                startClient(sharedPreferences!!.getString(Constants.User_name).toString())
                Timber.e("Start Sinch Client: "+ payload)
            }

            else if (mSinchClient == null && sharedPreferences!!.getString(Constants.User_name).toString().isEmpty()) {
                Log.e(TAG, "Can't start a SinchClient as no username is available, unable to relay push.")
                return null
            }
            return mSinchClient?.relayRemotePushNotificationPayload(payload)
        }

        fun callPhoneNumber(phoneNumber: String?): Call
        {
            return mSinchClient!!.callClient.callPhoneNumber(phoneNumber)
        }

        fun callUser(userId: String?): Call {
            return mSinchClient!!.callClient.callUser(userId)
        }

        fun callUser(userId: String?, headers: MutableMap<String, String>): Call {
            return mSinchClient!!.callClient.callUser(userId, headers)
        }

        val isStarted: Boolean
            get() = this@SinchService.isStarted

        fun startClient(userName: String) {
            start(userName)
        }

        fun stopClient() {
            stop()
        }

        fun setStartListener(listener: StartFailedListener?) {
            mListener = listener
        }

        fun getCall(callId: String?): Call {
            return mSinchClient!!.callClient.getCall(callId)
        }
    }

    interface StartFailedListener {
        fun onStartFailed(error: SinchError?)
        fun onStarted()
    }

    private inner class MySinchClientListener : SinchClientListener {
        override fun onClientFailed(client: SinchClient, error: SinchError) {
            if (mListener != null) {
                mListener!!.onStartFailed(error)
            }
            mSinchClient!!.terminate()
            mSinchClient = null
        }

        override fun onClientStarted(client: SinchClient) {
            Log.d(TAG, "SinchClient started")
            if (mListener != null) {
                mListener!!.onStarted()
            }
        }

        override fun onClientStopped(client: SinchClient) {
            Log.d(TAG, "SinchClient stopped")
        }

        override fun onLogMessage(level: Int, area: String, message: String) {
            when (level) {
                Log.DEBUG -> Log.d(area, message)
                Log.ERROR -> Log.e(area, message)
                Log.INFO -> Log.i(area, message)
                Log.VERBOSE -> Log.v(area, message)
                Log.WARN -> Log.w(area, message)
            }
        }

        override fun onRegistrationCredentialsRequired(client: SinchClient,
                                                       clientRegistration: ClientRegistration) {
        }
    }

    private inner class SinchCallClientListener : CallClientListener {
        override fun onIncomingCall(callClient: CallClient, call: Call)
        {
            Timber.e("Incoming call: "+call.callId)

            playRingtone()

            call.addCallListener(SinchCallListener())
            if(foregrounded())
            {
                val intent = Intent(this@SinchService, IncomingCallScreenActivity::class.java)

                intent.putExtra(CALL_ID, call.callId)

                if (call.headers["user_pic"].toString().equals("null"))//If coming from firebase messaging service
                    intent.putExtra(USERPIC, Constants.SINCH_CALLER_PIC)
                else
                    intent.putExtra(USERPIC, call.headers["user_pic"])

                if (call.headers["user_name"].toString().equals("null"))//If coming from firebase messaging service
                    intent.putExtra(USERNAME, Constants.SINCH_CALLER_NAME)
                else
                    intent.putExtra(USERNAME, call.headers["user_name"])

                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NEW_TASK)

                this@SinchService.startActivity(intent)

            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                {
                    val serviceIntent = Intent(applicationContext, HeadsUpNotificationService::class.java)
                    val mBundle = Bundle()
                    mBundle.putString("remote_userid", call.remoteUserId)
                    mBundle.putString(CALL_ID, call.callId)

                    if (call.headers["user_pic"].toString().equals("null"))//If coming from firebase messaging service
                        mBundle.putString(USERPIC, Constants.SINCH_CALLER_PIC)
                    else
                        mBundle.putString(USERPIC, call.headers["user_pic"])

                    if (call.headers["user_name"].toString().equals("null"))//If coming from firebase messaging service
                        mBundle.putString(USERNAME, Constants.SINCH_CALLER_NAME)
                    else
                        mBundle.putString(USERNAME, call.headers["user_name"])


                    serviceIntent.putExtras(mBundle)
                    ContextCompat.startForegroundService(applicationContext, serviceIntent)
                }
                else
                {
                    val intent = Intent(this@SinchService, IncomingCallScreenActivity::class.java)

                    intent.putExtra(CALL_ID, call.callId)

                    if (call.headers["user_pic"].toString().equals("null"))//If coming from firebase messaging service
                        intent.putExtra(USERPIC, Constants.SINCH_CALLER_PIC)
                    else
                        intent.putExtra(USERPIC, call.headers["user_pic"])

                    if (call.headers["user_name"].toString().equals("null"))//If coming from firebase messaging service
                        intent.putExtra(USERNAME, Constants.SINCH_CALLER_NAME)
                    else
                        intent.putExtra(USERNAME, call.headers["user_name"])


                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NEW_TASK)

                    this@SinchService.startActivity(intent)

                }
            }
        }

    }


    ///To check if the app is in foreground ///
    fun foregrounded(): Boolean
    {
        val appProcessInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return (appProcessInfo.importance === ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                || appProcessInfo.importance === ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE)
    }

    companion object {
        private const val APP_KEY = "ae140f05-c8ea-4a96-b372-7cb5f1592d2a"
        private const val APP_SECRET = "/r341jpEy0aBj9LDHZEBzA=="
        private const val ENVIRONMENT = "clientapi.sinch.com"
        const val LOCATION = "LOCATION"
        const val USERPIC = "USERPIC"
        const val USERNAME = "USERNAME"
        const val CALL_ID = "CALL_ID"
        val TAG = SinchService::class.java.simpleName
    }

    fun playRingtone()
    {
        mAudioPlayer = AudioPlayer(this)
        mAudioPlayer?.playRingtone()
    }

    fun stopRingtone()
    {
        if(mAudioPlayer!=null)
        mAudioPlayer?.stopRingtone()
    }

    private inner class SinchCallListener : CallListener
    {
        override fun onCallEnded(call: Call) {
            val cause = call.details.endCause
            Log.d(IncomingCallScreenActivity.TAG, "Call ended, cause: $cause")
            stopRingtone()
        }

        override fun onCallEstablished(call: Call) {
            Log.d(IncomingCallScreenActivity.TAG, "Call established")
            stopRingtone()
        }

        override fun onCallProgressing(call: Call) {
            Log.d(IncomingCallScreenActivity.TAG, "Call progressing")
        }

        override fun onShouldSendPushNotification(call: Call, pushPairs: List<PushPair>) {
            // Send a push through your push provider here, e.g. GCM

            Toast.makeText(this@SinchService , "Send Push notification" , Toast.LENGTH_SHORT).show()
        }
    }
}