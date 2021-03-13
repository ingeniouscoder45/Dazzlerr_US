package com.dazzlerr_usa.views.activities.editprofile

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
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityEditProfileImageBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.PermissionUtils
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.editprofile.models.UpdateProfileImageModel
import com.dazzlerr_usa.views.activities.editprofile.presenters.UpdateProfileImagePresenter
import com.dazzlerr_usa.views.activities.editprofile.views.UpdateProfileImageView
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
class EditProfileImageActivity : AppCompatActivity() , View.OnClickListener , UpdateProfileImageView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObj: ActivityEditProfileImageBinding
    var isPermissionsGiven = false
    lateinit var mPresenter : UpdateProfileImagePresenter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_edit_profile_image)
        initializations()
        clickListeners()
    }

    fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)

        bindingObj.titleLayout.titletxt.text = "Edit Profile Image"
        mPresenter = UpdateProfileImageModel(this , this)
        Glide.with(this@EditProfileImageActivity)
                .load("https://www.dazzlerr.com/API/"+sharedPreferences.getString(Constants.User_pic)).apply(RequestOptions().centerCrop())
                .placeholder(R.color.colorLightGrey)
                .error(R.color.colorLightGrey)
                .into(bindingObj.userImage)

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
        PermissionUtils.showRationalDialog(this@EditProfileImageActivity, R.string.permission_rationale_camera_storage, request)
    }

    @OnPermissionDenied(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE )
    fun cameraStorageDenied() {
        Toast.makeText(this@EditProfileImageActivity, R.string.permission_denied_camera_storage , Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE )
    fun cameraStorageNeverAsk()
    {
        PermissionUtils.showAppSettingsDialog(this, R.string.permission_never_ask_camera_storage, Constants.REQ_CODE_APP_SETTINGS)
    }

    private fun clickListeners()
    {

        bindingObj.userImage.setOnClickListener(this)
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.editProfilePicBtn.setOnClickListener(this)

    }

    private fun apiCalling(url:File)
    {

        if(this@EditProfileImageActivity?.isNetworkActive()!!)
        {
            mPresenter.uploadProfilePic(sharedPreferences.getString(Constants.User_id).toString() ,url)
        }

        else
        {

            val dialog = CustomDialog(this@EditProfileImageActivity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                apiCalling(url)
            })
            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                dialog.dismiss()
            })
            dialog.show()
        }
    }

    override fun onProfilePicUploaded(url: String) {
        sharedPreferences.saveString(Constants.User_pic , url)

        showSnackbar("Profile pic has been updated successfully")
        Glide.with(this@EditProfileImageActivity)
                .load("https://www.dazzlerr.com/API/" + url).apply(RequestOptions().centerCrop())
                .format(DecodeFormat.PREFER_ARGB_8888)
                .into(bindingObj.userImage)
    }
    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    fun showSnackbar(message : String)
    {
        Snackbar.make(findViewById(android.R.id.content) , message , Snackbar.LENGTH_SHORT ).show()
    }

    override fun showProgress(visiblity: Boolean)
    {
        if(visiblity)
            startProgressBarAnim()

        else
            stopProgressBarAnim()
    }

    fun startProgressBarAnim() {

        bindingObj.aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {

        bindingObj.aviProgressBar.setVisibility(View.GONE)
    }


    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.editProfilePicBtn->
            {
                if(isPermissionsGiven)
                    imagePickerDialog()

                else
                    askPermissions()
            }

            R.id.leftbtn->
                finish()
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
                try {

                    val photoPaths: ArrayList<String> = ArrayList()
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA))
                    Timber.e(photoPaths.toString())

                    CropImage.activity(Uri.fromFile(File(photoPaths.get(0))))
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(2, 3)
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setFixAspectRatio(true)
                            .setAllowRotation(false)
                            .setAllowCounterRotation(false)
                            .setAllowFlipping(false)
                            .setAutoZoomEnabled(true)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setCropMenuCropButtonTitle("Submit")
                            .start(this@EditProfileImageActivity)

                }
                catch (e:Exception)
                {
                    e.printStackTrace()
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE->
            {

                try {

                    val result = CropImage.getActivityResult(data)

                    if (resultCode == Activity.RESULT_OK) {
                        if (isNetworkActiveWithMessage())
                            apiCalling(File(result.uri.path))

                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Toast.makeText(this@EditProfileImageActivity, "Failed to crop image. Please try again later: " + result.error, Toast.LENGTH_LONG).show()
                    }

            }
            catch (e:Exception)
            {
                e.printStackTrace()
            }
            }

        }

    }

}
