package com.dazzlerr_usa.views.activities.mainactivity

import android.content.IntentSender
import android.os.Build
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.dazzlerr_usa.BuildConfig

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityMainBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.views.fragments.SplashFragment
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.activities.ContainerActivity
import com.dazzlerr_usa.views.fragments.currentlocation.LocationCallback
import com.dazzlerr_usa.views.fragments.forgotpassword.localfragments.ResetPasswordFragment
import com.dazzlerr_usa.views.fragments.login.LoginFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import timber.log.Timber

import javax.inject.Inject
import com.dazzlerr_usa.utilities.DeviceInfoConstants
import com.dazzlerr_usa.views.fragments.register.localfragments.ChooseRegisterFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability


class MainActivity : ContainerActivity(), View.OnClickListener
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    var wayLatitude = 0.0
    var wayLongitude = 0.0
    internal var currentAddress: String? = ""
    internal lateinit var fragment_manager: androidx.fragment.app.FragmentManager
    internal lateinit var fragment_transaction: androidx.fragment.app.FragmentTransaction
    lateinit var binderObject: ActivityMainBinding
    lateinit internal var locationCallback: LocationCallback

    private val REQ_CODE_VERSION_UPDATE = 530
    private var appUpdateManager: AppUpdateManager? = null
    private var installStateUpdatedListener: InstallStateUpdatedListener? = null
    var device_token =""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        super.hideStatusBar()
        binderObject = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initializations()
        getDeviceInfo()
    }

    fun getDeviceToken()
    {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful)
                    {
                        Timber.e( "getInstanceId failed "+ task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result!!.token

                    // Log and toast
                    //val msg = getString(R.string.msg_token_fmt, token)
                    Timber.e(token)
                    sharedPreferences.saveString(Constants.device_id , token)
                    device_token =token

                    DeviceInfoConstants.DEVICE_ID = device_token
                    //  Toast.makeText(this@MainActivity, token, Toast.LENGTH_SHORT).show()
                }
                )
    }

    fun initializations()
    {
        //--------Injecting the activity to dagger component-----
        (application as MyApplication).myComponent.inject(this)

        Constants.App_Version = BuildConfig.VERSION_NAME

        //-------Initially hiding the title bar visibility because we don't want it on splash screen.--
        titleVisibility(false)

        if (intent.extras != null && intent.extras!!.containsKey("ShouldOpenLogin"))
        {
            loadFirstFragment(LoginFragment())
        }
        else if (intent.extras != null && intent.extras!!.containsKey("ShouldOpenRegister"))
        {
            loadFirstFragment(ChooseRegisterFragment())
        }
        else
        {
            //------- Calling the Splash fragment---
            loadFirstFragment(SplashFragment())
        }

        binderObject.titleLayout.leftbtn.setOnClickListener(this)
    }

    fun LoadFragment(fragment: androidx.fragment.app.Fragment) {

        val backStateName = fragment.javaClass.name
        fragment_manager = supportFragmentManager
        val fragmentPopped = fragment_manager.popBackStackImmediate(backStateName, 0)

        if (!fragmentPopped && fragment_manager.findFragmentByTag(backStateName) == null) {

            //fragment not in back stack, create it.
            fragment_transaction = fragment_manager.beginTransaction()
            fragment_transaction.replace(R.id.fragment_container, fragment)
            fragment_transaction.addToBackStack(backStateName)
            fragment_transaction.commitAllowingStateLoss()
        }

    }


    fun loadFirstFragment(fragment: androidx.fragment.app.Fragment) {
        fragment_manager = supportFragmentManager
        fragment_transaction = fragment_manager.beginTransaction()
        fragment_transaction.replace(R.id.fragment_container, fragment)
        fragment_transaction.commitAllowingStateLoss()
    }


    fun clearAllFragments() {
        fragment_manager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun titleVisibility(visibility: Boolean) {
        if (visibility)
            binderObject.titleLayout.titleLayout.visibility = View.VISIBLE
        else
            binderObject.titleLayout.titleLayout.visibility = View.GONE
    }


    fun backpress() {

        val backStackEntryCount = supportFragmentManager.backStackEntryCount

        if (backStackEntryCount > 0) {
            fragment_manager.popBackStackImmediate()

        } else {
            onBackPressed()

        }
    }

    fun startProgressBarAnim() {

        binderObject.aviProgressBar.visibility = View.VISIBLE
    }

    fun stopProgressBarAnim() {

        binderObject.aviProgressBar.visibility = View.GONE
    }

    fun setTitle(title: String) {
        binderObject.titleLayout.titletxt.text = title
    }



    override fun onLocationfetched(latitude: Double, longitude: Double, address: String?)
    {
        wayLatitude = latitude
        wayLongitude = longitude
        currentAddress = address
        locationCallback.onLocationFetch(latitude, longitude, address!!)
    }

    fun getcurrentLocation(callback: LocationCallback)
    {
        locationCallback = callback
        super.getLocation()
    }

    override fun onBackPressed()
    {

        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment is ResetPasswordFragment) {
            backpress()
            backpress()
            backpress()
        } else {
            super.onBackPressed()
        }
    }

    fun getDeviceInfo()
    {
        try
        {
            sharedPreferences.saveString(Constants.DEVICE_MODEL , Build.MODEL)
            sharedPreferences.saveString(Constants.DEVICE_BRAND , Build.BRAND)
            sharedPreferences.saveString(Constants.DEVICE_VERSION , Build.VERSION_CODES::class.java.fields.get(
                    Build.VERSION_CODES::class.java.fields.size-1).getName())

            Log.i("TAG","MODEL: " + sharedPreferences.getString(Constants.DEVICE_MODEL))
            Log.i("TAG","brand: " + Build.BRAND)
            Log.i("TAG","Version Code: " + Build.VERSION_CODES::class.java.fields.get(Build.VERSION_CODES::class.java.fields.size-1).getName())

            DeviceInfoConstants.DEVICE_MODEL = Build.MODEL
            DeviceInfoConstants.DEVICE_BRAND = Build.BRAND
            DeviceInfoConstants.DEVICE_VERSION = Build.VERSION_CODES::class.java.fields.get(
                    Build.VERSION_CODES::class.java.fields.size-1).getName()
        }

        catch (e:Exception)
        {
            e.printStackTrace()
        }

    }


    override fun onClick(v: View) {}


    override fun onResume() {
        super.onResume()
        getDeviceToken()
       // checkNewAppVersionState()
    }

    override fun onDestroy() {
      //  unregisterInstallStateUpdListener()
        super.onDestroy()
    }


    public fun checkForAppUpdate()
    {
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo

        // Create a listener to track request state updates.
        installStateUpdatedListener = InstallStateUpdatedListener { installState ->
            // Show module progress, log state, or install the update.
            if (installState.installStatus() == InstallStatus.DOWNLOADED)
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app.
                popupSnackbarForCompleteUpdateAndUnregister()
        }

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                // Request the update.
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

                    // Before starting an update, register a listener for updates.
                    appUpdateManager?.registerListener(installStateUpdatedListener!!)
                    // Start an update.
                    startAppUpdateFlexible(appUpdateInfo)
                } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    // Start an update.
                    startAppUpdateImmediate(appUpdateInfo)
                }
            }
        }
    }

    private fun startAppUpdateImmediate(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager?.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    REQ_CODE_VERSION_UPDATE)
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }

    }

    private fun startAppUpdateFlexible(appUpdateInfo: AppUpdateInfo)
    {
        try
        {
            appUpdateManager?.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    REQ_CODE_VERSION_UPDATE)
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
            unregisterInstallStateUpdListener()
        }

    }

    /**
     * Displays the snackbar notification and call to action.
     * Needed only for Flexible app update
     */
    private fun popupSnackbarForCompleteUpdateAndUnregister() {

        try {
            var snackbar: Snackbar
            val parentLayout = findViewById<View>(android.R.id.content)
            snackbar  = Snackbar.make(parentLayout, "Update has been downloaded", Snackbar.LENGTH_SHORT)

            snackbar.view.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.appColor))

            snackbar.setAction("RESTART", View.OnClickListener { appUpdateManager?.completeUpdate() })

            snackbar.show()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }

        unregisterInstallStateUpdListener()
    }

    /**
     * Checks that the update is not installed during 'onResume()'.
     * However, you should execute this check at all app entry points.
     */
    private fun checkNewAppVersionState() {
        appUpdateManager?.getAppUpdateInfo()
                ?.addOnSuccessListener { appUpdateInfo ->
                    //FLEXIBLE:
                    // If the update is downloaded but not installed,
                    // notify the user to complete the update.
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackbarForCompleteUpdateAndUnregister()
                    }

                    //IMMEDIATE:
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        // If an in-app update is already running, resume the update.
                        startAppUpdateImmediate(appUpdateInfo)
                    }
                }

    }

    /**
     * Needed only for FLEXIBLE update
     */
    private fun unregisterInstallStateUpdListener() {
        if (appUpdateManager != null && installStateUpdatedListener != null)
            appUpdateManager?.unregisterListener(installStateUpdatedListener!!)
    }
}
