package com.ash.studios.musify.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.palette.graphics.Palette;

import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jackandphantom.blurimage.BlurImage;

import java.io.IOException;
import java.util.ArrayList;

import me.tankery.lib.circularseekbar.CircularSeekBar;

import static com.ash.studios.musify.Adapters.AllSongs.list;

public class Player extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    Toolbar toolbar;
    CircularSeekBar seekBar;
    FloatingActionButton playPause;
    TextView title, artist, duration;
    ImageView albumArt, background, previous, next, shuffle, repeat;

    static Uri uri;
    static Song song;
    static MediaPlayer mp;
    static ArrayList<Song> songs = new ArrayList<>();

    int position = -1;
    Handler handler = new Handler();
    Thread fabThread, prevThread, nextThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        Utils.setUpUI(this);
        setIDs();

        seekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                if (mp != null && fromUser) mp.seekTo((int) progress * 1000);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }
        });
        bindSeekBar();
        getSong();
        mp.setOnCompletionListener(this);
    }

    private void setIDs() {
        title = findViewById(R.id.title);
        artist = findViewById(R.id.artist);
        toolbar = findViewById(R.id.toolbar);
        seekBar = findViewById(R.id.seek_bar);
        duration = findViewById(R.id.duration);
        albumArt = findViewById(R.id.album_art);
        background = findViewById(R.id.background);

        next = findViewById(R.id.next);
        previous = findViewById(R.id.prev);
        repeat = findViewById(R.id.repeat);
        shuffle = findViewById(R.id.shuffle);
        playPause = findViewById(R.id.play_pause);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(view -> finish());
        toolbar.inflateMenu(R.menu.player);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.ic_equalizer)
                Toast.makeText(Player.this, "Equalizer", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private void getSong() {
        position = getIntent().getIntExtra("position", -1);
        songs = list;

        if (songs != null) {
            song = songs.get(position);
            uri = Uri.parse(songs.get(position).getPath());
            playPause.setImageResource(R.drawable.ic_pause);
        }

        if (mp != null) {
            mp.stop();
            mp.release();
        }
        mp = MediaPlayer.create(getApplicationContext(), uri);
        mp.start();

        seekBar.setProgress(0);
        seekBar.setMax(mp.getDuration() / 1000f);
        setSongAttrs(song);
    }

    private void bindSeekBar() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mp != null) {
                    int currentPosition = mp.getCurrentPosition() / 1000;
                    seekBar.setProgress(currentPosition);
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void fabThreadFun() {
        fabThread = new Thread() {

            @Override
            public void run() {
                super.run();
                playPause.setOnClickListener(v -> fabBtnClicked());
            }
        };
        fabThread.start();
    }

    private void fabBtnClicked() {
        if (mp.isPlaying()) {
            playPause.setImageResource(R.drawable.ic_play);
            mp.pause();
        } else {
            playPause.setImageResource(R.drawable.ic_pause);
            mp.start();
        }
        seekBar.setMax(mp.getDuration() / 1000f);
        bindSeekBar();
    }

    private void prevThreadFun() {
        prevThread = new Thread() {

            @Override
            public void run() {
                super.run();
                previous.setOnClickListener(v -> prevBtnClicked());
            }
        };
        prevThread.start();
    }

    private void prevBtnClicked() {
        if (mp.isPlaying()) {
            mp.stop();
            mp.release();
            position = ((position - 1) < 0 ? (songs.size() - 1) : (position - 1));
            uri = Uri.parse(songs.get(position).getPath());
            mp = MediaPlayer.create(getApplicationContext(), uri);

            seekBar.setProgress(0);
            seekBar.setMax(mp.getDuration() / 1000f);
            song = songs.get(position);
            setSongAttrs(song);

            bindSeekBar();
            mp.setOnCompletionListener(this);
            playPause.setBackgroundResource(R.drawable.ic_pause);
            mp.start();
        } else {
            mp.stop();
            mp.release();
            position = ((position - 1) < 0 ? (songs.size() - 1) : (position - 1));
            uri = Uri.parse(songs.get(position).getPath());
            mp = MediaPlayer.create(getApplicationContext(), uri);

            seekBar.setProgress(0);
            seekBar.setMax(mp.getDuration() / 1000f);
            song = songs.get(position);
            setSongAttrs(song);

            bindSeekBar();
            mp.setOnCompletionListener(this);
            playPause.setBackgroundResource(R.drawable.ic_play);
        }
    }

    private void nextThreadFun() {
        nextThread = new Thread() {

            @Override
            public void run() {
                super.run();
                next.setOnClickListener(v -> nextBtnClicked());
            }
        };
        nextThread.start();
    }

    private void nextBtnClicked() {
        if (mp.isPlaying()) {
            mp.stop();
            mp.release();
            position = ((position + 1) % songs.size());
            uri = Uri.parse(songs.get(position).getPath());
            mp = MediaPlayer.create(getApplicationContext(), uri);

            seekBar.setProgress(0);
            seekBar.setMax(mp.getDuration() / 1000f);
            song = songs.get(position);
            setSongAttrs(song);

            bindSeekBar();
            mp.setOnCompletionListener(this);
            playPause.setBackgroundResource(R.drawable.ic_pause);
            mp.start();
        } else {
            mp.stop();
            mp.release();
            position = ((position + 1) % songs.size());
            uri = Uri.parse(songs.get(position).getPath());
            mp = MediaPlayer.create(getApplicationContext(), uri);

            seekBar.setProgress(0);
            seekBar.setMax(mp.getDuration() / 1000f);
            song = songs.get(position);
            setSongAttrs(song);

            bindSeekBar();
            mp.setOnCompletionListener(this);
            playPause.setBackgroundResource(R.drawable.ic_play);
        }
    }

    private void setSongAttrs(Song song) {
        if (song != null) {
            Bitmap bitmap;
            title.setText(song.getTitle());
            title.setSelected(true);

            artist.setText(song.getArtist());
            duration.setText(Utils.getDuration(song.getDuration()));
            duration.setTypeface(ResourcesCompat.getFont(this, R.font.josefin_sans_bold));

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Utils.getAlbumArt(song.getAlbum_id()));
            } catch (IOException e) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
            }
            Glide.with(getApplicationContext()).load(bitmap).into(albumArt);
            albumArt.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_down));

            BlurImage.with(getApplicationContext()).load(bitmap).intensity(30).Async(true).into(background);
            Palette.from(bitmap).generate(palette -> {
                if (palette != null) {
                    String accent = "#000000", accentLight = "#80212121";
                    Palette.Swatch vibrant = palette.getDarkVibrantSwatch();
                    Palette.Swatch dominant = palette.getDarkVibrantSwatch();

                    if (vibrant != null) {
                        accent = "#" + String.format("%06X", (0xFFFFFF & vibrant.getRgb()));
                        accentLight = "#80" + String.format("%06X", (0xFFFFFF & vibrant.getRgb()));
                    } else if (dominant != null) {
                        accent = "#" + String.format("%06X", (0xFFFFFF & dominant.getRgb()));
                        accentLight = "#80" + String.format("%06X", (0xFFFFFF & dominant.getRgb()));
                    }

                    seekBar.setPointerColor(Color.parseColor(accent));
                    seekBar.setCircleColor(Color.parseColor(accentLight));
                    seekBar.setCircleProgressColor(Color.parseColor(accent));
                }
            });
        }
    }

    @Override
    protected void onResume() {
        fabThreadFun();
        prevThreadFun();
        nextThreadFun();
        super.onResume();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        nextBtnClicked();
        if (mp != null) {
            mp = MediaPlayer.create(getApplicationContext(), uri);
            mp.start();
            mp.setOnCompletionListener(this);
        }
    }
}