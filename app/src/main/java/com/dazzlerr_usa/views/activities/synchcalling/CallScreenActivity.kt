package com.dazzlerr_usa.views.activities.synchcalling

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.sinchcalling.AudioPlayer
import com.dazzlerr_usa.utilities.sinchcalling.HeadsUpNotificationService
import com.dazzlerr_usa.utilities.sinchcalling.OngoingCallNotificationService
import com.dazzlerr_usa.utilities.sinchcalling.SinchService
import com.dazzlerr_usa.views.activities.ContainerActivity
import com.google.android.material.snackbar.Snackbar
import com.sinch.android.rtc.PushPair
import com.sinch.android.rtc.calling.Call
import com.sinch.android.rtc.calling.CallListener
import kotlinx.android.synthetic.main.activity_call_screen.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CallScreenActivity : ContainerActivity(),  View.OnClickListener , AddCallLogsView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    private var mAudioPlayer: AudioPlayer? = null
    private var mCallId: String? = null
    private var mRemoteUserid: String? = null
    private var mUserPic: String? = null
    private var mUserName: String? = null
    var isMuted = false
    var isCallOnSpeaker = false
    var isCallInProgress = false
    lateinit var mPresenter: AddCallLogsPresenter
    lateinit var call: Call
    var isPrimaryCaller = false

    override fun onLocationfetched(latitude: Double, longitude: Double, address: String?)
    {
            //Not in use
    }


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_screen)
        initializations()
        clickListeners()
    }

    fun initializations()
    {

        (application as MyApplication).myComponent.inject(this)
        mAudioPlayer = AudioPlayer(this)
        mPresenter = AddCallLogModel(this ,this)

        mCallId = intent.getStringExtra(SinchService.CALL_ID)
        mUserPic = intent.getStringExtra(SinchService.USERPIC)
        mUserName = intent.getStringExtra(SinchService.USERNAME)
        isPrimaryCaller = intent.getBooleanExtra("isPrimaryCaller" ,false)

        Glide.with(this)
                .load("https://www.dazzlerr.com/API/"+mUserPic).apply(RequestOptions().centerCrop())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(callerImage)

    }

    fun clickListeners()
    {
    muteUnmuteButton.setOnClickListener(this)
    speakerButton.setOnClickListener(this)
    hangupButton.setOnClickListener(this)
    }

    public override fun onServiceConnected() {
         call = getSinchServiceInterface()!!.getCall(mCallId)
        if (call != null) {
            call.addCallListener(SinchCallListener())

            remoteUser.setText(mUserName?.capitalize())
            mRemoteUserid = call.remoteUserId
            // mCallState.setText(call.getState().toString());

            isCallInProgress =true

            if(call.state.name.equals("INITIATING"))
            {
                callState.setText("Connecting")
                callDuration.visibility = View.GONE
            }
             else if(call.state.name.equals("PROGRESSING"))
            {
                callState.setText("Ringing")
                callDuration.visibility = View.GONE
                //mCallStart = call.details.startedTime
            }
             else if(call.state.name.equals("ESTABLISHED"))
            {
                callState.setText("Connected")
               // mCallStart =  call.details.establishedTime
                callDuration.visibility = View.VISIBLE

                Timber.e(call.details.duration.toString())

                val timeInMilSeconds = (call.details.duration*1000)
                callDuration.base = SystemClock.elapsedRealtime() - timeInMilSeconds
                callDuration.start()
            }

            Timber.e(call.details.startedTime.toString())

           /* Timber.e(call.state.name)
            Timber.e(call.details.duration.toString())
            Timber.e( System.currentTimeMillis().toString())
            Timber.e(Timestamp(call.details.establishedTime).toString())
            */
        } else {
            Log.e(TAG, "Started with invalid callId, aborting.")
            finish()
        }
    }

    public override fun onPause() {
        super.onPause()

        if(isCallInProgress)
        {
            val serviceIntent = Intent(applicationContext, OngoingCallNotificationService::class.java)
            val mBundle = Bundle()
            mBundle.putString("remote_userid", mRemoteUserid)
            mBundle.putString(SinchService.CALL_ID, mCallId)
            mBundle.putString(SinchService.USERPIC, mUserPic)
            mBundle.putString(SinchService.USERNAME, mUserName)

            serviceIntent.putExtras(mBundle)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                Timber.e("Service Called")
                ContextCompat.startForegroundService(applicationContext, serviceIntent)
            }
            else
            {
                startService(serviceIntent)
            }
        }

    }


    override fun onBackPressed() {
        // User should exit activity by ending call, not by going back.
    }

    private fun endCall(cause : String , call:Call)
    {

        isCallInProgress = false
        applicationContext.stopService(Intent(applicationContext, HeadsUpNotificationService::class.java))
        val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        applicationContext.sendBroadcast(it)

        mAudioPlayer!!.stopProgressTone()

        var call_duration = "00:00"

        if (!(cause == "DENIED")
           && !(cause == "CANCELED")
           && !(cause == "NO_ANSWER"))
        {
            call_duration = formatTimespan(call.details.duration).toString()
        }


        val unixSeconds: Long = call.details.startedTime

        val date = Date(unixSeconds * 1000L)
        val sdf = SimpleDateFormat("dd MMM, yyyy hh:mm:ss a")
        val formattedDate = sdf.format(date)
        Timber.e(formattedDate)

        if(isNetworkActiveWithMessage())
        mPresenter.addCallLog(sharedPreferences.getString(Constants.User_id).toString(), mUserName.toString() ,call.remoteUserId,  call_duration  ,cause ,formattedDate,""+isPrimaryCaller)

    }

    private fun formatTimespan(totalSeconds: Int): String?
    {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    private inner class SinchCallListener : CallListener {
        override fun onCallEnded(call: Call) {
            val cause = call.details.endCause
            Log.e(TAG, "Call ended. Reason: $cause")
            mAudioPlayer!!.stopProgressTone()
            volumeControlStream = AudioManager.USE_DEFAULT_STREAM_TYPE
            val endMsg = "Call ended: " + call.details.toString()
            //Toast.makeText(CallScreenActivity.this, endMsg, Toast.LENGTH_LONG).show();
            if (cause.toString() == "DENIED")
                callState.setText("Rejected")
            else if (cause.toString() == "CANCELED")
                callState.setText("Canceled")
            else if (cause.toString() == "NO_ANSWER")
                callState.setText("No Answer")
            else
                callState.setText("Disconnected")

            endCall(cause.toString() ,call)
        }

        override fun onCallEstablished(call: Call) {
            Log.d(TAG, "Call established")
            callState.setText("Connected")
            mAudioPlayer!!.stopProgressTone()
            volumeControlStream = AudioManager.STREAM_VOICE_CALL
            callDuration.visibility = View.VISIBLE
            callDuration.start()
        }

        override fun onCallProgressing(call: Call) {
            Log.d(TAG, "Call progressing")
            callState.setText("Ringing")
            mAudioPlayer!!.playProgressTone()
        }

        override fun onShouldSendPushNotification(call: Call, pushPairs: List<PushPair>) {
            // Send a push through your push provider here, e.g. GCM

            Timber.e("remote id: "+ call.getRemoteUserId())
            Timber.e("Payload: "+pushPairs.get(0).getPushPayload())
            Timber.e("call id: "+ call.callId)

        }
    }


    override fun onClick(v: View?)
    {

        when(v?.id)
        {

            R.id.hangupButton->
            {
                call?.hangup()
            }
            R.id.muteUnmuteButton->
            {
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                if (!isMuted) {
                    if (audioManager.mode == AudioManager.MODE_IN_CALL || audioManager.mode == AudioManager.MODE_IN_COMMUNICATION) {
                        audioManager.isMicrophoneMute = true
                    }

                    muteUnmuteButton.setBackgroundResource(R.drawable.appcolor_dark_circle)
                    //Toast.makeText(this , "Muted" , Toast.LENGTH_SHORT).show()

                    isMuted = true
                } else {
                    if (audioManager.mode == AudioManager.MODE_IN_CALL || audioManager.mode == AudioManager.MODE_IN_COMMUNICATION) {
                        audioManager.isMicrophoneMute = false
                    }

                    muteUnmuteButton.setBackgroundResource(R.drawable.appcolor_circle)
                    isMuted = false
                }
            }

            R.id.speakerButton->
            {
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

                Timber.e(""+audioManager.mode)

                if (!isCallOnSpeaker) {
                    if (audioManager.mode == AudioManager.MODE_IN_CALL
                            || audioManager.mode == AudioManager.MODE_IN_COMMUNICATION
                            ||audioManager.mode == AudioManager.MODE_NORMAL)
                    {

                        audioManager.isSpeakerphoneOn = true
                       // Toast.makeText(this , "Speaker on" , Toast.LENGTH_SHORT).show()
                    }

                    speakerButton.setBackgroundResource(R.drawable.appcolor_dark_circle)

                    isCallOnSpeaker = true
                }
                else
                {
                    if (audioManager.mode == AudioManager.MODE_IN_CALL
                            || audioManager.mode == AudioManager.MODE_IN_COMMUNICATION
                            || audioManager.mode == AudioManager.MODE_NORMAL) {
                        audioManager.isSpeakerphoneOn = false

                        //Toast.makeText(this , "Speaker off" , Toast.LENGTH_SHORT).show()
                    }

                    speakerButton.setBackgroundResource(R.drawable.appcolor_circle)
                    isCallOnSpeaker = false
                }
            }
        }
    }
    companion object {
        val TAG = CallScreenActivity::class.java.simpleName
    }

    override fun onAddCallLogs() {

        finish()
    }

    override fun onFailed(message: String) {

        showSnackbar(message)
        finish()
    }

    override fun showProgress(showProgress: Boolean) {


            if (showProgress) {
                startProgressBarAnim()
            } else {
                stopProgressBarAnim()
            }

    }


    fun startProgressBarAnim() {

        aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {
        aviProgressBar.setVisibility(View.GONE)
    }

    fun showSnackbar(message: String)
    {
        try {
            val parentLayout = findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }


}