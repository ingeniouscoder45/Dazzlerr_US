package com.dazzlerr_usa.views.activities.portfolio

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityPortfolioBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.PermissionUtils
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.accountverification.AccountVerification
import com.dazzlerr_usa.views.activities.portfolio.addproject.PortfolioProjectActivity
import com.dazzlerr_usa.views.activities.portfolio.addvideo.AddVideoActivity
import com.dazzlerr_usa.views.fragments.portfolio.adapters.ViewPagerAdapter
import com.dazzlerr_usa.views.fragments.portfolio.localfragments.PortfolioAudiosFragment
import com.dazzlerr_usa.views.fragments.portfolio.localfragments.PortfolioImagesFragment
import com.dazzlerr_usa.views.fragments.portfolio.localfragments.PortfolioProjectsFragment
import com.dazzlerr_usa.views.fragments.portfolio.localfragments.PortfolioVideosFragment
import com.google.android.material.tabs.TabLayout
import droidninja.filepicker.FilePickerBuilder
import permissions.dispatcher.*
import javax.inject.Inject


@RuntimePermissions
class PortfolioActivity : AppCompatActivity(), View.OnClickListener
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    internal lateinit var bindingObj: ActivityPortfolioBinding
    internal lateinit var viewPagerAdapter: ViewPagerAdapter
    lateinit var fragmentManager: FragmentManager
    var currentFragment :String  = "ImagesFragment"
    var navigateFrom : String?=null
    var user_id = ""
    val REQUEST_CODE_VIDEO   = 100
    var isPermissionsGiven = false
    var shouldShowProjects = ""
    var shouldShowPopup = false
    var isAudioPermisssionGiven = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this, R.layout.activity_portfolio)
        initializations()
        tabListener()
       // askPermissions()
    }




    fun askPermissions()
    {
        showImagePickerWithPermissionCheck()
    }

    fun askAudioPermission()
    {
        showAudioPickerWithPermissionCheck()
    }
    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE )
    fun showImagePicker()
    {
        isPermissionsGiven =true
        imagePickerDialog()
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE )
    fun showAudioPicker()
    {
        isAudioPermisssionGiven = true
        audioPicker()
    }

    @OnShowRationale(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE )
    fun cameraStorageRationale(request: PermissionRequest) {
        PermissionUtils.showRationalDialog(this, R.string.permission_rationale_camera_storage, request)
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE )
    fun audioStorageRationale(request: PermissionRequest) {
        PermissionUtils.showRationalDialog(this, R.string.permission_rationale_storage, request)
    }

    @OnPermissionDenied(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE )
    fun cameraStorageDenied() {
        Toast.makeText(this , R.string.permission_denied_camera_storage , Toast.LENGTH_SHORT).show()
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE )
    fun audioStorageDenied() {
        Toast.makeText(this , R.string.permission_denied_storage , Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE )
    fun cameraStorageNeverAsk()
    {
        PermissionUtils.showAppSettingsDialog(this, R.string.permission_never_ask_camera_storage, Constants.REQ_CODE_APP_SETTINGS)
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE )
    fun audioStorageNeverAsk()
    {
        PermissionUtils.showAppSettingsDialog(this, R.string.permission_never_ask_storage, Constants.REQ_CODE_APP_SETTINGS)
    }

    private fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)

        val bundle : Bundle?  = intent!!.extras
        navigateFrom = bundle?.getString("from")
        user_id = bundle?.getString("user_id").toString()
        shouldShowProjects = bundle?.getString("shouldShowProjects").toString()
        //----------------Coming from talent screen
        if (navigateFrom!!.equals("talentFragment", ignoreCase = true))
        {
            bindingObj.titleLayout.rightbtn.visibility  =View.GONE
        }
        else
        {
            if(bundle?.containsKey("isProfileEdit")!! && bundle?.getString("isProfileEdit").equals("false"))
            bindingObj.titleLayout.rightbtn.visibility  =View.GONE
            else {
                bindingObj.titleLayout.rightbtn.visibility = View.VISIBLE
                shouldShowPopup =true
            }

        }

        if(shouldShowProjects.equals("false"))
        {
            bindingObj.tabLayout.tabMode = TabLayout.MODE_FIXED
        }

        fragmentManager = supportFragmentManager
        viewPagerAdapter = ViewPagerAdapter(fragmentManager,shouldShowProjects )
        bindingObj.viewPager.offscreenPageLimit = 4
        bindingObj.viewPager.adapter = viewPagerAdapter
        bindingObj.tabLayout.setupWithViewPager(bindingObj.viewPager)
        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.titleLayout.rightbtn.setOnClickListener(this)
        bindingObj.titleLayout.titletxt.text = "Portfolio"

        if(intent.extras?.containsKey("currentTab")!! && intent.extras?.getInt("currentTab")== 1) {
            bindingObj.tabLayout.getTabAt(0)!!.select()
            bindingObj.viewPager.currentItem=0
            currentFragment = "ImagesFragment"

/*            if(shouldShowPopup) {
                bindingObj.titleLayout.titleLayout.post(object : Runnable {
                    override fun run() {
                        // Thread.sleep(1000)

                        Tooltip.Builder(this@PortfolioActivity)
                                .anchor(bindingObj.titleLayout.rightbtn, 0, 0, true)
                                .closePolicy(ClosePolicy.TOUCH_ANYWHERE_CONSUME)
                                .showDuration(0)
                                .floatingAnimation(Tooltip.Animation.SLOW)
                                .arrow(true)
                                .styleId(R.style.ToolTipAltStyle)
                                .overlay(true)
                                .text("A profile with maximum photos attract casting directors. So upload up to 8 photos for a better portfolio.")
                                .create()
                                .show(bindingObj.titleLayout.rightbtn, Tooltip.Gravity.BOTTOM, false)
                    }
                })
            }*/

        }

        else if(intent.extras?.containsKey("currentTab")!! && intent.extras?.getInt("currentTab")== 2) {
            bindingObj.tabLayout.getTabAt(1)!!.select()
            bindingObj.viewPager.currentItem=1
            currentFragment = "VideosFragment"
        }

        else if(intent.extras?.containsKey("currentTab")!! && intent.extras?.getInt("currentTab")== 3) {
            bindingObj.tabLayout.getTabAt(2)!!.select()
            bindingObj.viewPager.currentItem=2
            currentFragment = "AudiosFragment"
        }

        else if(intent.extras?.containsKey("currentTab")!! && intent.extras?.getInt("currentTab")== 4) {
            bindingObj.tabLayout.getTabAt(3)!!.select()
            bindingObj.viewPager.currentItem=3
            currentFragment = "ProjectsFragment"
        }
    }

    private fun tabListener()
    {
        bindingObj.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab)
            {

                if (tab.text!!.toString().trim().equals("IMAGES", ignoreCase = true))
                {
                    currentFragment = "ImagesFragment"
                }

                else if (tab.text!!.toString().trim().equals("VIDEOS", ignoreCase = true))
                {
                    currentFragment = "VideosFragment"
                    bindingObj.deleteTitleLayout.visibility = View.GONE
                    bindingObj.titleLayout.titleLayout.visibility = View.VISIBLE
                }

                else if (tab.text!!.toString().trim().equals("AUDIOS", ignoreCase = true))
                {
                    currentFragment = "AudiosFragment"
                }

                else if (tab.text!!.toString().trim().equals("PROJECTS", ignoreCase = true))
                {
                    currentFragment = "ProjectsFragment"
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }
     fun imagePickerDialog()
    {
        FilePickerBuilder.instance
                .setMaxCount(1)
                .setActivityTheme(R.style.LibAppTheme)
                .enableCameraSupport(true)
                .pickPhoto(this)
    }

     fun videoPickerDialog()
    {
        FilePickerBuilder.instance
                .setMaxCount(1)
                .setActivityTheme(R.style.LibAppTheme)
                .enableCameraSupport(false)
                .enableImagePicker(false)
                .enableVideoPicker(true)
                .pickPhoto(this , REQUEST_CODE_VIDEO)
    }

/*     fun documentPickerDialog()
     {
        FilePickerBuilder.instance
                .setMaxCount(1)
                .setActivityTheme(R.style.LibAppTheme)
                .pickFile(this)
     }*/

    fun audioPicker()
    {

        val intent3 = Intent(this, AddAudioActivity::class.java)
        startActivityForResult(intent3, 1004)

    }
    override fun onClick(v: View)
    {
        when (v.id)
        {
            R.id.leftbtn -> finish()

            R.id.rightbtn ->
            {

                if(sharedPreferences.getString(Constants.Is_Phone_Verified).equals("1") || sharedPreferences.getString(Constants.Is_Email_Verified).equals("1"))
                {

                    if (currentFragment.equals("ImagesFragment" , ignoreCase = true))
                    {


                        if(isPermissionsGiven)
                            imagePickerDialog()

                        else
                            askPermissions()
                    }


                        else if (currentFragment.equals("VideosFragment" , ignoreCase = true))
                        {
                            val intent = Intent(this@PortfolioActivity, AddVideoActivity::class.java)
                            startActivityForResult(intent ,101)
                        }

                        else if (currentFragment.equals("AudiosFragment" , ignoreCase = true))
                        {
                            if(isAudioPermisssionGiven)
                                audioPicker()

                            else
                                askAudioPermission()


                        }

                        else if (currentFragment.equals("ProjectsFragment" , ignoreCase = true))
                        {

                            val intent = Intent(this@PortfolioActivity, PortfolioProjectActivity::class.java)
                            startActivityForResult(intent ,100)

                        }
                }
                else
                {

                    val dialog = CustomDialog(this@PortfolioActivity)
                            dialog.setMessage("Your email and mobile number is not verified. Please verify your details to proceed.")
                            dialog.setPositiveButton("Verify now", DialogListenerInterface.onPositiveClickListener {

                                val bundle = Bundle()
                                bundle.putString("type" , "emailOrPhoneVerification")
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
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {

        super.onActivityResult(requestCode, resultCode, data)

/*
        val fragment = fragmentManager.getFragments().get(bindingObj.viewPager.getCurrentItem())


        if(fragment is PortfolioProjectsFragment)
        {
            fragment.onActivityResult(requestCode, resultCode, data)
            Timber.e("Projects: "+currentFragment)
        }

        else if(fragment is PortfolioVideosFragment)
        {
            fragment.onActivityResult(requestCode, resultCode, data)
            Timber.e("Videos: "+currentFragment)
        }

        else if(fragment is PortfolioImagesFragment)
        {
            fragment.onActivityResult(requestCode, resultCode, data)
            Timber.e("Images: "+currentFragment)
        }

        else if(fragment is PortfolioDocumentsFragment)
        {
            fragment.onActivityResult(requestCode, resultCode, data)
            Timber.e("Documents: "+currentFragment)
        }
*/

        if(currentFragment.equals("ProjectsFragment"))
        {
            val fragment = bindingObj.viewPager.adapter?.instantiateItem(bindingObj.viewPager , bindingObj.viewPager.currentItem) as PortfolioProjectsFragment
            fragment.onActivityResult(requestCode, resultCode, data)
        }

        else if(currentFragment.equals("VideosFragment"))
        {
            val fragment =  bindingObj.viewPager.adapter?.instantiateItem(bindingObj.viewPager , bindingObj.viewPager.currentItem) as PortfolioVideosFragment
            fragment.onActivityResult(requestCode, resultCode, data)
        }

        else if(currentFragment.equals("ImagesFragment"))
        {
            val fragment =  bindingObj.viewPager.adapter?.instantiateItem(bindingObj.viewPager , bindingObj.viewPager.currentItem) as PortfolioImagesFragment
            fragment.onActivityResult(requestCode, resultCode, data)
        }

        else if(currentFragment.equals("AudiosFragment"))
        {
            val fragment =  bindingObj.viewPager.adapter?.instantiateItem(bindingObj.viewPager , bindingObj.viewPager.currentItem) as PortfolioAudiosFragment
            fragment.onActivityResult(requestCode, resultCode, data)
        }

    }


    fun startProgressBarAnim() {

        bindingObj.aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {

        bindingObj.aviProgressBar.setVisibility(View.GONE)
    }

}

