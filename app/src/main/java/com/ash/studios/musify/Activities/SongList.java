package com.ash.studios.musify.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
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
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Utils;
import com.google.android.material.appbar.AppBarLayout;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.ash.studios.musify.Utils.Utils.getDialog;
import static com.ash.studios.musify.Utils.Utils.getLR;
import static com.ash.studios.musify.Utils.Utils.getPlaylists;
import static com.ash.studios.musify.Utils.Utils.getTR;
import static com.ash.studios.musify.Utils.Utils.setUpUI;

@SuppressLint("SetTextI18n")
public class SongList extends AppCompatActivity {
    TextView title;
    RecyclerView rv;
    ProgressBar loader;
    AppBarLayout appBar;
    ConstraintLayout backToLib;
    ImageView icon, shuffleAllBtn, playAllBtn, searchBtn, optionsBtn;

    Dialog dialog;
    Context context;
    int lastPosition = 0;
    String listType, iconColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);
        setUpUI(this);

        setIDs();
        setListTitle();
    }

    private void setIDs() {
        context = this;
        rv = findViewById(R.id.song_list);
        appBar = findViewById(R.id.app_bar);
        icon = findViewById(R.id.activity_icon);
        backToLib = findViewById(R.id.lib_back);
        loader = findViewById(R.id.list_loader);
        title = findViewById(R.id.activity_title);
        searchBtn = findViewById(R.id.search_btn);
        optionsBtn = findViewById(R.id.options_btn);
        playAllBtn = findViewById(R.id.play_all_btn);
        shuffleAllBtn = findViewById(R.id.shuffle_all_btn);

        backToLib.setOnClickListener(v -> finish());
        rv.setLayoutManager(new LinearLayoutManager(context));
        OverScrollDecoratorHelper.setUpOverScroll(rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        rv.setHasFixedSize(true);

        optionsBtn.setOnClickListener(v -> {
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
                    AN.setVisibility(View.VISIBLE);
                    CL.setVisibility(View.VISIBLE);
                    break;
            }

            RM.setOnClickListener(rm -> {
                rv.setAdapter(null);
                loader.setVisibility(View.VISIBLE);
                dialog.dismiss();

                switch (listType) {
                    case "all_songs":
                        new Handler(Looper.getMainLooper()).postDelayed(() ->
                                rv.setAdapter(new AllSongs(context, Utils.getAllSongs(context), loader)), 10);
                        break;
                    case "folders":
                        //TODO:need to get all song folders
                        break;
                    case "albums":
                        new Handler(Looper.getMainLooper()).postDelayed(() ->
                                rv.setAdapter(new Albums(context, Utils.getAlbums(context), loader)), 10);
                        break;
                    case "artists":
                        new Handler(Looper.getMainLooper()).postDelayed(() ->
                                rv.setAdapter(new Artists(context, Utils.getArtists(context), loader)), 10);
                        break;
                    case "genres":
                        new Handler(Looper.getMainLooper()).postDelayed(() ->
                                rv.setAdapter(new Genres(context, Utils.getGenres(context), loader)), 10);
                        break;
                    case "playlists":
                        new Handler(Looper.getMainLooper()).postDelayed(() ->
                                rv.setAdapter(new Playlists(context, Utils.getPlaylists(context), loader)), 10);
                        break;
                    case "top_rated":
                        new Handler(Looper.getMainLooper()).postDelayed(() ->
                                rv.setAdapter(new AllSongs(context, getTR(context), loader)), 10);
                        break;
                    case "low_rated":
                        new Handler(Looper.getMainLooper()).postDelayed(() ->
                                rv.setAdapter(new AllSongs(context, getLR(context), loader)), 10);
                        break;
                    case "recently_added":
                        //TODO:need to get recently added songs
                        break;
                }
            });
            AN.setOnClickListener(an -> {
                dialog.dismiss();
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
                        Playlists playlists = new Playlists(context, getPlaylists(context), loader);
                        rv.setAdapter(playlists);
                        playlists.notifyDataSetChanged();
                        nameDialog.dismiss();
                    }
                });
            });
        });
    }

    private void setListTitle() {
        listType = getIntent().getStringExtra("list_type");
        iconColor = getIntent().getStringExtra("icon_color");
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

    private void getAllSongs(String color) {
        title.setText("All Songs");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_all_songs));
        icon.setColorFilter(Color.parseColor(color));

        if (Utils.songs == null || Utils.songs.size() == 0)
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    rv.setAdapter(new AllSongs(context, Utils.getAllSongs(context), loader)), 10);
        else rv.setAdapter(new AllSongs(context, Utils.songs, loader));
    }

    private void getFolders(String color) {
        title.setText("Folders");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_folders));
        icon.setColorFilter(Color.parseColor(color));
    }

    private void getAlbums(String color) {
        title.setText("Albums");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_album));
        icon.setColorFilter(Color.parseColor(color));

        if (Utils.albums == null || Utils.albums.size() == 0)
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    rv.setAdapter(new Albums(context, Utils.getAlbums(context), loader)), 10);
        else rv.setAdapter(new Albums(context, Utils.albums, loader));
    }

    private void getArtists(String color) {
        title.setText("Artists");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic));
        icon.setColorFilter(Color.parseColor(color));

        if (Utils.artists == null || Utils.artists.size() == 0)
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    rv.setAdapter(new Artists(context, Utils.getArtists(context), loader)), 10);
        else rv.setAdapter(new Artists(context, Utils.artists, loader));
    }

    private void getGenres(String color) {
        title.setText("Genre");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_genres));
        icon.setColorFilter(Color.parseColor(color));

        if (Utils.genres == null || Utils.genres.size() == 0)
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    rv.setAdapter(new Genres(context, Utils.getGenres(context), loader)), 10);
        else rv.setAdapter(new Genres(context, Utils.genres, loader));
    }

    private void getPlayLists(String color) {
        title.setText("Playlists");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_playlist));
        icon.setColorFilter(Color.parseColor(color));

        rv.setAdapter(new Playlists(context, getPlaylists(context), loader));
    }

    private void getTopRated(String color) {
        title.setText("Top Rated");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_top_rated));
        icon.setColorFilter(Color.parseColor(color));

        rv.setAdapter(new AllSongs(context, getTR(context), loader));
    }

    private void getLowRated(String color) {
        title.setText("Low Rated");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_low_rated));
        icon.setColorFilter(Color.parseColor(color));

        rv.setAdapter(new AllSongs(context, getLR(context), loader));
    }

    private void getRecentlyAdded(String color) {
        title.setText("Recently Added");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_added));
        icon.setColorFilter(Color.parseColor(color));
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
            case "top_rated":
                rv.setAdapter(new AllSongs(context, getTR(context), loader));
                rv.scrollToPosition(lastPosition);
                break;
            case "low_rated":
                rv.setAdapter(new AllSongs(context, getLR(context), loader));
                rv.scrollToPosition(lastPosition);
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
}