package com.ash.studios.musify.Activities;

import static com.ash.studios.musify.Utils.Instance.mp;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
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

import com.ash.studios.musify.BottomSheets.InfoSheet;
import com.ash.studios.musify.BottomSheets.PlaylistSheet;
import com.ash.studios.musify.Interfaces.IControl;
import com.ash.studios.musify.Interfaces.IService;
import com.ash.studios.musify.Models.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Services.MusicService;
import com.ash.studios.musify.Utils.App;
import com.ash.studios.musify.Utils.Constants;
import com.ash.studios.musify.Utils.Engine;
import com.ash.studios.musify.Utils.Instance;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jackandphantom.blurimage.BlurImage;

import java.io.IOException;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class Player extends AppCompatActivity implements
        MediaPlayer.OnCompletionListener, IService, IControl {
    ImageView albumArt, background, previousBtn, nextBtn, shuffleBtn, repeatBtn, likeBtn, dislikeBtn;
    TextView title, artist, duration;
    FloatingActionButton playPause;
    CircularSeekBar seekBar;
    Toolbar toolbar;

    Song song;
    Engine engine;
    Dialog dialog;
    Context context;
    Handler handler = new Handler();
    boolean liked = false, disliked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        setIDs();
        updateUI();
        bindSeekBar();
        if (mp != null) mp.setOnCompletionListener(this);
        seekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                if (mp != null && fromUser) mp.seekTo((int) progress * 1000);
                if (mp != null)
                    duration.setText(Utils.getDuration(mp.getCurrentPosition()));
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }
        });
    }

    private void setIDs() {
        context = this;
        engine = new Engine(context);
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

        likeBtn.setOnClickListener(v -> {
            if (liked) {
                liked = false;
                likeBtn.setImageResource(R.drawable.ic_like_off);
                Utils.deleteFromTR(context, song);
            } else {
                if (disliked) {
                    disliked = false;
                    dislikeBtn.setImageResource(R.drawable.ic_dislike_off);
                    if (Utils.getLR(context).contains(song)) Utils.deleteFromLR(this, song);
                }
                liked = true;
                likeBtn.setImageResource(R.drawable.ic_like_on);
                Utils.saveToTR(this, song);
                Toast.makeText(context, song.getTitle() + " added to the Top-Rated", Toast.LENGTH_SHORT).show();
            }
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
                    if (Utils.getTR(context).contains(song)) Utils.deleteFromTR(this, song);
                }
                disliked = true;
                dislikeBtn.setImageResource(R.drawable.ic_dislike_on);
                Utils.saveToLR(this, song);
                Toast.makeText(context, song.getTitle() + " added to the Low-Rated", Toast.LENGTH_SHORT).show();
            }
        });
        repeatBtn.setOnClickListener(view -> {
            if (Instance.repeat) {
                Instance.repeat = false;
                repeatBtn.setBackgroundResource(0);
                repeatBtn.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN);
            } else {
                Instance.repeat = true;
                repeatBtn.setBackgroundResource(R.drawable.btn_on_bg);
                repeatBtn.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
            }
            Utils.putRepStatus(context, Instance.repeat);
        });
        shuffleBtn.setOnClickListener(view -> {
            if (Instance.shuffle) {
                Instance.shuffle = false;
                shuffleBtn.setBackgroundResource(0);
                shuffleBtn.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN);
            } else {
                Instance.shuffle = true;
                shuffleBtn.setBackgroundResource(R.drawable.btn_on_bg);
                shuffleBtn.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
            }
            Utils.putShflStatus(context, Instance.shuffle);
        });

        if (Instance.repeat) {
            repeatBtn.setBackgroundResource(R.drawable.btn_on_bg);
            repeatBtn.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
        } else {
            repeatBtn.setBackgroundResource(0);
            repeatBtn.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN);
        }
        if (Instance.shuffle) {
            shuffleBtn.setBackgroundResource(R.drawable.btn_on_bg);
            shuffleBtn.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
        } else {
            shuffleBtn.setBackgroundResource(0);
            shuffleBtn.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN);
        }
        if (mp == null && Instance.songs != null) {
            Instance.uri = Uri.parse(Instance.songs.get(Instance.position).getPath());
            mp = MediaPlayer.create(context, Instance.uri);
        }
    }

    private void updateUI() {
        song = Instance.songs == null ? null : Instance.songs.get(Instance.position);

        if (song != null) {
            title.setText(song.getTitle());
            movingTitleAnim(song.getTitle());
            artist.setText(song.getArtist());

            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Utils.getAlbumArt(song.getAlbum_id()));
                BlurImage.with(getApplicationContext()).load(bitmap).intensity(30).Async(true).into(background);
            } catch (IOException e) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
                background.setImageBitmap(null);
            }

            Glide.with(getApplicationContext()).load(bitmap).into(albumArt);
            albumArt.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_down));
            background.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
            Palette.from(bitmap).generate(palette -> {
                if (palette != null) {
                    String accent = "#000000", accentLight = "#80212121";
                    Palette.Swatch vibrant = palette.getDarkVibrantSwatch();

                    if (vibrant != null) {
                        accent = "#" + String.format("%06X", (0xFFFFFF & vibrant.getRgb()));
                        accentLight = "#80" + String.format("%06X", (0xFFFFFF & vibrant.getRgb()));
                    }

                    seekBar.setPointerColor(Color.parseColor(accent));
                    seekBar.setCircleColor(Color.parseColor(accentLight));
                    seekBar.setCircleProgressColor(Color.parseColor(accent));
                }
            });

            if (mp != null) {
                seekBar.setMax(mp.getDuration() / 1000f);
                duration.setText(Utils.getDuration(mp.getCurrentPosition()));

                if (mp.isPlaying()) {
                    playPause.setImageResource(R.drawable.ic_pause);
                    if (duration.getAnimation() != null) duration.getAnimation().cancel();
                } else {
                    playPause.setImageResource(R.drawable.ic_play);
                    blinkingTimeAnim();
                }
            } else {
                playPause.setImageResource(R.drawable.ic_play);
                blinkingTimeAnim();
            }
            if (Utils.getTR(context).contains(song)) {
                liked = true;
                likeBtn.setImageResource(R.drawable.ic_like_on);
            } else {
                liked = false;
                likeBtn.setImageResource(R.drawable.ic_like_off);
            }
            if (Utils.getLR(context).contains(song)) {
                disliked = true;
                dislikeBtn.setImageResource(R.drawable.ic_dislike_on);
            } else {
                disliked = false;
                dislikeBtn.setImageResource(R.drawable.ic_dislike_off);
            }
            setDialogAttrs();
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

    private void getSongDialog() {
        dialog = Utils.getDialog(context, R.layout.player_dg);
        setDialogAttrs();

        ConstraintLayout SI = dialog.findViewById(R.id.info_btn);
        ConstraintLayout AA = dialog.findViewById(R.id.album_art_btn);
        ConstraintLayout DS = dialog.findViewById(R.id.delete_song_btn);
        ConstraintLayout AP = dialog.findViewById(R.id.add_to_playlist_btn);

        AA.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(context, "In development", Toast.LENGTH_SHORT).show();
        });
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
        DS.setVisibility(View.GONE);
    }

    private void deleteSong() {
        Toast.makeText(context, "In development", Toast.LENGTH_SHORT).show();
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Instance.songs.get(Instance.position).getId());
        try {
            //searchAndDelete(this, song);
            getContentResolver().delete(uri, null, null);

            int delPos = Instance.position;
            Instance.songs.remove(delPos);
            if (Instance.songs.size() == 0) {
                if (Instance.mp != null) {
                    Instance.mp.stop();
                    Instance.mp.release();
                }
                finish();
                return;
            }
            Instance.position = delPos - 1;
            engine.playNextSong();
            updateUI();

            Toast.makeText(context, "Song deleted from this device :)", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Couldn't delete the song :(", Toast.LENGTH_SHORT).show();
        }
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
            dgDuration.setText(Utils.getDuration(song.getDuration()));
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(Utils.getAlbumArt(song.getAlbum_id()))
                    .placeholder(R.drawable.placeholder)
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

    private void playPrev() {
        engine.playPrevSong();
        seekBar.setProgress(0);
        seekBar.setMax(mp.getDuration() / 100f);

        updateUI();
        bindSeekBar();
        if (mp.isPlaying())
            playPause.setBackgroundResource(R.drawable.ic_pause);
        else
            playPause.setBackgroundResource(R.drawable.ic_play);
    }

    private void playNext() {
        engine.playNextSong();
        seekBar.setProgress(0);
        seekBar.setMax(mp.getDuration() / 1000f);

        updateUI();
        bindSeekBar();
        if (mp.isPlaying())
            playPause.setBackgroundResource(R.drawable.ic_pause);
        else
            playPause.setBackgroundResource(R.drawable.ic_play);
    }

    private void playPauseSong() {
        if (mp != null) {

            if (mp.isPlaying()) {
                playPause.setImageResource(R.drawable.ic_play);
                mp.pause();
                blinkingTimeAnim();
                stopService(new Intent(context, MusicService.class));
            } else {
                playPause.setImageResource(R.drawable.ic_pause);
                mp.start();
                Instance.playing = true;
                if (duration.getAnimation() != null) duration.getAnimation().cancel();
                startService(new Intent(context, MusicService.class).setAction(Constants.ACTION.CREATE));
            }
        } else {
            engine.startPlayer();
            playPause.setImageResource(R.drawable.ic_pause);
            mp.setOnCompletionListener(Player.this);
            if (duration.getAnimation() != null) duration.getAnimation().cancel();
        }
        seekBar.setMax(mp.getDuration() / 1000f);
        bindSeekBar();
    }

    @Override
    protected void onResume() {
        new Thread(() -> nextBtn.setOnClickListener(v -> playNext())).start();
        new Thread(() -> playPause.setOnClickListener(v -> playPauseSong())).start();
        new Thread(() -> previousBtn.setOnClickListener(v -> playPrev())).start();

        ((App) getApplicationContext()).setCurrentContext(context);
        super.onResume();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        engine.playNextSong();
        if (mp != null) {
            mp = MediaPlayer.create(getApplicationContext(), Instance.uri);
            mp.start();
            mp.setOnCompletionListener(this);
        }
        Utils.putCurrentPosition(context, Instance.position);
        updateUI();
    }

    @Override
    public void onStartService() {
    }

    @Override
    public void onStopService() {
        playPauseSong();
        stopService(new Intent(context, MusicService.class));
    }

    @Override
    public void onPrevClicked() {
        playPrev();
    }

    @Override
    public void onNextClicked() {
        playNext();
    }

    @Override
    public void onPlayClicked() {
        playPauseSong();
    }

    @Override
    public void onPauseClicked() {
        if (Instance.mp != null) Instance.mp.pause();
        updateUI();
    }
}