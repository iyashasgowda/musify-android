package com.ash.studios.musify.Activities;

import android.annotation.SuppressLint;
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
        if (listType != null) {

            switch (listType) {
                case "all_songs":
                    getAllSongs();
                    break;
                case "folders":
                    getFolders();
                    break;
                case "albums":
                    getAlbums();
                    break;
                case "artists":
                    getArtists();
                    break;
                case "genres":
                    getGenres();
                    break;
                case "play_lists":
                    getPlayLists();
                    break;
                case "top_rated":
                    getTopRated();
                    break;
                case "recently_added":
                    getRecentlyAdded();
                    break;
            }
        }
    }

    private void getAllSongs() {
        activityTitle.setText("All Songs");
        activityIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_all_songs));

        AllSongs allSongs = new AllSongs(this, Utils.getAllSongs(this));
        songListView.setLayoutManager(new LinearLayoutManager(this));
        songListView.setAdapter(allSongs);
    }

    private void getFolders() {
        activityTitle.setText("Folders");
        activityIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_folders));

        /*FolderSongs folderSongs = new FolderSongs(this, GetSongFolders.getSongFolders(this));
        songListView.setLayoutManager(new LinearLayoutManager(this));
        songListView.setAdapter(folderSongs);*/
    }

    private void getAlbums() {
        activityTitle.setText("Albums");
        activityIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_album));
    }

    private void getArtists() {
        activityTitle.setText("Artists");
        activityIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic));
    }

    private void getGenres() {
        activityTitle.setText("Genres");
        activityIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_genres));
    }

    private void getPlayLists() {
        activityTitle.setText("Play Lists");
        activityIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_playlist));
    }

    private void getTopRated() {
        activityTitle.setText("Top Rated");
        activityIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_top_rated));
    }

    private void getRecentlyAdded() {
        activityTitle.setText("Recently Added");
        activityIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_added));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.lib_back) finish();
    }
}