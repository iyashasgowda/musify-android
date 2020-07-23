package com.ash.studios.musify.Activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
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
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Utils;

@SuppressLint("SetTextI18n")
public class SongList extends AppCompatActivity {
    TextView title;
    ImageView icon;
    RecyclerView rv;
    ProgressBar loader;
    ConstraintLayout backToLib;

    int lastPosition = 0;
    String listType, iconColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);
        Utils.setUpUI(this);

        setIDs();
        setListTitle();
    }

    private void setIDs() {
        rv = findViewById(R.id.song_list);
        icon = findViewById(R.id.activity_icon);
        backToLib = findViewById(R.id.lib_back);
        loader = findViewById(R.id.list_loader);
        title = findViewById(R.id.activity_title);

        backToLib.setOnClickListener(v -> finish());
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
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
                case "play_lists":
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
    }

    private void getAllSongs(String color) {
        title.setText("All Songs");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_all_songs));
        icon.setColorFilter(Color.parseColor(color));

        if (Utils.songs == null || Utils.songs.size() == 0) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                rv.setAdapter(new AllSongs(SongList.this, Utils.getAllSongs(SongList.this)));
                loader.setVisibility(View.GONE);
            }, 50);
        } else {
            rv.setAdapter(new AllSongs(this, Utils.songs));
            loader.setVisibility(View.GONE);
        }
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

        if (Utils.albums == null || Utils.albums.size() == 0) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                rv.setAdapter(new Albums(SongList.this, Utils.getAlbums(SongList.this)));
                loader.setVisibility(View.GONE);
            }, 50);
        } else {
            rv.setAdapter(new Albums(this, Utils.albums));
            loader.setVisibility(View.GONE);
        }
    }

    private void getArtists(String color) {
        title.setText("Artists");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic));
        icon.setColorFilter(Color.parseColor(color));

        if (Utils.artists == null || Utils.artists.size() == 0) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                rv.setAdapter(new Artists(SongList.this, Utils.getArtists(SongList.this)));
                loader.setVisibility(View.GONE);
            }, 50);
        } else {
            rv.setAdapter(new Artists(this, Utils.artists));
            loader.setVisibility(View.GONE);
        }
    }

    private void getGenres(String color) {
        title.setText("Genre");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_genres));
        icon.setColorFilter(Color.parseColor(color));

        if (Utils.genres == null || Utils.genres.size() == 0) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                rv.setAdapter(new Genres(SongList.this, Utils.getGenres(SongList.this)));
                loader.setVisibility(View.GONE);
            }, 50);
        } else {
            rv.setAdapter(new Genres(this, Utils.genres));
            loader.setVisibility(View.GONE);
        }
    }

    private void getPlayLists(String color) {
        title.setText("Play Lists");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_playlist));
        icon.setColorFilter(Color.parseColor(color));
    }

    private void getTopRated(String color) {
        title.setText("Top Rated");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_top_rated));
        icon.setColorFilter(Color.parseColor(color));

        rv.setAdapter(new AllSongs(this, Utils.getTR(this)));
        loader.setVisibility(View.GONE);
    }

    private void getLowRated(String color) {
        title.setText("Low Rated");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_low_rated));
        icon.setColorFilter(Color.parseColor(color));

        rv.setAdapter(new AllSongs(this, Utils.getLR(this)));
        loader.setVisibility(View.GONE);
    }

    private void getRecentlyAdded(String color) {
        title.setText("Recently Added");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_added));
        icon.setColorFilter(Color.parseColor(color));
    }

    @Override
    protected void onResume() {
        if (listType.equals("top_rated")) {
            rv.setAdapter(new AllSongs(this, Utils.getTR(this)));
            rv.scrollToPosition(lastPosition);
        } else if (listType.equals("low_rated")) {
            rv.setAdapter(new AllSongs(this, Utils.getLR(this)));
            rv.scrollToPosition(lastPosition);
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