package com.ash.studios.musify.activities.categories

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ash.studios.musify.adapters.PLBunchAdapter
import com.ash.studios.musify.interfaces.IControl
import com.ash.studios.musify.interfaces.IService
import com.ash.studios.musify.models.Playlist
import com.ash.studios.musify.models.Song
import com.ash.studios.musify.R
import com.ash.studios.musify.services.MusicService
import com.ash.studios.musify.activities.Player
import com.ash.studios.musify.activities.searchList.BunchSearch
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
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import java.util.*

class PLBunchList : AppCompatActivity(), OnCompletionListener, IControl, IService {
    private lateinit var coverArt: ImageView
    private lateinit var shuffleAllBtn: ImageView
    private lateinit var sequenceAllBtn: ImageView
    private lateinit var searchBtn: ImageView
    private lateinit var optionBtn: ImageView
    private lateinit var snippetArt: ImageView
    private lateinit var snippetPlayBtn: ImageView
    private lateinit var bunchTitle: TextView
    private lateinit var goBackTo: TextView
    private lateinit var notFound: TextView
    private lateinit var snippetTitle: TextView
    private lateinit var snippetArtist: TextView
    private lateinit var goBackBtn: ConstraintLayout
    private lateinit var loader: ProgressBar
    private lateinit var snippet: CardView
    private lateinit var rv: RecyclerView
    private lateinit var fs: FastScroller
    private lateinit var engine: Engine
    private lateinit var listName: String
    private lateinit var context: Context
    private lateinit var list: ArrayList<Song>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bunch_list)
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        setIDs()
        goBackBtn.setOnClickListener { finish() }
        snippet.setOnClickListener { startActivity(Intent(context, Player::class.java)) }
        searchBtn.setOnClickListener {
            if (rv.adapter != null && rv.adapter!!.itemCount > 0) startActivity(
                Intent(context, BunchSearch::class.java)
                    .putExtra("list_name", listName)
                    .putExtra("list", list)
            )
        }
        Thread {
            if (rv.adapter != null && rv.adapter!!
                    .itemCount > 0
            ) shuffleAllBtn.setOnClickListener {
                if (list.size > 0) {
                    songs = list
                    shuffle = true
                    position = Random().nextInt(songs!!.size - 1 + 1)
                    engine.startPlayer()
                    mp!!.setOnCompletionListener(this@PLBunchList)
                    updateSnippet()
                    Utils.putShflStatus(context, true)
                    Toast.makeText(context, "Shuffle all songs", Toast.LENGTH_SHORT).show()
                } else Toast.makeText(context, "No songs found in the current list :(", Toast.LENGTH_SHORT).show()
            }
        }.start()
        Thread {
            if (rv.adapter != null && rv.adapter!!
                    .itemCount > 0
            ) sequenceAllBtn.setOnClickListener {
                if (list.size > 0) {
                    songs = list
                    shuffle = false
                    position = 0
                    engine.startPlayer()
                    mp!!.setOnCompletionListener(this@PLBunchList)
                    updateSnippet()
                    Utils.putShflStatus(context, false)
                    Toast.makeText(context, "Sequence all songs", Toast.LENGTH_SHORT).show()
                } else Toast.makeText(context, "No songs found in the current list :(", Toast.LENGTH_SHORT).show()
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
    }

    private fun setIDs() {
        val playlist = intent.getSerializableExtra("playlist") as Playlist
        listName = playlist.name
        val temp = playlist.songs
        list = temp ?: ArrayList()
        val position = intent.getIntExtra("position", -1)
        context = this
        engine = Engine(context)
        rv = findViewById(R.id.bunch_rv)
        snippet = findViewById(R.id.snippet)
        loader = findViewById(R.id.bunch_pb)
        notFound = findViewById(R.id.nothing_found)
        fs = findViewById(R.id.fast_bunch_list)
        coverArt = findViewById(R.id.cover_art)
        goBackTo = findViewById(R.id.go_back_to)
        goBackBtn = findViewById(R.id.go_back_btn)
        bunchTitle = findViewById(R.id.bunch_title)
        searchBtn = findViewById(R.id.bunch_search_btn)
        optionBtn = findViewById(R.id.bunch_option_btn)
        snippetTitle = snippet.findViewById(R.id.snip_title)
        snippetArt = snippet.findViewById(R.id.snip_album_art)
        snippetArtist = snippet.findViewById(R.id.snip_artist)
        shuffleAllBtn = findViewById(R.id.bunch_shuffle_all_btn)
        snippetPlayBtn = snippet.findViewById(R.id.snip_play_btn)
        sequenceAllBtn = findViewById(R.id.bunch_sequence_all_btn)
        bunchTitle.text = listName
        snippetTitle.isSelected = true
        goBackTo.text = StringBuilder("Playlist")
        if (list.size > 0) Glide.with(applicationContext)
            .asBitmap().load(Utils.getAlbumArt(list[0].album_id))
            .placeholder(R.drawable.placeholder)
            .into(coverArt)
        if (songs != null) updateSnippet()
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = PLBunchAdapter(context, list, position, loader, notFound)
        if (rv.adapter == null || rv.adapter!!.itemCount == 0) hideAttributes()
        OverScrollDecoratorHelper.setUpOverScroll(rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
        fs.setRecyclerView(rv)
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

    private fun hideAttributes() {
        searchBtn.alpha = 0.4f
        shuffleAllBtn.alpha = 0.4f
        sequenceAllBtn.alpha = 0.4f
        searchBtn.setOnClickListener(null)
        shuffleAllBtn.setOnClickListener(null)
        sequenceAllBtn.setOnClickListener(null)
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
            mp!!.setOnCompletionListener(this@PLBunchList)
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