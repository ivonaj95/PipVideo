package com.example.pipvideo

import android.app.PictureInPictureParams
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainActivity : AppCompatActivity() {

    private var exoPlayer: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var textView: TextView

    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L

    private val playbackListener = playbackStateListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playerView = findViewById(R.id.video_view)
        textView = findViewById(R.id.text_view)
        Log.d("IVONA", "onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.d("IVONA", "onStart")
        initializePlayer()
    }

    public override fun onStop() {
        super.onStop()
        Log.d("IVONA", "onStop")
        releasePlayer()
    }

    private fun releasePlayer() {
        exoPlayer?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.removeListener(playbackListener)
            exoPlayer.release()
        }
        exoPlayer = null
    }


    private fun initializePlayer() {
        Log.d("IVONA", "initializePlayer")
        if (exoPlayer == null) {
            Log.d("IVONA", "initializePlayer was null")
            exoPlayer = ExoPlayer.Builder(this)
                .build()
                .also { exoPlayer ->
                    playerView.player = exoPlayer

                    val mediaItem = MediaItem.Builder()
                        .setUri(getString(R.string.media_url_dash))
                        .setMimeType(MimeTypes.APPLICATION_MPD)
                        .build()

//                val mediaItem = MediaItem.fromUri(getString(R.string.media_url_mp4)) // mp4 or mp3
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.playWhenReady = playWhenReady
                    exoPlayer.seekTo(currentItem, playbackPosition)
                    exoPlayer.addListener(playbackListener)
                    exoPlayer.prepare()

                }
        }

    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d("IVONA", "changed state to $stateString")
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        Log.d("IVONA", "onUserLeaveHint")
        enterPictureInPictureMode(PictureInPictureParams.Builder().build())
    }

    override fun onPause() {
        super.onPause()
        Log.d("IVONA", "onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.d("IVONA", "onResume")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d("IVONA", "onNewIntent " + intent.toString())
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean, newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            textView.visibility = View.GONE
        } else {
            textView.visibility = View.VISIBLE
        }
    }
}