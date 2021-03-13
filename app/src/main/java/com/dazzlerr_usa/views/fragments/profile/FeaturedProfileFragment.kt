package com.dazzlerr_usa.views.fragments.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FeatureFragmentProfileBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.*
import com.dazzlerr_usa.utilities.PermissionUtils
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.utilities.customdialogs.ProfileCompletenessAlertDialog
import com.dazzlerr_usa.utilities.customdialogs.ProfileCompletenessListener
import com.dazzlerr_usa.utilities.imageslider.*
import com.dazzlerr_usa.utilities.sinchcalling.SinchService
import com.dazzlerr_usa.views.activities.OthersProfileActivity
import com.dazzlerr_usa.views.activities.accountverification.AccountVerification
import com.dazzlerr_usa.views.activities.editprofile.EditProfileActivity
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.activities.messagewindow.MessageWindowActivity
import com.dazzlerr_usa.views.activities.mymembership.MyMembershipActivity
import com.dazzlerr_usa.views.activities.portfolio.PortfolioActivity
import com.dazzlerr_usa.views.activities.report.ProfileReportActivity
import com.dazzlerr_usa.views.activities.settings.SettingsActivity
import com.dazzlerr_usa.views.activities.synchcalling.CallScreenActivity
import com.dazzlerr_usa.views.activities.talentfollowers.TalentFollowersActivity
import com.dazzlerr_usa.views.activities.talentlikes.TalentLikesActivity
import com.dazzlerr_usa.views.activities.talentviews.TalentViewsActivity
import com.dazzlerr_usa.views.fragments.portfolio.models.PortfolioImagesModel
import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioImagesPojo
import com.dazzlerr_usa.views.fragments.portfolio.pojos.UploadImagePojo
import com.dazzlerr_usa.views.fragments.portfolio.presenters.PortfolioImagesPresenter
import com.dazzlerr_usa.views.fragments.portfolio.views.PortfolioImagesView
import com.dazzlerr_usa.views.fragments.profile.adapters.FeaturedProfileImagesAdapter
import com.dazzlerr_usa.views.fragments.profile.adapters.InterestAdapter
import com.dazzlerr_usa.views.fragments.profile.adapters.ProfileFragmentsServicesViewPagerAdapter
import com.dazzlerr_usa.views.fragments.profile.adapters.ProfileFragmentsViewPagerAdapter
import com.dazzlerr_usa.views.fragments.profile.pojo.GetContactDetailsPojo
import com.google.android.flexbox.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.sinch.android.rtc.calling.Call
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import permissions.dispatcher.*
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.set

/**
 * A simple [Fragment] subclass.
 */

@RuntimePermissions
class FeaturedProfileFragment : androidx.fragment.app.Fragment()  , ProfileView ,View.OnClickListener ,   PortfolioImagesView, ImageSliderView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    var imageList = ArrayList<PreviewFile>()
    lateinit var mPresenter:ProfilePresenter
    lateinit var bindingObj: FeatureFragmentProfileBinding
    lateinit var mLikesPresenter: ImageSliderPresenter
    lateinit var mImagesPresenter: PortfolioImagesPresenter
    lateinit var mImagesAdapter: FeaturedProfileImagesAdapter
    var portfolioList: MutableList<PortfolioImagesPojo.DataBean>? = java.util.ArrayList()
    var navigateFrom: String? = ""
    var user_role: String? = ""
    var username: String? = ""
    var profile_id:String = ""
    var profileDataJson= ""
    var isPermissionsGiven = false
    var isCallPermissionsGiven = false
    var likeStatus = 0
    var likesCount = 0
    var followCount = 0
    var followStatus = 0
    var shortlistStatus = 0
    var OPENFLAG: Boolean = false// to check the state if activity is opening first time
    var profilePhone = ""
    var profileEmail = ""
    var isContactPublic = "0"
    var can_contact =""
    var contact_count =""
    var mPagerServicesAdapter:ProfileFragmentsServicesViewPagerAdapter ?= null
    var mPagerAdapter:ProfileFragmentsViewPagerAdapter ?= null
    var is_sender_blocked =""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {

        // Inflate the layout for this fragment
        bindingObj = DataBindingUtil.inflate(inflater, R.layout.feature_fragment_profile, container, false)
        initializations()
        clickListeners()
        apiCalling(profile_id)
        return bindingObj.root
    }

    fun askPermissions()
    {
        showImagePickerWithPermissionCheck()
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

    @OnShowRationale(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE )
    fun cameraStorageRationale(request: PermissionRequest)
    {
        PermissionUtils.showRationalDialog(activity as Activity, R.string.permission_rationale_camera_storage, request)
    }

    @OnPermissionDenied(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE )
    fun cameraStorageDenied() {
        Toast.makeText(activity as Activity , R.string.permission_denied_camera_storage , Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE )
    fun cameraStorageNeverAsk()
    {
        PermissionUtils.showAppSettingsDialog(this, R.string.permission_never_ask_camera_storage, Constants.REQ_CODE_APP_SETTINGS)
    }


    //Call related permissions started


    fun askCallPermissions()
    {
        callUserWithPermissionCheck()
    }

    @NeedsPermission(Manifest.permission.RECORD_AUDIO , Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.READ_PHONE_STATE )
    fun callUser()
    {
        isCallPermissionsGiven =true
        makeCall()
    }

    @OnShowRationale(Manifest.permission.RECORD_AUDIO , Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.READ_PHONE_STATE)
    fun callPermissionRationale(request: PermissionRequest)
    {
        PermissionUtils.showRationalDialog(activity as Activity, R.string.permission_rationale_call, request)
    }

    @OnPermissionDenied(Manifest.permission.RECORD_AUDIO , Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.READ_PHONE_STATE)
    fun callPermissionDenied() {
        Toast.makeText(activity as Activity , R.string.permission_denied_call , Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.RECORD_AUDIO , Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.READ_PHONE_STATE)
    fun callPermissionNeverAsk()
    {
        PermissionUtils.showAppSettingsDialog(this, R.string.permission_never_ask_call, Constants.REQ_CODE_APP_SETTINGS)
    }


    private fun initializations()
    {


        ((activity?.application) as MyApplication).myComponent.inject(this)

        profile_id = arguments?.getString("user_id" , "").toString()
        //-----Checking if user is coming from talent screen or dashboard screen
        //navigateFrom = arguments!!.getString("from")
        navigateFrom = if(profile_id.equals(sharedPreferences.getString(Constants.User_id).toString())) "dashboard" else "talentFragment"
        user_role = arguments!!.getString("user_role")


        mImagesPresenter = PortfolioImagesModel(activity as Activity, this)
        mLikesPresenter = ImageSliderModel(activity as Activity , this)
        mImagesAdapter = FeaturedProfileImagesAdapter(activity as Activity ,portfolioList!! , sharedPreferences.getString(Constants.User_id).toString() ,this@FeaturedProfileFragment)
        bindingObj.profileImagesRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        val spanCount = 2 // 3 columns
        val spacing = 2 // 4px
        val includeEdge = true
        bindingObj.profileImagesRecyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
        bindingObj.profileImagesRecyclerView.adapter = mImagesAdapter

        if(activity?.isNetworkActiveWithMessage()!!)
            mImagesPresenter?.getPortfolioImages(profile_id, sharedPreferences.getString(Constants.User_id).toString(), "1" , "")


        // Timber.e("Profile Role "+user_role)

        //----------------Coming from talent screen
        if (navigateFrom!!.equals("talentFragment", ignoreCase = true))
        {

            bindingObj.likeFollowLayout.visibility = View.VISIBLE
            bindingObj.editProfileLayout.visibility = View.GONE
            bindingObj.editProfilePicBtn.visibility = View.GONE
            bindingObj.profileContactLayout.visibility = View.VISIBLE
            //bindingObj.profileContactLayout.visibility = View.GONE

            if(sharedPreferences.getString(Constants.User_Role).equals("casting" ,ignoreCase = true))
            {
                bindingObj.shortlistBtn.visibility = View.VISIBLE
            }

            else
            {
                bindingObj.shortlistBtn.visibility = View.GONE
            }

        }
        else
        {

            bindingObj.likeFollowLayout.visibility = View.GONE
            bindingObj.editProfileLayout.visibility = View.VISIBLE
            bindingObj.editProfilePicBtn.visibility = View.VISIBLE
            bindingObj.profileContactLayout.visibility = View.GONE

            if(activity is HomeActivity)
                (activity as HomeActivity).titleSettings(ContextCompat.getColor(context!!.applicationContext ,R.color.appColor), true, resources.getColor(R.color.colorWhite), resources.getColor(R.color.colorWhite), resources.getColor(R.color.colorWhite), "Profile", "", R.drawable.ic_share)
            if(activity is HomeActivity)
                (activity as HomeActivity).setBottomNavigationMenu(true, false, false, false, false)

        }

        mPresenter = ProfileModel(activity as Activity  , this)

    }

    private fun clickListeners()
    {
        bindingObj.userImage.setOnClickListener(this)
        bindingObj.profileCallBtn.setOnClickListener(this)
        bindingObj.editProfileLayout.setOnClickListener(this)
        bindingObj.editProfilePicBtn.setOnClickListener(this)
        bindingObj.likeBtn.setOnClickListener(this)
        bindingObj.followBtn.setOnClickListener(this)
        bindingObj.editProfilePicBtn.setOnClickListener(this)
        bindingObj.shortlistBtn.setOnClickListener(this)
        bindingObj.profileContactBtn.setOnClickListener(this)
        bindingObj.profileMessagesBtn.setOnClickListener(this)
        //bindingObj.profileReportBtn.setOnClickListener(this)
        bindingObj.profileLikesLayout.setOnClickListener(this)
        bindingObj.profileFollowersLayout.setOnClickListener(this)
        bindingObj.profileViewsLayout.setOnClickListener(this)
        bindingObj.profileImagesExploreBtn.setOnClickListener(this)
    }

    private fun apiCalling(user_id: String)
    {
        if(activity?.isNetworkActive()!!)
        {
            mPresenter?.getProfile(user_id , sharedPreferences.getString(Constants.User_id).toString())
        }
        else
        {

            val dialog  =  CustomDialog(activity as Activity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                apiCalling(user_id)
            })
            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                dialog.dismiss()
            })
            dialog.show()
        }
    }

    private fun tabSettings(tabs:String)
    {

        var fragmentCount =0
        if(tabs.contains("Services" ,ignoreCase = true)!!)
        {
            fragmentCount=5
            mPagerServicesAdapter = ProfileFragmentsServicesViewPagerAdapter(childFragmentManager, fragmentCount, profile_id ,profileDataJson ,navigateFrom!! ,user_role!!)
            bindingObj.profileViewPager.adapter = mPagerServicesAdapter
        }
        else
        {
            fragmentCount = 4
            mPagerAdapter = ProfileFragmentsViewPagerAdapter(childFragmentManager, fragmentCount, profile_id ,profileDataJson ,navigateFrom!! ,user_role!!)
            bindingObj.profileViewPager.adapter = mPagerAdapter
        }


        bindingObj.tabLayout.setupWithViewPager(bindingObj.profileViewPager)
    }


    @SuppressLint("DefaultLocale")
    override fun onGetProfileSuccess(model: ProfilePojo)
    {

        if(model.data?.size!=0)
        {

            bindingObj.profileMainLayout.visibility = View.VISIBLE
            //bindingObj.profileMessagesBtn.visibility = View.VISIBLE


            bindingObj.profileBinder = model.data?.get(0)
            /* val anim = AnimationUtils.loadAnimation(activity, R.anim.enter_from_bottom_slow)
             bindingObj.profileMessagesBtn.startAnimation(anim)*/


            //Start of Rates and travel data


            var CurrentCurrency = if (Constants.CURRENT_CURRENCY.isEmpty() || Constants.CURRENT_CURRENCY.equals("inr", ignoreCase = true)) {
                Constants.RupeesSign
            } else {
                Constants.DollorSign
            }
            bindingObj.profileBinder?.full_day = if (model.data?.get(0)?.full_day?.isNotEmpty()!! && !model.data?.get(0)?.full_day.equals("Available on request", ignoreCase = true)) CurrentCurrency + model.data?.get(0)!!.full_day else model.data?.get(0)!!.full_day
            bindingObj.profileBinder?.half_day = if (model.data?.get(0)?.half_day?.isNotEmpty()!! && !model.data?.get(0)?.half_day.equals("Available on request", ignoreCase = true)) CurrentCurrency + model.data?.get(0)!!.half_day else model.data?.get(0)!!.half_day
            bindingObj.profileBinder?.hourly = if(model.data?.get(0)?.hourly?.isNotEmpty()!! && !model.data?.get(0)?.hourly.equals("Available on request",ignoreCase = true) )
                CurrentCurrency +model.data?.get(0)!!.hourly else model.data?.get(0)!!.hourly

            bindingObj.executePendingBindings()

            if (model.data?.get(0)?.will_fly?.equals("1")!!)
                bindingObj.travel.text = "Yes"
            else
                bindingObj.travel.text = "No"

            if (model.data?.get(0)?.test?.equals("1")!!)
                bindingObj.test.text = "Yes"
            else if (model.data?.get(0)?.test?.equals("0")!!)
                bindingObj.test.text = "No"
            else
                bindingObj.test.text = "Maybe"

            if (model.data?.get(0)?.passport_ready?.equals("1")!!)
                bindingObj.isPassportReady.text = "Yes"
            else if (model.data?.get(0)?.passport_ready?.equals("0")!!)
                bindingObj.isPassportReady.text = "No"
            else
                bindingObj.isPassportReady.text = "Applied"

            //Start of General Information data-------------------------
            var mAvailableForList = "-"
            if (model.data?.get(0)?.available_for != null && !model.data?.get(0)?.available_for.equals(""))
                mAvailableForList = model.data?.get(0)?.available_for?.split(",").toString().replace("[","").replace("]" , "")

            bindingObj.expertise.text = mAvailableForList
            //------Available For End

            if (model.data?.get(0)?.gender.equals("female", ignoreCase = true))
                bindingObj.biceps.text = "N/A"



            if (model.data?.get(0)?.agency_signed.equals("1"))
                bindingObj.agencySigned.text = "Yes"
            else
                bindingObj.agencySigned.text = "No"

            if (model.data?.get(0)?.marital_status.equals("1"))
                bindingObj.maritalStatus.text = "Married"
            else if (model.data?.get(0)?.marital_status.equals("2"))
                bindingObj.maritalStatus.text = "Divorced"
            else
                bindingObj.maritalStatus.text = "Single"


            //End of General Info data=----------------------


            //--------Hiding appearance tab for some roles
            if(model?.data?.get(0)?.profile_tabs?.contains("Appearance" ,ignoreCase = true)!!)
            {
                bindingObj.physicalAppearanceLayout.visibility = View.VISIBLE
            }
            else
                bindingObj.physicalAppearanceLayout.visibility = View.GONE



            if(model.data?.get(0)?.profile_tabs!!.contains("Services",ignoreCase = true))
            {

                bindingObj.servicesLayout.visibility = View.VISIBLE

                var mServicesList: MutableList<String> = ArrayList()

                if (model.data?.get(0)?.user_services != null && !model.data?.get(0)?.user_services.equals(""))
                    mServicesList = model.data?.get(0)?.user_services?.split(",") as MutableList<String>

                var mServicesAdapter = InterestAdapter(activity as Activity, mServicesList!!)

                val servicesLayoutManager = FlexboxLayoutManager(activity)
                servicesLayoutManager.flexDirection = FlexDirection.ROW
                servicesLayoutManager.justifyContent = JustifyContent.FLEX_START
                //layoutManager.alignContent = AlignContent.STRETCH
                servicesLayoutManager.alignItems = AlignItems.STRETCH
                servicesLayoutManager.flexWrap = FlexWrap.WRAP
                bindingObj.servicesRecycler.setLayoutManager(servicesLayoutManager)
                bindingObj.servicesRecycler.adapter = mServicesAdapter

                if (mServicesList.size == 0)
                    bindingObj.servicesEmptyLayout.visibility = View.VISIBLE
                else
                    bindingObj.servicesEmptyLayout.visibility = View.GONE

            }

            else
            {
                bindingObj.servicesLayout.visibility = View.GONE
            }

            is_sender_blocked = model.data?.get(0)?.sender_blocked!!

            if (model.data?.get(0)?.about.toString().isNotEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    bindingObj.aboutTxt.setText(Html.fromHtml(model.data?.get(0)?.about.toString().capitalize(), Html.FROM_HTML_MODE_LEGACY))
                } else {
                    bindingObj.aboutTxt.setText(Html.fromHtml(model.data?.get(0)?.about.toString().capitalize()))
                }

            }


            //Hiding the state if state and city name is same
            if (model.data?.get(0)?.city.equals(model.data?.get(0)?.state_name, ignoreCase = true))
                bindingObj.userstate.visibility = View.GONE

            if (model.data?.get(0)?.is_trending.equals("0") && model.data?.get(0)?.toprated.equals("0") && model.data?.get(0)?.is_featured.equals("0")) {
                bindingObj.isFeaturedOrTopRated.visibility = View.GONE
            }

            if (model.data?.get(0)?.toprated.equals("1")) {
                bindingObj.isFeaturedOrTopRated.visibility = View.VISIBLE
                bindingObj.isFeaturedOrTopRated.setImageResource(R.drawable.ic_top_rated)
            }

            if (model.data?.get(0)?.is_featured.equals("1")) {
                bindingObj.isFeaturedOrTopRated.visibility = View.VISIBLE
                bindingObj.isFeaturedOrTopRated.setImageResource(R.drawable.ic_featured)
            }

            if (model.data?.get(0)?.is_trending.equals("1")) {
                bindingObj.isFeaturedOrTopRated.visibility = View.VISIBLE
                bindingObj.isFeaturedOrTopRated.setImageResource(R.drawable.ic_trending)
            }

            if (activity is OthersProfileActivity)
                (activity as OthersProfileActivity).CUST_ID = model.data?.get(0)?.cust_id.toString()
            else if (activity is HomeActivity)
                (activity as HomeActivity).CUST_ID = model.data?.get(0)?.cust_id.toString()


            isContactPublic = model.data?.get(0)?.is_public.toString()

            can_contact = model.data?.get(0)?.can_contact.toString()
            contact_count = model.data?.get(0)?.contact_count.toString()

            val gson = GsonBuilder().create()
            profileDataJson = gson.toJson(model)

            user_role = model?.data?.get(0)?.user_role
            username = model?.data?.get(0)?.username


            //tabSettings(model?.data?.get(0)?.profile_tabs!!)

            OPENFLAG = true

            if (!model.data?.get(0)?.gender.equals("") && !model.data?.get(0)?.gender.equals("null"))
                bindingObj.profileGender.text = " | " + model.data?.get(0)?.gender?.capitalize()

            if (!model.data?.get(0)?.age.equals("") && !model.data?.get(0)?.age.equals("null"))
                bindingObj.profileAge.text = " | " + model.data?.get(0)?.age?.capitalize()


            if (model.data?.get(0)?.verified.equals("1"))
                bindingObj.isVerifiedUserIcon.visibility = View.VISIBLE
            else
                bindingObj.isVerifiedUserIcon.visibility = View.GONE


            if (!model.data?.get(0)?.likes_count.equals("") && !model.data?.get(0)?.likes_count.equals("null"))
                likesCount = model.data?.get(0)?.likes_count?.toInt()!!
            else
                likesCount = 0

            if (!model.data?.get(0)?.followers_count.equals("") && !model.data?.get(0)?.followers_count.equals("null"))
                followCount = model.data?.get(0)?.followers_count?.toInt()!!
            else
                followCount = 0

            likeStatus = model.data?.get(0)?.is_liked as Int
            likeDislikeHandler()
            followStatus = model.data?.get(0)?.is_followed as Int
            followUnfollowHandler()

            shortlistStatus = model.data?.get(0)?.is_shortlisted as Int
            shortListHandler()


            Glide.with(activity as Activity)
                    .load("https://www.dazzlerr.com/API/" + model.data?.get(0)?.profile_pic).apply(RequestOptions().centerCrop())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .into(bindingObj.userImage)


            Glide.with(activity as Activity)
                    .load("https://www.dazzlerr.com/API/" + model.data?.get(0)?.profile_pic).apply(RequestOptions().centerCrop())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .into(bindingObj.profileBackgroundImage)

            Glide.with(activity as Activity)
                    .load("https://www.dazzlerr.com/API/" + model.data?.get(0)?.profile_pic).apply(RequestOptions().centerCrop())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .into(bindingObj.profileBannerImage)

            imageList.clear()
            imageList.add(PreviewFile(model.data?.get(0)?.profile_pic, false, false, "", "", "", 0, 0, "", ""))


            if (model.data?.get(0)?.user_id?.toString().equals(sharedPreferences.getString(Constants.User_id).toString())!!) {
                sharedPreferences.saveString(Constants.User_pic, model.data?.get(0)?.profile_pic.toString())
                sharedPreferences.saveString(Constants.Is_Featured, model.data?.get(0)?.is_featured.toString())
                sharedPreferences.saveString(Constants.Profile_Complete, model.data?.get(0)?.profile_complete.toString())
                sharedPreferences.saveString(Constants.User_name, model.data?.get(0)?.name.toString())
                sharedPreferences.saveString(Constants.User_Role, model.data?.get(0)?.user_role.toString())
                sharedPreferences.saveString(Constants.User_Phone, model.data?.get(0)?.phone.toString())
                sharedPreferences.saveString(Constants.User_Email, model.data?.get(0)?.email.toString())
                sharedPreferences.saveString(Constants.Current_city, model.data?.get(0)?.current_city.toString())
                sharedPreferences.saveString(Constants.Current_state, model.data?.get(0)?.current_state.toString())
                sharedPreferences.saveString(Constants.Exp_type, model.data?.get(0)?.exp_type.toString())
                sharedPreferences.saveString(Constants.Is_Email_Verified, "" + model.data?.get(0)?.email_isverified)// 0 or 1 format
                sharedPreferences.saveString(Constants.Is_Phone_Verified, "" + model.data?.get(0)?.phone_isverified) // 0 or 1 format
                sharedPreferences.saveString(Constants.IsProfile_published, "" + model.data?.get(0)?.is_published)
                sharedPreferences.saveString(Constants.Account_type, "" + model.data?.get(0)?.account_type)
                sharedPreferences.saveString(Constants.Membership_id, "" + model.data?.get(0)?.membership_id)
                sharedPreferences.saveString(Constants.User_Secondary_Role, "" + model.data?.get(0)?.secondary_role)

            }

            // bindingObj.agencySigned.text = "Yes"

        }
    }

    fun likeDislikeHandler()
    {

        if(likeStatus==1)
        {
            bindingObj.likeBtnTxt.text = "Liked"
            bindingObj.likeBtn.setBackgroundResource(R.drawable.edittext_whitetransparentbackground)
            bindingObj.likeBtnTxt.setTextColor(activity?.resources?.getColor(R.color.colorBlack)!!)
            bindingObj.likeBtnIcon.setColorFilter(ContextCompat.getColor(activity as Activity, R.color.colorBlack), android.graphics.PorterDuff.Mode.MULTIPLY)

        }
        else
        {
            bindingObj.likeBtnTxt.text = "Like"
            bindingObj.likeBtnTxt.setTextColor(activity?.resources?.getColor(R.color.colorWhite)!!)
            bindingObj.likeBtn.setBackgroundResource(R.drawable.edittext_round_border)
            bindingObj.likeBtnIcon.setColorFilter(ContextCompat.getColor(activity as Activity, R.color.colorWhite), android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }

    fun followUnfollowHandler()
    {

        if(followStatus==0)
        {
            bindingObj.followBtnTxt.text = "Follow"
            bindingObj.followIcon.visibility = View.VISIBLE
            bindingObj.followBtnTxt.setTextColor(activity?.resources?.getColor(R.color.colorWhite)!!)
            bindingObj.followBtn.setBackgroundResource(R.drawable.edittext_round_border)
        }
        else
        {
            bindingObj.followBtnTxt.text = "Following"
            bindingObj.followBtnTxt.setTextColor(activity?.resources?.getColor(R.color.colorBlack)!!)
            bindingObj.followBtn.setBackgroundResource(R.drawable.edittext_whitetransparentbackground)

            bindingObj.followIcon.visibility = View.GONE
        }
    }

    fun shortListHandler()
    {
        if(shortlistStatus==0)
        {
            //bindingObj.shortlistBtn.visibility = View.VISIBLE
            bindingObj.shortlistBtn.setImageResource(R.drawable.ic_add_wishlist)
        }
        else
        {
            // bindingObj.shortlistBtn.visibility = View.VISIBLE
            bindingObj.shortlistBtn.setImageResource(R.drawable.ic_added_wishlist)
        }
    }

    override fun onGetContactDetails(model: GetContactDetailsPojo)
    {
        profilePhone = if(model.data?.get(0)?.phone.toString().equals("0")) "" else model.data?.get(0)?.phone.toString()

        profileEmail = model.data?.get(0)?.email.toString()

        getContactDetailsDialog(model.contact_count_left, model.total_contact_count)
    }

    override fun onFollowOrUnfollow(status: String)
    {
        followStatus = status.toInt()

        if(followStatus==1)
        {
            try
            {
                followCount =(followCount+1)
                bindingObj.followCountTxt.text= followCount.toString()
            }

            catch (e : Exception)
            {
                e.printStackTrace()
            }
        }
        else
        {
            try
            {
                followCount =(followCount-1)
                bindingObj.followCountTxt.text= followCount.toString()
            }

            catch (e : Exception)
            {
                e.printStackTrace()
            }
        }
        followUnfollowHandler()
    }


    override fun onLikeOrDislike(status: String)
    {
        Timber.e("Likes Count "+bindingObj.profileBinder?.likes_count)
        likeStatus = status.toInt()

        if(likeStatus==1) {
            try {
                likesCount = (likesCount + 1)
                bindingObj.likesCountTxt.text = likesCount.toString()

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        else
        {
            try {
                likesCount = (likesCount - 1)
                bindingObj.likesCountTxt.text = likesCount.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        likeDislikeHandler()
    }


    override fun onShortList(status: String)
    {
        shortlistStatus = status.toInt()

        if(shortlistStatus==1)
        {
            showSnackbar("User has been added into your shortlisted talents list")
        }
        else
        {
            showSnackbar("User has been removed from your shortlisted talents list")
        }

        shortListHandler()
    }

    override fun onProfilePicUploaded(url: String)
    {

        sharedPreferences.saveString(Constants.User_pic , url)
        imageList.clear()
        imageList.add(
                PreviewFile(url, false , false , "" ,"" , "" ,0,0,"",""))

        showSnackbar("Profile pic has been updated successfully")
        Glide.with(activity as Activity)
                .load("https://www.dazzlerr.com/API/" + url).apply(RequestOptions().centerCrop())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .into(bindingObj.userImage)

        Glide.with(activity as Activity)
                .load("https://www.dazzlerr.com/API/" + url).apply(RequestOptions().centerCrop())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .into(bindingObj.profileBackgroundImage)

        Glide.with(activity as Activity)
                .load("https://www.dazzlerr.com/API/" +url).apply(RequestOptions().centerCrop())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .into(bindingObj.profileBannerImage)
    }


    override fun onSuccess(model: PortfolioImagesPojo) {

        if (model.data?.size != 0)
        {
            bindingObj.emptyLayout.visibility = View.GONE
            portfolioList?.clear()
            portfolioList?.addAll(model?.data!!)
            mImagesAdapter.notifyDataSetChanged()
        }
        else
        {

            bindingObj.emptyLayout.visibility = View.VISIBLE
        }
    }

    fun likeOrDislike(user_id:String ,profile_id:String , status:String, portfolio_id:String ,position:Int,likeBtn : ImageView)
    {
        if(activity?.isNetworkActiveWithMessage()!!)
        {
            likeBtn.isEnabled = false
            mLikesPresenter.like(user_id , profile_id ,status ,  portfolio_id ,"is_like" , position ,likeBtn)
        }
    }

    fun heartOrDisheart(user_id:String ,profile_id:String , status:String, portfolio_id:String ,position:Int ,heartBtn : ImageView)
    {
        if(activity?.isNetworkActiveWithMessage()!!)
        {
            heartBtn.isEnabled = false
            mLikesPresenter.heart(user_id , profile_id ,status ,  portfolio_id ,"is_heart" , position , heartBtn)
        }
    }
    override fun onLikeOrDislike(status: String, position: Int ,likeBtn : ImageView)
    {
        likeBtn.isEnabled = true
        if(status.equals("1"))
        {
            portfolioList?.get(position)?.likes= ((portfolioList?.get(position)?.likes)!!.toInt() + 1).toString()
        }
        else
            portfolioList?.get(position)?.likes= ((portfolioList?.get(position)?.likes)!!.toInt() - 1).toString()

        portfolioList?.get(position)?.is_like = status.toInt()
        mImagesAdapter?.notifyDataSetChanged()
    }

    override fun onHeartOrDisheart(status: String, position: Int , heartBtn: ImageView)
    {
        heartBtn.isEnabled = true

        if(status.equals("1"))
        {
            portfolioList?.get(position)?.hearts= ((portfolioList?.get(position)?.hearts)!!.toInt() + 1).toString()
        }
        else
            portfolioList?.get(position)?.hearts= ((portfolioList?.get(position)?.hearts)!!.toInt() - 1).toString()

        portfolioList?.get(position)?.is_heart = status.toInt()
        mImagesAdapter?.notifyDataSetChanged()

    }


    override fun onImageDelete(selectedItemsToDelete: java.util.ArrayList<String>) {

    }

    override fun onImageUpload(model: UploadImagePojo) {

    }


    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    override fun onProfileFailed(message: String)
    {
        bindingObj.profileMainLayout.visibility = View.GONE
        showSnackbar(message)
    }

    override fun showProgress(visiblity: Boolean)
    {

        if(activity !=null)
        {
            if (visiblity)
            {
                if (activity is HomeActivity)
                    (activity as HomeActivity).startProgressBarAnim()
                else
                    (activity as OthersProfileActivity).startProgressBarAnim()

                // bindingObj.profileMainLayout.visibility = View.GONE
            }
            else
            {
                if (activity is HomeActivity)
                    (activity as HomeActivity).stopProgressBarAnim()
                else
                    (activity as OthersProfileActivity).stopProgressBarAnim()

            }

        }
    }

    override fun showProgress(visiblity: Int) {

    }

    fun showSnackbar(message: String)
    {
        try {
            val parentLayout = (activity as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.userImage->
            {
                if(imageList.size!=0)
                {
                    val intent = Intent(context,
                            ImagePreviewActivity::class.java)

                    intent.putExtra(ImagePreviewActivity.IMAGE_LIST, imageList)
                    intent.putExtra(ImagePreviewActivity.CURRENT_ITEM, 0)
                    context?.startActivity(intent)
                }
            }

            R.id.profileFollowersLayout->
            {
                if(profile_id.equals(sharedPreferences.getString(Constants.User_id))) {
                    val newIntent = Intent(activity as Activity, TalentFollowersActivity::class.java)
                    startActivity(newIntent)
                }
            }

            R.id.profileLikesLayout->
            {
                if(profile_id.equals(sharedPreferences.getString(Constants.User_id))) {
                    val newIntent = Intent(activity as Activity, TalentLikesActivity::class.java)
                    startActivity(newIntent)
                }
            }

            R.id.profileViewsLayout->
            {
                if(profile_id.equals(sharedPreferences.getString(Constants.User_id))) {
                    val newIntent = Intent(activity as Activity, TalentViewsActivity::class.java)
                    startActivity(newIntent)
                }
            }



            R.id.editProfilePicBtn->
            {
                if(isPermissionsGiven)
                    imagePickerDialog()

                else
                    askPermissions()
            }


            R.id.profileMessagesBtn->
            {
                if(isUserLogined())
                {

                    if(sharedPreferences.getString(Constants.IsProfile_published).equals("1"))
                    {

                        if (!sharedPreferences.getString(Constants.User_Role).equals("casting" ,ignoreCase = true) && sharedPreferences.getString(Constants.Profile_Complete)!!.toInt() < 60)
                        {

                            val mDialog = ProfileCompletenessAlertDialog(activity as Activity)
                            mDialog.setprogressValue(sharedPreferences.getString(Constants.Profile_Complete)!!.toInt())
                            mDialog.setMessage("Your profile should be completed at least 60%. Please complete your profile first to enjoy the benefits.")
                            mDialog.setPositiveButton("Complete Now", object : ProfileCompletenessListener.onPositiveClickListener {
                                override fun onPositiveClick() {
                                    val bundle = Bundle()
                                    bundle.putString("from", "jobsDetails")
                                    bundle.putString("type", "profileComplete")
                                    bundle.putString("user_id", sharedPreferences.getString(Constants.User_id))
                                    val intent = Intent(activity as Activity, EditProfileActivity::class.java)
                                    intent.putExtras(bundle)
                                    startActivityForResult(intent, 100)
                                    mDialog.dismiss()
                                }
                            })

                            mDialog.setNegativeButton("I'll complete it later", object : ProfileCompletenessListener.onNegativeClickListener {
                                override fun onNegativeClick() {
                                    mDialog.dismiss()
                                }
                            })
                            mDialog.show()

                        }

                        else
                        {

                            if(sharedPreferences.getString(Constants.Is_Phone_Verified).equals("1") || sharedPreferences.getString(Constants.Is_Email_Verified).equals("1"))
                            {

                                if(sharedPreferences.getString(Constants.User_Role).equals("casting" ,ignoreCase = true))
                                {

                                    if (sharedPreferences.getString(Constants.Is_Documement_Verified).toString().equals("1", ignoreCase = true)
                                            && sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("1", ignoreCase = true))
                                    {
                                        if(is_sender_blocked.equals("1"))
                                        {
                                            var mDialog = CustomDialog(activity)
                                            mDialog.setTitle("Blocked")
                                            mDialog.setMessage("Can't initiate chat. Because you are blocked by this user")

                                            mDialog.setPositiveButton("Ok" , DialogListenerInterface.onPositiveClickListener {
                                                mDialog.dismiss()
                                            })

                                            mDialog.show()
                                        }

                                        else
                                        {
                                            val bundle = Bundle()
                                            bundle.putString("Username", bindingObj.userName.text.toString())
                                            bundle.putString("thread_id", "")
                                            bundle.putString("sender_id", profile_id)
                                            startActivity(Intent(activity, MessageWindowActivity::class.java).putExtras(bundle))
                                        }

                                    }

                                    else if(sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("0" , ignoreCase = true)
                                            || sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("" , ignoreCase = true)
                                            || sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("null" , ignoreCase = true))
                                    {
                                        val dialog = CustomDialog(activity)
                                        dialog.setMessage("Your identity is not verified. It is very important to help protect our community and ensure that Dazzlerr stays safe and secure. We verify identity in several ways:\n" +
                                                "\n" +
                                                "- Government ID Verification.\n" +
                                                "- Video Verification.")
                                        dialog.setPositiveButton("Verify now", DialogListenerInterface.onPositiveClickListener {
                                            dialog.dismiss()
                                            val bundle = Bundle()
                                            bundle.putString("type", "documentVerification")
                                            val newIntent = Intent(activity, AccountVerification::class.java)
                                            newIntent.putExtras(bundle)
                                            startActivity(newIntent)
                                        })
                                        dialog.setNegativeButton("Later", DialogListenerInterface.onNegetiveClickListener {
                                            dialog.dismiss()
                                        })

                                        dialog.show()

                                    }

                                    else if (sharedPreferences.getString(Constants.Is_Documement_Verified).toString().equals("0", ignoreCase = true)
                                            && sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("1", ignoreCase = true)) {
                                        val dialog = CustomDialog(activity)
                                        dialog.setMessage("Your identity verification is under process. It will take 24-48 hours.")
                                        dialog.setPositiveButton("Ok",DialogListenerInterface.onPositiveClickListener {
                                            dialog.dismiss()
                                        })

                                        dialog.show()
                                    }

                                }

                                else
                                {

                                    // if member is free. Ask him to upgrade membership
                                    if(sharedPreferences.getString(Constants.Membership_id).equals("0"))
                                    {
                                        val dialog = CustomDialog(activity)
                                        dialog.setTitle("Alert!")
                                        dialog.setMessage("Please purchase a membership.")
                                        dialog.setPositiveButton("Buy now", DialogListenerInterface.onPositiveClickListener {
                                            var bundle = Bundle()
                                            bundle.putString("membership_type" , "upgrade")

                                            startActivityForResult(Intent(activity  , MyMembershipActivity::class.java).putExtras(bundle) ,100)
                                        })

                                        dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                                            dialog.dismiss()
                                        })
                                        dialog.show()
                                    }

                                    else if(sharedPreferences.getString(Constants.Membership_id).equals("1") ||sharedPreferences.getString(Constants.Membership_id).equals("2"))
                                    {

                                        if(can_contact.equals("true"))
                                        {
                                            /*val bundle = Bundle()
                                            bundle.putString("profile_id", profile_id)
                                            startActivityForResult(Intent(activity, ProfileContactActivity::class.java).putExtras(bundle),100)*/

                                            if(is_sender_blocked.equals("1"))
                                            {
                                                var mDialog = CustomDialog(activity)
                                                mDialog.setTitle("Blocked")
                                                mDialog.setMessage("Can't initiate chat. Because you are blocked by this user")

                                                mDialog.setPositiveButton("Ok" , DialogListenerInterface.onPositiveClickListener {
                                                    mDialog.dismiss()
                                                })

                                                mDialog.show()
                                            }

                                            else
                                            {
                                                val bundle = Bundle()
                                                bundle.putString("Username", bindingObj.userName.text.toString())
                                                bundle.putString("thread_id", "")
                                                bundle.putString("sender_id", profile_id)
                                                startActivityForResult(Intent(activity, MessageWindowActivity::class.java).putExtras(bundle) ,100)
                                            }
                                        }
                                        else
                                        {

                                            val dialog = CustomDialog(activity)
                                            dialog.setTitle("Alert!")

                                            dialog.setMessage("Your daily limit ("+ contact_count+" contacts) is over. Please upgrade your membership.")
                                            dialog.setPositiveButton("Upgrade now", DialogListenerInterface.onPositiveClickListener {

                                                dialog.dismiss()
                                                var bundle = Bundle()
                                                bundle.putString("membership_type" , "upgrade")

                                                startActivityForResult(Intent(activity  , MyMembershipActivity::class.java).putExtras(bundle) ,100)
                                            })

                                            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                                                dialog.dismiss()
                                            })
                                            dialog.show()

                                        }

                                    }

                                    else if(sharedPreferences.getString(Constants.Membership_id).equals("3"))
                                    {

                                        if(can_contact.equals("true"))
                                        {
                                            /* val bundle = Bundle()
                                             bundle.putString("profile_id", profile_id)
                                             startActivityForResult(Intent(activity, ProfileContactActivity::class.java).putExtras(bundle),100)*/

                                            if(is_sender_blocked.equals("1"))
                                            {
                                                var mDialog = CustomDialog(activity)
                                                mDialog.setTitle("Blocked")
                                                mDialog.setMessage("Can't initiate chat. Because you are blocked by this user")

                                                mDialog.setPositiveButton("Ok" , DialogListenerInterface.onPositiveClickListener {
                                                    mDialog.dismiss()
                                                })

                                                mDialog.show()
                                            }

                                            else
                                            {
                                                val bundle = Bundle()
                                                bundle.putString("Username", bindingObj.userName.text.toString())
                                                bundle.putString("thread_id", "")
                                                bundle.putString("sender_id", profile_id)
                                                startActivityForResult(Intent(activity, MessageWindowActivity::class.java).putExtras(bundle) ,100)
                                            }
                                        }
                                        else
                                        {

                                            val dialog = CustomDialog(activity)
                                            dialog.setTitle("Alert!")
                                            dialog.setMessage("Your daily limit ("+ contact_count+" contacts) is over. Please try again tomorrow.")
                                            dialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {

                                                dialog.dismiss()
                                            })

                                            dialog.show()

                                        }
                                    }
                                }
                            }
                            else
                            {
                                val dialog = CustomDialog(activity)
                                dialog.setMessage("Your email and mobile number is not verified. Please verify your details to proceed.")
                                dialog.setPositiveButton("Verify now", DialogListenerInterface.onPositiveClickListener {

                                    val bundle = Bundle()
                                    bundle.putString("type" , "emailOrPhoneVerification")
                                    val newIntent = Intent(activity, AccountVerification::class.java)
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

                    else
                    {
                        val dialog = CustomDialog(activity)
                        dialog.setTitle(activity?.resources?.getString(R.string.published_error_title))
                        dialog.setMessage(activity?.resources?.getString(R.string.published_error_message))
                        dialog.setPositiveButton("SETTINGS", DialogListenerInterface.onPositiveClickListener {

                            val newIntent = Intent(activity, SettingsActivity::class.java)
                            startActivity(newIntent)
                        })

                        dialog.setNegativeButton("Later", DialogListenerInterface.onNegetiveClickListener {
                            dialog.dismiss()
                        })
                        dialog.show()
                    }
                }
            }


            R.id.profileContactBtn->
            {

                if(isUserLogined())
                {

                    if(sharedPreferences.getString(Constants.IsProfile_published).equals("1"))
                    {

                        if (!sharedPreferences.getString(Constants.User_Role).equals("casting" ,ignoreCase = true) && sharedPreferences.getString(Constants.Profile_Complete)!!.toInt() < 60)
                        {

                            val mDialog = ProfileCompletenessAlertDialog(activity as Activity)
                            mDialog.setprogressValue(sharedPreferences.getString(Constants.Profile_Complete)!!.toInt())
                            mDialog.setMessage("Your profile should be completed at least 60%. Please complete your profile first to enjoy the benefits.")
                            mDialog.setPositiveButton("Complete Now", object : ProfileCompletenessListener.onPositiveClickListener {
                                override fun onPositiveClick()
                                {
                                    val bundle = Bundle()
                                    bundle.putString("from", "jobsDetails")
                                    bundle.putString("type", "profileComplete")
                                    bundle.putString("user_id", sharedPreferences.getString(Constants.User_id))
                                    val intent = Intent(activity as Activity, EditProfileActivity::class.java)
                                    intent.putExtras(bundle)
                                    startActivityForResult(intent, 100)
                                    mDialog.dismiss()
                                }
                            })

                            mDialog.setNegativeButton("I'll complete it later", object : ProfileCompletenessListener.onNegativeClickListener {
                                override fun onNegativeClick() {
                                    mDialog.dismiss()
                                }
                            })
                            mDialog.show()

                        }

                        else
                        {

                            if(sharedPreferences.getString(Constants.Is_Phone_Verified).equals("1") || sharedPreferences.getString(Constants.Is_Email_Verified).equals("1"))
                            {

                                if(sharedPreferences.getString(Constants.User_Role).equals("casting" ,ignoreCase = true))
                                {

                                    if (sharedPreferences.getString(Constants.Is_Documement_Verified).toString().equals("1", ignoreCase = true)
                                            && sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("1", ignoreCase = true))
                                    {

                                        if ((activity as Activity).isNetworkActiveWithMessage()) {
                                            mPresenter.getContactDetails(sharedPreferences.getString(Constants.User_id).toString(), profile_id)
                                        }

                                    }

                                    else if(sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("0" , ignoreCase = true)
                                            || sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("" , ignoreCase = true)
                                            || sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("null" , ignoreCase = true))
                                    {
                                        val dialog = CustomDialog(activity)
                                        dialog.setMessage("Your identity is not verified. It is very important to help protect our community and ensure that Dazzlerr stays safe and secure. We verify identity in several ways:\n" +
                                                "\n" +
                                                "- Government ID Verification.\n" +
                                                "- Video Verification.")
                                        dialog.setPositiveButton("Verify now", DialogListenerInterface.onPositiveClickListener {
                                            dialog.dismiss()
                                            val bundle = Bundle()
                                            bundle.putString("type", "documentVerification")
                                            val newIntent = Intent(activity, AccountVerification::class.java)
                                            newIntent.putExtras(bundle)
                                            startActivity(newIntent)
                                        })
                                        dialog.setNegativeButton("Later", DialogListenerInterface.onNegetiveClickListener {
                                            dialog.dismiss()
                                        })

                                        dialog.show()

                                    }

                                    else if (sharedPreferences.getString(Constants.Is_Documement_Verified).toString().equals("0", ignoreCase = true)
                                            && sharedPreferences.getString(Constants.Is_Documement_Submitted).toString().equals("1", ignoreCase = true)) {
                                        val dialog = CustomDialog(activity)
                                        dialog.setMessage("Your identity verification is under process. It will take 24-48 hours.")
                                        dialog.setPositiveButton("Ok",DialogListenerInterface.onPositiveClickListener {
                                            dialog.dismiss()
                                        })

                                        dialog.show()
                                    }

                                }

                                else
                                {

                                    // if member is free. Ask him to upgrade membership
                                    if(sharedPreferences.getString(Constants.Membership_id).equals("0"))
                                    {
                                        val dialog = CustomDialog(activity)
                                        dialog.setTitle("Alert!")
                                        dialog.setMessage("Please purchase a membership.")
                                        dialog.setPositiveButton("Buy now", DialogListenerInterface.onPositiveClickListener {
                                            var bundle = Bundle()
                                            bundle.putString("membership_type" , "upgrade")

                                            startActivityForResult(Intent(activity  , MyMembershipActivity::class.java).putExtras(bundle) ,100)
                                        })

                                        dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                                            dialog.dismiss()
                                        })
                                        dialog.show()
                                    }

                                    else
                                    {

                                        if (isContactPublic.equals("1"))
                                        {
                                            if ((activity as Activity).isNetworkActiveWithMessage()) {
                                                mPresenter.getContactDetails(sharedPreferences.getString(Constants.User_id).toString(), profile_id)
                                            }

                                        } else {
                                            showSnackbar("Contact details are hidden by the user.")
                                        }

                                    }

                                }
                            }
                            else
                            {
                                val dialog = CustomDialog(activity)
                                dialog.setMessage("Your email and mobile number is not verified. Please verify your details to proceed.")
                                dialog.setPositiveButton("Verify now", DialogListenerInterface.onPositiveClickListener {

                                    val bundle = Bundle()
                                    bundle.putString("type" , "emailOrPhoneVerification")
                                    val newIntent = Intent(activity, AccountVerification::class.java)
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

                    else
                    {
                        val dialog = CustomDialog(activity)
                        dialog.setTitle(activity?.resources?.getString(R.string.published_error_title))
                        dialog.setMessage(activity?.resources?.getString(R.string.published_error_message))
                        dialog.setPositiveButton("SETTINGS", DialogListenerInterface.onPositiveClickListener {

                            val newIntent = Intent(activity, SettingsActivity::class.java)
                            startActivity(newIntent)
                        })

                        dialog.setNegativeButton("Later", DialogListenerInterface.onNegetiveClickListener {
                            dialog.dismiss()
                        })
                        dialog.show()
                    }
                }
            }

            R.id.profileReportBtn->
            {
                val bundle = Bundle()
                bundle.putString("profile_id", profile_id)
                bundle.putString("type", "Profile")
                startActivity(Intent(activity, ProfileReportActivity::class.java).putExtras(bundle))
            }

            R.id.editProfileLayout->
            {
                if(sharedPreferences.getString(Constants.Is_Phone_Verified).equals("1") || sharedPreferences.getString(Constants.Is_Email_Verified).equals("1"))
                {
                    val bundle = Bundle()
                    bundle.putString("from", navigateFrom)
                    bundle.putString("user_id", profile_id)
                    bundle.putString("data", profileDataJson)

                    val intent = Intent(activity, EditProfileActivity::class.java)
                    intent.putExtras(bundle)
                    startActivityForResult(intent, 100)

                }

                else
                {

                    val dialog = CustomDialog(activity as Activity)
                    dialog.setMessage("Your email and mobile number is not verified. Please verify your details to proceed.")
                    dialog.setPositiveButton("Continue", DialogListenerInterface.onPositiveClickListener {
                        val bundle = Bundle()
                        bundle.putString("Verify now" , "emailOrPhoneVerification")
                        val newIntent = Intent(activity, AccountVerification::class.java)
                        newIntent.putExtras(bundle)
                        startActivity(newIntent)
                    })

                    dialog.setNegativeButton("Later", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })
                    dialog.show()
                }

            }

            R.id.likeBtn->
            {
                if(isUserLogined())
                {
                    //---- profile_id is other's user id whom we are watching
                    if(likeStatus==1)
                        mPresenter.likeOrDislike(sharedPreferences.getString(Constants.User_id).toString() ,profile_id , ""+0)

                    else if(likeStatus==0)
                        mPresenter.likeOrDislike(sharedPreferences.getString(Constants.User_id).toString() ,profile_id , ""+1)

                }
            }

            R.id.followBtn->
            {
                if(isUserLogined())
                {
                    //---- profile_id is other's user id whom we are watching
                    if(followStatus==1)
                        mPresenter.followOrUnfollow(sharedPreferences.getString(Constants.User_id).toString() ,profile_id , ""+0)

                    else if(followStatus==0)
                        mPresenter.followOrUnfollow(sharedPreferences.getString(Constants.User_id).toString() ,profile_id , ""+1)

                }
            }

            R.id.shortlistBtn->
            {
                if(isUserLogined())
                {
                    if(activity?.isNetworkActiveWithMessage()!!)
                    {
                        //---- profile_id is other's user id whom we are watching
                        if (shortlistStatus == 1)
                            mPresenter.shortList(sharedPreferences.getString(Constants.User_id).toString(), profile_id, "" + 0)
                        else if (shortlistStatus == 0)
                            mPresenter.shortList(sharedPreferences.getString(Constants.User_id).toString(), profile_id, "" + 1)
                    }
                }
            }

            R.id.profileImagesExploreBtn -> {
                val bundle = Bundle()
                bundle.putString("from", navigateFrom)
                bundle.putString("user_id", profile_id)
                bundle.putString("shouldShowProjects", "true")
                bundle.putString("isProfileEdit", "false")
                val intent = Intent(activity, PortfolioActivity::class.java)
                intent.putExtras(bundle)
                startActivityForResult(intent ,1000)
            }

            R.id.profileCallBtn->
            {
                if(isCallPermissionsGiven)
                    makeCall()
                else
                    askCallPermissions()
            }
        }
    }


    fun makeCall()
    {
        val headers: MutableMap<String, String> = HashMap()
        headers["location"] = sharedPreferences.getString(Constants.Current_city).toString()+", "+sharedPreferences.getString(Constants.Current_state).toString()
        headers["user_name"] = sharedPreferences.getString(Constants.User_name).toString()
        headers["user_pic"] = sharedPreferences.getString(Constants.User_pic).toString()

        val userName: String = bindingObj?.profileBinder?.user_id.toString()

        var call: Call?=null
        var callId : String?=null

        if(activity is HomeActivity)
        {
            call = (activity as HomeActivity).getSinchServiceInterface()?.callUser(userName, headers)!!
            callId = call?.callId
        }
        else
        {
            call = (activity as OthersProfileActivity).getSinchServiceInterface()?.callUser(userName, headers)!!
            callId = call?.callId
        }

        val callScreen = Intent(activity, CallScreenActivity::class.java)
        callScreen.putExtra(SinchService.CALL_ID, callId)
        callScreen.putExtra(SinchService.USERPIC, bindingObj?.profileBinder?.profile_pic)
        callScreen.putExtra(SinchService.USERNAME, bindingObj?.profileBinder?.name)
        callScreen.putExtra("isPrimaryCaller",  true)
        startActivity(callScreen)
    }


    fun getContactDetailsDialog(totalCountLeft : String , totalCount :String)
    {
        val dialog = Dialog(activity as Activity, R.style.NewDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.contact_dialog_layout)

        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT)


        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        val phoneLayout = dialog.findViewById<LinearLayout>(R.id.phoneLayout)
        val phoneNumber = dialog.findViewById<TextView>(R.id.contactPhone)
        val Email = dialog.findViewById<TextView>(R.id.contactEmail)
        val contactCountTxt = dialog.findViewById<TextView>(R.id.contactCountTxt)
        val emailLayout = dialog.findViewById<LinearLayout>(R.id.emailLayout)

        if(totalCount.isNotEmpty())
        {
            contactCountTxt.visibility = View.VISIBLE
            contactCountTxt.text = "Your daily limit used "+totalCountLeft+" of "+totalCount
        }

        if (!profilePhone.equals("")) {
            phoneNumber.text = profilePhone
        } else {
            phoneNumber.text = "Hidden by user"
        }

        if (!profileEmail.equals("")) {
            Email.text = profileEmail
        } else
        {
            Email.text = "Hidden by user"
        }

        phoneLayout.setOnClickListener {

            if (!profilePhone.equals("")) {
                try {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", profilePhone, null))
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        emailLayout.setOnClickListener {

            if (!profileEmail.equals("")) {
                try {
                    val emailIntent = Intent(Intent.ACTION_SENDTO)
                    emailIntent.data = Uri.parse("mailto:" + profileEmail)
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        dialog.show()
    }
    fun isUserLogined():Boolean
    {
        if(sharedPreferences.getString(Constants.User_id).equals(""))
        {
            val dialog =  CustomDialog(activity as Activity)
            dialog.setMessage(resources.getString(R.string.signin_txt))
            dialog.setPositiveButton("Continue", DialogListenerInterface.onPositiveClickListener {

                val bundle = Bundle()
                bundle.putString("ShouldOpenLogin"  , "true")
                val newIntent = Intent(activity, MainActivity::class.java)
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
            return true
    }


    fun imagePickerDialog()
    {
        FilePickerBuilder.instance
                .setMaxCount(1)
                .setActivityTheme(R.style.LibAppTheme)
                .enableCameraSupport(true)
                .pickPhoto(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode)
        {

            100->  apiCalling(profile_id)

            1000 ->
            {
                mImagesPresenter?.getPortfolioImages(profile_id, sharedPreferences.getString(Constants.User_id).toString(), "1" , "")
            }
            FilePickerConst.REQUEST_CODE_PHOTO -> if (resultCode == Activity.RESULT_OK && data != null)
            {
                val photoPaths: ArrayList<String> = ArrayList()
                photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA))
                Timber.e(photoPaths.toString())

                CropImage.activity(Uri.fromFile(File(photoPaths.get(0))))
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(2 ,3)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setFixAspectRatio(true)
                        .setAllowRotation(false)
                        .setAllowCounterRotation(false)
                        .setAllowFlipping(false)
                        .setAutoZoomEnabled(true)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropMenuCropButtonTitle("Submit")
                        .start(context!!, this)

            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE->
            {
                val result = CropImage.getActivityResult(data)

                if (resultCode == RESULT_OK)
                {
                    mPresenter.uploadProfilePic(profile_id , File(result.uri.path))

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(activity, "Failed to crop image. Please try again later: " + result.error, Toast.LENGTH_LONG).show()
                }

            }

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        mPresenter.cancelRetrofitRequest()
    }
}// Required empty public constructor
