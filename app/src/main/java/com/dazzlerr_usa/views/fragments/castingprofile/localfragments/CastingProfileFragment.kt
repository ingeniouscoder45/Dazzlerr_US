package com.dazzlerr_usa.views.fragments.castingprofile.localfragments


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentCastingProfileBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.PermissionUtils
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.utilities.imageslider.ImagePreviewActivity
import com.dazzlerr_usa.utilities.imageslider.PreviewFile
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.activities.OthersProfileActivity
import com.dazzlerr_usa.views.activities.accountverification.AccountVerification
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.activities.editcastingprofile.EditCastingProfileActivity
import com.dazzlerr_usa.views.fragments.castingprofile.models.CastingProfileModel
import com.dazzlerr_usa.views.fragments.castingprofile.pojo.CastingProfilePojo
import com.dazzlerr_usa.views.fragments.castingprofile.presenters.CastingProfilePresenter
import com.dazzlerr_usa.views.fragments.castingprofile.views.CastingProfileView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.views.activities.addjob.AddOrEditJobActivity
import com.dazzlerr_usa.views.activities.myJobs.MyJobsActivity
import com.dazzlerr_usa.views.activities.portfolio.addproject.PortfolioProjectActivity
import com.dazzlerr_usa.views.activities.talentviews.TalentViewsActivity
import com.dazzlerr_usa.views.fragments.castingprofile.adapters.CastingMyJobsAdapter
import com.dazzlerr_usa.views.fragments.castingprofile.adapters.CastingProjectsAdapter
import com.dazzlerr_usa.views.fragments.castingprofile.pojo.CastingMyJobPojo
import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioProjectsPojo
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import permissions.dispatcher.*
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */

@RuntimePermissions
class CastingProfileFragment : Fragment() , View.OnClickListener  , CastingProfileView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var mPresenter: CastingProfilePresenter
    lateinit var bindingObj: FragmentCastingProfileBinding
    var imageList = ArrayList<PreviewFile>()
    var navigateFrom = ""
    var profile_id:String = ""
    var profileDataJson= ""
    var isPermissionsGiven = false

    var likeStatus = 0
    var likesCount = 0
    var followCount = 0
    var followStatus = 0

    val PAGE_START = 1
    private var currentPage = PAGE_START
    private var isLoading = false
    var mProjectsAdapter:  CastingProjectsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {

        // Inflate the layout for this fragment
        bindingObj =  DataBindingUtil.inflate(inflater ,R.layout.fragment_casting_profile, container, false)
        initializations()
        clickListeners()
        pagination()
        apiCalling(profile_id)
        //-----------Calling get active jobs api----
        mPresenter?.getMyJobs(profile_id , "1" ,"1")
        //-----------Calling get previous projects api
        mPresenter?.getPreviousProjects(profile_id , currentPage.toString())

        return bindingObj.root

    }

    private fun initializations()
    {

        ((activity?.application) as MyApplication).myComponent.inject(this)

        profile_id = arguments?.getString("user_id" , "").toString()

        //-----Checking if user is coming from talent screen or dashboard screen
        navigateFrom = if(profile_id.equals(sharedPreferences.getString(Constants.User_id).toString())) "dashboard" else "othersFragment"

        //Timber.e("NavigateFrom"+ profile_id)

        //----------------Watching other user profile
        if (navigateFrom.equals("othersFragment", ignoreCase = true))
        {
            bindingObj.editProfileLayout.visibility = View.GONE
            bindingObj.editProfilePicBtn.visibility = View.GONE
            bindingObj.likeFollowLayout.visibility = View.VISIBLE
            bindingObj.profileContactLayout.visibility = View.GONE
            bindingObj.addNewProjectBtn.visibility = View.GONE
            bindingObj.castingDashviewActiveJobsBtn.visibility = View.GONE
        }
        else //----------------Watching own profile
        {

            bindingObj.editProfileLayout.visibility = View.VISIBLE
            bindingObj.editProfilePicBtn.visibility = View.VISIBLE
            bindingObj.addNewProjectBtn.visibility = View.VISIBLE
            bindingObj.likeFollowLayout.visibility = View.GONE
            bindingObj.profileContactLayout.visibility = View.GONE
            bindingObj.castingDashviewActiveJobsBtn.visibility = View.VISIBLE

            if(activity is HomeActivity)
            {
                (activity as HomeActivity).titleSettings(ContextCompat.getColor(context!!.applicationContext, R.color.appColor), true, resources.getColor(R.color.colorWhite), resources.getColor(R.color.colorWhite), resources.getColor(R.color.colorWhite), "Profile", "", R.drawable.ic_share)

                (activity as HomeActivity).setBottomNavigationMenu(true, false, false, false, false)
            }

        }

        mPresenter = CastingProfileModel(activity as Activity, this)

        bindingObj.activeJobsRecycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        bindingObj.projectsRecyclerView.layoutManager = LinearLayoutManager(activity)
        mProjectsAdapter = CastingProjectsAdapter(activity as Activity , ArrayList() , this ,!navigateFrom.equals("othersFragment",ignoreCase = true))
        bindingObj.projectsRecyclerView.adapter = mProjectsAdapter
    }

    private fun pagination()
    {
        bindingObj.projectsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleThreshold = 2
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val lastItem = layoutManager!!.findLastCompletelyVisibleItemPosition()
                val currentTotalCount = layoutManager.itemCount
                if (currentTotalCount <= lastItem + visibleThreshold && !isLoading)
                {
                    isLoading = true
                    mPresenter.getPreviousProjects(profile_id  , currentPage.toString())
                }
            }
        })
    }

    private fun clickListeners()
    {
        bindingObj.userImage.setOnClickListener(this)
        bindingObj.editProfileLayout.setOnClickListener(this)
        bindingObj.editProfilePicBtn.setOnClickListener(this)
        bindingObj.profileContactBtn.setOnClickListener(this)
        bindingObj.profileMessagesBtn.setOnClickListener(this)
        bindingObj.profileReportBtn.setOnClickListener(this)
        bindingObj.likeBtn.setOnClickListener(this)
        bindingObj.followBtn.setOnClickListener(this)
        bindingObj.castingDashviewActiveJobsBtn.setOnClickListener(this)
        bindingObj.addNewProjectBtn.setOnClickListener(this)
        bindingObj.castingActiveJobsBtn.setOnClickListener(this)
        bindingObj.castingCompletedJobsBtn.setOnClickListener(this)
        bindingObj.castingViewsBtn.setOnClickListener(this)
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
    fun cameraStorageRationale(request: PermissionRequest) {
        PermissionUtils.showRationalDialog(activity as Activity, R.string.permission_rationale_camera_storage, request)
    }

    @OnPermissionDenied(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE )
    fun cameraStorageDenied()
    {
        Toast.makeText(activity as Activity, R.string.permission_denied_camera_storage , Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE )
    fun cameraStorageNeverAsk()
    {
        PermissionUtils.showAppSettingsDialog(this, R.string.permission_never_ask_camera_storage, Constants.REQ_CODE_APP_SETTINGS)
    }

    private fun apiCalling(user_id: String)
    {
        if(activity?.isNetworkActive()!!)
        {
            mPresenter?.getProfile(user_id , sharedPreferences.getString(Constants.User_id).toString())
        }
        else
        {
            val dialog= CustomDialog(activity as Activity)
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




    override fun onGetProfileSuccess(model: CastingProfilePojo)
    {

        if(model.data?.size!=0)
        {

            bindingObj.profileMainLayout.visibility = View.VISIBLE
            bindingObj.profileBinder = model.data?.get(0)
            bindingObj.executePendingBindings()

            Glide.with(activity as Activity)
                    .load("https://www.dazzlerr.com/API/" + model.data?.get(0)?.profile_pic).apply(RequestOptions().centerCrop())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .into(bindingObj.userImage)

            likeStatus  = model.data?.get(0)?.is_liked as Int
            likeDislikeHandler()
            followStatus  = model.data?.get(0)?.is_followed as Int
            followUnfollowHandler()

           /* Glide.with(activity as Activity)
                    .load("https://www.dazzlerr.com/API/" + model.data?.get(0)?.profile_pic).apply(RequestOptions().centerCrop())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .into(bindingObj.profileBannerImage)*/

            if(activity is OthersProfileActivity)
                (activity as OthersProfileActivity).CUST_ID = model.data?.get(0)?.cust_id.toString()

            else if(activity is HomeActivity)
                (activity as HomeActivity).CUST_ID = model.data?.get(0)?.cust_id.toString()


            imageList.add(PreviewFile(model.data?.get(0)?.profile_pic, false ,false ,"" ,"" ,"" ,0 ,0 ,"" ,""))

            val gson = GsonBuilder().create()
            profileDataJson = gson.toJson(model)

            if( model.data?.get(0)?.user_id?.toString().equals(sharedPreferences.getString(Constants.User_id).toString())!! )
            {
                sharedPreferences.saveString(Constants.User_pic, model.data?.get(0)?.profile_pic.toString())
                sharedPreferences.saveString(Constants.User_Role, model.data?.get(0)?.user_role.toString())
                sharedPreferences.saveString(Constants.User_Phone, model.data?.get(0)?.phone.toString())
                sharedPreferences.saveString(Constants.Current_city, model.data?.get(0)?.city.toString())
                sharedPreferences.saveString(Constants.Current_state, model.data?.get(0)?.state_name.toString())
                sharedPreferences.saveString(Constants.User_name, model.data?.get(0)?.name.toString())
                sharedPreferences.saveString(Constants.identity_video, model.data?.get(0)?.identity_video.toString())
                sharedPreferences.saveString(Constants.identity_proof, model.data?.get(0)?.identity_proof.toString())
                sharedPreferences.saveString(Constants.CASTING_REPRESENTER, model.data?.get(0)?.representer!!)
                sharedPreferences.saveString(Constants.Is_Email_Verified , ""+model.data?.get(0)?.email_isverified)// 0 or 1 format
                sharedPreferences.saveString(Constants.Is_Phone_Verified , ""+model.data?.get(0)?.phone_isverified) // 0 or 1 format
                sharedPreferences.saveString(Constants.Is_Documement_Verified , ""+model.data?.get(0)?.is_document_verified) // 0 or 1 format
                sharedPreferences.saveString(Constants.Is_Documement_Submitted , ""+model.data?.get(0)?.is_document_submitted) // 0 or 1 format
                sharedPreferences.saveString(Constants.IsProfile_published , ""+ model.data?.get(0)?.is_published)
            }
        }
    }

    override fun onFollowOrUnfollow(status: String)
    {
        followStatus = status.toInt()

        if(followStatus==1)
        {
            try
            {
                followCount =(followCount+1)
               // bindingObj.followCountTxt.text= followCount.toString()
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
                //bindingObj.followCountTxt.text= followCount.toString()
            }

            catch (e : Exception)
            {
                e.printStackTrace()
            }
        }
        followUnfollowHandler()
    }

    override fun onGetActiveJobsSuccess(model: CastingMyJobPojo)
    {

        if(model.data?.size!=0)
            bindingObj.activeJobsRecycler.adapter = CastingMyJobsAdapter(activity as Activity , model.data!! , this ,!navigateFrom.equals("othersFragment",ignoreCase = true))
        else
            bindingObj.castingDashActiveJobsLayout.visibility = View.GONE
    }

    fun addItem( list : MutableList<PortfolioProjectsPojo.DataBean>)
    {
        if (currentPage != PAGE_START)
            mProjectsAdapter?.removeLoading()

        mProjectsAdapter?.addAll(list)
        mProjectsAdapter?.addLoading()
    }

    override fun onGetProjectsSuccess(model: PortfolioProjectsPojo)
    {
        if (model.data?.size!=0)
        {
            if(currentPage==PAGE_START)
            {
                mProjectsAdapter?.addAll(model.data!!)
                mProjectsAdapter?.addLoading()
            }
            else
                addItem(model.data!!)

            currentPage++
            isLoading = false
            bindingObj.emptyLayout.visibility = View.GONE
        }
        else
        {
            if(currentPage!=PAGE_START)
                mProjectsAdapter?.removeLoading()

            else if(currentPage==PAGE_START)
            {
                bindingObj.emptyLayout.visibility = View.VISIBLE
            }
        }

    }

    fun deleteProject(project_id:String , position:Int)
    {
        mPresenter.deleteProject(profile_id ,project_id ,position)
    }

    override fun onProjectDelete(position: Int)
    {
        mProjectsAdapter?.removeItem(position)
        showSnackbar("Project has been deleted successfully.")
    }


    override fun onLikeOrDislike(status: String)
    {
       // Timber.e("Likes Count "+bindingObj.profileBinder?.likes_count)
        likeStatus = status.toInt()

        if(likeStatus==1) {
            try {
                likesCount = (likesCount + 1)
                //bindingObj.likesCountTxt.text = likesCount.toString()

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        else
        {
            try {
                likesCount = (likesCount - 1)
                //bindingObj.likesCountTxt.text = likesCount.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        likeDislikeHandler()
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



    override fun onProfilePicUploaded(url: String)
    {

        sharedPreferences.saveString(Constants.User_pic , url)
        imageList.clear()
        imageList.add(
        PreviewFile(url, false , false ,"", "" ,"",0,0 ,"" , ""))

        showSnackbar("Profile pic has been updated successfully")
        Glide.with(activity as Activity)
                .load("https://www.dazzlerr.com/API/" + url).apply(RequestOptions().centerCrop())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .into(bindingObj.userImage)

       /* Glide.with(activity as Activity)
                .load("https://www.dazzlerr.com/API/" +url).apply(RequestOptions().centerCrop())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .into(bindingObj.profileBannerImage)*/

    }

    override fun onFailed(message: String)
    {
        isLoading =false
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

                    intent.putExtra(ImagePreviewActivity.IMAGE_LIST,
                            imageList)
                    intent.putExtra(ImagePreviewActivity.CURRENT_ITEM, 0)
                    context?.startActivity(intent)
                }
            }

            R.id.editProfilePicBtn->
            {
                if(isPermissionsGiven)
                    imagePickerDialog()

                else
                    askPermissions()
            }

            R.id.editProfileLayout->
            {
                Timber.e(sharedPreferences.getString(Constants.Is_Documement_Submitted))
                if(sharedPreferences.getString(Constants.Is_Phone_Verified).equals("1") || sharedPreferences.getString(Constants.Is_Email_Verified).equals("1"))
                {

                    val bundle = Bundle()
                    bundle.putString("from", navigateFrom)
                    bundle.putString("user_id", profile_id)
                    bundle.putString("data", profileDataJson)

                    if(profileDataJson!=null && !profileDataJson.equals(""))
                    {
                        val intent = Intent(activity, EditCastingProfileActivity::class.java)
                        intent.putExtras(bundle)
                        startActivityForResult(intent, 102)
                    }
                }
                else
                {
                    val dialog = CustomDialog(activity as Activity)
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

            R.id.castingDashviewActiveJobsBtn->
            {
                val bundle = Bundle()
                bundle.putString("selectedTab" , "active")
                startActivity(Intent(activity as Activity , MyJobsActivity::class.java).putExtras(bundle))
            }

            R.id.addNewProjectBtn->
            {
                val intent = Intent(activity, PortfolioProjectActivity::class.java)
                startActivityForResult(intent ,100)
            }

            R.id.castingActiveJobsBtn->
            {
                if(navigateFrom.equals("dashboard",ignoreCase = true)) {
                    val bundle = Bundle()
                    bundle.putString("selectedTab", "active")
                    startActivity(Intent(activity as Activity, MyJobsActivity::class.java).putExtras(bundle))
                }

            }

            R.id.castingCompletedJobsBtn->
            {
                if(navigateFrom.equals("dashboard",ignoreCase = true)) {
                    val bundle = Bundle()
                    bundle.putString("selectedTab", "completed")
                    startActivity(Intent(activity as Activity, MyJobsActivity::class.java).putExtras(bundle))
                }
            }

            R.id.castingViewsBtn-> {
                if (navigateFrom.equals("dashboard", ignoreCase = true)) {
                    startActivity(Intent(activity as Activity, TalentViewsActivity::class.java))
                }
            }
        }
    }

    fun editJob(call_id:String)
    {
        val bundle = Bundle()
        bundle.putString("type" , "Edit")
        bundle.putString("call_id" ,call_id)
        val intent = Intent(activity , AddOrEditJobActivity::class.java)
        intent.putExtras(bundle)
        startActivityForResult(intent ,101)
    }

    fun isUserLogined():Boolean
    {
        if(sharedPreferences.getString(Constants.User_id).equals(""))
        {

            val dialog = CustomDialog(activity as Activity)
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
        //Toast.makeText(activity , "request" , Toast.LENGTH_LONG).show()
        when (requestCode)
        {

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

                if (resultCode == Activity.RESULT_OK)
                {
                    mPresenter.uploadProfilePic(profile_id , File(result.uri.path))

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(activity, "Failed to crop image. Please try again later: " + result.error, Toast.LENGTH_LONG).show()
                }
            }

            102->
            {
                apiCalling(sharedPreferences.getString(Constants.User_id).toString())
            }

            101 ->
            {
                //-----------Calling get active jobs api----
                mPresenter?.getMyJobs(profile_id , "1" ,"1")// Status 1 for active jobs
            }

           100->
            {
                if(data?.extras != null && data.extras!!.containsKey("extras"))
                {
                    mProjectsAdapter?.removeAll()
                    currentPage = PAGE_START
                    mPresenter.getPreviousProjects(profile_id ,currentPage.toString())
                }
            }

        }

    }
    override fun onDestroy() {
        super.onDestroy()
        mPresenter.cancelRetrofitRequest()
    }

}
