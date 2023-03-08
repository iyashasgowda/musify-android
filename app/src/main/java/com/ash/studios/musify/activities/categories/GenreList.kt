package com.ash.studios.musify.activities.categories

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ash.studios.musify.adapters.GenreAdapter
import com.ash.studios.musify.bottomSheets.GenresSort
import com.ash.studios.musify.interfaces.IControl
import com.ash.studios.musify.interfaces.IService
import com.ash.studios.musify.models.Song
import com.ash.studios.musify.R
import com.ash.studios.musify.Services.MusicService
import com.ash.studios.musify.activities.Player
import com.ash.studios.musify.activities.searchList.CategorySearch
import com.ash.studios.musify.utils.App
import com.ash.studios.musify.utils.Constants
import com.ash.studios.musify.utils.Engine
import com.ash.studios.musify.utils.Instance.mp
import com.ash.studios.musify.utils.Instance.playing
import com.ash.studios.musify.utils.Instance.position
import com.ash.studios.musify.utils.Instance.shuffle
import com.ash.studios.musify.utils.Instance.songs
import com.ash.studios.musify.utils.Instance.uri
import com.ash.studios.musify.utils.Utils
import com.bumptech.glide.Glide
import com.futuremind.recyclerviewfastscroll.FastScroller
import com.google.android.material.appbar.AppBarLayout
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import java.util.*

class GenreList : AppCompatActivity(), OnCompletionListener, IControl, IService {
    private lateinit var icon: ImageView
    private lateinit var shufflePlay: ImageView
    private lateinit var sequencePlay: ImageView
    private lateinit var searchBtn: ImageView
    private lateinit var optionsBtn: ImageView
    private lateinit var snippetArt: ImageView
    private lateinit var snippetPlayBtn: ImageView
    private lateinit var title: TextView
    private lateinit var notFound: TextView
    private lateinit var snippetTitle: TextView
    private lateinit var snippetArtist: TextView
    private lateinit var backToLib: ConstraintLayout
    private lateinit var appBar: AppBarLayout
    private lateinit var loader: ProgressBar
    private lateinit var snippet: CardView
    private lateinit var rv: RecyclerView
    private lateinit var fs: FastScroller
    private lateinit var engine: Engine
    private lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.songs_list)
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        setIDs()
        backToLib.setOnClickListener { finish() }
        snippet.setOnClickListener { startActivity(Intent(context, Player::class.java)) }
        searchBtn.setOnClickListener {
            if (rv.adapter != null && rv.adapter!!.itemCount > 0) startActivity(
                Intent(context, CategorySearch::class.java)
                    .putExtra("cat_key", 4).putExtra("cat_name", "Genres")
            )
        }
        Thread {
            if (rv.adapter != null && rv.adapter!!.itemCount > 0) shufflePlay.setOnClickListener {
                Thread {
                    val list = ArrayList<Song>()
                    for (genre in Utils.genres) list.addAll(Utils.getGenreSongs(context, genre.genre_id))
                    shufflePlay.post {
                        if (list.size > 0) {
                            songs = list
                            shuffle = true
                            position = Random().nextInt(songs!!.size - 1 + 1)
                            engine.startPlayer()
                            mp!!.setOnCompletionListener(this@GenreList)
                            updateSnippet()
                            Utils.putShflStatus(context, true)
                            Toast.makeText(context, "Shuffle all songs", Toast.LENGTH_SHORT).show()
                        } else Toast.makeText(context, "No songs found in any genres :(", Toast.LENGTH_SHORT).show()
                    }
                }.start()
            }
        }.start()
        Thread {
            if (rv.adapter != null && rv.adapter!!.itemCount > 0) sequencePlay.setOnClickListener {
                Thread {
                    val list = ArrayList<Song>()
                    for (genre in Utils.genres) list.addAll(Utils.getGenreSongs(context, genre.genre_id))
                    sequencePlay.post {
                        if (list.size > 0) {
                            songs = list
                            shuffle = false
                            position = 0
                            engine.startPlayer()
                            mp!!.setOnCompletionListener(this@GenreList)
                            updateSnippet()
                            Utils.putShflStatus(context, false)
                            Toast.makeText(context, "Sequence all songs", Toast.LENGTH_SHORT).show()
                        } else Toast.makeText(context, "No songs found in any genres :(", Toast.LENGTH_SHORT).show()
                    }
                }.start()
            }
        }.start()
        Thread {
            snippetPlayBtn.setOnClickListener {
                if (mp != null) {
                    if (mp!!.isPlaying) {
                        snippetPlayBtn.setImageResource(R.drawable.ic_play)
                        mp!!.pause()
                        stopService(Intent(context, MusicService::class.java))
                    } else {
                        snippetPlayBtn.setImageResource(R.drawable.ic_pause)
                        mp!!.start()
                        playing = true
                        startService(Intent(context, MusicService::class.java).setAction(Constants.ACTION.CREATE.label))
                    }
                } else {
                    engine.startPlayer()
                    snippetPlayBtn.setImageResource(R.drawable.ic_pause)
                }
            }
        }.start()
        optionsBtn.setOnClickListener {
            val dialog = Utils.getDialog(context, R.layout.options_dg)
            val dialogName = dialog.findViewById<TextView>(R.id.dialog_name)
            val dialogIcon = dialog.findViewById<ImageView>(R.id.dialog_icon)
            val rescanMedia = dialog.findViewById<ConstraintLayout>(R.id.rescan_media)
            val listingOption = dialog.findViewById<ConstraintLayout>(R.id.listing_options)
            val settings = dialog.findViewById<ConstraintLayout>(R.id.settings)
            dialogName.text = title.text
            dialogIcon.setImageDrawable(icon.drawable)
            settings.visibility = View.GONE
            rescanMedia.setOnClickListener {
                dialog.dismiss()
                rv.adapter = null
                notFound.visibility = View.GONE
                loader.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    rv.adapter = GenreAdapter(context, Utils.getGenres(context), loader, notFound)
                    if (rv.adapter == null || rv.adapter!!.itemCount == 0) hideAttributes()
                }, 10)
            }
            listingOption.setOnClickListener {
                dialog.dismiss()
                val genresSort = GenresSort(context, rv, loader, notFound)
                genresSort.show(supportFragmentManager, null)
            }
        }
    }

    private fun setIDs() {
        context = this
        engine = Engine(context)
        rv = findViewById(R.id.song_list)
        appBar = findViewById(R.id.app_bar)
        snippet = findViewById(R.id.snippet)
        notFound = findViewById(R.id.nothing_found)
        fs = findViewById(R.id.fast_song_list)
        icon = findViewById(R.id.activity_icon)
        backToLib = findViewById(R.id.lib_back)
        loader = findViewById(R.id.list_loader)
        title = findViewById(R.id.activity_title)
        searchBtn = findViewById(R.id.search_btn)
        optionsBtn = findViewById(R.id.options_btn)
        sequencePlay = findViewById(R.id.play_all_btn)
        shufflePlay = findViewById(R.id.shuffle_all_btn)
        snippetTitle = snippet.findViewById(R.id.snip_title)
        snippetArt = snippet.findViewById(R.id.snip_album_art)
        snippetArtist = snippet.findViewById(R.id.snip_artist)
        snippetPlayBtn = snippet.findViewById(R.id.snip_play_btn)
        snippetTitle.isSelected = true
        title.text = StringBuilder("Genres")
        icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_genres))
        icon.setColorFilter(Color.parseColor(intent.getStringExtra("icon_color")))
        if (songs != null) updateSnippet()
        rv.layoutManager = LinearLayoutManager(context)
        if (Utils.genres == null || Utils.genres.size == 0) Handler(Looper.getMainLooper()).postDelayed(
            { rv.adapter = GenreAdapter(context, Utils.getGenres(context), loader, notFound) },
            10
        ) else rv.adapter = GenreAdapter(context, Utils.genres, loader, notFound)
        if (rv.adapter == null || rv.adapter!!.itemCount == 0) hideAttributes()
        OverScrollDecoratorHelper.setUpOverScroll(rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
        fs.setRecyclerView(rv)
    }

    private fun hideAttributes() {
        shufflePlay.alpha = 0.4f
        sequencePlay.alpha = 0.4f
        searchBtn.alpha = 0.4f
        shufflePlay.setOnClickListener(null)
        sequencePlay.setOnClickListener(null)
        searchBtn.setOnClickListener(null)
    }

    private fun updateSnippet() {
        if (songs!!.size > 0) {
            snippet.visibility = View.VISIBLE
            snippetTitle.text = songs!![position].title
            snippetArtist.text = songs!![position].artist
            Glide.with(applicationContext)
                .asBitmap()
                .placeholder(R.drawable.placeholder)
                .load(Utils.getAlbumArt(songs!![position].album_id))
                .into(snippetArt)
            val accents = Utils.getSecondaryColors(context, Utils.getAlbumArt(songs!![position].album_id))
            snippet.setCardBackgroundColor(accents[0])
            snippetTitle.setTextColor(accents[1])
            snippetArtist.setTextColor(accents[1])
            snippetPlayBtn.setColorFilter(accents[1], PorterDuff.Mode.SRC_IN)
            if (mp != null && mp!!.isPlaying) snippetPlayBtn.setImageResource(R.drawable.ic_pause) else snippetPlayBtn.setImageResource(R.drawable.ic_play)
        } else snippet.visibility = View.GONE
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
            mp!!.setOnCompletionListener(this@GenreList)
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
        snippetPlayBtn.setImageResource(R.drawable.ic_play)
        stopService(Intent(context, MusicService::class.java))
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        (applicationContext as App).currentContext = context
        if (songs != null) updateSnippet()
        if (mp != null) mp!!.setOnCompletionListener(this)
        if (rv.adapter != null) {
            rv.adapter!!.notifyDataSetChanged()
            if (rv.adapter!!.itemCount == 0) hideAttributes()
        }
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
}