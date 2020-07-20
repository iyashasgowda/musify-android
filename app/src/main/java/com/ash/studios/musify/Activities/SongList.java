package com.ash.studios.musify.Activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.Adapters.AllSongs;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Utils;

@SuppressLint("SetTextI18n")
public class SongList extends AppCompatActivity implements View.OnClickListener {
    ConstraintLayout backToLib;
    RecyclerView songListView;
    TextView activityTitle;
    ImageView activityIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list);
        Utils.setUpUI(this);

        setIDs();
        setListTitle();
    }

    private void setIDs() {
        backToLib = findViewById(R.id.lib_back);
        songListView = findViewById(R.id.song_list);
        activityIcon = findViewById(R.id.activity_icon);
        activityTitle = findViewById(R.id.activity_title);

        backToLib.setOnClickListener(this);
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
        activityTitle.setText("All Songs");
        activityIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_all_songs));
        activityIcon.setColorFilter(Color.parseColor(color));

        AllSongs allSongs = new AllSongs(this, Utils.getAllSongs(this));
        songListView.setLayoutManager(new LinearLayoutManager(this));
        songListView.setAdapter(allSongs);
    }

    private void getFolders(String color) {
        activityTitle.setText("Folders");
        activityIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_folders));
        activityIcon.setColorFilter(Color.parseColor(color));

        /*FolderSongs folderSongs = new FolderSongs(this, GetSongFolders.getSongFolders(this));
        songListView.setLayoutManager(new LinearLayoutManager(this));
        songListView.setAdapter(folderSongs);*/
    }

    private void getAlbums(String color) {
        activityTitle.setText("Albums");
        activityIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_album));
        activityIcon.setColorFilter(Color.parseColor(color));
    }

    private void getArtists(String color) {
        activityTitle.setText("Artists");
        activityIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic));
        activityIcon.setColorFilter(Color.parseColor(color));
    }

    private void getGenres(String color) {
        activityTitle.setText("Genres");
        activityIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_genres));
        activityIcon.setColorFilter(Color.parseColor(color));
    }

    private void getPlayLists(String color) {
        activityTitle.setText("Play Lists");
        activityIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_playlist));
        activityIcon.setColorFilter(Color.parseColor(color));
    }

    private void getTopRated(String color) {
        activityTitle.setText("Top Rated");
        activityIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_top_rated));
        activityIcon.setColorFilter(Color.parseColor(color));
    }

    private void getRecentlyAdded(String color) {
        activityTitle.setText("Recently Added");
        activityIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_added));
        activityIcon.setColorFilter(Color.parseColor(color));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.lib_back) finish();
    }
}