package com.dazzlerr_usa.views.activities.synchcalling

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.sinchcalling.HeadsUpNotificationService
import com.dazzlerr_usa.utilities.sinchcalling.SinchService
import com.dazzlerr_usa.views.activities.ContainerActivity
import com.google.android.material.snackbar.Snackbar
import com.sinch.android.rtc.PushPair
import com.sinch.android.rtc.calling.Call
import com.sinch.android.rtc.calling.CallListener
import kotlinx.android.synthetic.main.activity_incoming_call_screen.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class IncomingCallScreenActivity : ContainerActivity(), AddCallLogsView
{

    lateinit var mPresenter: AddCallLogsPresenter
    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    private var mCallId: String? = null
    private var mUserPic: String? = null
    private var mUserName: String? = null
    private var mRemoteUserid: String? = null
  //  private var mAudioPlayer: AudioPlayer? = null
    var isCallInProgress = true

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call_screen)

        initializations()
        clickListeners()
    }

    fun initializations()
    {

        (application as MyApplication).myComponent.inject(this)

        mPresenter = AddCallLogModel(this , this)

      //  mAudioPlayer = AudioPlayer(this)
        mCallId = intent.getStringExtra(SinchService.CALL_ID)
        mUserPic = intent.getStringExtra(SinchService.USERPIC)
        mUserName = intent.getStringExtra(SinchService.USERNAME)
    }
    fun clickListeners()
    {

        answerButton.setOnClickListener(mClickListener)
        declineButton.setOnClickListener(mClickListener)
    }

    override fun onServiceConnected()
    {
        val call = getSinchServiceInterface()!!.getCall(mCallId)
        if (call != null)
        {

           // mAudioPlayer!!.playRingtone()

            call.addCallListener(SinchCallListener())
            val remoteUser = findViewById<View>(R.id.remoteUser) as TextView
            remoteUser.text = mUserName?.capitalize()
            mRemoteUserid = call.remoteUserId
            Glide.with(this)
                    .load("https://www.dazzlerr.com/API/"+mUserPic).apply(RequestOptions().fitCenter())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(remoteUserPic)

        } else {
            Log.e(TAG, "Started with invalid callId, aborting")
            finish()

        }
    }

    private fun answerClicked() {
      //  mAudioPlayer!!.stopRingtone()
        val call = getSinchServiceInterface()!!.getCall(mCallId)
        if (call != null) {
            isCallInProgress = false
            call.answer()
            val intent = Intent(this, CallScreenActivity::class.java)
            intent.putExtra(SinchService.CALL_ID, mCallId)
            intent.putExtra(SinchService.USERPIC, mUserPic)
            startActivity(intent)
        } else {
            finish()
        }
    }

    private fun declineClicked() {
      //  mAudioPlayer!!.stopRingtone()
        isCallInProgress = false
        val call = getSinchServiceInterface()!!.getCall(mCallId)
        call?.hangup()

        if(isNetworkActiveWithMessage())
        {

            val unixSeconds: Long = call.details.startedTime

            val date = Date(unixSeconds * 1000L)
            val sdf = SimpleDateFormat("dd MMM, yyyy hh:mm:ss a")
            val formattedDate = sdf.format(date)
            Timber.e(formattedDate)
            mPresenter.addCallLog(sharedPreferences.getString(Constants.User_id).toString() ,mUserName.toString(), call.remoteUserId,  "00:00"  ,"REJECTED" ,formattedDate, "false")

        }
    }

    override fun onLocationfetched(latitude: Double, longitude: Double, address: String?) {}



    private inner class SinchCallListener : CallListener
    {
        override fun onCallEnded(call: Call) {
            val cause = call.details.endCause
            isCallInProgress = false
            Log.d(TAG, "Call ended, cause: $cause")
        //    mAudioPlayer!!.stopRingtone()
        }

        override fun onCallEstablished(call: Call) {
            Log.d(TAG, "Call established")
            isCallInProgress = false
        }

        override fun onCallProgressing(call: Call) {
            Log.d(TAG, "Call progressing")
            isCallInProgress  = true
        }

        override fun onShouldSendPushNotification(call: Call, pushPairs: List<PushPair>) {
            // Send a push through your push provider here, e.g. GCM
        }
    }

    private val mClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.answerButton -> answerClicked()
            R.id.declineButton -> declineClicked()
        }
    }

    override fun onPause() {
        super.onPause()

        Timber.e(isCallInProgress.toString())
        if(isCallInProgress)
        {
            val serviceIntent = Intent(applicationContext, HeadsUpNotificationService::class.java)
            val mBundle = Bundle()
            mBundle.putString("remote_userid", mRemoteUserid)
            mBundle.putString(SinchService.CALL_ID, mCallId)
            mBundle.putString(SinchService.USERPIC, mUserPic)
            mBundle.putString(SinchService.USERNAME, mUserName)

            serviceIntent.putExtras(mBundle)
            ContextCompat.startForegroundService(applicationContext, serviceIntent)
            finish()
        }
    }
    companion object {
        val TAG = IncomingCallScreenActivity::class.java.simpleName
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