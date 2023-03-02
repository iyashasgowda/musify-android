package com.ash.studios.musify.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.ash.studios.musify.bottomSheets.LibrarySheet
import com.ash.studios.musify.interfaces.IControl
import com.ash.studios.musify.interfaces.IService
import com.ash.studios.musify.R
import com.ash.studios.musify.Services.MusicService
import com.ash.studios.musify.activities.categories.*
import com.ash.studios.musify.utils.App
import com.ash.studios.musify.utils.Constants
import com.ash.studios.musify.utils.Constants.LIBRARY_OPTIONS
import com.ash.studios.musify.utils.Engine
import com.ash.studios.musify.utils.Instance.mp
import com.ash.studios.musify.utils.Instance.playing
import com.ash.studios.musify.utils.Instance.position
import com.ash.studios.musify.utils.Instance.songs
import com.ash.studios.musify.utils.Instance.uri
import com.ash.studios.musify.utils.Utils
import com.bumptech.glide.Glide
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper

class Library : AppCompatActivity(), View.OnClickListener, OnCompletionListener, IService, IControl {
    private lateinit var allSong: ImageView
    private lateinit var folders: ImageView
    private lateinit var albums: ImageView
    private lateinit var artists: ImageView
    private lateinit var genres: ImageView
    private lateinit var playlists: ImageView
    private lateinit var topRated: ImageView
    private lateinit var lowRated: ImageView
    private lateinit var years: ImageView
    private lateinit var optionsBtn: ImageView
    private lateinit var snipArt: ImageView
    private lateinit var snipBtn: ImageView
    private lateinit var list0: ConstraintLayout
    private lateinit var list1: ConstraintLayout
    private lateinit var list2: ConstraintLayout
    private lateinit var list3: ConstraintLayout
    private lateinit var list4: ConstraintLayout
    private lateinit var list5: ConstraintLayout
    private lateinit var list6: ConstraintLayout
    private lateinit var list7: ConstraintLayout
    private lateinit var list8: ConstraintLayout
    private lateinit var snipTitle: TextView
    private lateinit var snipArtist: TextView
    private lateinit var scrollView: ScrollView
    private lateinit var snippet: CardView
    private lateinit var engine: Engine
    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences
    private var colors = arrayOfNulls<String>(9)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.library)
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        setIds()
        setColors()
    }

    private fun setIds() {
        context = this
        engine = Engine(context)
        snippet = findViewById(R.id.snippet)
        years = findViewById(R.id.years_icon)
        albums = findViewById(R.id.albums_icon)
        genres = findViewById(R.id.genres_icon)
        folders = findViewById(R.id.folders_icon)
        artists = findViewById(R.id.artists_icon)
        allSong = findViewById(R.id.all_song_icon)
        topRated = findViewById(R.id.top_rated_icon)
        lowRated = findViewById(R.id.low_rated_icon)
        playlists = findViewById(R.id.playlists_icon)
        scrollView = findViewById(R.id.library_scroll_view)
        optionsBtn = findViewById(R.id.library_options_btn)
        snipTitle = findViewById(R.id.snip_title)
        snipBtn = findViewById(R.id.snip_play_btn)
        snipArt = findViewById(R.id.snip_album_art)
        snipArtist = findViewById(R.id.snip_artist)
        list0 = findViewById(R.id.all_songs)
        list0.setOnClickListener(this)
        list1 = findViewById(R.id.folders)
        list1.setOnClickListener(this)
        list2 = findViewById(R.id.albums)
        list2.setOnClickListener(this)
        list3 = findViewById(R.id.artists)
        list3.setOnClickListener(this)
        list4 = findViewById(R.id.genres)
        list4.setOnClickListener(this)
        list5 = findViewById(R.id.years)
        list5.setOnClickListener(this)
        list6 = findViewById(R.id.play_lists)
        list6.setOnClickListener(this)
        list7 = findViewById(R.id.top_rated)
        list7.setOnClickListener(this)
        list8 = findViewById(R.id.low_rated)
        list8.setOnClickListener(this)
        OverScrollDecoratorHelper.setUpOverScroll(scrollView)
        prefs = getSharedPreferences(LIBRARY_OPTIONS, MODE_PRIVATE)
        optionsBtn.setOnClickListener {
            val dialog = Utils.getDialog(context, R.layout.options_dg)
            val icon = dialog.findViewById<ImageView>(R.id.dialog_icon)
            val title = dialog.findViewById<TextView>(R.id.dialog_name)
            title.text = StringBuilder("Library")
            icon.setImageResource(R.drawable.ic_library)
            icon.setColorFilter(Color.parseColor(Utils.getNewColor()), PorterDuff.Mode.SRC_IN)
            val rescanMedia = dialog.findViewById<ConstraintLayout>(R.id.rescan_media)
            val listOption = dialog.findViewById<ConstraintLayout>(R.id.listing_options)
            val settings = dialog.findViewById<ConstraintLayout>(R.id.settings)
            settings.setOnClickListener {
                dialog.dismiss()
                startActivity(Intent(context, Settings::class.java))
            }
            listOption.setOnClickListener {
                dialog.dismiss()
                val librarySheet = LibrarySheet(context)
                librarySheet.show(supportFragmentManager, null)
            }
            rescanMedia.setOnClickListener {
                dialog.dismiss()
                Utils.fetchAllSongs(context)
            }
        }
        snipTitle.isSelected = true
        val savedList = Utils.getCurrentList(context)
        if (savedList != null) {
            songs = savedList
            position = Utils.getCurrentPosition(context)
            updateSnippet()
        }
        if (mp != null) mp!!.setOnCompletionListener(this)
        snipBtn.setOnClickListener {
            if (mp != null) {
                if (mp!!.isPlaying) {
                    mp!!.pause()
                    snipBtn.setImageResource(R.drawable.ic_play)
                    stopService(Intent(context, MusicService::class.java))
                } else {
                    mp!!.start()
                    playing = true
                    snipBtn.setImageResource(R.drawable.ic_pause)
                    startService(Intent(context, MusicService::class.java).setAction(Constants.ACTION.CREATE.label))
                }
            } else {
                engine.startPlayer()
                snipBtn.setImageResource(R.drawable.ic_pause)
            }
        }
        snippet.setOnClickListener { startActivity(Intent(context, Player::class.java)) }
        checkListStates()
    }

    private fun setColors() {
        val icons = arrayOf(allSong, folders, albums, artists, genres, playlists, topRated, lowRated, years)
        for (i in icons.indices) {
            val col = Utils.getNewColor()
            colors[i] = col
            icons[i].setColorFilter(Color.parseColor(col), PorterDuff.Mode.SRC_IN)
        }
    }

    private fun updateSnippet() {
        snippet.visibility = View.VISIBLE
        snipTitle.text = songs!![position].title
        snipArtist.text = songs!![position].artist
        Glide.with(applicationContext)
            .asBitmap()
            .placeholder(R.drawable.placeholder)
            .load(Utils.getAlbumArt(songs!![position].album_id))
            .into(snipArt)
        val accents = Utils.getSecondaryColors(context, Utils.getAlbumArt(songs!![position].album_id))
        snippet.setCardBackgroundColor(accents[0])
        snipTitle.setTextColor(accents[1])
        snipArtist.setTextColor(accents[1])
        snipBtn.setColorFilter(accents[1], PorterDuff.Mode.SRC_IN)
        if (mp != null && mp!!.isPlaying) snipBtn.setImageResource(R.drawable.ic_pause) else snipBtn.setImageResource(R.drawable.ic_play)
    }

    private fun checkListStates() {
        if (!prefs.getBoolean("state0", true)) list0.visibility = View.GONE
        if (!prefs.getBoolean("state1", true)) list1.visibility = View.GONE
        if (!prefs.getBoolean("state2", true)) list2.visibility = View.GONE
        if (!prefs.getBoolean("state3", true)) list3.visibility = View.GONE
        if (!prefs.getBoolean("state4", true)) list4.visibility = View.GONE
        if (!prefs.getBoolean("state5", true)) list5.visibility = View.GONE
        if (!prefs.getBoolean("state6", true)) list6.visibility = View.GONE
        if (!prefs.getBoolean("state7", true)) list7.visibility = View.GONE
        if (!prefs.getBoolean("state8", true)) list8.visibility = View.GONE
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.all_songs -> startActivity(Intent(context, AllSongList::class.java).putExtra("icon_color", colors[0]))
            R.id.folders -> startActivity(Intent(context, FolderList::class.java).putExtra("icon_color", colors[1]))
            R.id.albums -> startActivity(Intent(context, AlbumList::class.java).putExtra("icon_color", colors[2]))
            R.id.artists -> startActivity(Intent(context, ArtistList::class.java).putExtra("icon_color", colors[3]))
            R.id.genres -> startActivity(Intent(context, GenreList::class.java).putExtra("icon_color", colors[4]))
            R.id.play_lists -> startActivity(Intent(context, PlayList::class.java).putExtra("icon_color", colors[5]))
            R.id.top_rated -> startActivity(Intent(context, TRList::class.java).putExtra("icon_color", colors[6]))
            R.id.low_rated -> startActivity(Intent(context, LRList::class.java).putExtra("icon_color", colors[7]))
            R.id.years -> startActivity(Intent(context, YearList::class.java).putExtra("icon_color", colors[8]))
        }
    }

    override fun onResume() {
        super.onResume()
        (applicationContext as App).currentContext = context
        if (songs != null) updateSnippet()
        if (mp != null) mp!!.setOnCompletionListener(this)
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        engine.playNextSong()
        if (mp != null) {
            mp = MediaPlayer.create(applicationContext, uri)
            mp!!.start()
            mp!!.setOnCompletionListener(this)
        }
        Utils.putCurrentPosition(context, position)
        updateSnippet()
    }

    override fun onStartService() {}
    override fun onStopService() {
        if (mp != null) {
            mp!!.stop()
            mp!!.release()
            mp = null
        }
        snipBtn.setImageResource(R.drawable.ic_play)
        stopService(Intent(context, MusicService::class.java))
    }

    override fun onPrevClicked() {
        engine.playPrevSong()
        updateSnippet()
        startService(Intent(context, MusicService::class.java).setAction(Constants.ACTION.CREATE.label))
    }

    override fun onNextClicked() {
        engine.playNextSong()
        updateSnippet()
        startService(Intent(context, MusicService::class.java).setAction(Constants.ACTION.CREATE.label))
    }

    override fun onPlayClicked() {
        if (mp != null) mp!!.start() else {
            engine.startPlayer()
            mp!!.setOnCompletionListener(this@Library)
        }
        updateSnippet()
    }

    override fun onPauseClicked() {
        if (mp != null) mp!!.pause()
        updateSnippet()
    }
}