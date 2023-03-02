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
import com.ash.studios.musify.adapters.AllSongAdapter
import com.ash.studios.musify.interfaces.IControl
import com.ash.studios.musify.interfaces.IService
import com.ash.studios.musify.Models.Song
import com.ash.studios.musify.R
import com.ash.studios.musify.Services.MusicService
import com.ash.studios.musify.activities.Player
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
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import java.util.*

class BunchSearch : AppCompatActivity(), OnCompletionListener, IControl, IService {
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
    private lateinit var engine: Engine
    private lateinit var context: Context
    private lateinit var list: ArrayList<Song>
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
        val temp = intent.getSerializableExtra("list") as ArrayList<Song>
        list = temp
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
        searchText.requestFocus()
        optionBtn.alpha = 0.4f
        shuffleBtn.alpha = 0.4f
        sequenceBtn.alpha = 0.4f
        snipTitle.isSelected = true
        if (songs != null) updateSnippet()
        rv.layoutManager = LinearLayoutManager(context)
        searchType.text = intent.getStringExtra("list_name")
        OverScrollDecoratorHelper.setUpOverScroll(rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.toString().trim { it <= ' ' } != "") {
                    val tempList = ArrayList<Song>()
                    for (songs in list) if (songs.title.lowercase(Locale.getDefault()).contains(editable.toString().lowercase(Locale.getDefault()))) tempList.add(songs)
                    rv.adapter = AllSongAdapter(context, tempList, null, null)
                    if (rv.adapter != null && rv.adapter!!.itemCount > 0) {
                        shuffleBtn.alpha = 1f
                        sequenceBtn.alpha = 1f
                        shuffleBtn.setOnClickListener { shufflePlay(tempList) }
                        sequenceBtn.setOnClickListener { sequencePlay(tempList) }
                    } else hideBtnAndAdapter()
                } else hideBtnAndAdapter()
            }
        })
    }

    private fun sequencePlay(list: ArrayList<Song>) {
        songs = list
        shuffle = false
        Utils.putShflStatus(context, false)
        position = 0
        engine.startPlayer()
        mp!!.setOnCompletionListener(this@BunchSearch)
        updateSnippet()
    }

    private fun shufflePlay(list: ArrayList<Song>) {
        songs = list
        shuffle = true
        Utils.putShflStatus(context, true)
        position = Random().nextInt(songs!!.size - 1 + 1)
        engine.startPlayer()
        mp!!.setOnCompletionListener(this@BunchSearch)
        updateSnippet()
    }

    private fun hideBtnAndAdapter() {
        rv.adapter = null
        shuffleBtn.alpha = 0.4f
        sequenceBtn.alpha = 0.4f
        shuffleBtn.setOnClickListener(null)
        sequenceBtn.setOnClickListener(null)
    }

    private fun updateSnippet() {
        if (songs!!.size > 0) {
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
            snipPlayBtn.setColorFilter(accents[1], PorterDuff.Mode.SRC_IN)
            if (mp != null && mp!!.isPlaying) snipPlayBtn.setImageResource(R.drawable.ic_pause) else snipPlayBtn.setImageResource(R.drawable.ic_play)
        } else snippet.visibility = View.GONE
    }

    private fun hideBtn() {
        shuffleBtn.alpha = 0.4f
        sequenceBtn.alpha = 0.4f
        shuffleBtn.setOnClickListener(null)
        sequenceBtn.setOnClickListener(null)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        (applicationContext as App).currentContext = context
        if (songs != null) updateSnippet()
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
            mp!!.setOnCompletionListener(this@BunchSearch)
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
            mp = MediaPlayer.create(applicationContext, uri)
            mp!!.start()
            mp!!.setOnCompletionListener(this)
        }
        Utils.putCurrentPosition(context, position)
        updateSnippet()
    }
}