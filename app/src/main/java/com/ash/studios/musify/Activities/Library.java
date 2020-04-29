package com.ash.studios.musify.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ash.studios.musify.R;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.ash.studios.musify.Utils.Utils.fetchAllSongs;
import static com.ash.studios.musify.Utils.Utils.getNewColor;
import static com.ash.studios.musify.Utils.Utils.getOptionsDialog;
import static com.ash.studios.musify.Utils.Utils.setUpUI;

public class Library extends AppCompatActivity implements View.OnClickListener {
    ImageView allSong, folders, albums, artists, genres, playlists, topRated, lowRated, recentlyAdded, optionsBtn;
    String[] colors = new String[9];
    ScrollView scrollView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);
        setUpUI(this);

        setIds();
        setColors();
    }

    private void setColors() {
        ImageView[] icons = {allSong, folders, albums, artists, genres, playlists, topRated, lowRated, recentlyAdded};
        for (int i = 0; i < icons.length; i++) {
            String col = getNewColor();
            colors[i] = col;
            icons[i].setColorFilter(Color.parseColor(col), PorterDuff.Mode.SRC_IN);
        }
    }

    private void setIds() {
        context = this;
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
        recentlyAdded = findViewById(R.id.recently_added_icon);

        findViewById(R.id.all_songs).setOnClickListener(this);
        findViewById(R.id.folders).setOnClickListener(this);
        findViewById(R.id.albums).setOnClickListener(this);
        findViewById(R.id.artists).setOnClickListener(this);
        findViewById(R.id.genres).setOnClickListener(this);
        findViewById(R.id.play_lists).setOnClickListener(this);
        findViewById(R.id.top_rated).setOnClickListener(this);
        findViewById(R.id.low_rated).setOnClickListener(this);
        findViewById(R.id.recently_added).setOnClickListener(this);

        OverScrollDecoratorHelper.setUpOverScroll(scrollView);
        optionsBtn.setOnClickListener(v -> {
            Dialog dialog = getOptionsDialog(context);
            ImageView icon = dialog.findViewById(R.id.dialog_icon);
            TextView title = dialog.findViewById(R.id.dialog_name);

            title.setText("Library");
            icon.setImageResource(R.drawable.ic_library);
            icon.setColorFilter(Color.parseColor(getNewColor()), PorterDuff.Mode.SRC_IN);

            ConstraintLayout rescanMedia = dialog.findViewById(R.id.rescan_media);
            rescanMedia.setOnClickListener(rm -> {
                fetchAllSongs(context);
                dialog.dismiss();
            });
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, SongList.class);

        switch (view.getId()) {
            case R.id.all_songs:
                intent.putExtra("list_type", "all_songs");
                intent.putExtra("icon_color", colors[0]);
                startActivity(intent);
                break;
            case R.id.folders:
                intent.putExtra("list_type", "folders");
                intent.putExtra("icon_color", colors[1]);
                startActivity(intent);
                break;
            case R.id.albums:
                intent.putExtra("list_type", "albums");
                intent.putExtra("icon_color", colors[2]);
                startActivity(intent);
                break;
            case R.id.artists:
                intent.putExtra("list_type", "artists");
                intent.putExtra("icon_color", colors[3]);
                startActivity(intent);
                break;
            case R.id.genres:
                intent.putExtra("list_type", "genres");
                intent.putExtra("icon_color", colors[4]);
                startActivity(intent);
                break;
            case R.id.play_lists:
                intent.putExtra("list_type", "play_lists");
                intent.putExtra("icon_color", colors[5]);
                startActivity(intent);
                break;
            case R.id.top_rated:
                intent.putExtra("list_type", "top_rated");
                intent.putExtra("icon_color", colors[6]);
                startActivity(intent);
                break;
            case R.id.low_rated:
                intent.putExtra("list_type", "low_rated");
                intent.putExtra("icon_color", colors[7]);
                startActivity(intent);
                break;
            case R.id.recently_added:
                intent.putExtra("list_type", "recently_added");
                intent.putExtra("icon_color", colors[8]);
                startActivity(intent);
                break;
        }
    }
}