package com.dazzlerr_usa.views.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dazzlerr_usa.R
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.PermissionUtils
import com.dazzlerr_usa.utilities.sinchcalling.SinchService
import com.dazzlerr_usa.views.fragments.currentlocation.AppConstants
import com.dazzlerr_usa.views.fragments.currentlocation.GpsUtils
import com.dazzlerr_usa.views.fragments.currentlocation.LocationAddress
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import permissions.dispatcher.*
import timber.log.Timber
import java.util.*

@RuntimePermissions
abstract class ContainerActivity : AppCompatActivity(), ServiceConnection
{

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var wayLatitude = 0.0
    private var wayLongitude = 0.0
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var isGPS = false


    private var mSinchServiceInterface: SinchService.SinchServiceInterface? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        applicationContext.bindService(Intent(this, SinchService::class.java), this,
                Context.BIND_AUTO_CREATE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AppConstants.GPS_REQUEST)
        {
            when (resultCode) {
                Activity.RESULT_OK -> {

                    // All required changes were successfully made
                    isGPS = true // flag maintain before get location
                    getLocation()
                }

                Activity.RESULT_CANCELED -> Snackbar.make(findViewById(android.R.id.content), "GPS is mandatory to get your accurate location. Please enable it",
                        Snackbar.LENGTH_INDEFINITE).setAction("Enable"
                ) { getLocation() }.show()

                else -> {
                }
            }// The user was asked to change settings, but chose not to


        }

    }

    fun showSnackBar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }

    fun hideStatusBar() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

    }

    fun showStatusBar()
    {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION ,Manifest.permission.ACCESS_COARSE_LOCATION)
    fun getLocationUpdates()
    {
        mFusedLocationClient!!.lastLocation.addOnSuccessListener(this@ContainerActivity) { location ->
            if (location != null) {
                wayLatitude = location.latitude
                wayLongitude = location.longitude

                getAddressFromLocation(wayLatitude, wayLongitude)

                //onLocationfetched(wayLatitude ,wayLongitude);
                Timber.e(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude))
            } else {
                mFusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback!!, null)
            }
        }
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION ,Manifest.permission.ACCESS_COARSE_LOCATION)
    fun getLocationRationale(request: PermissionRequest) {
        com.dazzlerr_usa.utilities.PermissionUtils.showRationalDialog(this, R.string.permission_rationale_camera_storage, request)
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION ,Manifest.permission.ACCESS_COARSE_LOCATION)
    fun getLocationDenied() {
        Toast.makeText(this , R.string.permission_denied_camera_storage , Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION ,Manifest.permission.ACCESS_COARSE_LOCATION)
    fun getLocationNeverAsk()
    {
        PermissionUtils.showAppSettingsDialog(this, R.string.permission_never_ask_camera_storage, Constants.REQ_CODE_APP_SETTINGS)
    }


    fun getLocation()
    {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = (10 * 1000).toLong() // 10 seconds
        locationRequest!!.fastestInterval = (5 * 1000).toLong() // 5 seconds

        GpsUtils(this).turnGPSOn(object : GpsUtils.onGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                // turn on GPS
                isGPS = isGPSEnable
            }
        })

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                        wayLatitude = location.latitude
                        wayLongitude = location.longitude

                        getAddressFromLocation(wayLatitude, wayLongitude)
                        //onLocationfetched(wayLatitude ,wayLongitude);

                        removeLocationUpdates()
                    }
                }
            }
        }

        if (!isGPS) {
            Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show()
            return
        }


        getLocationUpdatesWithPermissionCheck()
    }

    fun removeLocationUpdates()
    {
        if (mFusedLocationClient != null)
        {
            mFusedLocationClient!!.removeLocationUpdates(locationCallback!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeLocationUpdates()
    }
    private fun getAddressFromLocation(latitude: Double, longitude: Double) {

        val locationAddress = LocationAddress()
        locationAddress.getAddressFromLocation(latitude, longitude,
                applicationContext, GeocoderHandler())



    }

    abstract fun onLocationfetched(latitude: Double, longitude: Double, address: String?)

    @SuppressLint("HandlerLeak")
    private inner class GeocoderHandler : Handler()
    {
        override fun handleMessage(message: Message) {
            val locationAddress: String?
            when (message.what) {
                1 -> {
                    val bundle = message.data
                    locationAddress = bundle.getString("address")
                    removeLocationUpdates()
                    onLocationfetched(wayLatitude, wayLongitude, locationAddress)
                }
                2 ->
                {
                    removeLocationUpdates()
                    onLocationfetched(wayLatitude, wayLongitude, "Location Unavailable!")
                }

            }

        }
    }


    //Sinch code started
    open override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder): Unit {
        if (SinchService::class.java.name == componentName.className) {
            mSinchServiceInterface = iBinder as SinchService.SinchServiceInterface
            onServiceConnected()
        }
    }

    override fun onServiceDisconnected(componentName: ComponentName) { if (SinchService::class.java.name == componentName.className) {
            mSinchServiceInterface = null
            onServiceDisconnected()
        }
    }

    protected open fun onServiceConnected() {
        // for subclasses
    }

    protected open fun onServiceDisconnected() {
        // for subclasses
    }

    open fun getSinchServiceInterface(): SinchService.SinchServiceInterface? {
        return mSinchServiceInterface
    }
}
