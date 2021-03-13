package com.dazzlerr_usa.views.activities.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityHomeBinding
import com.dazzlerr_usa.utilities.*
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.utilities.sinchcalling.SinchService
import com.dazzlerr_usa.views.activities.ContainerActivity
import com.dazzlerr_usa.views.activities.WelcomeActivity
import com.dazzlerr_usa.views.activities.location.LocationActivity
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.activities.messages.MessagesActivity
import com.dazzlerr_usa.views.activities.notifications.NotificationsActivity
import com.dazzlerr_usa.views.fragments.castingdashboard.CastingDashboardFragment
import com.dazzlerr_usa.views.fragments.castingprofile.localfragments.CastingProfileFragment
import com.dazzlerr_usa.views.fragments.currentlocation.LocationCallback
import com.dazzlerr_usa.views.fragments.home.HomeFragment
import com.dazzlerr_usa.views.fragments.jobs.JobsFragment
import com.dazzlerr_usa.views.fragments.profile.FeaturedProfileFragment
import com.dazzlerr_usa.views.fragments.profile.ProfileFragment
import com.dazzlerr_usa.views.fragments.talent.TalentFragment
import com.dazzlerr_usa.views.fragments.talentdashboard.TalentDashboardFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.iid.FirebaseInstanceId
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu
import com.sinch.android.rtc.PushTokenRegistrationCallback
import com.sinch.android.rtc.SinchError
import timber.log.Timber
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig
import javax.inject.Inject

class HomeActivity : ContainerActivity(), View.OnClickListener , HomeView, SinchService.StartFailedListener , PushTokenRegistrationCallback
{

    lateinit  var binderObject: ActivityHomeBinding

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var mPresenter : HomePresenter
    var wayLatitude = 0.0
    var wayLongitude = 0.0
    var currentAddress: String? = ""
    internal lateinit var fragment_manager: androidx.fragment.app.FragmentManager
    internal lateinit var fragment_transaction: androidx.fragment.app.FragmentTransaction
    internal var locationCallback: LocationCallback? = null
    lateinit var menu: SlidingMenu
    lateinit var locationUpdate: LocationUpdate
    val LOCATIONREQUESTCODE = 100
    val ADDJOBREQUESTCODE = 101
    internal var doubleBackToExitPressedOnce = false

    private val REQ_CODE_VERSION_UPDATE = 530
    private var appUpdateManager: AppUpdateManager? = null
    private var installStateUpdatedListener: InstallStateUpdatedListener? = null
    var CUST_ID  = ""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binderObject = DataBindingUtil.setContentView<ActivityHomeBinding>(this, R.layout.activity_home)

        initializations()
        clickListerners()
        navigationMenuSettings()
       // showcaseView()
    }

    private fun initializations()
    {
        //--- Checking for app update if available
        checkForAppUpdate()

        getDeviceToken()

        (application as MyApplication).myComponent.inject(this)
        mPresenter = HomeModel(this)

        if (intent.extras != null && intent.extras!!.containsKey("openJobs"))
        {
            //val bundle = Bundle()
            //bundle.putString("selectedTab" , "recommended")
            var fragment = JobsFragment()
           // fragment.arguments = bundle
            loadFirstFragment(fragment)
        }

        else
        {
            if (intent.extras != null && intent.extras!!.containsKey("from"))
            {
                val bundle = Bundle()
                bundle.putString("from", "dashboard")
                bundle.putString("user_id", sharedPreferences.getString(Constants.User_id))

                val intent = Intent(this@HomeActivity, WelcomeActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)

            }
            currentAddress = sharedPreferences.getString(Constants.CurrentAddress)

            if (sharedPreferences.getString(Constants.User_type).equals("GuestUser", ignoreCase = true))
                loadFirstFragment(HomeFragment())
            else {

                if (sharedPreferences.getString(Constants.User_Role).equals("casting", ignoreCase = true)) {
                    loadFirstFragment(CastingDashboardFragment())
                } else {
                    loadFirstFragment(TalentDashboardFragment())
                }

            }
        }
    }

    //This method is used for adding current logined user into sinch
    private fun addSinchClient()
    {

        if(sharedPreferences.getString(Constants.User_id).toString().isNotEmpty())
        {
            if(!getSinchServiceInterface()!!.isStarted)
            getSinchServiceInterface()!!.startClient(sharedPreferences.getString(Constants.User_id).toString()/*+"_"+sharedPreferences.getString(Constants.User_id).toString()*/)
        }
    }

    override fun onStarted()
    {
        getSinchServiceInterface()?.getManagedPush(sharedPreferences.getString(Constants.User_id).toString()/*+"_"+sharedPreferences.getString(Constants.User_id).toString()*/)?.registerPushToken(this)
    }

    override fun onStartFailed(error: SinchError?) {
        Timber.e("Home Screen- Sinch Error: "+error)
    }

    override fun onServiceConnected() {
        getSinchServiceInterface()!!.setStartListener(this)

        Timber.e("Sinch user added")
        addSinchClient()
    }
    override fun tokenRegistered() {

        Timber.e("Sinch FCM Token registered")
    }

    override fun tokenRegistrationFailed(sinchError: SinchError?)
    {

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

                    DeviceInfoConstants.DEVICE_ID = token
                    //  Toast.makeText(this@MainActivity, token, Toast.LENGTH_SHORT).show()
                }
                )
    }
    private fun clickListerners()
    {
        binderObject.bottomNavProfileBtn.setOnClickListener(this)
        binderObject.bottomNavTalentBtn.setOnClickListener(this)
        binderObject.bottomNavJobsBtn.setOnClickListener(this)
        binderObject.bottomNavHomeBtn.setOnClickListener(this)
        binderObject.bottomNavDashboardBtn.setOnClickListener(this)
        binderObject.titleLayout.leftLayout.setOnClickListener(this)
        binderObject.titleLayout.rightlayout.setOnClickListener(this)
        binderObject.titleLayout.messageCountLayout.setOnClickListener(this)
        binderObject.titleLayout.notificationCountLayout.setOnClickListener(this)

    }

    fun showcaseView()
    {
        val config  = ShowcaseConfig()
        val color  = Color.parseColor("#C4000000")
// sequence example
        config.setDelay(200); // half second between each showcase view
        val sequence = MaterialShowcaseSequence(this, /*sharedPreferences.getString(Constants.User_id)*/ "5");
        sequence.setConfig(config);

        sequence.addSequenceItem(MaterialShowcaseView.Builder(this)
                .setTarget(binderObject.titleLayout.leftLayout)
                .setDismissText("GOT IT")
                .setContentText("You can navigate from here to the other screens like settings, notifications etc.")
                .setMaskColour(color)
                .setShapePadding(30)
                .setDismissTextColor(ContextCompat.getColor(this , R.color.appColor))
                .build())

        sequence.addSequenceItem(MaterialShowcaseView.Builder(this)
                .setTarget(binderObject.titleLayout.righttxt)
                .setDismissText("GOT IT")
                .setContentText("This is your current set location to get the results accordingly. You can change it anytime by tap on it.")
                .setDismissTextColor(ContextCompat.getColor(this , R.color.appColor))
                .setMaskColour(color)
                .build())

        sequence.addSequenceItem(MaterialShowcaseView.Builder(this)
                .setTarget(binderObject.bottomNavProfileBtn)
                .setDismissText("GOT IT")
                .setContentText("You can view and update your profile from here.")
                .setMaskColour(color)
                .setDismissTextColor(ContextCompat.getColor(this , R.color.appColor))
                .build())

        sequence.addSequenceItem(MaterialShowcaseView.Builder(this)
                .setTarget(binderObject.bottomNavTalentBtn)
                .setDismissText("GOT IT")
                .setContentText("Talents. You can find the number of talents here according to their profession.")
                .setMaskColour(color)
                .setDismissTextColor(ContextCompat.getColor(this , R.color.appColor))
                .build())

        sequence.start();
    }

    private fun navigationMenuSettings() {
        menu = Common.setSlidingMenu(this)
        val slidingmenu = SideMenuFunctions(menu, this)

        // Disabling the slide
        slide_disable()

        menu.setOnOpenListener {

            // Disabling the slide
            slide_enable()
            slidingmenu.setProfilePic()

            val view = this@HomeActivity.currentFocus
            if (view != null)
            {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }

            binderObject.mainView.setAlpha(0f)
            binderObject.mainView.setVisibility(View.VISIBLE)
            binderObject.mainView.animate()
                    .alpha(0.6f)
                    .setDuration(400)
                    .setListener(null)
        }

        menu.setOnCloseListener {
            binderObject.mainView.setAlpha(0.6f)
            binderObject.mainView.animate()
                    .alpha(0f)
                    .setDuration(50)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            binderObject.mainView.setVisibility(View.GONE)
                        }
                    })
            // Disabling the slide
            slide_disable()
        }
    }

    fun LoadFragment(fragment: Fragment) {
        val backStateName = fragment.javaClass.name
        fragment_manager = supportFragmentManager
        val fragmentPopped = fragment_manager.popBackStackImmediate(backStateName, 0)

        if (!fragmentPopped && fragment_manager.findFragmentByTag(backStateName) == null) {

            //fragment not in back stack, create it.
            fragment_transaction = fragment_manager.beginTransaction()
            fragment_transaction.add(R.id.fragment_container, fragment)
            fragment_transaction.addToBackStack(backStateName)
            fragment_transaction.commitAllowingStateLoss()
        }

    }


    fun loadFirstFragment(fragment: Fragment) {
        fragment_manager = supportFragmentManager
        fragment_transaction = fragment_manager.beginTransaction()
        fragment_transaction.replace(R.id.fragment_container, fragment)
        fragment_transaction.commitAllowingStateLoss()
    }

    fun clearAllFragments() {
        fragment_manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun backpress()
    {
        fragment_manager.popBackStackImmediate()
    }

    fun startProgressBarAnim() {

        binderObject.aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {

        binderObject.aviProgressBar.setVisibility(View.GONE)
    }

    fun slidemenuOff()
    {
        if (menu.isMenuShowing)
        {
            menu.showContent(false)
            menu.setFadeEnabled(true)
            //bottommenu.setSlidingEnabled(false);
        }
    }

    fun showmenu() {
        if (menu.isMenuShowing)
        {
            menu.showContent(true)
            menu.setFadeEnabled(true)
        } else
            menu.showMenu()


    }

    fun slide_disable() {
        menu.isSlidingEnabled = false
    }

    fun slide_enable() {
        menu.isSlidingEnabled = true
    }


    override fun onLocationfetched(latitude: Double, longitude: Double, address: String?) {

        wayLatitude = latitude
        wayLongitude = longitude
        currentAddress = address

        binderObject.titleLayout.righttxt.setText(currentAddress)
        if (locationCallback != null)
            locationCallback!!.onLocationFetch(latitude, longitude, address!!)
    }

    fun getcurrentLocation(callback: LocationCallback) {
        locationCallback = callback
        super.getLocation()
    }

    fun titleSettings(backgroundcolor: Int, rightBtnVisibility: Boolean, leftBtnColor: Int, rightBtnColor: Int, titleColor: Int, title: String, rightTxt: String , drawable: Int)
    {
        binderObject.titleLayout.titleLayout.setCardBackgroundColor(backgroundcolor)
        //-----------Resting the left Button color------------
        var leftbuttonDrawable = binderObject.titleLayout.leftbtn.getBackground()
        leftbuttonDrawable = DrawableCompat.wrap(leftbuttonDrawable)
        //the color is a direct color int and not a color resource
        DrawableCompat.setTint(leftbuttonDrawable, leftBtnColor)
        binderObject.titleLayout.leftbtn.setBackground(leftbuttonDrawable)


        if (rightBtnVisibility)
        {
            binderObject.titleLayout.rightlayout.visibility = View.VISIBLE
            binderObject.titleLayout.rightbtn.setVisibility(View.VISIBLE)
            binderObject.titleLayout.notificationMessageLayout.setVisibility(View.GONE)

            if(drawable == R.drawable.ic_add) {
                binderObject.titleLayout.rightbtn.setImageResource(drawable)
                binderObject.titleLayout.rightbtn.setPadding(2,2 ,2 ,2)
                binderObject.titleLayout.rightbtn.setBackgroundResource(0)
            }
            else if(drawable == R.drawable.ic_notifications) {
                binderObject.titleLayout.rightbtn.setImageResource(drawable)
                //binderObject.titleLayout.rightbtn.setPadding(2,2 ,2 ,2)
                binderObject.titleLayout.rightbtn.setBackgroundResource(0)
            }
            else {
                binderObject.titleLayout.rightbtn.setImageResource(0)
                binderObject.titleLayout.rightbtn.setBackgroundResource(drawable)
                //-----------Resting the left Button color------------
                var rightbuttonDrawable = binderObject.titleLayout.rightbtn.getBackground()
                rightbuttonDrawable = DrawableCompat.wrap(rightbuttonDrawable)
                //the color is a direct color int and not a color resource
                DrawableCompat.setTint(rightbuttonDrawable, rightBtnColor)
                binderObject.titleLayout.rightbtn.setBackground(rightbuttonDrawable)
            }

        }
        else {

            if(sharedPreferences.getString(Constants.User_id)!!.isNotEmpty()) {
                if (title.equals("dashboard", ignoreCase = true)) {
                    binderObject.titleLayout.notificationMessageLayout.visibility = View.VISIBLE
                    binderObject.titleLayout.rightlayout.visibility = View.GONE

                } else {
                    binderObject.titleLayout.notificationMessageLayout.visibility = View.GONE
                    binderObject.titleLayout.rightlayout.visibility = View.VISIBLE
                }
            }
            else
            {
                binderObject.titleLayout.notificationMessageLayout.visibility = View.GONE
                binderObject.titleLayout.rightlayout.visibility = View.VISIBLE
            }

            binderObject.titleLayout.rightbtn.setVisibility(View.GONE)

        }

        binderObject.titleLayout.titletxt.setTextColor(titleColor)
        binderObject.titleLayout.titletxt.setText(title)

        if(title.equals("Talent",ignoreCase = true) || title.equals("Jobs",ignoreCase = true)) {
            binderObject.titleLayout.titletxt.setText(title+"  ")
            binderObject.titleLayout.titletxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_search, 0)
        }
        else
            binderObject.titleLayout.titletxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

        binderObject.titleLayout.righttxt.setTextColor(titleColor)
        binderObject.titleLayout.righttxt.setText(rightTxt)

    }


    override fun onClick(v: View) {

        val currentLoadedFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        when (v.id)
        {

            R.id.leftLayout -> showmenu()

            R.id.notificationCountLayout->
            {
                startActivity(Intent(this@HomeActivity , NotificationsActivity::class.java))
            }


            R.id.messageCountLayout->
            {
                val newIntent = Intent(this@HomeActivity, MessagesActivity::class.java)
                startActivity(newIntent)
            }


            R.id.rightlayout ->
            {
/*                if (currentLoadedFragment is CastingProfileFragment)
                {
                    if(isUserLogined())
                    {
                        if (sharedPreferences.getString(Constants.Is_Phone_Verified).equals("1") || sharedPreferences.getString(Constants.Is_Email_Verified).equals("1")) {

                            if (sharedPreferences.getString(Constants.Is_Documement_Verified).toString().equals("1", ignoreCase = true)
                                    && sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("1", ignoreCase = true)) {
                                val bundle = Bundle()
                                bundle.putString("type", "Add")
                                bundle.putString("call_id", "")
                                val intent = Intent(this, AddOrEditJobActivity::class.java)
                                intent.putExtras(bundle)
                                startActivityForResult(intent ,101)
                            }
                            else if (sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("0", ignoreCase = true)
                                    || sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("", ignoreCase = true)
                                    || sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("null", ignoreCase = true))
                            {

                                val dialog = CustomDialog(this@HomeActivity)
                                        dialog.setMessage("Your identity is not verified. It is very important to help protect our community and ensure that Dazzlerr stays safe and secure. We verify identity in several ways:\n" +
                                                "\n" +
                                                "- Government ID Verification.\n" +
                                                "- Video Verification.")
                                        dialog.setPositiveButton("Verify now", DialogListenerInterface.onPositiveClickListener {

                                            dialog.dismiss()
                                            val bundle = Bundle()
                                            bundle.putString("type", "documentVerification")
                                            val newIntent = Intent(this, AccountVerification::class.java)
                                            newIntent.putExtras(bundle)
                                            startActivity(newIntent)

                                        })
                                        dialog.setNegativeButton("Later", DialogListenerInterface.onNegetiveClickListener {
                                            dialog.dismiss()
                                        })

                                        dialog.show()


                            } else if (sharedPreferences.getString(Constants.Is_Documement_Verified).toString().equals("0", ignoreCase = true)
                                    && sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("1", ignoreCase = true)) {


                                val dialog = CustomDialog(this@HomeActivity)
                                        dialog.setMessage("Your identity verification is under process. It will take 24-48 hours.")
                                        dialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {
                                            dialog.dismiss()
                                        })

                                        dialog.show()
                            }
                        }
                        else {
                            val dialog = CustomDialog(this@HomeActivity)
                                    dialog.setMessage("Your email and mobile number is not verified. Please verify your details to proceed.")
                                    dialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {

                                        val bundle = Bundle()
                                        bundle.putString("type", "emailOrPhoneVerification")
                                        val newIntent = Intent(this, AccountVerification::class.java)
                                        newIntent.putExtras(bundle)
                                        startActivity(newIntent)
                                    })

                                    dialog.setNegativeButton("Later", DialogListenerInterface.onNegetiveClickListener {
                                        dialog.dismiss()
                                    })
                                    dialog.show()
                        }
                    }
                }*/

                 if((currentLoadedFragment is FeaturedProfileFragment||currentLoadedFragment is ProfileFragment || currentLoadedFragment is CastingProfileFragment) && isUserLogined())
                {
                    if(!CUST_ID.equals("")) {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "https://www.dazzlerr.com/profile/"+CUST_ID)

                            // (Optional) Here we're setting the title of the content
                            putExtra(Intent.EXTRA_TITLE, "Dazzlerr - Connecting Talent")
                            type = "text/plain"
                        }

                        val shareIntent = Intent.createChooser(sendIntent, null)
                        startActivity(shareIntent)
                    }
                }

                else if(!(currentLoadedFragment is TalentDashboardFragment || currentLoadedFragment is CastingDashboardFragment))
                 {
                     startActivityForResult(Intent(this@HomeActivity , LocationActivity::class.java ) ,LOCATIONREQUESTCODE)
                 }
                else
                {

                }

            }

            R.id.bottomNav_profileBtn -> {
                if (isUserLogined())
                {
                    val bundle = Bundle()
                    bundle.putString("from", "dashboard")
                    bundle.putString("user_id", sharedPreferences.getString(Constants.User_id))
                    bundle.putString("user_role", sharedPreferences.getString(Constants.User_Role))

                    if (sharedPreferences.getString(Constants.User_Role).equals("casting", ignoreCase = true))
                    {
                        if(currentLoadedFragment !is CastingProfileFragment)
                        {
                            val fragment = CastingProfileFragment()
                            fragment.arguments = bundle
                            loadFirstFragment(fragment)
                        }

                    }
                    else
                    {
                        if(sharedPreferences.getString(Constants.Is_Featured).equals("1"))
                        {
                            if (currentLoadedFragment !is FeaturedProfileFragment) {
                                val fragment = FeaturedProfileFragment()
                                fragment.arguments = bundle
                                loadFirstFragment(fragment)
                            }
                        }

                        else
                        {
                            if (currentLoadedFragment !is ProfileFragment) {
                                val fragment = ProfileFragment()
                                fragment.arguments = bundle
                                loadFirstFragment(fragment)
                            }
                        }
                    }
                }

             //   val fragment = CastingProfileVideoProofFragment()
             //   loadFirstFragment(fragment)
            }

            R.id.bottomNav_talentBtn -> if (currentLoadedFragment !is TalentFragment)
                loadFirstFragment(TalentFragment())

            R.id.bottomNav_jobsBtn ->
                if (currentLoadedFragment !is JobsFragment)
                loadFirstFragment(JobsFragment())

            R.id.bottomNav_homeBtn ->
            {
                if (currentLoadedFragment !is HomeFragment)
                    loadFirstFragment(HomeFragment())
            }

            R.id.bottomNav_dashboardBtn ->
            {
                if(sharedPreferences.getString(Constants.User_Role).equals("casting" ,ignoreCase = true))
                {
                    if (isUserLogined() && currentLoadedFragment !is CastingDashboardFragment)
                    {
                        loadFirstFragment(CastingDashboardFragment())
                    }
                }

                else
                {
                    if (isUserLogined() && currentLoadedFragment !is TalentDashboardFragment)
                    {
                        loadFirstFragment(TalentDashboardFragment())
                    }
                }
            }
        }
    }

    fun setBottomNavigationMenu(profileSelector: Boolean, talentSelector: Boolean, jobsSelector: Boolean, homeSelector: Boolean, dashboardSelector: Boolean) {
        if (profileSelector) {
            binderObject.bottomNavProfileBtn.setBackgroundResource(R.drawable.bottom_nav_selector)
            binderObject.bottomNavTalentBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.appColor))
            binderObject.bottomNavJobsBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.appColor))
            binderObject.bottomNavDashboardBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.appColor))

        } else if (talentSelector) {
            binderObject.bottomNavTalentBtn.setBackgroundResource(R.drawable.bottom_nav_selector)
            binderObject.bottomNavProfileBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.appColor))
            binderObject.bottomNavJobsBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.appColor))
            binderObject.bottomNavDashboardBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.appColor))

        } else if (jobsSelector) {
            binderObject.bottomNavJobsBtn.setBackgroundResource(R.drawable.bottom_nav_selector)
            binderObject.bottomNavProfileBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.appColor))
            binderObject.bottomNavTalentBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.appColor))
            binderObject.bottomNavDashboardBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.appColor))
        } else if (homeSelector) {
            binderObject.bottomNavDashboardBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.appColor))
            binderObject.bottomNavProfileBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.appColor))
            binderObject.bottomNavTalentBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.appColor))
            binderObject.bottomNavJobsBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.appColor))
        } else if (dashboardSelector) {
            binderObject.bottomNavDashboardBtn.setBackgroundResource(R.drawable.bottom_nav_selector)
            binderObject.bottomNavProfileBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.appColor))
            binderObject.bottomNavTalentBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.appColor))
            binderObject.bottomNavJobsBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.appColor))
        }
    }

    fun isUserLogined():Boolean
    {
        if(sharedPreferences.getString(Constants.User_id).equals(""))
        {

            val dialog = CustomDialog(this)
            dialog.setMessage(resources.getString(R.string.signin_txt))
                    dialog.setPositiveButton("Continue", DialogListenerInterface.onPositiveClickListener {

                        val bundle = Bundle()
                        bundle.putString("ShouldOpenLogin"  , "true")
                        val newIntent = Intent(this@HomeActivity, MainActivity::class.java)
                        newIntent.putExtras(bundle)
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(newIntent)
                    })

                    dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })
                    dialog.show()

            return false
        }

        else
        {
        return true
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)



/*        if(requestCode == Crop.REQUEST_CROP)
        {
            if (currentFragment is ProfileFragment) {

                currentFragment.onActivityResult(requestCode, resultCode, data)
            }
        }*/
        if(requestCode==LOCATIONREQUESTCODE)
        {
            currentAddress = sharedPreferences.getString(Constants.CurrentAddress)
            if(binderObject.titleLayout.righttxt.length()!=0 && !binderObject.titleLayout.righttxt.text.toString().equals(currentAddress, ignoreCase = true))
            {
                binderObject.titleLayout.righttxt.text = currentAddress
                val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (fragment is HomeFragment)
                {
                    locationUpdate = fragment
                    locationUpdate.onCurrentLocationUpdate()
                }
                else if(fragment is TalentFragment)
                {
                    locationUpdate = fragment
                    locationUpdate.onCurrentLocationUpdate()
                }

                else if(fragment is JobsFragment)
                {
                    locationUpdate = fragment
                    locationUpdate.onCurrentLocationUpdate()
                }

            }
        }

        if(requestCode==ADDJOBREQUESTCODE)
        {
            if(currentFragment is CastingProfileFragment)
            {
                (currentFragment as CastingProfileFragment).onActivityResult(requestCode, resultCode, data)
            }
        }


    }

    override fun onClearDeviceid()
    {
        slidemenuOff()
        sharedPreferences.clear()

        val bundle = Bundle()
        bundle.putString("ShouldOpenLogin"  , "true")
        val newIntent = Intent(this, MainActivity::class.java)
        newIntent.putExtras(bundle)
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(newIntent)
    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    override fun showProgress(visibility: Boolean) {
        if(visibility)
            startProgressBarAnim()
        else
            stopProgressBarAnim()
    }

    fun showSnackbar(message: String)
    {
        try
        {
            val parentLayout = findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onDestroy()
    {
        unregisterInstallStateUpdListener()
        super.onDestroy()
        mPresenter.cancelRetrofitRequest()

    }

    override fun onBackPressed()
    {

        if (doubleBackToExitPressedOnce)
        {
              super.onBackPressed()
        }

        else
        {

            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this@HomeActivity, "Press back button again to exit", Toast.LENGTH_SHORT).show()

            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        }

        }

    override fun onResume() {
        super.onResume()
        checkNewAppVersionState()
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

    private fun startAppUpdateFlexible(appUpdateInfo: AppUpdateInfo) {
        try {
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

            snackbar.view.setBackgroundColor(ContextCompat.getColor(this@HomeActivity, R.color.appColor))

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
