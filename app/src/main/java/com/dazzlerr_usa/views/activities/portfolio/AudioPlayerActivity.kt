package com.dazzlerr_usa.views.activities.portfolio

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityAudioPlayerBinding
import timber.log.Timber
import java.io.IOException


class AudioPlayerActivity : AppCompatActivity() ,View.OnClickListener , MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener ,AudioManager.OnAudioFocusChangeListener , SeekBar.OnSeekBarChangeListener
{
    // Media Player
    private var mediaPlayer: MediaPlayer? = null


    // Handler to update UI timer, progress bar etc,.
    private val mHandler: Handler = Handler()
    private val seekForwardTime = 20000 // 20000 milliseconds

    private val seekBackwardTime = 20000 // 20000 milliseconds

    var currentSongIndex = 0
    var currentSongIndexResume = 0
    private val isShuffle = false
    private val isRepeat = false
   var songsList = ArrayList<HashMap<String, String>>()

    private var audioManager: AudioManager? = null
    //Used to pause/resume MediaPlayer
    private var resumePosition = 0

    lateinit var bindingObj  : ActivityAudioPlayerBinding

    var currentBufferingPrecentage = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this ,R.layout.activity_audio_player)
        initMediaPlayer()
        clickListeners()
    }
    private fun initMediaPlayer()
    {

        songsList = intent.getSerializableExtra("songList") as ArrayList<HashMap<String, String>>
        currentSongIndex = intent.extras?.getInt("songIndex")!!
        Timber.e("Song list size : " +currentSongIndex)


        mediaPlayer = MediaPlayer()
        //Set up MediaPlayer event listeners
        mediaPlayer!!.setOnCompletionListener(this)
        mediaPlayer!!.setOnErrorListener(this)
        mediaPlayer!!.setOnPreparedListener(this)
        mediaPlayer!!.setOnBufferingUpdateListener(this)
        mediaPlayer!!.setOnSeekCompleteListener(this)
        mediaPlayer!!.setOnInfoListener(this)

        bindingObj.songProgressBar.setOnSeekBarChangeListener(this)


        prepareSong()
    }

    fun prepareSong()
    {
        bindingObj.songNameTxt.text = songsList.get(currentSongIndex).get("songTitle")
        bindingObj.songNameTitleTxt.text = songsList.get(currentSongIndex).get("songTitle")
        bindingObj.songNameTitleTxt.isSelected= true
        // set Progress bar values
        bindingObj.songProgressBar.setProgress(0)
        bindingObj.songProgressBar.setMax(100)

        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer!!.reset()
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)

/*        //Request audio focus
        if (requestAudioFocus() == false) {
            //Could not gain focus
            stopMedia();
        }*/

        try {
            // Set the data source to the mediaFile location
            mediaPlayer!!.setDataSource(songsList[currentSongIndex].get("songPath"))
        } catch (e: IOException) {
            e.printStackTrace()

        }
        bindingObj.progressTxt.text  = "Preparing..."
        bindingObj.aviProgressBar.visibility = View.VISIBLE
        mediaPlayer!!.prepareAsync()

    }
    fun clickListeners()
    {
        bindingObj.btnPlay.setOnClickListener(this)
        bindingObj.btnPrevious.setOnClickListener(this)
        bindingObj.btnNext.setOnClickListener(this)
        bindingObj.audioCloseBtn.setOnClickListener(this)
    }
    private fun playMedia() {
        if (!mediaPlayer?.isPlaying!!)
        {
            // Updating progress bar
            updateProgressBar()

            mediaPlayer!!.seekTo(0)
            mediaPlayer?.start()
            bindingObj.btnPlay.setImageResource(R.drawable.ic_baseline_pause_24)

        }
    }

    private fun stopMedia()
    {
        if (mediaPlayer == null) return
        if (mediaPlayer?.isPlaying!!) {
            mediaPlayer!!.stop()
            resumePosition =0
            bindingObj.btnPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24)

        }
    }

    private fun pauseMedia() {
        if (mediaPlayer?.isPlaying!!) {
            mediaPlayer!!.pause()
            resumePosition = mediaPlayer?.currentPosition!!

            bindingObj.btnPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24)

        }
    }

    private fun resumeMedia()
    {
        if (!mediaPlayer?.isPlaying!!)
        {

            mediaPlayer?.seekTo(resumePosition)
            mediaPlayer?.start()
            bindingObj.btnPlay.setImageResource(R.drawable.ic_baseline_pause_24)

        }
    }

    private fun playNextSong()
    {

        // Displaying Total Duration time
        bindingObj.songTotalDurationLabel!!.text = "00.00"
        // Displaying time completed playing
        bindingObj.songCurrentDurationLabel!!.text = "00.00"

        prepareSong()
    }

    private fun playPreviousSong()
    {
        // Displaying Total Duration time
        bindingObj.songTotalDurationLabel!!.text = "00.00"
        // Displaying time completed playing
        bindingObj.songCurrentDurationLabel!!.text = "00.00"

        prepareSong()
    }
    override fun onCompletion(mp: MediaPlayer?)
    {
        stopMedia()
    }

    override fun onPrepared(mp: MediaPlayer?)
    {
        bindingObj.aviProgressBar.visibility = View.GONE
        playMedia()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {

        when (what) {
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK $extra")
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED $extra")
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN $extra")
        }
        return false
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {


        currentBufferingPrecentage = percent

        Timber.e("Is Looping: "+mp!!.isLooping)
    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {

        return false
    }

    override fun onSeekComplete(mp: MediaPlayer?) {

        Timber.e("OnSeekComplete")
        bindingObj.aviProgressBar.visibility = View.GONE
    }

    override fun onAudioFocusChange(focusChange: Int) {

        //Invoked when the audio focus of the system is updated.
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                // resume playback
                if (mediaPlayer == null) initMediaPlayer() else if (!mediaPlayer!!.isPlaying) mediaPlayer!!.start()
                mediaPlayer!!.setVolume(1.0f, 1.0f)
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer!!.isPlaying) mediaPlayer!!.stop()
                mediaPlayer!!.release()
                mediaPlayer = null
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ->             // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer!!.isPlaying) mediaPlayer!!.pause()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->             // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer!!.isPlaying) mediaPlayer!!.setVolume(0.1f, 0.1f)
        }
    }


    /**
     * When user starts moving the progress handler
     */
    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromTouch: Boolean)
    {

        if(fromTouch)
        {

            val totalDuration: Int = mediaPlayer!!.getDuration()
            val currentDuration: Int = progressToTimer(progress, totalDuration)

            // Displaying Total Duration time
            bindingObj.songTotalDurationLabel!!.text = "" + milliSecondsToTimer(totalDuration.toLong())
            // Displaying time completed playing
            bindingObj.songCurrentDurationLabel!!.text = "" + milliSecondsToTimer(currentDuration.toLong())

        }
    }

    /**
     * When user stops moving the progress hanlder
     */
    override fun onStopTrackingTouch(seekBar: SeekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask)
        val totalDuration: Int = mediaPlayer!!.getDuration()
        val currentPosition: Int = progressToTimer(seekBar.progress, totalDuration)


        bindingObj.progressTxt.text=  "Buffering"
        bindingObj.aviProgressBar.visibility= View.VISIBLE
        // forward or backward to certain seconds
        mediaPlayer!!.seekTo(currentPosition)

        // update timer progress again
        updateProgressBar()
    }


    /**
     * Update timer on seekbar
     */
    fun updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100)
    }

    /**
     * Background Runnable thread
     */
    private val mUpdateTimeTask: Runnable = object : Runnable {
        override fun run() {

            if(mediaPlayer!!.isPlaying) {
                val totalDuration: Int = mediaPlayer!!.duration
                val currentDuration: Int = mediaPlayer!!.currentPosition

                // Displaying Total Duration time
                bindingObj.songTotalDurationLabel!!.text = "" + milliSecondsToTimer(totalDuration.toLong())
                // Displaying time completed playing
                bindingObj.songCurrentDurationLabel!!.text = "" + milliSecondsToTimer(currentDuration.toLong())

                // Updating progress bar
                val progress = getProgressPercentage(currentDuration.toLong(), totalDuration.toLong()) as Int
                //Log.d("Progress", ""+progress);
                bindingObj.songProgressBar!!.progress = progress

            }
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100)
        }
    }




    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     */
    fun milliSecondsToTimer(milliseconds: Long): String? {
        var finalTimerString = ""
        var secondsString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        // Add hours if there
        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        // Prepending 0 to seconds if it is one digit
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        finalTimerString = "$finalTimerString$minutes:$secondsString"

        // return timer string
        return finalTimerString
    }

    /**
     * Function to get Progress percentage
     * @param currentDuration
     * @param totalDuration
     */
    fun getProgressPercentage(currentDuration: Long, totalDuration: Long): Int {
        var percentage = 0.toDouble()
        val currentSeconds: Long = (currentDuration / 1000) as Long
        val totalSeconds: Long = (totalDuration / 1000) as Long

        // calculating percentage
        percentage = currentSeconds.toDouble() / totalSeconds * 100

        // return percentage
        return percentage.toInt()
    }

    /**
     * Function to change progress to timer
     * @param progress -
     * @param totalDuration
     * returns current duration in milliseconds
     */
    fun progressToTimer(progress: Int, totalDuration: Int): Int {
        var mTotalDuration = totalDuration
        var currentDuration = 0
        mTotalDuration = (totalDuration / 1000)
        currentDuration = (progress.toDouble() / 100 * mTotalDuration).toInt()

        // return current duration in milliseconds
        return currentDuration * 1000
    }

    override fun onClick(v: View?) {

        when(v?.id)
        {

            R.id.audioCloseBtn->
            {
                finish()
            }
            R.id.btnPlay->
            {
                if(mediaPlayer!=null)
                {
                    if(mediaPlayer!!.isPlaying)
                        pauseMedia()

                    else if(resumePosition!=0)
                    {
                        resumeMedia()
                    }

                    else
                        playMedia()
                }

            }

            R.id.btnPrevious->
            {
                if(currentSongIndex > 0){
                    currentSongIndex -= 1
                    playPreviousSong()
                }else{
                    // play last song
                    currentSongIndex = songsList.size - 1
                    playPreviousSong()
                }
            }

            R.id.btnNext->
            {
                // check if next song is there or not
                if(currentSongIndex < (songsList.size - 1)){

                    currentSongIndex += 1
                    playNextSong()
                }else{

                    currentSongIndex = 0
                    playNextSong()
                }
            }
        }
    }
    override fun onDestroy() {
        mHandler.removeCallbacks(mUpdateTimeTask)
        super.onDestroy()
        if (mediaPlayer != null) {
            stopMedia()
            mediaPlayer!!.release()

        }
    }

}