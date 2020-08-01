package com.ash.studios.musify.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.Adapters.Albums;
import com.ash.studios.musify.Adapters.AllSongs;
import com.ash.studios.musify.Adapters.Artists;
import com.ash.studios.musify.Adapters.Genres;
import com.ash.studios.musify.Adapters.Playlists;
import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Engine;
import com.ash.studios.musify.Utils.Utils;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.Random;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.ash.studios.musify.Utils.Instance.mp;
import static com.ash.studios.musify.Utils.Instance.position;
import static com.ash.studios.musify.Utils.Instance.repeat;
import static com.ash.studios.musify.Utils.Instance.shuffle;
import static com.ash.studios.musify.Utils.Instance.songs;
import static com.ash.studios.musify.Utils.Instance.uri;
import static com.ash.studios.musify.Utils.Utils.getDialog;
import static com.ash.studios.musify.Utils.Utils.getLR;
import static com.ash.studios.musify.Utils.Utils.getPlaylists;
import static com.ash.studios.musify.Utils.Utils.getTR;
import static com.ash.studios.musify.Utils.Utils.setUpUI;

@SuppressLint("SetTextI18n")
public class SongList extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    RecyclerView rv;
    ProgressBar loader;
    TextView title, NF;
    AppBarLayout appBar;
    ConstraintLayout backToLib;
    ImageView icon, shuffleAllBtn, playAllBtn, searchBtn, optionsBtn;

    Engine engine;
    Dialog dialog;
    Context context;
    String listType, iconColor;

    int lastPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);
        setUpUI(this);

        setIDs();
        getData();
    }

    private void setIDs() {
        context = this;
        rv = findViewById(R.id.song_list);
        appBar = findViewById(R.id.app_bar);
        NF = findViewById(R.id.nothing_found);
        icon = findViewById(R.id.activity_icon);
        backToLib = findViewById(R.id.lib_back);
        loader = findViewById(R.id.list_loader);
        title = findViewById(R.id.activity_title);
        searchBtn = findViewById(R.id.search_btn);
        optionsBtn = findViewById(R.id.options_btn);
        playAllBtn = findViewById(R.id.play_all_btn);
        shuffleAllBtn = findViewById(R.id.shuffle_all_btn);
        listType = getIntent().getStringExtra("list_type");
        iconColor = getIntent().getStringExtra("icon_color");

        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setHasFixedSize(true);

        OverScrollDecoratorHelper.setUpOverScroll(rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        backToLib.setOnClickListener(v -> finish());
        playAllBtn.setOnClickListener(v -> sequenceAndPlay());
        shuffleAllBtn.setOnClickListener(v -> shuffleAndPlay());
        optionsBtn.setOnClickListener(v -> openOptionsDialog());
        searchBtn.setOnClickListener(v -> startActivity(new Intent(this, Search.class).putExtra("search_type", listType)));
    }

    private void getData() {
        if (listType != null) {

            switch (listType) {
                case "all_songs":
                    getAllSongs(iconColor);
                    break;
                case "folders":
                    getFolders(iconColor);
                    break;
                case "albums":
                    getAlbums(iconColor);
                    break;
                case "artists":
                    getArtists(iconColor);
                    break;
                case "genres":
                    getGenres(iconColor);
                    break;
                case "playlists":
                    getPlayLists(iconColor);
                    break;
                case "top_rated":
                    getTopRated(iconColor);
                    break;
                case "low_rated":
                    getLowRated(iconColor);
                    break;
                case "recently_added":
                    getRecentlyAdded(iconColor);
                    break;
            }
        }
        if (rv.getAdapter() == null || rv.getAdapter().getItemCount() == 0) disableBtns();
    }

    private void shuffleAndPlay() {
        switch (listType) {
            case "all_songs":
                startShufflePlr((Utils.songs == null || Utils.songs.size() == 0) ? Utils.getAllSongs(context) : Utils.songs);
                break;
            case "top_rated":
                if (Utils.getTR(context) != null)
                    startShufflePlr(Utils.getTR(context));
                break;
            case "low_rated":
                if (Utils.getLR(context) != null)
                    startShufflePlr(Utils.getLR(context));
                break;
            case "recently_added":
                if (Utils.getRecentlyAdded(context) != null)
                    startShufflePlr(Utils.getRecentlyAdded(context));
                break;
        }
    }

    private void sequenceAndPlay() {
        switch (listType) {
            case "all_songs":
                startSequencePlr((Utils.songs == null || Utils.songs.size() == 0) ? Utils.getAllSongs(context) : Utils.songs);
                break;
            case "top_rated":
                if (Utils.getTR(context) != null)
                    startSequencePlr(Utils.getTR(context));
                break;
            case "low_rated":
                if (Utils.getLR(context) != null)
                    startSequencePlr(Utils.getLR(context));
                break;
            case "recently_added":
                if (Utils.getRecentlyAdded(context) != null)
                    startSequencePlr(Utils.getRecentlyAdded(context));
                break;
        }
    }

    private void openOptionsDialog() {
        dialog = getDialog(context, R.layout.options_dg);

        TextView dialogName = dialog.findViewById(R.id.dialog_name);
        ImageView dialogIcon = dialog.findViewById(R.id.dialog_icon);
        ConstraintLayout SF = dialog.findViewById(R.id.select_folders);
        ConstraintLayout RM = dialog.findViewById(R.id.rescan_media);
        ConstraintLayout LO = dialog.findViewById(R.id.listing_options);
        ConstraintLayout AN = dialog.findViewById(R.id.add_new);
        ConstraintLayout CL = dialog.findViewById(R.id.clear_list);

        dialogName.setText(title.getText());
        dialogIcon.setImageDrawable(icon.getDrawable());

        switch (listType) {
            case "artists":
            case "genres":
                SF.setVisibility(View.GONE);
                break;
            case "playlists":
                AN.setVisibility(View.VISIBLE);
                break;
            case "recently_added":
                CL.setVisibility(View.VISIBLE);
                break;
        }

        RM.setOnClickListener(rm -> {
            dialog.dismiss();
            rv.setAdapter(null);
            NF.setVisibility(View.GONE);
            loader.setVisibility(View.VISIBLE);

            switch (listType) {
                case "all_songs":
                    new Handler(Looper.getMainLooper()).postDelayed(() ->
                            rv.setAdapter(new AllSongs(context, Utils.getAllSongs(context), loader, NF)), 10);
                    break;
                case "folders":
                    //TODO:need to get all song folders
                    break;
                case "albums":
                    new Handler(Looper.getMainLooper()).postDelayed(() ->
                            rv.setAdapter(new Albums(context, Utils.getAlbums(context), loader, NF)), 10);
                    break;
                case "artists":
                    new Handler(Looper.getMainLooper()).postDelayed(() ->
                            rv.setAdapter(new Artists(context, Utils.getArtists(context), loader, NF)), 10);
                    break;
                case "genres":
                    new Handler(Looper.getMainLooper()).postDelayed(() ->
                            rv.setAdapter(new Genres(context, Utils.getGenres(context), loader, NF)), 10);
                    break;
                case "playlists":
                    new Handler(Looper.getMainLooper()).postDelayed(() ->
                            rv.setAdapter(new Playlists(context, Utils.getPlaylists(context), loader, NF)), 10);
                    break;
                case "top_rated":
                    new Handler(Looper.getMainLooper()).postDelayed(() ->
                            rv.setAdapter(new AllSongs(context, getTR(context), loader, NF)), 10);
                    break;
                case "low_rated":
                    new Handler(Looper.getMainLooper()).postDelayed(() ->
                            rv.setAdapter(new AllSongs(context, getLR(context), loader, NF)), 10);
                    break;
                case "recently_added":
                    new Handler(Looper.getMainLooper()).postDelayed(() ->
                            rv.setAdapter(new AllSongs(context, Utils.getRecentlyAdded(context), loader, NF)), 10);
                    break;
            }
            new Handler(Looper.myLooper()).postDelayed(() -> {
                if (rv.getAdapter() == null || rv.getAdapter().getItemCount() == 0)
                    disableBtns();
            }, 20);
        });
        AN.setOnClickListener(an -> {
            dialog.dismiss();

            if (listType.equals("playlists")) {
                Dialog nameDialog = getDialog(this, R.layout.name_dg);
                TextView okBtn = nameDialog.findViewById(R.id.ok_btn);
                TextView cancel = nameDialog.findViewById(R.id.cancel_btn);
                EditText plEditText = nameDialog.findViewById(R.id.playlist_edit_text);
                plEditText.requestFocus();

                cancel.setOnClickListener(c -> nameDialog.dismiss());
                okBtn.setOnClickListener(ok -> {
                    String playlistName = plEditText.getText().toString().trim();

                    if (!TextUtils.isEmpty(playlistName)) {
                        Utils.createNewPlaylist(context, playlistName);
                        Playlists playlists = new Playlists(context, getPlaylists(context), loader, NF);
                        rv.setAdapter(playlists);
                        playlists.notifyDataSetChanged();
                        nameDialog.dismiss();
                    }

                    if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0) {
                        searchBtn.setAlpha(1f);
                        NF.setVisibility(View.GONE);
                        searchBtn.setOnClickListener(sb -> startActivity(new Intent(this, Search.class).putExtra("search_type", listType)));
                    }
                });
            }
        });
    }

    private void getAllSongs(String color) {
        title.setText("All Songs");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_all_songs));
        icon.setColorFilter(Color.parseColor(color));

        if (Utils.songs == null || Utils.songs.size() == 0)
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    rv.setAdapter(new AllSongs(context, Utils.getAllSongs(context), loader, NF)), 10);
        else rv.setAdapter(new AllSongs(context, Utils.songs, loader, NF));
    }

    private void getFolders(String color) {
        title.setText("Folders");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_folders));
        icon.setColorFilter(Color.parseColor(color));

        playAllBtn.setAlpha(0.4f);
        shuffleAllBtn.setAlpha(0.4f);
        playAllBtn.setOnClickListener(null);
        shuffleAllBtn.setOnClickListener(null);
    }

    private void getAlbums(String color) {
        title.setText("Albums");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_album));
        icon.setColorFilter(Color.parseColor(color));

        playAllBtn.setAlpha(0.4f);
        shuffleAllBtn.setAlpha(0.4f);
        playAllBtn.setOnClickListener(null);
        shuffleAllBtn.setOnClickListener(null);

        if (Utils.albums == null || Utils.albums.size() == 0)
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    rv.setAdapter(new Albums(context, Utils.getAlbums(context), loader, NF)), 10);
        else rv.setAdapter(new Albums(context, Utils.albums, loader, NF));
    }

    private void getArtists(String color) {
        title.setText("Artists");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic));
        icon.setColorFilter(Color.parseColor(color));

        playAllBtn.setAlpha(0.4f);
        shuffleAllBtn.setAlpha(0.4f);
        playAllBtn.setOnClickListener(null);
        shuffleAllBtn.setOnClickListener(null);

        if (Utils.artists == null || Utils.artists.size() == 0)
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    rv.setAdapter(new Artists(context, Utils.getArtists(context), loader, NF)), 10);
        else rv.setAdapter(new Artists(context, Utils.artists, loader, NF));
    }

    private void getGenres(String color) {
        title.setText("Genre");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_genres));
        icon.setColorFilter(Color.parseColor(color));

        playAllBtn.setAlpha(0.4f);
        shuffleAllBtn.setAlpha(0.4f);
        playAllBtn.setOnClickListener(null);
        shuffleAllBtn.setOnClickListener(null);

        if (Utils.genres == null || Utils.genres.size() == 0)
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    rv.setAdapter(new Genres(context, Utils.getGenres(context), loader, NF)), 10);
        else rv.setAdapter(new Genres(context, Utils.genres, loader, NF));
    }

    private void getPlayLists(String color) {
        title.setText("Playlists");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_playlist));
        icon.setColorFilter(Color.parseColor(color));

        playAllBtn.setAlpha(0.4f);
        shuffleAllBtn.setAlpha(0.4f);
        playAllBtn.setOnClickListener(null);
        shuffleAllBtn.setOnClickListener(null);

        rv.setAdapter(new Playlists(context, getPlaylists(context), loader, NF));
    }

    private void getTopRated(String color) {
        title.setText("Top Rated");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_top_rated));
        icon.setColorFilter(Color.parseColor(color));

        rv.setAdapter(new AllSongs(context, getTR(context), loader, NF));
    }

    private void getLowRated(String color) {
        title.setText("Low Rated");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_low_rated));
        icon.setColorFilter(Color.parseColor(color));

        rv.setAdapter(new AllSongs(context, getLR(context), loader, NF));
    }

    private void getRecentlyAdded(String color) {
        title.setText("Recently Added");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_added));
        icon.setColorFilter(Color.parseColor(color));

        rv.setAdapter(new AllSongs(context, Utils.getRecentlyAdded(context), loader, NF));
    }

    private void startShufflePlr(ArrayList<Song> list) {
        songs = list;
        shuffle = true;
        repeat = false;
        position = new Random().nextInt((songs.size() - 1) + 1);

        engine = new Engine(this);
        engine.startPlayer();
        mp.setOnCompletionListener(this);
    }

    private void startSequencePlr(ArrayList<Song> list) {
        songs = list;
        shuffle = false;
        repeat = false;
        position = 0;

        engine = new Engine(this);
        engine.startPlayer();
        mp.setOnCompletionListener(this);
    }

    private void disableBtns() {
        searchBtn.setAlpha(0.4f);
        playAllBtn.setAlpha(0.4f);
        shuffleAllBtn.setAlpha(0.4f);

        searchBtn.setOnClickListener(null);
        playAllBtn.setOnClickListener(null);
        shuffleAllBtn.setOnClickListener(null);
    }

    @Override
    protected void onResume() {
        switch (listType) {
            case "all_songs":
                if (rv.getAdapter() != null) rv.getAdapter().notifyDataSetChanged();
                break;
            case "top_rated":
                rv.setAdapter(new AllSongs(context, getTR(context), loader, NF));
                rv.scrollToPosition(lastPosition);
                if (rv.getAdapter().getItemCount() == 0) disableBtns();
                break;
            case "low_rated":
                rv.setAdapter(new AllSongs(context, getLR(context), loader, NF));
                rv.scrollToPosition(lastPosition);
                if (rv.getAdapter().getItemCount() == 0) disableBtns();
                break;
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (listType.equals("top_rated") || listType.equals("low_rated"))
            lastPosition = ((LinearLayoutManager) rv.getLayoutManager()).findFirstVisibleItemPosition();
        super.onPause();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        engine.playNextSong();
        if (mp != null) {
            mp = MediaPlayer.create(getApplicationContext(), uri);
            mp.start();
            mp.setOnCompletionListener(this);
        }
    }
}