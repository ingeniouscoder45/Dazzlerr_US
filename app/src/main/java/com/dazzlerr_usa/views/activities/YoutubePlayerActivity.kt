package com.dazzlerr_usa.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dazzlerr_usa.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


lateinit var youTubePlayerView : YouTubePlayerView

class YoutubePlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_player)

        youTubePlayerView= findViewById(R.id.youtube_player_view)
        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                //val videoId = "HT5lLNI0TIU"
                youTubePlayer.loadVideo(intent.extras?.getString("video_url" , "").toString(), 0f)
            }
        })
    }



    override fun onDestroy() {
        super.onDestroy()
        if(youTubePlayerView!=null)
            youTubePlayerView.release()
    }
}
