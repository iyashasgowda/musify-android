package com.ash.studios.musify.bottomSheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ash.studios.musify.models.Song
import com.ash.studios.musify.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InfoSheet(private val song: Song) : BottomSheetDialogFragment() {
    private lateinit var path: TextView
    private lateinit var title: TextView
    private lateinit var track: TextView
    private lateinit var year: TextView
    private lateinit var artist: TextView
    private lateinit var album: TextView
    private lateinit var albumArtist: TextView
    private lateinit var composer: TextView
    private lateinit var close: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.info_sheet, container, false)
        setID(v)
        setSongInfo()
        return v
    }

    private fun setSongInfo() {
        path.text = if (song.path == null) "-" else song.path
        year.text = if (song.year == null) "-" else song.year
        title.text = if (song.title == null) "-" else song.title
        track.text = if (song.track == null) "-" else song.track
        album.text = if (song.album == null) "-" else song.album
        artist.text = if (song.artist == null) "-" else song.artist
        composer.text = if (song.composer == null) "-" else song.composer
        albumArtist.text = if (song.album_artist == null) "-" else song.album_artist
    }

    private fun setID(v: View) {
        path = v.findViewById(R.id.song_path)
        year = v.findViewById(R.id.song_year)
        title = v.findViewById(R.id.song_title)
        track = v.findViewById(R.id.song_track)
        album = v.findViewById(R.id.song_album)
        artist = v.findViewById(R.id.song_artist)
        close = v.findViewById(R.id.close_sheet_btn)
        composer = v.findViewById(R.id.song_composer)
        albumArtist = v.findViewById(R.id.song_album_artist)
        close.setOnClickListener { dismiss() }
    }
}