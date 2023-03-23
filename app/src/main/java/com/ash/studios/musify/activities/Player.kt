package com.ash.studios.musify.activities

import android.app.Dialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.palette.graphics.Palette
import com.ash.studios.musify.bottomSheets.InfoSheet
import com.ash.studios.musify.bottomSheets.PlaylistSheet
import com.ash.studios.musify.interfaces.IControl
import com.ash.studios.musify.interfaces.IService
import com.ash.studios.musify.models.Song
import com.ash.studios.musify.R
import com.ash.studios.musify.services.MusicService
import com.ash.studios.musify.utils.App
import com.ash.studios.musify.utils.Constants
import com.ash.studios.musify.utils.Engine
import com.ash.studios.musify.utils.Instance.mp
import com.ash.studios.musify.utils.Instance.playing
import com.ash.studios.musify.utils.Instance.position
import com.ash.studios.musify.utils.Instance.shuffle
import com.ash.studios.musify.utils.Instance.repeat
import com.ash.studios.musify.utils.Instance.songs
import com.ash.studios.musify.utils.Instance.uri
import com.ash.studios.musify.utils.Utils
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jackandphantom.blurimage.BlurImage
import me.tankery.lib.circularseekbar.CircularSeekBar
import java.io.IOException

class Player : AppCompatActivity(), OnCompletionListener, IService, IControl {
    private lateinit var albumArt: ImageView
    private lateinit var background: ImageView
    private lateinit var previousBtn: ImageView
    private lateinit var nextBtn: ImageView
    private lateinit var shuffleBtn: ImageView
    private lateinit var repeatBtn: ImageView
    private lateinit var likeBtn: ImageView
    private lateinit var dislikeBtn: ImageView
    private lateinit var title: TextView
    private lateinit var artist: TextView
    private lateinit var duration: TextView
    private lateinit var playPause: FloatingActionButton
    private lateinit var seekBar: CircularSeekBar
    private lateinit var toolbar: Toolbar
    private lateinit var song: Song
    private lateinit var engine: Engine
    private lateinit var dialog: Dialog
    private lateinit var context: Context
    private var handler = Handler()
    private var liked = false
    private var disliked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player)
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        setIDs()
        updateUI()
        bindSeekBar()
        if (mp != null) mp!!.setOnCompletionListener(this)
        seekBar.setOnSeekBarChangeListener(object : CircularSeekBar.OnCircularSeekBarChangeListener {
            override fun onProgressChanged(circularSeekBar: CircularSeekBar?, progress: Float, fromUser: Boolean) {
                if (mp != null && fromUser) mp!!.seekTo(progress.toInt() * 1000)
                if (mp != null) duration.text = Utils.getDuration(mp!!.currentPosition.toLong())
            }

            override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {}
            override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {}
        })
    }

    private fun setIDs() {
        context = this
        engine = Engine(context)
        title = findViewById(R.id.title)
        artist = findViewById(R.id.artist)
        toolbar = findViewById(R.id.toolbar)
        seekBar = findViewById(R.id.seek_bar)
        duration = findViewById(R.id.duration)
        albumArt = findViewById(R.id.album_art)
        background = findViewById(R.id.background)
        nextBtn = findViewById(R.id.next)
        previousBtn = findViewById(R.id.prev)
        repeatBtn = findViewById(R.id.repeat)
        likeBtn = findViewById(R.id.like_btn)
        shuffleBtn = findViewById(R.id.shuffle)
        playPause = findViewById(R.id.play_pause)
        dislikeBtn = findViewById(R.id.dislike_btn)
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener { finish() }
        toolbar.inflateMenu(R.menu.player)
        toolbar.setOnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.ic_options) songDialog
            true
        }
        likeBtn.setOnClickListener {
            if (liked) {
                liked = false
                likeBtn.setImageResource(R.drawable.ic_like_off)
                Utils.deleteFromTR(context, song)
            } else {
                if (disliked) {
                    disliked = false
                    dislikeBtn.setImageResource(R.drawable.ic_dislike_off)
                    if (Utils.getLR(context).contains(song)) Utils.deleteFromLR(this, song)
                }
                liked = true
                likeBtn.setImageResource(R.drawable.ic_like_on)
                Utils.saveToTR(this, song)
                Toast.makeText(context, song.title + " added to the Top-Rated", Toast.LENGTH_SHORT).show()
            }
        }
        dislikeBtn.setOnClickListener {
            if (disliked) {
                disliked = false
                dislikeBtn.setImageResource(R.drawable.ic_dislike_off)
                Utils.deleteFromLR(this, song)
            } else {
                if (liked) {
                    liked = false
                    likeBtn.setImageResource(R.drawable.ic_like_off)
                    if (Utils.getTR(context).contains(song)) Utils.deleteFromTR(this, song)
                }
                disliked = true
                dislikeBtn.setImageResource(R.drawable.ic_dislike_on)
                Utils.saveToLR(this, song)
                Toast.makeText(context, song.title + " added to the Low-Rated", Toast.LENGTH_SHORT).show()
            }
        }
        repeatBtn.setOnClickListener {
            if (repeat) {
                repeat = false
                repeatBtn.setBackgroundResource(0)
                repeatBtn.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN)
            } else {
                repeat = true
                repeatBtn.setBackgroundResource(R.drawable.btn_on_bg)
                repeatBtn.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN)
            }
            Utils.putRepStatus(context, repeat)
        }
        shuffleBtn.setOnClickListener {
            if (shuffle) {
                shuffle = false
                shuffleBtn.setBackgroundResource(0)
                shuffleBtn.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN)
            } else {
                shuffle = true
                shuffleBtn.setBackgroundResource(R.drawable.btn_on_bg)
                shuffleBtn.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN)
            }
            Utils.putShflStatus(context, shuffle)
        }
        if (repeat) {
            repeatBtn.setBackgroundResource(R.drawable.btn_on_bg)
            repeatBtn.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN)
        } else {
            repeatBtn.setBackgroundResource(0)
            repeatBtn.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN)
        }
        if (shuffle) {
            shuffleBtn.setBackgroundResource(R.drawable.btn_on_bg)
            shuffleBtn.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN)
        } else {
            shuffleBtn.setBackgroundResource(0)
            shuffleBtn.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN)
        }
        if (mp == null && songs != null) {
            uri = Uri.parse(songs!![position].path)
            mp = MediaPlayer.create(context, uri)
        }
    }

    private fun updateUI() {
        if (songs != null) song = songs!![position]
        title.text = song.title
        movingTitleAnim(song.title)
        artist.text = song.artist
        var bitmap: Bitmap?
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, Utils.getAlbumArt(song.album_id))
            BlurImage.with(applicationContext).load(bitmap).intensity(30f).Async(true).into(background)
        } catch (e: IOException) {
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.placeholder)
            background.setImageBitmap(null)
        }
        Glide.with(applicationContext).load(bitmap).into(albumArt)
        albumArt.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_down))
        background.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
        Palette.from(bitmap!!).generate { palette: Palette? ->
            if (palette != null) {
                var accent = "#000000"
                var accentLight = "#80212121"
                val vibrant = palette.darkVibrantSwatch
                if (vibrant != null) {
                    accent = "#" + String.format("%06X", 0xFFFFFF and vibrant.rgb)
                    accentLight = "#80" + String.format("%06X", 0xFFFFFF and vibrant.rgb)
                }
                seekBar.pointerColor = Color.parseColor(accent)
                seekBar.circleColor = Color.parseColor(accentLight)
                seekBar.circleProgressColor = Color.parseColor(accent)
            }
        }
        if (mp != null) {
            seekBar.max = mp!!.duration / 1000f
            duration.text = Utils.getDuration(mp!!.currentPosition.toLong())
            if (mp!!.isPlaying) {
                playPause.setImageResource(R.drawable.ic_pause)
                if (duration.animation != null) duration.animation.cancel()
            } else {
                playPause.setImageResource(R.drawable.ic_play)
                blinkingTimeAnim()
            }
        } else {
            playPause.setImageResource(R.drawable.ic_play)
            blinkingTimeAnim()
        }
        if (Utils.getTR(context).contains(song)) {
            liked = true
            likeBtn.setImageResource(R.drawable.ic_like_on)
        } else {
            liked = false
            likeBtn.setImageResource(R.drawable.ic_like_off)
        }
        if (Utils.getLR(context).contains(song)) {
            disliked = true
            dislikeBtn.setImageResource(R.drawable.ic_dislike_on)
        } else {
            disliked = false
            dislikeBtn.setImageResource(R.drawable.ic_dislike_off)
        }
        setDialogAttrs()
    }

    private fun bindSeekBar() {
        runOnUiThread(object : Runnable {
            override fun run() {
                if (mp != null) {
                    val currentPosition: Int = mp!!.currentPosition / 1000
                    seekBar.progress = currentPosition.toFloat()
                }
                handler.postDelayed(this, 1000)
            }
        })
    }

    private val songDialog: Unit
        get() {
            dialog = Utils.getDialog(context, R.layout.player_dg)
            setDialogAttrs()
            val infoBtn = dialog.findViewById<ConstraintLayout>(R.id.info_btn)
            val albumArtBtn = dialog.findViewById<ConstraintLayout>(R.id.album_art_btn)
            val deleteSongBtn = dialog.findViewById<ConstraintLayout>(R.id.delete_song_btn)
            val addPlayListBtn = dialog.findViewById<ConstraintLayout>(R.id.add_to_playlist_btn)
            albumArtBtn.setOnClickListener {
                dialog.dismiss()
                Toast.makeText(context, "In development", Toast.LENGTH_SHORT).show()
            }
            infoBtn.setOnClickListener {
                dialog.dismiss()
                val infoSheet = InfoSheet(song)
                infoSheet.show(supportFragmentManager, null)
            }
            addPlayListBtn.setOnClickListener {
                dialog.dismiss()
                val playlistSheet = PlaylistSheet()
                playlistSheet.show(supportFragmentManager, null)
            }
            deleteSongBtn.visibility = View.GONE
        }

    private fun deleteSong() {
        Toast.makeText(context, "In development", Toast.LENGTH_SHORT).show()
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songs!![position].id)
        try {
            //searchAndDelete(this, song);
            contentResolver.delete(uri, null, null)
            val delPos: Int = position
            songs!!.removeAt(delPos)
            if (songs!!.size === 0) {
                if (mp != null) {
                    mp!!.stop()
                    mp!!.release()
                }
                finish()
                return
            }
            position = delPos - 1
            engine.playNextSong()
            updateUI()
            Toast.makeText(context, "Song deleted from this device :)", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Couldn't delete the song :(", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setDialogAttrs() {
        val dgTitle = dialog.findViewById<TextView>(R.id.player_dialog_title)
        val dgArtist = dialog.findViewById<TextView>(R.id.player_dialog_artist)
        val dgDuration = dialog.findViewById<TextView>(R.id.player_dialog_duration)
        val dgAlbumArt = dialog.findViewById<ImageView>(R.id.player_dialog_album_art)
        dgTitle.isSelected = true
        dgTitle.text = song.title
        dgArtist.text = song.artist
        dgDuration.text = Utils.getDuration(song.duration)
        Glide.with(applicationContext)
            .asBitmap()
            .load(Utils.getAlbumArt(song.album_id))
            .placeholder(R.drawable.placeholder)
            .into(dgAlbumArt)
    }

    private fun blinkingTimeAnim() {
        val blink: Animation = AlphaAnimation(0.0f, 1.0f)
        blink.repeatCount = Animation.INFINITE
        blink.repeatMode = Animation.REVERSE
        blink.startOffset = 600
        blink.duration = 300
        duration.startAnimation(blink)
    }

    private fun movingTitleAnim(content: String) {
        if (content.length > 23) {
            val slide = TranslateAnimation((content.length * 10).toFloat(), (-content.length * 10).toFloat(), 0f, 0f)
            slide.interpolator = LinearInterpolator()
            slide.duration = (content.length * 250).toLong()
            slide.repeatCount = Animation.INFINITE
            slide.repeatMode = Animation.REVERSE
            title.startAnimation(slide)
        } else if (title.animation != null) title.animation.cancel()
    }

    private fun playPrev() {
        engine.playPrevSong()
        seekBar.progress = 0F
        seekBar.max = mp!!.duration / 100f
        updateUI()
        bindSeekBar()
        if (mp!!.isPlaying) playPause.setBackgroundResource(R.drawable.ic_pause) else playPause.setBackgroundResource(R.drawable.ic_play)
    }

    private fun playNext() {
        engine.playNextSong()
        seekBar.progress = 0F
        seekBar.max = mp!!.duration / 1000f
        updateUI()
        bindSeekBar()
        if (mp!!.isPlaying) playPause.setBackgroundResource(R.drawable.ic_pause) else playPause.setBackgroundResource(R.drawable.ic_play)
    }

    private fun playPauseSong() {
        if (mp != null) {
            if (mp!!.isPlaying) {
                playPause.setImageResource(R.drawable.ic_play)
                mp!!.pause()
                blinkingTimeAnim()
                stopService(Intent(context, MusicService::class.java))
            } else {
                playPause.setImageResource(R.drawable.ic_pause)
                mp!!.start()
                playing = true
                if (duration.animation != null) duration.animation.cancel()
                startService(Intent(context, MusicService::class.java).setAction(Constants.ACTION.CREATE.label))
            }
        } else {
            engine.startPlayer()
            playPause.setImageResource(R.drawable.ic_pause)
            mp!!.setOnCompletionListener(this@Player)
            if (duration.animation != null) duration.animation.cancel()
        }
        seekBar.max = mp!!.duration / 1000f
        bindSeekBar()
    }

    override fun onResume() {
        Thread { nextBtn.setOnClickListener { playNext() } }.start()
        Thread { playPause.setOnClickListener { playPauseSong() } }.start()
        Thread { previousBtn.setOnClickListener { playPrev() } }.start()
        (applicationContext as App).currentContext = context
        super.onResume()
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        engine.playNextSong()
        if (mp != null) {
            mp = MediaPlayer.create(applicationContext, uri)
            mp!!.start()
            mp!!.setOnCompletionListener(this)
        }
        Utils.putCurrentPosition(context, position)
        updateUI()
    }

    override fun onStartService() {}
    override fun onStopService() {
        playPauseSong()
        stopService(Intent(context, MusicService::class.java))
    }

    override fun onPrevClicked() {
        playPrev()
    }

    override fun onNextClicked() {
        playNext()
    }

    override fun onPlayClicked() {
        playPauseSong()
    }

    override fun onPauseClicked() {
        if (mp != null) mp!!.pause()
        updateUI()
    }
}