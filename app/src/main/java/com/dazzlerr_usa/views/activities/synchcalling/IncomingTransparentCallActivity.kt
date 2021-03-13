package com.dazzlerr_usa.views.activities.synchcalling

import android.os.Bundle
import android.os.Parcelable
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.sinchcalling.NotificationCallVo
import com.dazzlerr_usa.views.activities.ContainerActivity


class IncomingTransparentCallActivity : ContainerActivity()
{

    private var mCallVo: NotificationCallVo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getParcelableExtra<Parcelable?>(Constants.PARCELABLE) != null &&
                intent.getParcelableExtra<Parcelable>(Constants.PARCELABLE) is NotificationCallVo) {
            mCallVo = intent.getParcelableExtra<Parcelable>(Constants.PARCELABLE) as NotificationCallVo
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        val sinchServiceInterface = getSinchServiceInterface()
/*        if (mCallVo != null) {
            val result: NotificationResult? = mCallVo?.getData()?.let { sinchServiceInterface!!.relayRemotePushNotificationPayload(it as HashMap<String, String>) }
            setContentView(R.layout.activity_incoming_transparent_call)
            var map = mCallVo
        }*/
    }

    override fun onLocationfetched(latitude: Double, longitude: Double, address: String?) {

    }


}