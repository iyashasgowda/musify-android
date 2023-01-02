package com.ash.studios.musify.utils

import android.media.MediaPlayer
import android.net.Uri
import com.ash.studios.musify.Models.Song

object Instance {
    public var position = 0
    public var repeat = false
    public var shuffle = false
    public var playing = false
    public var uri: Uri? = null
    public var mp: MediaPlayer? = null
    public var songs: ArrayList<Song>? = null
}