package com.ash.studios.musify.activities.searchList

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ash.studios.musify.activities.Player
import com.ash.studios.musify.adapters.*
import com.ash.studios.musify.Interfaces.IControl
import com.ash.studios.musify.Interfaces.IService
import com.ash.studios.musify.Models.*
import com.ash.studios.musify.R
import com.ash.studios.musify.Services.MusicService
import com.ash.studios.musify.utils.*
import com.ash.studios.musify.utils.Instance.mp
import com.ash.studios.musify.utils.Instance.playing
import com.bumptech.glide.Glide
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import java.util.*

class CategorySearch : AppCompatActivity(), OnCompletionListener, IControl, IService {
    private lateinit var shuffleBtn: ImageView
    private lateinit var sequenceBtn: ImageView
    private lateinit var optionBtn: ImageView
    private lateinit var snipArt: ImageView
    private lateinit var snipPlayBtn: ImageView
    private lateinit var close: ImageView
    private lateinit var searchType: TextView
    private lateinit var snipTitle: TextView
    private lateinit var snipArtist: TextView
    private lateinit var searchText: EditText
    private lateinit var snippet: CardView
    private lateinit var rv: RecyclerView
    private var type = 0
    private lateinit var engine: Engine
    private lateinit var context: Context
    private lateinit var categoryName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search)
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        setIDs()
        close.setOnClickListener { finish() }
        snippet.setOnClickListener { startActivity(Intent(context, Player::class.java)) }
        Thread {
            snipPlayBtn.setOnClickListener {
                if (mp != null) {
                    if (mp!!.isPlaying) {
                        snipPlayBtn.setImageResource(R.drawable.ic_play)
                        mp!!.pause()
                        stopService(Intent(context, MusicService::class.java))
                    } else {
                        snipPlayBtn.setImageResource(R.drawable.ic_pause)
                        mp!!.start()
                        playing = true
                        startService(Intent(context, MusicService::class.java).setAction(Constants.ACTION.CREATE.label))
                    }
                } else {
                    engine.startPlayer()
                    snipPlayBtn.setImageResource(R.drawable.ic_pause)
                }
            }
        }.start()
    }

    private fun setIDs() {
        context = this
        engine = Engine(context)
        close = findViewById(R.id.close)
        rv = findViewById(R.id.search_rv)
        snippet = findViewById(R.id.snippet)
        searchText = findViewById(R.id.search_text)
        searchType = findViewById(R.id.search_type)
        optionBtn = findViewById(R.id.search_option)
        shuffleBtn = findViewById(R.id.search_shuffle)
        sequenceBtn = findViewById(R.id.search_sequence)
        snipTitle = snippet.findViewById(R.id.snip_title)
        snipArt = snippet.findViewById(R.id.snip_album_art)
        snipArtist = snippet.findViewById(R.id.snip_artist)
        snipPlayBtn = snippet.findViewById(R.id.snip_play_btn)
        categoryName = intent.getStringExtra("cat_name").toString()
        type = intent.getIntExtra("cat_key", 0)
        searchText.requestFocus()
        optionBtn.alpha = 0.4f
        shuffleBtn.alpha = 0.4f
        sequenceBtn.alpha = 0.4f
        snipTitle.isSelected = true
        searchType.text = categoryName
        if (Instance.songs != null) updateSnippet()
        rv.layoutManager = LinearLayoutManager(context)
        OverScrollDecoratorHelper.setUpOverScroll(rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.toString().trim { it <= ' ' } != "") {
                    when (type) {
                        0 -> {
                            val tempSongs = ArrayList<Song>()
                            for (s in Utils.getAllSongs(context)) if (s.title.lowercase(Locale.getDefault()).contains(editable.toString().lowercase(Locale.getDefault()))) tempSongs.add(s)
                            updateSongs(tempSongs)
                        }
                        1 -> {
                            val tempFolders = ArrayList<Folder>()
                            for (fo in Utils.getFolders(context)) if (fo.name.lowercase(Locale.getDefault()).contains(editable.toString().lowercase(Locale.getDefault()))) tempFolders.add(fo)
                            updateFolders(tempFolders)
                        }
                        2 -> {
                            val tempAlbums = ArrayList<Album>()
                            for (al in Utils.albums) if (al.album.lowercase(Locale.getDefault()).contains(editable.toString().lowercase(Locale.getDefault()))) tempAlbums.add(al)
                            updateAlbums(tempAlbums)
                        }
                        3 -> {
                            val tempArtists = ArrayList<Artist>()
                            for (ar in Utils.artists) if (ar.artist.lowercase(Locale.getDefault()).contains(editable.toString().lowercase(Locale.getDefault()))) tempArtists.add(ar)
                            updateArtists(tempArtists)
                        }
                        4 -> {
                            val tempGenres = ArrayList<Genre>()
                            for (g in Utils.genres) if (g.genre.lowercase(Locale.getDefault()).contains(editable.toString().lowercase(Locale.getDefault()))) tempGenres.add(g)
                            updateGenres(tempGenres)
                        }
                        5 -> {
                            val tempPlaylists = ArrayList<Playlist>()
                            for (p in Utils.getPlaylists(context)) if (p.name.lowercase(Locale.getDefault()).contains(editable.toString().lowercase(Locale.getDefault()))) tempPlaylists.add(p)
                            updatePlaylists(tempPlaylists)
                        }
                        6 -> {
                            val tempTR = ArrayList<Song>()
                            for (s in Utils.getTR(context)) if (s.title.lowercase(Locale.getDefault()).contains(editable.toString().lowercase(Locale.getDefault()))) tempTR.add(s)
                            updateSongs(tempTR)
                        }
                        7 -> {
                            val tempLR = ArrayList<Song>()
                            for (s in Utils.getLR(context)) if (s.title.lowercase(Locale.getDefault()).contains(editable.toString().lowercase(Locale.getDefault()))) tempLR.add(s)
                            updateSongs(tempLR)
                        }
                        8 -> {
                            val tempYear = ArrayList<Year>()
                            for (year in Utils.years) if (year.year.contains(editable.toString())) tempYear.add(year)
                            updateYears(tempYear)
                        }
                    }
                } else hideBtnAndAdapter()
            }
        })
    }

    private fun updateFolders(list: ArrayList<Folder>) {
        rv.adapter = FolderAdapter(context, list, null, null)
        if (rv.adapter != null && rv.adapter!!.itemCount > 0) {
            Thread {
                val folderSongs = ArrayList<Song>()
                for (folder in list) folderSongs.addAll(folder.songs)
                if (folderSongs.size > 0) shuffleBtn.post {
                    if (rv.adapter != null && rv.adapter!!.itemCount > 0) {
                        shuffleBtn.alpha = 1f
                        sequenceBtn.alpha = 1f
                        shuffleBtn.setOnClickListener { shufflePlay(folderSongs) }
                        sequenceBtn.setOnClickListener { sequencePlay(folderSongs) }
                    } else hideBtnAndAdapter()
                }
            }.start()
        } else hideBtnAndAdapter()
    }

    private fun updateYears(list: ArrayList<Year>) {
        rv.adapter = YearAdapter(context, list, null, null)
        if (rv.adapter != null && rv.adapter!!.itemCount > 0) {
            Thread {
                val yearSongs = ArrayList<Song>()
                for (year in list) yearSongs.addAll(year.songs)
                if (yearSongs.size > 0) shuffleBtn.post {
                    if (rv.adapter != null && rv.adapter!!.itemCount > 0) {
                        shuffleBtn.alpha = 1f
                        sequenceBtn.alpha = 1f
                        shuffleBtn.setOnClickListener { shufflePlay(yearSongs) }
                        sequenceBtn.setOnClickListener { sequencePlay(yearSongs) }
                    } else hideBtnAndAdapter()
                }
            }.start()
        } else hideBtnAndAdapter()
    }

    private fun updateSongs(list: ArrayList<Song>) {
        rv.adapter = AllSongAdapter(context, list, null, null)
        if (rv.adapter != null && rv.adapter!!.itemCount > 0) {
            shuffleBtn.alpha = 1f
            sequenceBtn.alpha = 1f
            shuffleBtn.setOnClickListener { shufflePlay(list) }
            sequenceBtn.setOnClickListener { sequencePlay(list) }
        } else hideBtnAndAdapter()
    }

    private fun updateAlbums(list: ArrayList<Album>) {
        rv.adapter = AlbumAdapter(context, list, null, null)
        if (rv.adapter != null && rv.adapter!!.itemCount > 0) {
            Thread {
                val albumSongs = ArrayList<Song>()
                for (album in list) albumSongs.addAll(Utils.getAlbumSongs(context, album.album_id))
                if (albumSongs.size > 0) shuffleBtn.post {
                    if (rv.adapter != null && rv.adapter!!.itemCount > 0) {
                        shuffleBtn.alpha = 1f
                        sequenceBtn.alpha = 1f
                        shuffleBtn.setOnClickListener { shufflePlay(albumSongs) }
                        sequenceBtn.setOnClickListener { sequencePlay(albumSongs) }
                    } else hideBtnAndAdapter()
                }
            }.start()
        } else hideBtnAndAdapter()
    }

    private fun updateArtists(list: ArrayList<Artist>) {
        rv.adapter = ArtistAdapter(context, list, null, null)
        if (rv.adapter != null && rv.adapter!!.itemCount > 0) {
            Thread {
                val artistSongs = ArrayList<Song>()
                for (artist in list) artistSongs.addAll(Utils.getArtistSongs(context, artist.artist_id))
                if (artistSongs.size > 0) shuffleBtn.post {
                    if (rv.adapter != null && rv.adapter!!.itemCount > 0) {
                        shuffleBtn.alpha = 1f
                        sequenceBtn.alpha = 1f
                        shuffleBtn.setOnClickListener { shufflePlay(artistSongs) }
                        sequenceBtn.setOnClickListener { sequencePlay(artistSongs) }
                    } else hideBtnAndAdapter()
                }
            }.start()
        } else hideBtnAndAdapter()
    }

    private fun updateGenres(list: ArrayList<Genre>) {
        rv.adapter = GenreAdapter(context, list, null, null)
        if (rv.adapter != null && rv.adapter!!.itemCount > 0) {
            Thread {
                val genreSongs = ArrayList<Song>()
                for (genre in list) genreSongs.addAll(Utils.getGenreSongs(context, genre.genre_id))
                if (genreSongs.size > 0) shuffleBtn.post {
                    if (rv.adapter != null && rv.adapter!!.itemCount > 0) {
                        shuffleBtn.alpha = 1f
                        sequenceBtn.alpha = 1f
                        shuffleBtn.setOnClickListener { shufflePlay(genreSongs) }
                        sequenceBtn.setOnClickListener { sequencePlay(genreSongs) }
                    } else hideBtnAndAdapter()
                }
            }.start()
        } else hideBtnAndAdapter()
    }

    private fun updatePlaylists(list: ArrayList<Playlist>) {
        rv.adapter = PlaylistAdapter(context, list, null, null)
        if (rv.adapter != null && rv.adapter!!.itemCount > 0) {
            Thread {
                val playlistSongs = ArrayList<Song>()
                for (playlist in list) playlistSongs.addAll(playlist.songs)
                if (playlistSongs.size > 0) shuffleBtn.post {
                    if (rv.adapter != null && rv.adapter!!.itemCount > 0) {
                        shuffleBtn.alpha = 1f
                        sequenceBtn.alpha = 1f
                        shuffleBtn.setOnClickListener { shufflePlay(playlistSongs) }
                        sequenceBtn.setOnClickListener { sequencePlay(playlistSongs) }
                    } else hideBtnAndAdapter()
                }
            }.start()
        } else hideBtnAndAdapter()
    }

    private fun hideBtn() {
        shuffleBtn.alpha = 0.4f
        sequenceBtn.alpha = 0.4f
        shuffleBtn.setOnClickListener(null)
        sequenceBtn.setOnClickListener(null)
    }

    private fun updateSnippet() {
        if (Instance.songs!!.size > 0) {
            snippet.visibility = View.VISIBLE
            snipTitle.text = Instance.songs!![Instance.position].title
            snipArtist.text = Instance.songs!![Instance.position].artist
            Glide.with(applicationContext)
                .asBitmap()
                .placeholder(R.drawable.placeholder)
                .load(Utils.getAlbumArt(Instance.songs!![Instance.position].album_id))
                .into(snipArt)
            val accents = Utils.getSecondaryColors(context, Utils.getAlbumArt(Instance.songs!![Instance.position].album_id))
            snippet.setCardBackgroundColor(accents[0])
            snipTitle.setTextColor(accents[1])
            snipArtist.setTextColor(accents[1])
            snipPlayBtn.setColorFilter(accents[1], PorterDuff.Mode.SRC_IN)
            if (mp != null && mp!!.isPlaying) snipPlayBtn.setImageResource(R.drawable.ic_pause) else snipPlayBtn.setImageResource(R.drawable.ic_play)
        } else snippet.visibility = View.GONE
    }

    private fun hideBtnAndAdapter() {
        rv.adapter = null
        shuffleBtn.alpha = 0.4f
        sequenceBtn.alpha = 0.4f
        shuffleBtn.setOnClickListener(null)
        sequenceBtn.setOnClickListener(null)
    }

    private fun shufflePlay(list: ArrayList<Song>) {
        Instance.songs = list
        Instance.shuffle = true
        Utils.putShflStatus(context, true)
        Instance.position = Random().nextInt(Instance.songs!!.size - 1 + 1)
        engine.startPlayer()
        mp!!.setOnCompletionListener(this@CategorySearch)
        updateSnippet()
    }

    private fun sequencePlay(list: ArrayList<Song>) {
        Instance.songs = list
        Instance.shuffle = false
        Utils.putShflStatus(context, false)
        Instance.position = 0
        engine.startPlayer()
        mp!!.setOnCompletionListener(this@CategorySearch)
        updateSnippet()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        (applicationContext as App).currentContext = context
        if (Instance.songs != null) updateSnippet()
        if (mp != null) mp!!.setOnCompletionListener(this)
        if (rv.adapter != null) {
            rv.adapter!!.notifyDataSetChanged()
            if (rv.adapter!!.itemCount == 0) hideBtn()
        }
    }

    override fun onStartService() {
        engine.startPlayer()
        updateSnippet()
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
            mp!!.setOnCompletionListener(this@CategorySearch)
        }
        updateSnippet()
    }

    override fun onPauseClicked() {
        if (mp != null) mp!!.pause()
        updateSnippet()
    }

    override fun onStopService() {
        if (mp != null) {
            mp!!.stop()
            mp!!.release()
            mp = null
        }
        snipPlayBtn.setImageResource(R.drawable.ic_play)
        stopService(Intent(context, MusicService::class.java))
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        engine.playNextSong()
        if (mp != null) {
            mp = MediaPlayer.create(applicationContext, Instance.uri)
            mp!!.start()
            mp!!.setOnCompletionListener(this)
        }
        Utils.putCurrentPosition(context, Instance.position)
        updateSnippet()
    }
}