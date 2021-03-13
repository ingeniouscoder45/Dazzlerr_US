package com.dazzlerr_usa.views.fragments.profileemailphoneverification


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentCastingImageProofBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.PermissionUtils
import com.dazzlerr_usa.utilities.retrofit.ProgressRequestBody
import com.dazzlerr_usa.views.activities.accountverification.AccountVerification
import com.google.android.material.snackbar.Snackbar
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import permissions.dispatcher.*
import timber.log.Timber
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
@RuntimePermissions
class CastingProfleImageProofFragment : Fragment() , View.OnClickListener , CastingProofView, ProgressRequestBody.UploadCallbacks
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObj : FragmentCastingImageProofBinding
    lateinit var mPresenter: CastingProofPresenter
    var imageUri: Uri? = null
    var path = ""
    var isPermissionsGiven = false
    var isImageUploaded = false
    lateinit var image: File
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        bindingObj =  DataBindingUtil.inflate(inflater , R.layout.fragment_casting_image_proof, container, false)

        initializations()
        clickListeners()
        return  bindingObj.root
    }

    private fun initializations()
    {
        ((activity?.application) as MyApplication).myComponent.inject(this)
        bindingObj.titleLayout.titletxt.text = "Upload identity proof"

        mPresenter = CastingProofModel(this, this)
    }

    private fun clickListeners()
    {
        bindingObj.nextBtn.setOnClickListener(this)
        bindingObj.captureBtn.setOnClickListener(this)
        bindingObj.identityProofImage.setOnClickListener(this)
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
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
        //imagePickerDialog()
        imageOptionSelectorDialog()
    }

    @OnShowRationale(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE )
    fun cameraStorageRationale(request: PermissionRequest) {
        PermissionUtils.showRationalDialog(activity as Activity, R.string.permission_rationale_camera_storage, request)
    }

    @OnPermissionDenied(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE )
    fun cameraStorageDenied() {
        Toast.makeText(activity as Activity, R.string.permission_denied_camera_storage , Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE )
    fun cameraStorageNeverAsk()
    {
        PermissionUtils.showAppSettingsDialog(this, R.string.permission_never_ask_camera_storage, Constants.REQ_CODE_APP_SETTINGS)
    }
    fun imagePickerDialog()
    {
        FilePickerBuilder.instance
                .setMaxCount(1)
                .setActivityTheme(R.style.LibAppTheme)
                .enableCameraSupport(false)
                .pickPhoto(this)
    }


    fun captureImage()
    {
        val fileUri = getOutputMediaFileUriV();

        var intent =  Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        //intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,12)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1)
        startActivityForResult(intent, 1111)
    }

    //----------For video
    /** Create a file Uri for saving an image or video */
    private  fun getOutputMediaFileUriV() : Uri
    {
        return FileProvider.getUriForFile(activity as Activity, "com.dazzlerr" + ".provider",getOutputMediaFileV())
    }


    /** Create a File for saving an image or video */
    private  fun getOutputMediaFileV():File {

        // Check that the SDCard is mounted
        var mediaStorageDir = File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Dazzlerr");

        // Create the storage directory(MyCameraVideo) if it does not exist
        if (! mediaStorageDir.exists()){

            if (! mediaStorageDir.mkdirs())
            {

                Toast.makeText(activity, "Failed to create directory Dazzlerr.",Toast.LENGTH_LONG).show();

                Timber.e("Dazzlerr "+"Failed to create directory Dazzlerr.")
                val file = File("")
                return file
            }
        }

        val date= java.util.Date()
        val timeStamp =  SimpleDateFormat("yyyyMMdd_HHmmss").format(date.getTime())

        val mediaFile = File(mediaStorageDir.getPath() + File.separator +"IMG_"+ timeStamp + ".jpg")
        path= mediaFile.getAbsolutePath()


        Timber.e("mediaFile.getName() "+ mediaFile.getAbsolutePath());

        return mediaFile;
    }




    private fun imageOptionSelectorDialog()
    {
        val dialog = Dialog(activity as Activity , R.style.Theme_Dialog)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.image_option_dialog_layout)

        val captureVideoBtn = dialog.findViewById<TextView>(R.id.captureImageBtn)
        val pickFromGalleryBtn = dialog.findViewById<TextView>(R.id.pickFromGalleryBtn)
        val dialogCancelBtn = dialog.findViewById<TextView>(R.id.dialogCancelBtn)

        captureVideoBtn.setOnClickListener {
            dialog.dismiss()
            captureImage()

        }

        pickFromGalleryBtn.setOnClickListener {

            imagePickerDialog()
            dialog.dismiss()
        }

        dialogCancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode)
        {

            1111->
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    CropImage.activity(Uri.fromFile(File(path)))
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(3 ,2)
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
            }

            FilePickerConst.REQUEST_CODE_PHOTO ->
            if (resultCode == Activity.RESULT_OK && data != null)
            {
                val photoPaths: ArrayList<String> = ArrayList()
                photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA))
                Timber.e(photoPaths.toString())

                CropImage.activity(Uri.fromFile(File(photoPaths.get(0))))
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(3 ,2)
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
                    image = File(result.uri.path)
                    bindingObj.nextBtn.isEnabled = true
                    bindingObj.captureBtn.text = "Recapture"
                    Glide.with(activity as Activity)
                            .load(image).apply(RequestOptions().centerCrop())
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .into(bindingObj.identityProofImage)
                }

                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                {
                    Toast.makeText(activity, "Failed to crop image. Please try again later: " + result.error, Toast.LENGTH_LONG).show()
                }

            }

        }

    }


    override fun onUpload()
    {
        bindingObj.nextBtn.visibility = View.VISIBLE
        showSnackbar("Id proof has been uploaded successfully")
        isImageUploaded = true

        val fragment = CastingProfileVideoProofFragment()
        (activity as AccountVerification).loadFirstFragment(fragment)


    }

    override fun showProgress(visiblity: Boolean)
    {
        if(visiblity)
            startProgressBarAnim()

        else
            stopProgressBarAnim()
    }

    override fun onFinish() {
        // Not in use here
    }

    override fun onProgressUpdate(percentage: Int) {
        // Not in use here
    }

    override fun onError() {
        // Not in use here
    }

    fun startProgressBarAnim() {

        bindingObj.aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {
        bindingObj.aviProgressBar.setVisibility(View.GONE)
    }

    override fun onFailed(message: String)
    {
        try {
            showSnackbar(message)
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    fun showSnackbar(message : String)
    {
        Snackbar.make((activity as Activity).findViewById(android.R.id.content) , message , Snackbar.LENGTH_SHORT ).show()
    }

    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.nextBtn->
            {

                mPresenter.uploadCastingImageProof(sharedPreferences.getString(Constants.User_id).toString() , image , "image")

/*                if(isImageUploaded)
                {
                    if (activity is AccountVerification)
                    {

                    }
                }
                else
                {
                    showSnackbar("Please capture any valid identity proof first to proceed.")
                }*/

            }

            R.id.captureBtn->
            {
                if(isPermissionsGiven)
                    imageOptionSelectorDialog()

                else
                    askPermissions()
            }

            R.id.leftbtn-> activity?.finish()
        }
    }
}
