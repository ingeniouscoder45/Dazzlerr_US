package com.dazzlerr_usa.views.fragments.profileemailphoneverification


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentCastingVideoProofBinding
import com.dazzlerr_usa.utilities.*
import com.dazzlerr_usa.utilities.PermissionUtils
import com.dazzlerr_usa.utilities.retrofit.ProgressRequestBody
import com.dazzlerr_usa.views.activities.accountverification.AccountVerification
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import permissions.dispatcher.*
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
@RuntimePermissions
class CastingProfileVideoProofFragment : Fragment() , View.OnClickListener , CastingProofView, ProgressRequestBody.UploadCallbacks
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObj : FragmentCastingVideoProofBinding
    lateinit var mPresenter: CastingProofPresenter

    var isPermissionsGiven = false
    var isVideoUploaded = false
    var path : String = ""
    val VIDEOCAPTUREREQUESTCODE = 3
    val VIDEOPREVIEWREQUESTCODE = 4
    lateinit var progressDialog : ProgressDialog
    lateinit var  bitmap:Bitmap
    lateinit var videoFile : File
    var mediacontroller : MediaController?= null
    var stopPosition: Int = 0
    var isVideoCaptured = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        bindingObj =  DataBindingUtil.inflate(inflater , R.layout.fragment_casting_video_proof, container, false)

        initializations()
        clickListeners()
        return  bindingObj.root
    }

    private fun initializations()
    {
        ((activity?.application) as MyApplication).myComponent.inject(this)
        bindingObj.titleLayout.titletxt.text = "Step 2: Upload video"

        // ---Videoview initializations--------
        mediacontroller = MediaController(activity)
        mediacontroller?.setAnchorView(bindingObj.videoView)
        bindingObj.videoView.setMediaController(mediacontroller)

        bindingObj.videoView.setOnPreparedListener(MediaPlayer.OnPreparedListener { mediaPlayer ->

            bindingObj.pd.visibility = View.GONE
            bindingObj.videoView.setMediaController(mediacontroller)
            mediacontroller?.setAnchorView(bindingObj.videoView)
        })

        bindingObj.videoView.setOnCompletionListener (MediaPlayer.OnCompletionListener { mediaPlayer ->

        })

        //------------------------

        mPresenter = CastingProofModel(this, this)
        progressDialog = ProgressDialog(activity)

        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setCanceledOnTouchOutside(false)

        progressDialog.setMax(100)
        progressDialog.setProgress(0)
        progressDialog.setTitle("Please wait...");

        progressDialog.setMessage("Uploading video of your identity proof")
    }

    private fun clickListeners()
    {
        bindingObj.SubmitBtn.setOnClickListener(this)
        bindingObj.CaptureBtn.setOnClickListener(this)
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.titleLayout.leftbtn.visibility = View.VISIBLE
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
       // bindingObj.identityProofImage.
        captureVideo()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode)
        {

            VIDEOCAPTUREREQUESTCODE ->
            {

                val selectedVideoUri = data?.data
                val real_path = path
                val mFile =  File(real_path)
                 //--------------------------Length of video------------------------------------
                    val fileSizeInBytes = mFile.length()
                    // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                    val fileSizeInKB = fileSizeInBytes / 1024
                    // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                    val fileSizeInMB = fileSizeInKB / 1024
                    Timber.e("File Size " + fileSizeInMB)

                if(fileSizeInMB>0)
                {
                    val bitmap = getVideoThumbnailPath(activity as Activity, mFile.getPath())
                    // Glide.with(activity as Activity).load(bitmap).into(bindingObj.identityProofImage.imageView)

                    bindingObj.SubmitBtn.isEnabled =true
                    bindingObj.CaptureBtn.text = "Recapture"
                    videoFile = mFile


                    if(isVideoCaptured)
                    bindingObj.videoView.stopPlayback()

                    bindingObj.pd.visibility = View.VISIBLE
                    bindingObj.videoView.setVideoURI(selectedVideoUri)
                    bindingObj.videoView.requestFocus()
                    bindingObj.videoView.start()
                    isVideoCaptured = true
                }

            }

        }

    }


    fun captureVideo()
    {
        val fileUri = getOutputMediaFileUriV();

        var intent =  Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,12)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra("android.intent.extras.CAMERA_FACING", 0)
        startActivityForResult(intent, VIDEOCAPTUREREQUESTCODE)
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
        var mediaStorageDir = File(context?.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "Dazzlerr");

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
        val timeStamp =  SimpleDateFormat("yyyyMMdd_HHmmss").format(date.getTime());

           val mediaFile = File(mediaStorageDir.getPath() + File.separator +"VID_"+ timeStamp + ".mp4");
            path= mediaFile.getAbsolutePath();


        Timber.e("mediaFile.getName() "+ mediaFile.getAbsolutePath());

        return mediaFile;
    }


    fun getVideoThumbnailPath(context : Context,
                                        filePath :String) : Bitmap
    {

        val btmapOptions = BitmapFactory.Options()
        btmapOptions.inSampleSize = 1;
        val thumb = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.MINI_KIND)

        return thumb

    }


    override fun onUpload()
    {

        (activity as AccountVerification).sharedPreferences.saveString(Constants.Is_Documement_Submitted , "1")
        (activity as AccountVerification).sharedPreferences.saveString(Constants.identity_video , "uploaded")
         Toast.makeText(activity , "Video has been uploaded successfully" , Toast.LENGTH_SHORT).show()
        (activity as AccountVerification).loadFirstFragment(CastingProfileVerificationSuccessfullFragment())
    }

    override fun showProgress(visiblity: Boolean)
    {

        try {

            if (visiblity)
                progressDialog.show()
            //startProgressBarAnim()

            else
                progressDialog.dismiss()
            // stopProgressBarAnim()


        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
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
        bindingObj.SubmitBtn.text = "Retry"
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

    override fun onProgressUpdate(percentage: Int)
    {
        // set current progress
        progressDialog.setProgress(percentage)
    }

    override fun onError() {

    }

    override fun onFinish() {
        // set current progress
        progressDialog.setProgress(100)
        progressDialog.dismiss()
    }


    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.SubmitBtn->
            {
                if(activity?.isNetworkActiveWithMessage()!!)
                {
                    apiCalling()
                }
            }

            R.id.leftbtn->
            {
                if(activity is AccountVerification)
                (activity as AccountVerification).finish()
            }

            R.id.CaptureBtn->
            {
                askPermissions()
            }

        }
    }


    private fun apiCalling()
    {
        if(activity?.isNetworkActive()!!)
        {
            mPresenter.uploadCastingVideoProof(sharedPreferences.getString(Constants.User_id).toString(), videoFile, "video")
        }
        else
        {
            AlertDialog.Builder(activity as Activity)
                    .setTitle("No internet Connection!")
                    .setMessage("Please connect to Mobile network or WiFi.")
                    .setPositiveButton("Retry", DialogInterface.OnClickListener { dialog, which ->
                        apiCalling()
                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    })
                    .show()
        }
    }

    override fun onPause() {

        super.onPause()
        stopPosition = bindingObj.videoView.getCurrentPosition() //stopPosition is an int
        bindingObj.videoView.pause()
    }

    override fun onResume()
    {
        super.onResume()

        bindingObj.videoView.seekTo(stopPosition)
        bindingObj.videoView.start() //Or use resume() if it doesn't work. I'm not sure
    }


}
