package com.dazzlerr_usa.utilities.imageslider


import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityImagePreviewBinding
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.activities.report.ProfileReportActivity
import com.dazzlerr_usa.utilities.DownloadFile
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import javax.inject.Inject


class ImagePreviewActivity : AppCompatActivity(), ImageSliderView , View.OnClickListener
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var mPresenter: ImageSliderPresenter
    lateinit var bindingObj:ActivityImagePreviewBinding
    private var mUriList: List<PreviewFile>? = null
    var slideAdapter: SlideAdapter ?=null
    var currentPosition : Int = 0


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this ,R.layout.activity_image_preview)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        initializations()
        clickListeners()
        setUpViews()
    }

    fun initializations()
    {

        mUriList = intent.getSerializableExtra(IMAGE_LIST) as List<PreviewFile>

        (application as MyApplication).myComponent.inject(this)
        // add emoji sources
        bindingObj.groupEmojiContainer?.addEmoji(R.drawable.heart_selected)

        mPresenter = ImageSliderModel(this , this)

        if(mUriList!!.isNotEmpty())
        {
            if(mUriList!!.get(0).isLikeEnabled!! || mUriList!!.get(0).isDownload_enabled!!)
            {
                bindingObj.menuBtn.visibility =View.VISIBLE
            }
            else
                bindingObj.menuBtn.visibility =View.GONE
        }
    }

    private fun clickListeners()
    {
        bindingObj.menuBtn.setOnClickListener(this)
        bindingObj.closeBtn.setOnClickListener(this)
    }

    private fun setUpViews()
    {

        slideAdapter = SlideAdapter(this, mUriList!!)
        bindingObj.vPager.adapter = slideAdapter

        currentPosition = intent.getIntExtra(CURRENT_ITEM, 0)
        bindingObj.vPager.currentItem = currentPosition

        if(mUriList!!.size==1)
            bindingObj.photosCounterTxt.visibility = View.GONE

        else
        bindingObj.photosCounterTxt.text = (bindingObj.vPager.currentItem+1).toString()+" of "+mUriList!!.size.toString()

        bindingObj.vPager.setPageTransformer(false) { page, position ->
            val normalizedPosition = Math.abs(Math.abs(position) - 1)
            page.scaleX = normalizedPosition / 2 + 0.5f
            page.scaleY = normalizedPosition / 2 + 0.5f
        }

        bindingObj.vPager.addOnPageChangeListener(object:ViewPager.OnPageChangeListener
        {
            override fun onPageSelected(position: Int)
            {
                bindingObj.photosCounterTxt.text = (position+1).toString()+" of "+mUriList!!.size.toString()
                currentPosition = position
            }

            override fun onPageScrollStateChanged(state: Int)
            {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)
            {

            }

        })
    }

    fun likeOrDislike(user_id:String ,profile_id:String , status:String, portfolio_id:String ,position:Int , likeBtn : ImageView)
    {
        if(isNetworkActiveWithMessage() && isUserLogined())
        {
            likeBtn.isEnabled = false
            mPresenter.like(user_id , profile_id ,status ,  portfolio_id ,"is_like" , position , likeBtn)
        }
    }

    fun heartOrDisheart(user_id:String ,profile_id:String , status:String, portfolio_id:String ,position:Int , heartBtn  :ImageView)
    {
        if(isNetworkActiveWithMessage()&& isUserLogined())
        {
            heartBtn.isEnabled = false
            mPresenter.heart(user_id , profile_id ,status ,  portfolio_id ,"is_heart" , position , heartBtn)
        }
    }

    override fun onLikeOrDislike(status: String, position: Int , likeBtn: ImageView)
    {

        likeBtn.isEnabled = true
        mUriList?.get(position)?.is_like = status.toInt()

        if(status.equals("1"))
        {
            mUriList?.get(position)?.total_likes = ((mUriList?.get(position)?.total_likes?.toInt()!!)+1).toString()
        }
        else
            mUriList?.get(position)?.total_likes = ( (mUriList?.get(position)?.total_likes?.toInt()!!)-1).toString()

        slideAdapter?.notifyDataSetChanged()
    }

    override fun onHeartOrDisheart(status: String, position: Int , heartBtn : ImageView)
    {

        heartBtn.isEnabled = true
        if(status.equals("1"))
        {
            try {
                bindingObj.groupEmojiContainer?.startDropping()
            }
            catch(e:Exception)
            {
                e.printStackTrace()
            }

            mUriList?.get(position)?.total_hearts = (( mUriList?.get(position)?.total_hearts?.toInt()!!)+1).toString()
        }
        else
            mUriList?.get(position)?.total_hearts = ( (mUriList?.get(position)?.total_hearts?.toInt()!!)-1).toString()

        mUriList?.get(position)?.is_heart = status.toInt()
        slideAdapter?.notifyDataSetChanged()

    }

    override fun onFailed(message: String) {

        showSnackbar(message)
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

    companion object
    {
        var IMAGE_LIST = "intent_image_item"
        var CURRENT_ITEM = "current_item"
    }


    override fun onBackPressed()
    {
        super.onBackPressed()
        setResult(1000)
        finish()
    }

    override fun onClick(v: View?)
    {

        when(v?.id)
        {

             R.id.closeBtn->
            {
                finish()
            }

            R.id.menuBtn-> {
                val popupMenu: PopupMenu = PopupMenu(this, bindingObj.menuBtn)

                if (mUriList!!.get(0).isLikeEnabled!!) {
                    popupMenu.menu.add("Report")

                    popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                        override fun onMenuItemClick(item: MenuItem?): Boolean {

                            if (isNetworkActiveWithMessage() && isUserLogined()) {
                                val bundle = Bundle()
                                bundle.putString("profile_id", mUriList?.get(currentPosition)?.profile_id)
                                bundle.putString("type", "portfolio")
                                bundle.putString("portfolio_url", "https://www.dazzlerr.com/API/" + mUriList?.get(currentPosition)?.imageURL)
                                startActivity(Intent(this@ImagePreviewActivity, ProfileReportActivity::class.java).putExtras(bundle))
                            }
                            return true

                        }
                    })
                    popupMenu.show()
                }

                else if(mUriList!!.get(0).isDownload_enabled!!)
                {

                    popupMenu.menu.add("Save to gallery")

                    popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                        override fun onMenuItemClick(item: MenuItem?): Boolean {

                            if (isNetworkActiveWithMessage() && isUserLogined()) {

                                //downloadFile("https://www.dazzlerr.com/API/" + mUriList?.get(currentPosition)?.imageURL)


                                val date= java.util.Date()
                                val timeStamp =  SimpleDateFormat("yyyyMMdd_HHmmss").format(date.getTime())


                                var image_link = "https://www.dazzlerr.com/API/" + mUriList?.get(currentPosition)?.imageURL
                                var path = ""+getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS + "IMG_"+timeStamp+".jpg")

                                val mFile = File(path)
                                mFile.delete()

                                Timber.e(path)

                                var mDownload = DownloadFile()
                                mDownload.DownloadData(
                                        Uri.parse(image_link),
                                        this@ImagePreviewActivity
                                )
                                // downloadFile(Constants.IMAGE_BASE_URL+ mList[position].video_link)

                                Toast.makeText(this@ImagePreviewActivity, "Downloading...", Toast.LENGTH_SHORT).show()

                            }
                            return true

                        }
                    })
                    popupMenu.show()
                }
            }

        }
    }


    fun downloadFile(uRl: String?) {
        val direct = File(Environment.getExternalStorageDirectory()
                .toString() + "/Dazzlerr")
        if (!direct.exists()) {
            direct.mkdirs()
        }
        val mgr: DownloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri: Uri = Uri.parse(uRl)
        val request: DownloadManager.Request = DownloadManager.Request(
                downloadUri)

        val date= java.util.Date()
        val timeStamp =  SimpleDateFormat("yyyyMMdd_HHmmss").format(date.getTime())

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Downloading..")
                .setDescription("Downloading"+ " IMG_"+ timeStamp + ".jpg")
                .setDestinationInExternalPublicDir("/Dazzlerr", "IMG_"+ timeStamp + ".jpg")
        mgr.enqueue(request)
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
                val newIntent = Intent(this@ImagePreviewActivity, MainActivity::class.java)
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
}
