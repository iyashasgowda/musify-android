package com.ash.studios.musify.Activities;

import android.app.Dialog;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;

import com.ash.studios.musify.BtmSheets.InfoSheet;
import com.ash.studios.musify.BtmSheets.PlaylistSheet;
import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jackandphantom.blurimage.BlurImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import me.tankery.lib.circularseekbar.CircularSeekBar;

import static com.ash.studios.musify.Utils.Instance.mp;
import static com.ash.studios.musify.Utils.Instance.position;
import static com.ash.studios.musify.Utils.Instance.repeat;
import static com.ash.studios.musify.Utils.Instance.shuffle;
import static com.ash.studios.musify.Utils.Instance.song;
import static com.ash.studios.musify.Utils.Instance.songs;
import static com.ash.studios.musify.Utils.Instance.uri;
import static com.ash.studios.musify.Utils.Utils.getAlbumArt;
import static com.ash.studios.musify.Utils.Utils.getDialog;
import static com.ash.studios.musify.Utils.Utils.getDuration;
import static com.ash.studios.musify.Utils.Utils.setUpUI;

public class Player extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    Toolbar toolbar;
    CircularSeekBar seekBar;
    FloatingActionButton playPause;
    TextView title, artist, duration;
    ImageView albumArt, background, previousBtn, nextBtn, shuffleBtn, repeatBtn, likeBtn, dislikeBtn;

    Dialog dialog;
    ArrayList<Song> TRList, LRList;
    Handler handler = new Handler();
    Thread fabThread, prevThread, nextThread;

    boolean liked = false, disliked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        setUpUI(this);
        setIDs();

        seekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                if (mp != null && fromUser) mp.seekTo((int) progress * 1000);
                if (mp != null) duration.setText(getDuration(mp.getCurrentPosition()));
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

        shuffleBtn.setOnClickListener(view -> {
            if (shuffle) {
                shuffle = false;
                shuffleBtn.setBackgroundResource(0);
                shuffleBtn.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN);
            } else {
                shuffle = true;
                shuffleBtn.setBackgroundResource(R.drawable.btn_on_bg);
                shuffleBtn.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
            }
        });
        repeatBtn.setOnClickListener(view -> {
            if (repeat) {
                repeat = false;
                repeatBtn.setBackgroundResource(0);
                repeatBtn.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN);
            } else {
                repeat = true;
                repeatBtn.setBackgroundResource(R.drawable.btn_on_bg);
                repeatBtn.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
            }
        });
        likeBtn.setOnClickListener(v -> {
            if (liked) {
                liked = false;
                likeBtn.setImageResource(R.drawable.ic_like_off);
                Utils.deleteFromTR(this, song);
            } else {
                if (disliked) {
                    disliked = false;
                    dislikeBtn.setImageResource(R.drawable.ic_dislike_off);
                    if (LRList.contains(song)) Utils.deleteFromLR(this, song);
                }
                liked = true;
                likeBtn.setImageResource(R.drawable.ic_like_on);
                Utils.saveToTR(this, song);
            }
            TRList = Utils.getTR(this);
        });
        dislikeBtn.setOnClickListener(v -> {
            if (disliked) {
                disliked = false;
                dislikeBtn.setImageResource(R.drawable.ic_dislike_off);
                Utils.deleteFromLR(this, song);
            } else {
                if (liked) {
                    liked = false;
                    likeBtn.setImageResource(R.drawable.ic_like_off);
                    if (TRList.contains(song)) Utils.deleteFromTR(this, song);
                }
                disliked = true;
                dislikeBtn.setImageResource(R.drawable.ic_dislike_on);
                Utils.saveToLR(this, song);
            }
            LRList = Utils.getLR(this);
        });
    }

    private void setIDs() {
        title = findViewById(R.id.title);
        artist = findViewById(R.id.artist);
        toolbar = findViewById(R.id.toolbar);
        seekBar = findViewById(R.id.seek_bar);
        duration = findViewById(R.id.duration);
        albumArt = findViewById(R.id.album_art);
        background = findViewById(R.id.background);

        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.prev);
        repeatBtn = findViewById(R.id.repeat);
        likeBtn = findViewById(R.id.like_btn);
        shuffleBtn = findViewById(R.id.shuffle);
        playPause = findViewById(R.id.play_pause);
        dislikeBtn = findViewById(R.id.dislike_btn);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(view -> finish());
        toolbar.inflateMenu(R.menu.player);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.ic_options)
                getSongDialog();
            return true;
        });

        TRList = Utils.getTR(this) == null ? new ArrayList<>() : Utils.getTR(this);
        LRList = Utils.getLR(this) == null ? new ArrayList<>() : Utils.getLR(this);
    }

    private void getSong() {
        position = getIntent().getIntExtra("position", -1);

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

        if (TRList.contains(song)) {
            liked = true;
            likeBtn.setImageResource(R.drawable.ic_like_on);
        } else {
            liked = false;
            likeBtn.setImageResource(R.drawable.ic_like_off);
        }

        if (LRList.contains(song)) {
            disliked = true;
            dislikeBtn.setImageResource(R.drawable.ic_dislike_on);
        } else {
            disliked = false;
            dislikeBtn.setImageResource(R.drawable.ic_dislike_off);
        }
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

    private void prevThreadFun() {
        prevThread = new Thread() {

            @Override
            public void run() {
                super.run();
                previousBtn.setOnClickListener(v -> prevBtnClicked());
            }
        };
        prevThread.start();
    }

    private void prevBtnClicked() {
        if (mp.isPlaying()) {
            mp.stop();
            mp.release();

            if (shuffle && !repeat)
                position = new Random().nextInt((songs.size() - 1) + 1);
            else if (!shuffle && !repeat)
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

            if (shuffle && !repeat)
                position = new Random().nextInt((songs.size() - 1) + 1);
            else if (!shuffle && !repeat)
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
            blinkingTimeAnim();
        } else {
            playPause.setImageResource(R.drawable.ic_pause);
            mp.start();
            if (duration.getAnimation() != null) duration.getAnimation().cancel();
        }
        seekBar.setMax(mp.getDuration() / 1000f);
        bindSeekBar();
    }

    private void nextThreadFun() {
        nextThread = new Thread() {

            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(v -> nextBtnClicked());
            }
        };
        nextThread.start();
    }

    private void nextBtnClicked() {
        if (mp.isPlaying()) {
            mp.stop();
            mp.release();

            if (shuffle && !repeat) position = new Random().nextInt((songs.size() - 1) + 1);
            else if (!shuffle && !repeat) position = ((position + 1) % songs.size());
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
            if (shuffle && !repeat) position = new Random().nextInt((songs.size() - 1) + 1);
            else if (!shuffle && !repeat) position = ((position + 1) % songs.size());
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
            title.setText(song.getTitle());
            movingTitleAnim(song.getTitle());
            artist.setText(song.getArtist());

            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Utils.getAlbumArt(song.getAlbum_id()));
            } catch (IOException e) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
            }

            Glide.with(getApplicationContext()).load(bitmap).into(albumArt);
            albumArt.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_down));

            BlurImage.with(getApplicationContext()).load(bitmap).intensity(30).Async(true).into(background);
            background.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));

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
            if (TRList.contains(song)) {
                liked = true;
                likeBtn.setImageResource(R.drawable.ic_like_on);
            } else {
                liked = false;
                likeBtn.setImageResource(R.drawable.ic_like_off);
            }
            if (LRList.contains(song)) {
                disliked = true;
                dislikeBtn.setImageResource(R.drawable.ic_dislike_on);
            } else {
                disliked = false;
                dislikeBtn.setImageResource(R.drawable.ic_dislike_off);
            }
            setDialogAttrs();
        }
    }

    private void getSongDialog() {
        dialog = getDialog(this, R.layout.player_dg);
        setDialogAttrs();

        ConstraintLayout SI = dialog.findViewById(R.id.info_btn);
        ConstraintLayout AA = dialog.findViewById(R.id.album_art_btn);
        ConstraintLayout DS = dialog.findViewById(R.id.delete_song_btn);
        ConstraintLayout AP = dialog.findViewById(R.id.add_to_playlist_btn);

        AA.setOnClickListener(v -> Toast.makeText(this, "In development", Toast.LENGTH_SHORT).show());
        SI.setOnClickListener(si -> {
            dialog.dismiss();
            InfoSheet infoSheet = new InfoSheet(song);
            infoSheet.show(getSupportFragmentManager(), null);
        });
        AP.setOnClickListener(ap -> {
            dialog.dismiss();
            PlaylistSheet playlistSheet = new PlaylistSheet();
            playlistSheet.show(getSupportFragmentManager(), null);
        });
        DS.setOnClickListener(ds -> {
            dialog.dismiss();
            Dialog delDialog = Utils.getDialog(this, R.layout.delete_dg);
            TextView cancel = delDialog.findViewById(R.id.close_del_dg_btn);
            TextView delete = delDialog.findViewById(R.id.del_song_btn);

            cancel.setOnClickListener(c -> delDialog.dismiss());
            delete.setOnClickListener(d -> {
                delDialog.dismiss();
                Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songs.get(position).getId());

                try {
                    getContentResolver().delete(uri, null, null);
                    songs.remove(position);
                    nextBtnClicked();
                    Toast.makeText(this, "Song deleted from this device :)", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Couldn't delete the song :(", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setDialogAttrs() {
        if (dialog != null) {
            TextView dgTitle = dialog.findViewById(R.id.player_dialog_title);
            TextView dgArtist = dialog.findViewById(R.id.player_dialog_artist);
            TextView dgDuration = dialog.findViewById(R.id.player_dialog_duration);
            ImageView dgAlbumArt = dialog.findViewById(R.id.player_dialog_album_art);

            dgTitle.setSelected(true);
            dgTitle.setText(song.getTitle());
            dgArtist.setText(song.getArtist());
            dgDuration.setText(getDuration(song.getDuration()));
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(getAlbumArt(song.getAlbum_id()))
                    .placeholder(R.mipmap.icon)
                    .into(dgAlbumArt);
        }
    }

    private void blinkingTimeAnim() {
        Animation blink = new AlphaAnimation(0.0f, 1.0f);
        blink.setRepeatCount(Animation.INFINITE);
        blink.setRepeatMode(Animation.REVERSE);
        blink.setStartOffset(600);
        blink.setDuration(300);
        duration.startAnimation(blink);
    }

    private void movingTitleAnim(String content) {
        if (content.length() > 23) {
            TranslateAnimation slide = new TranslateAnimation(content.length() * 10, -content.length() * 10, 0, 0);
            slide.setInterpolator(new LinearInterpolator());
            slide.setDuration(content.length() * 250);
            slide.setRepeatCount(Animation.INFINITE);
            slide.setRepeatMode(Animation.REVERSE);
            title.startAnimation(slide);
        } else if (title.getAnimation() != null) title.getAnimation().cancel();
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