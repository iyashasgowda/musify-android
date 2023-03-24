package com.ash.studios.musify.services

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.app.NotificationCompat
import com.ash.studios.musify.R
import com.ash.studios.musify.activities.Player
import com.ash.studios.musify.interfaces.IControl
import com.ash.studios.musify.interfaces.IService
import com.ash.studios.musify.models.Song
import com.ash.studios.musify.utils.App
import com.ash.studios.musify.utils.Constants
import com.ash.studios.musify.utils.Constants.FOREGROUND_SERVICE
import com.ash.studios.musify.utils.Engine
import com.ash.studios.musify.utils.Instance.mp
import com.ash.studios.musify.utils.Instance.playing
import com.ash.studios.musify.utils.Instance.position
import com.ash.studios.musify.utils.Instance.songs
import com.ash.studios.musify.utils.Instance.uri
import com.ash.studios.musify.utils.Utils

class MusicService : Service(), OnCompletionListener {
    private lateinit var context: Context
    private lateinit var mediaSession: MediaSessionCompat
    override fun onCreate() {
        super.onCreate()
        context = this
        mediaSession = MediaSessionCompat(context, "MUSIFY")
        mediaSession.setMetadata(
            MediaMetadataCompat.fromMediaMetadata(
                MediaMetadata.Builder().putLong(MediaMetadata.METADATA_KEY_DURATION, -1L).build()
            )
        )
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (Constants.ACTION.CREATE.label == intent.action) {
            if (playing) notificationForPlay() else notificationForPause()
        } else if (Constants.ACTION.STOP_SERVICE.label == intent.action) {
            ((applicationContext as App).currentContext as IService?)!!.onStopService()
            stopSelf()
        } else if (Constants.ACTION.PREV.label == intent.action) {
            ((applicationContext as App).currentContext as IControl?)!!.onPrevClicked()
        } else if (Constants.ACTION.PLAY.label == intent.action) {
            playing = true
            ((applicationContext as App).currentContext as IControl?)!!.onPlayClicked()
            notificationForPlay()
        } else if (Constants.ACTION.PAUSE.label == intent.action) {
            playing = false
            ((applicationContext as App).currentContext as IControl?)!!.onPauseClicked()
            notificationForPause()
        } else if (Constants.ACTION.NEXT.label == intent.action) {
            ((applicationContext as App).currentContext as IControl?)!!.onNextClicked()
        }
        return START_NOT_STICKY
    }

    private fun notificationForPlay() {
        val mainIntent = Intent(context, Player::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val mainPending = PendingIntent.getActivity(context, 0, mainIntent, 0)
        val stopIntent = Intent(context, MusicService::class.java).setAction(Constants.ACTION.STOP_SERVICE.label)
        val stopPending = PendingIntent.getService(context, 0, stopIntent, 0)
        val prevIntent = Intent(context, MusicService::class.java).setAction(Constants.ACTION.PREV.label)
        val prevPending = PendingIntent.getService(context, 0, prevIntent, 0)
        val pauseIntent = Intent(context, MusicService::class.java).setAction(Constants.ACTION.PAUSE.label)
        val pausePending = PendingIntent.getService(context, 0, pauseIntent, 0)
        val nextIntent = Intent(context, MusicService::class.java).setAction(Constants.ACTION.NEXT.label)
        val nextPending = PendingIntent.getService(context, 0, nextIntent, 0)
        val song: Song = songs!![position]
        val count: StringBuilder = StringBuilder().append(position + 1).append("/").append(songs!!.size)
        val notification = androidx.core.app.NotificationCompat.Builder(context, App.CHANNEL_ID)
            .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_previous, "Previous", prevPending)
            .addAction(R.drawable.ic_pause, "Pause", pausePending)
            .addAction(R.drawable.ic_next, "Next", nextPending)
            .addAction(R.drawable.ic_close, "Stop", stopPending)
            .setStyle(
                NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(1, 2, 3)
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setLargeIcon(getBitmap(Utils.getAlbumArt(song.album_id)))
            .setSmallIcon(R.drawable.ic_icon)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setSubText(count)
            .setContentIntent(mainPending)
            .build()
        startForeground(FOREGROUND_SERVICE, notification)
    }

    private fun notificationForPause() {
        val mainIntent = Intent(context, Player::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val mainPending = PendingIntent.getActivity(context, 0, mainIntent, 0)
        val stopIntent = Intent(context, MusicService::class.java).setAction(Constants.ACTION.STOP_SERVICE.label)
        val stopPending = PendingIntent.getService(context, 3, stopIntent, 0)
        val prevIntent = Intent(context, MusicService::class.java).setAction(Constants.ACTION.PREV.label)
        val prevPending = PendingIntent.getService(context, 0, prevIntent, 0)
        val playIntent = Intent(context, MusicService::class.java).setAction(Constants.ACTION.PLAY.label)
        val playPending = PendingIntent.getService(context, 1, playIntent, 0)
        val nextIntent = Intent(context, MusicService::class.java).setAction(Constants.ACTION.NEXT.label)
        val nextPending = PendingIntent.getService(context, 2, nextIntent, 0)
        val song: Song = songs!!.get(position)
        val count: StringBuilder = StringBuilder().append(position + 1).append("/").append(songs!!.size)
        val notification = androidx.core.app.NotificationCompat.Builder(context, App.CHANNEL_ID)
            .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_previous, "Previous", prevPending)
            .addAction(R.drawable.ic_play, "Play", playPending)
            .addAction(R.drawable.ic_next, "Next", nextPending)
            .addAction(R.drawable.ic_close, "Stop", stopPending)
            .setStyle(
                NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(1, 2, 3)
                    .setMediaSession(mediaSession.sessionToken)
            )
            .setLargeIcon(getBitmap(Utils.getAlbumArt(song.album_id)))
            .setSmallIcon(R.drawable.ic_icon)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setSubText(count)
            .setContentIntent(mainPending)
            .build()
        startForeground(FOREGROUND_SERVICE, notification)
    }

    private fun getBitmap(uri: Uri): Bitmap {
        val bitmap: Bitmap = try {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } catch (e: Exception) {
            BitmapFactory.decodeResource(resources, R.drawable.placeholder)
        }
        return bitmap
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        Engine(context).playNextSong()
        if (mp != null) {
            mp = MediaPlayer.create(applicationContext, uri)
            mp!!.start()
            mp!!.setOnCompletionListener(this)
        }
        Utils.putCurrentPosition(context, position)
        notificationForPlay()
    }
}