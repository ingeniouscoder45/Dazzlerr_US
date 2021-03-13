package com.dazzlerr_usa.views.activities.videopreview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityVideoPreviewBinding
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.view.View
import android.widget.MediaController
import android.content.Intent

class VideoPreviewActivity : AppCompatActivity() , View.OnClickListener {


    var loadURLData: Boolean? = null
    var stopPosition: Int = 0

    lateinit var bindingObj: ActivityVideoPreviewBinding
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_video_preview)

        initializations()
        setData()
        clickListeners()
    }

    fun initializations()
    {
    }

    fun getScreenOrientation(player: MediaPlayer) {
        val getOrient = windowManager.defaultDisplay
        val orientation = Configuration.ORIENTATION_UNDEFINED
        /* int videoWidth = player.getVideoWidth();
        int videoHeight = player.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        android.view.ViewGroup.LayoutParams lp = myVideoView.getLayoutParams();

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        myVideoView.setLayoutParams(lp);*/
        /* if(getOrient.getWidth()==getOrient.getHeight()){
            orientation = Configuration.ORIENTATION_SQUARE;
        } else{
            if(getOrient.getWidth() < getOrient.getHeight()){
                Log.e("Print_height_width",""+getOrient.getHeight()+getOrient.getWidth());
                orientation = Configuration.ORIENTATION_PORTRAIT;
            }else {
                Log.e("Print_height_width",""+getOrient.getHeight()+getOrient.getWidth());
                if(getOrient.getHeight()<=480){
                    ViewGroup.LayoutParams params = myVideoView.getLayoutParams();
                    params.height = 300;
                    myVideoView.setLayoutParams(params);
                }
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }*/

    }

    fun clickListeners() {
        bindingObj.doneBtn.setOnClickListener(this)
        bindingObj.cancelBtn.setOnClickListener(this)

    }

    fun setData() {

        //pd.setVisibility(View.GONE);
        bindingObj.videoView.setVideoURI(Uri.parse(intent.getStringExtra("url_link").toString()))
        bindingObj.videoView.setMediaController(MediaController(this))
        bindingObj.videoView.requestFocus()
        bindingObj.videoView.start()
        bindingObj.videoView.setOnPreparedListener(MediaPlayer.OnPreparedListener { mediaPlayer ->
            bindingObj.pd.visibility = View.GONE
            getScreenOrientation(mediaPlayer)
        })



        //-----------------------Exit-------------------------------------------------------

    }

    override fun onClick(v: View?) {

        when(v?.id)
        {
            R.id.cancelBtn->
            {
                finish()
            }

            R.id.doneBtn->
            {

                val intent = Intent()
                intent.putExtra("url_link", this@VideoPreviewActivity.intent.getStringExtra("url_link").toString())
                setResult(4, intent)
                finish()
            }
        }
    }

    public override fun onPause() {

        super.onPause()
        stopPosition = bindingObj.videoView.getCurrentPosition() //stopPosition is an int
        bindingObj.videoView.pause()
    }

    public override fun onResume()
    {
        super.onResume()

        bindingObj.videoView.seekTo(stopPosition)
        bindingObj.videoView.start() //Or use resume() if it doesn't work. I'm not sure
    }
}
