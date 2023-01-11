package com.ash.studios.musify.activities;

import static com.ash.studios.musify.utils.Constants.LIBRARY_OPTIONS;
import static com.ash.studios.musify.utils.Instance.mp;
import static com.ash.studios.musify.utils.Utils.fetchAllSongs;
import static com.ash.studios.musify.utils.Utils.getDialog;
import static com.ash.studios.musify.utils.Utils.getNewColor;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ash.studios.musify.activities.categories.AlbumList;
import com.ash.studios.musify.activities.categories.AllSongList;
import com.ash.studios.musify.activities.categories.ArtistList;
import com.ash.studios.musify.activities.categories.FolderList;
import com.ash.studios.musify.activities.categories.GenreList;
import com.ash.studios.musify.activities.categories.LRList;
import com.ash.studios.musify.activities.categories.PlayList;
import com.ash.studios.musify.activities.categories.TRList;
import com.ash.studios.musify.activities.categories.YearList;
import com.ash.studios.musify.BottomSheets.LibrarySheet;
import com.ash.studios.musify.Interfaces.IControl;
import com.ash.studios.musify.Interfaces.IService;
import com.ash.studios.musify.Models.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Services.MusicService;
import com.ash.studios.musify.utils.App;
import com.ash.studios.musify.utils.Constants;
import com.ash.studios.musify.utils.Engine;
import com.ash.studios.musify.utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class Library extends AppCompatActivity implements
        View.OnClickListener, MediaPlayer.OnCompletionListener, IService, IControl {
    ImageView allSong, folders, albums, artists, genres, playlists, topRated, lowRated, years, optionsBtn, snipArt, snipBtn;
    ConstraintLayout list0, list1, list2, list3, list4, list5, list6, list7, list8;
    TextView snipTitle, snipArtist;
    ScrollView scrollView;
    CardView snippet;

    Engine engine;
    Context context;
    SharedPreferences prefs;
    String[] colors = new String[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        setIds();
        setColors();
    }

    private void setIds() {
        context = this;
        engine = new Engine(context);
        snippet = findViewById(R.id.snippet);
        years = findViewById(R.id.years_icon);
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

        list0 = findViewById(R.id.all_songs);
        list0.setOnClickListener(this);

        list1 = findViewById(R.id.folders);
        list1.setOnClickListener(this);

        list2 = findViewById(R.id.albums);
        list2.setOnClickListener(this);

        list3 = findViewById(R.id.artists);
        list3.setOnClickListener(this);

        list4 = findViewById(R.id.genres);
        list4.setOnClickListener(this);

        list5 = findViewById(R.id.years);
        list5.setOnClickListener(this);

        list6 = findViewById(R.id.play_lists);
        list6.setOnClickListener(this);

        list7 = findViewById(R.id.top_rated);
        list7.setOnClickListener(this);

        list8 = findViewById(R.id.low_rated);
        list8.setOnClickListener(this);

        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
        prefs = getSharedPreferences(LIBRARY_OPTIONS, MODE_PRIVATE);
        optionsBtn.setOnClickListener(v -> {
            Dialog dialog = getDialog(context, R.layout.options_dg);
            ImageView icon = dialog.findViewById(R.id.dialog_icon);
            TextView title = dialog.findViewById(R.id.dialog_name);

            title.setText(new StringBuilder("Library"));
            icon.setImageResource(R.drawable.ic_library);
            icon.setColorFilter(Color.parseColor(getNewColor()), PorterDuff.Mode.SRC_IN);

            ConstraintLayout rescanMedia = dialog.findViewById(R.id.rescan_media);
            ConstraintLayout listOption = dialog.findViewById(R.id.listing_options);
            ConstraintLayout settings = dialog.findViewById(R.id.settings);

            settings.setOnClickListener(s -> {
                dialog.dismiss();
                startActivity(new Intent(context, Settings.class));
            });
            listOption.setOnClickListener(lo -> {
                dialog.dismiss();
                LibrarySheet librarySheet = new LibrarySheet(context);
                librarySheet.show(getSupportFragmentManager(), null);
            });
            rescanMedia.setOnClickListener(rm -> {
                dialog.dismiss();
                fetchAllSongs(context);
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
                    Instance.mp.pause();
                    snipBtn.setImageResource(R.drawable.ic_play);
                    stopService(new Intent(context, MusicService.class));
                } else {
                    Instance.mp.start();
                    Instance.playing = true;
                    snipBtn.setImageResource(R.drawable.ic_pause);
                    startService(new Intent(context, MusicService.class).setAction(Constants.ACTION.CREATE.getLabel()));
                }
            } else {
                engine.startPlayer();
                snipBtn.setImageResource(R.drawable.ic_pause);
            }
        });
        snippet.setOnClickListener(v -> startActivity(new Intent(context, Player.class)));
        checkListStates();
    }

    private void setColors() {
        ImageView[] icons = {allSong, folders, albums, artists, genres, playlists, topRated, lowRated, years};
        for (int i = 0; i < icons.length; i++) {
            String col = getNewColor();
            colors[i] = col;
            icons[i].setColorFilter(Color.parseColor(col), PorterDuff.Mode.SRC_IN);
        }
    }

    private void updateSnippet() {
        snippet.setVisibility(View.VISIBLE);
        snipTitle.setText(Instance.songs.get(Instance.position).getTitle());
        snipArtist.setText(Instance.songs.get(Instance.position).getArtist());
        Glide.with(getApplicationContext())
                .asBitmap()
                .placeholder(R.drawable.placeholder)
                .load(Utils.getAlbumArt(Instance.songs.get(Instance.position).getAlbum_id()))
                .into(snipArt);

        int[] accents = Utils.getSecondaryColors(context, Utils.getAlbumArt(Instance.songs.get(Instance.position).getAlbum_id()));
        snippet.setCardBackgroundColor(accents[0]);
        snipTitle.setTextColor(accents[1]);
        snipArtist.setTextColor(accents[1]);
        snipBtn.setColorFilter(accents[1], PorterDuff.Mode.SRC_IN);

        if (Instance.mp != null && Instance.mp.isPlaying())
            snipBtn.setImageResource(R.drawable.ic_pause);
        else snipBtn.setImageResource(R.drawable.ic_play);
    }

    private void checkListStates() {
        if (!prefs.getBoolean("state0", true)) list0.setVisibility(View.GONE);
        if (!prefs.getBoolean("state1", true)) list1.setVisibility(View.GONE);
        if (!prefs.getBoolean("state2", true)) list2.setVisibility(View.GONE);
        if (!prefs.getBoolean("state3", true)) list3.setVisibility(View.GONE);
        if (!prefs.getBoolean("state4", true)) list4.setVisibility(View.GONE);
        if (!prefs.getBoolean("state5", true)) list5.setVisibility(View.GONE);
        if (!prefs.getBoolean("state6", true)) list6.setVisibility(View.GONE);
        if (!prefs.getBoolean("state7", true)) list7.setVisibility(View.GONE);
        if (!prefs.getBoolean("state8", true)) list8.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.all_songs:
                startActivity(new Intent(context, AllSongList.class).putExtra("icon_color", colors[0]));
                break;
            case R.id.folders:
                startActivity(new Intent(context, FolderList.class).putExtra("icon_color", colors[1]));
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
            case R.id.years:
                startActivity(new Intent(context, YearList.class).putExtra("icon_color", colors[8]));
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
        snipBtn.setImageResource(R.drawable.ic_play);
        stopService(new Intent(context, MusicService.class));
    }

    @Override
    public void onPrevClicked() {
        engine.playPrevSong();
        updateSnippet();
        startService(new Intent(context, MusicService.class).setAction(Constants.ACTION.CREATE.getLabel()));
    }

    @Override
    public void onNextClicked() {
        engine.playNextSong();
        updateSnippet();
        startService(new Intent(context, MusicService.class).setAction(Constants.ACTION.CREATE.getLabel()));
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