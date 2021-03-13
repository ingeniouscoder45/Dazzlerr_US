package com.dazzlerr_usa.views.activities.profileteam

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityAddTeamMemberBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.PermissionUtils
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.profileteam.models.AddOrUpdateTeamModel
import com.dazzlerr_usa.views.activities.profileteam.presenter.AddOrUpdateTeamPresenter
import com.dazzlerr_usa.views.activities.profileteam.views.AddOrUpdateTeamView
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import permissions.dispatcher.*
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@RuntimePermissions
class AddTeamMemberActivity : AppCompatActivity(), View.OnClickListener , AddOrUpdateTeamView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObj: ActivityAddTeamMemberBinding
    var isPermissionsGiven = false
    var memberImage: File? = null
    var isImageUploaded= false
    lateinit var mPresenter: AddOrUpdateTeamPresenter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this, R.layout.activity_add_team_member)
        intializations()
        clicklisteners()

    }

    fun intializations()
    {
        bindingObj.titleLayout.titletxt.text = "Add Team Member"
        (application as MyApplication).myComponent.inject(this)
        mPresenter = AddOrUpdateTeamModel(this , this)
    }

    private fun apiCalling()
    {

        if(this@AddTeamMemberActivity?.isNetworkActive()!!)
        {
            mPresenter.validateTeamMember(bindingObj.memberNameTxt.text.toString().trim() ,bindingObj.memberRoleTxt.text.toString().trim() ,isImageUploaded)
        }

        else
        {

            val dialog = CustomDialog(this@AddTeamMemberActivity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                apiCalling()
            })
            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                dialog.dismiss()
            })
            dialog.show()
        }
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
        PermissionUtils.showRationalDialog(this@AddTeamMemberActivity, R.string.permission_rationale_camera_storage, request)
    }

    @OnPermissionDenied(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE )
    fun cameraStorageDenied()
    {
        Toast.makeText(this@AddTeamMemberActivity, R.string.permission_denied_camera_storage , Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE )
    fun cameraStorageNeverAsk()
    {
        PermissionUtils.showAppSettingsDialog(this, R.string.permission_never_ask_camera_storage, Constants.REQ_CODE_APP_SETTINGS)
    }

    fun clicklisteners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.teamMemberPic.setOnClickListener(this)
        bindingObj.addTeamMemberSubmitBtn.setOnClickListener(this)
    }

    override fun onValidateTeamMember()
    {
        mPresenter.addTeamMember(sharedPreferences.getString(Constants.User_id).toString() ,bindingObj.memberNameTxt.text.toString().trim() ,bindingObj.memberRoleTxt.text.toString().trim() ,memberImage!!)

    }



    override fun onTeamAddOrUpdate()
    {
        Toast.makeText(this@AddTeamMemberActivity , "Team member has been added successfully." ,Toast.LENGTH_SHORT).show()
        setResult(100)
        finish()
    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    override fun showProgress(visiblity: Boolean)
    {

            if (visiblity)
            {
                startProgressBarAnim()
            }
            else
            {
                stopProgressBarAnim()
            }
    }

    fun startProgressBarAnim() {

        bindingObj.aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {
        bindingObj.aviProgressBar.setVisibility(View.GONE)
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


    override fun onClick(v: View?)
    {

        when (v?.id)
        {

            R.id.leftbtn ->
            {
                finish()
            }

            R.id.teamMemberPic ->
            {
                if(isPermissionsGiven)
                    imagePickerDialog()

                else
                    askPermissions()
            }

            R.id.addTeamMemberSubmitBtn->
            {
                apiCalling()
            }
        }
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
                        .start(this)

            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE->
            {
                val result = CropImage.getActivityResult(data)

                if (resultCode == Activity.RESULT_OK)
                {
                    isImageUploaded = true
                    memberImage = File(result.uri.path)

                    Glide.with(this@AddTeamMemberActivity)
                            .load(memberImage).apply(RequestOptions().centerCrop())
                            .placeholder(R.drawable.model_placeholder)
                            .into(bindingObj.teamMemberPic)

                  //  mPresenter.uploadProfilePic(user_id , File(result.uri.path))

                }
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(this@AddTeamMemberActivity, "Failed to crop image. Please try again later: " + result.error, Toast.LENGTH_LONG).show()
                }

            }

        }

    }
}
