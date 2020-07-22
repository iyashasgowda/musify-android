package com.ash.studios.musify.Activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
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
    ConstraintLayout backToLib;

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
        title = findViewById(R.id.activity_title);

        backToLib.setOnClickListener(v -> finish());
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
    }

    private void setListTitle() {
        String listType = getIntent().getStringExtra("list_type");
        String iconColor = getIntent().getStringExtra("icon_color");
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

        AllSongs allSongs = new AllSongs(this, Utils.songs);
        rv.setAdapter(allSongs);
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

        Albums albums = new Albums(this, Utils.albums);
        rv.setAdapter(albums);
    }

    private void getArtists(String color) {
        title.setText("Artists");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic));
        icon.setColorFilter(Color.parseColor(color));

        Artists artists = new Artists(this, Utils.artists);
        rv.setAdapter(artists);
    }

    private void getGenres(String color) {
        title.setText("Genre");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_genres));
        icon.setColorFilter(Color.parseColor(color));

        Genres genres = new Genres(this, Utils.genres);
        rv.setAdapter(genres);
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
    }

    private void getRecentlyAdded(String color) {
        title.setText("Recently Added");
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_added));
        icon.setColorFilter(Color.parseColor(color));
    }
}