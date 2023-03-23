package com.ash.studios.musify.utils

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import com.ash.studios.musify.services.MusicService
import java.util.*

class Engine {
    private var context: Context? = null

    constructor()
    constructor(context: Context?) {
        this.context = context
    }

    fun startPlayer() {
        if (Instance.songs != null) Instance.uri = Uri.parse(Instance.songs!![Instance.position].path)
        if (Instance.mp != null) {
            Instance.mp!!.stop()
            Instance.mp!!.reset()
        }
        if (Instance.uri == null && Instance.uri == Uri.EMPTY) return
        Instance.mp = MediaPlayer.create(context, Instance.uri)
        Instance.mp!!.start()
        Instance.playing = true
        context!!.startService(Intent(context, MusicService::class.java).setAction(Constants.ACTION.CREATE.label))
        setCurrentPlayBack()
    }

    fun playNextSong() {
        if (Instance.mp != null) {
            if (Instance.mp!!.isPlaying) {
                Instance.mp!!.stop()
                Instance.mp!!.reset()
                if (Instance.shuffle && !Instance.repeat) Instance.position = Random().nextInt(Instance.songs!!.size - 1 + 1) else if (!Instance.shuffle && !Instance.repeat) Instance.position = (Instance.position + 1) % Instance.songs!!.size
                Instance.uri = Uri.parse(Instance.songs!![Instance.position].path)
                Instance.mp = MediaPlayer.create(context, Instance.uri)
                Instance.mp!!.setOnCompletionListener(context as OnCompletionListener?)
                Instance.mp!!.start()
            } else {
                Instance.mp!!.stop()
                Instance.mp!!.reset()
                if (Instance.shuffle && !Instance.repeat) Instance.position = Random().nextInt(Instance.songs!!.size - 1 + 1) else if (!Instance.shuffle && !Instance.repeat) Instance.position = (Instance.position + 1) % Instance.songs!!.size
                Instance.uri = Uri.parse(Instance.songs!![Instance.position].path)
                Instance.mp = MediaPlayer.create(context, Instance.uri)
                Instance.mp!!.setOnCompletionListener(context as OnCompletionListener?)
            }
        } else {
            if (Instance.shuffle && !Instance.repeat) Instance.position = Random().nextInt(Instance.songs!!.size - 1 + 1) else if (!Instance.shuffle && !Instance.repeat) Instance.position = (Instance.position + 1) % Instance.songs!!.size
            Instance.uri = Uri.parse(Instance.songs!![Instance.position].path)
            Instance.mp = MediaPlayer.create(context, Instance.uri)
            Instance.mp!!.setOnCompletionListener(context as OnCompletionListener?)
        }
        context!!.startService(Intent(context, MusicService::class.java).setAction(Constants.ACTION.CREATE.label))
        setCurrentPlayBack()
    }

    fun playPrevSong() {
        if (Instance.mp != null) {
            if (Instance.mp!!.isPlaying) {
                Instance.mp!!.stop()
                Instance.mp!!.reset()
                if (Instance.shuffle && !Instance.repeat) Instance.position = Random().nextInt(Instance.songs!!.size - 1 + 1) else if (!Instance.shuffle && !Instance.repeat) Instance.position =
                    if (Instance.position - 1 < 0) Instance.songs!!.size - 1 else Instance.position - 1
                Instance.uri = Uri.parse(Instance.songs!![Instance.position].path)
                Instance.mp = MediaPlayer.create(context, Instance.uri)
                Instance.mp!!.setOnCompletionListener(context as OnCompletionListener?)
                Instance.mp!!.start()
            } else {
                Instance.mp!!.stop()
                Instance.mp!!.reset()
                if (Instance.shuffle && !Instance.repeat) Instance.position = Random().nextInt(Instance.songs!!.size - 1 + 1) else if (!Instance.shuffle && !Instance.repeat) Instance.position =
                    if (Instance.position - 1 < 0) Instance.songs!!.size - 1 else Instance.position - 1
                Instance.uri = Uri.parse(Instance.songs!![Instance.position].path)
                Instance.mp = MediaPlayer.create(context, Instance.uri)
                Instance.mp!!.setOnCompletionListener(context as OnCompletionListener?)
            }
        } else {
            if (Instance.shuffle && !Instance.repeat) Instance.position = Random().nextInt(Instance.songs!!.size - 1 + 1) else if (!Instance.shuffle && !Instance.repeat) Instance.position =
                if (Instance.position - 1 < 0) Instance.songs!!.size - 1 else Instance.position - 1
            Instance.uri = Uri.parse(Instance.songs!![Instance.position].path)
            Instance.mp = MediaPlayer.create(context, Instance.uri)
            Instance.mp!!.setOnCompletionListener(context as OnCompletionListener?)
        }
        context!!.startService(Intent(context, MusicService::class.java).setAction(Constants.ACTION.CREATE.label))
        setCurrentPlayBack()
    }

    private fun setCurrentPlayBack() {
        object : Thread() {
            override fun run() {
                super.run()
                Utils.putCurrentList(context, Instance.songs)
                Utils.putCurrentPosition(context, Instance.position)
            }
        }.start()
    }
}