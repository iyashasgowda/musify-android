package com.ash.studios.musify.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ash.studios.musify.Activities.Categories.AlbumList;
import com.ash.studios.musify.Activities.Categories.AllSongList;
import com.ash.studios.musify.Activities.Categories.ArtistList;
import com.ash.studios.musify.Activities.Categories.GenreList;
import com.ash.studios.musify.Activities.Categories.LRList;
import com.ash.studios.musify.Activities.Categories.PlayList;
import com.ash.studios.musify.Activities.Categories.TRList;
import com.ash.studios.musify.Interfaces.IControl;
import com.ash.studios.musify.Interfaces.IService;
import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.App;
import com.ash.studios.musify.Utils.Constants;
import com.ash.studios.musify.Utils.Engine;
import com.ash.studios.musify.Utils.Instance;
import com.ash.studios.musify.Utils.MusicService;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.ash.studios.musify.Utils.Instance.mp;
import static com.ash.studios.musify.Utils.Utils.fetchAllSongs;
import static com.ash.studios.musify.Utils.Utils.getDialog;
import static com.ash.studios.musify.Utils.Utils.getNewColor;
import static com.ash.studios.musify.Utils.Utils.setUpUI;

public class Library extends AppCompatActivity implements
        View.OnClickListener, MediaPlayer.OnCompletionListener, IService, IControl {
    ImageView allSong, folders, albums, artists, genres, playlists, topRated, lowRated, optionsBtn, snipArt, snipBtn;
    String[] colors = new String[8];
    TextView snipTitle, snipArtist;
    ScrollView scrollView;
    CardView snippet;
    Context context;
    Engine engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);
        setUpUI(this);

        setIds();
        setColors();
    }

    private void setColors() {
        ImageView[] icons = {allSong, folders, albums, artists, genres, playlists, topRated, lowRated};
        for (int i = 0; i < icons.length; i++) {
            String col = getNewColor();
            colors[i] = col;
            icons[i].setColorFilter(Color.parseColor(col), PorterDuff.Mode.SRC_IN);
        }
    }

    private void setIds() {
        context = this;
        engine = new Engine(context);
        snippet = findViewById(R.id.snippet);
        albums = findViewById(R.id.albums_icon);
        genres = findViewById(R.id.genres_icon);
        folders = findViewById(R.id.folders_icon);
        artists = findViewById(R.id.artists_icon);
        allSong = findViewById(R.id.all_song_icon);
        topRated = findViewById(R.id.top_rated_icon);
        lowRated = findViewById(R.id.low_rated_icon);
        playlists = findViewById(R.id.playlists_icon);
        scrollView = findViewById(R.id.library_scroll_view);
        optionsBtn = findViewById(R.id.library_options_btn);

        snipTitle = findViewById(R.id.snip_title);
        snipBtn = findViewById(R.id.snip_play_btn);
        snipArt = findViewById(R.id.snip_album_art);
        snipArtist = findViewById(R.id.snip_artist);

        findViewById(R.id.all_songs).setOnClickListener(this);
        findViewById(R.id.folders).setOnClickListener(this);
        findViewById(R.id.albums).setOnClickListener(this);
        findViewById(R.id.artists).setOnClickListener(this);
        findViewById(R.id.genres).setOnClickListener(this);
        findViewById(R.id.play_lists).setOnClickListener(this);
        findViewById(R.id.top_rated).setOnClickListener(this);
        findViewById(R.id.low_rated).setOnClickListener(this);

        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
        optionsBtn.setOnClickListener(v -> {
            Dialog dialog = getDialog(context, R.layout.options_dg);
            ImageView icon = dialog.findViewById(R.id.dialog_icon);
            TextView title = dialog.findViewById(R.id.dialog_name);

            title.setText(new StringBuilder("Library"));
            icon.setImageResource(R.drawable.ic_library);
            icon.setColorFilter(Color.parseColor(getNewColor()), PorterDuff.Mode.SRC_IN);

            ConstraintLayout rescanMedia = dialog.findViewById(R.id.rescan_media);
            rescanMedia.setOnClickListener(rm -> {
                fetchAllSongs(context);
                dialog.dismiss();
            });
        });

        snipTitle.setSelected(true);
        ArrayList<Song> savedList = Utils.getCurrentList(context);
        if (savedList != null) {
            Instance.songs = savedList;
            Instance.position = Utils.getCurrentPosition(context);
            updateSnippet();
        }
        if (Instance.mp != null) Instance.mp.setOnCompletionListener(this);

        snipBtn.setOnClickListener(v -> {
            if (Instance.mp != null) {
                if (Instance.mp.isPlaying()) {
                    snipBtn.setImageResource(R.drawable.ic_play_small);
                    Instance.mp.pause();
                    stopService(new Intent(context, MusicService.class));
                } else {
                    snipBtn.setImageResource(R.drawable.ic_pause);
                    Instance.mp.start();
                    startService(new Intent(context, MusicService.class).setAction(Constants.ACTION.CREATE));
                }
            } else {
                engine.startPlayer();
                snipBtn.setImageResource(R.drawable.ic_pause);
            }
        });
        snippet.setOnClickListener(v -> startActivity(new Intent(context, Player.class)));
    }

    private void updateSnippet() {
        snippet.setVisibility(View.VISIBLE);
        snipTitle.setText(Instance.songs.get(Instance.position).getTitle());
        snipArtist.setText(Instance.songs.get(Instance.position).getArtist());
        Glide.with(getApplicationContext())
                .asBitmap()
                .placeholder(R.mipmap.ic_abstract)
                .load(Utils.getAlbumArt(Instance.songs.get(Instance.position).getAlbum_id()))
                .into(snipArt);
        if (Instance.mp != null && Instance.mp.isPlaying())
            snipBtn.setImageResource(R.drawable.ic_pause);
        else snipBtn.setImageResource(R.drawable.ic_play_small);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.all_songs:
                startActivity(new Intent(context, AllSongList.class).putExtra("icon_color", colors[0]));
                break;
            case R.id.folders:
                //TODO
                break;
            case R.id.albums:
                startActivity(new Intent(context, AlbumList.class).putExtra("icon_color", colors[2]));
                break;
            case R.id.artists:
                startActivity(new Intent(context, ArtistList.class).putExtra("icon_color", colors[3]));
                break;
            case R.id.genres:
                startActivity(new Intent(context, GenreList.class).putExtra("icon_color", colors[4]));
                break;
            case R.id.play_lists:
                startActivity(new Intent(context, PlayList.class).putExtra("icon_color", colors[5]));
                break;
            case R.id.top_rated:
                startActivity(new Intent(context, TRList.class).putExtra("icon_color", colors[6]));
                break;
            case R.id.low_rated:
                startActivity(new Intent(context, LRList.class).putExtra("icon_color", colors[7]));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((App) getApplicationContext()).setCurrentContext(context);
        if (Instance.songs != null) updateSnippet();
        if (Instance.mp != null) Instance.mp.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        engine.playNextSong();
        if (Instance.mp != null) {
            Instance.mp = MediaPlayer.create(getApplicationContext(), Instance.uri);
            Instance.mp.start();
            Instance.mp.setOnCompletionListener(this);
        }
        Utils.putCurrentPosition(context, Instance.position);
        updateSnippet();
    }

    @Override
    public void onStartService() {
    }

    @Override
    public void onStopService() {
        if (Instance.mp != null) {
            Instance.mp.stop();
            Instance.mp.release();
            Instance.mp = null;
        }
        snipBtn.setImageResource(R.drawable.ic_play_small);
        stopService(new Intent(context, MusicService.class));
    }

    @Override
    public void onPrevClicked() {
        engine.playPrevSong();
        updateSnippet();
        startService(new Intent(context, MusicService.class).setAction(Constants.ACTION.CREATE));
    }

    @Override
    public void onNextClicked() {
        engine.playNextSong();
        updateSnippet();
        startService(new Intent(context, MusicService.class).setAction(Constants.ACTION.CREATE));
    }

    @Override
    public void onPlayClicked() {
        if (Instance.mp != null) mp.start();
        else {
            engine.startPlayer();
            mp.setOnCompletionListener(Library.this);
        }
        updateSnippet();
    }

    @Override
    public void onPauseClicked() {
        if (Instance.mp != null) Instance.mp.pause();
        updateSnippet();
    }
}