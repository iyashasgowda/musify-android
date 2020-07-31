package com.ash.studios.musify.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.Adapters.AllSongs;
import com.ash.studios.musify.Model.Album;
import com.ash.studios.musify.Model.Artist;
import com.ash.studios.musify.Model.Genre;
import com.ash.studios.musify.Model.Playlist;
import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Engine;
import com.ash.studios.musify.Utils.Instance;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.ash.studios.musify.Utils.Instance.mp;
import static com.ash.studios.musify.Utils.Instance.position;
import static com.ash.studios.musify.Utils.Instance.repeat;
import static com.ash.studios.musify.Utils.Instance.shuffle;
import static com.ash.studios.musify.Utils.Instance.uri;
import static com.ash.studios.musify.Utils.Utils.setUpUI;

@SuppressLint("SetTextI18n")
public class Bunch extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    RecyclerView rv;
    ProgressBar loader;
    ConstraintLayout goBackBtn;
    TextView bunchTitle, goBackTo, NF;
    ImageView coverArt, shuffleAllBtn, sequenceAllBtn, searchBtn;

    Album album;
    Genre genre;
    Engine engine;
    Artist artist;
    String bunchType;
    Playlist playlist;
    boolean selectionMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bunch_list);
        setUpUI(this);

        setIDs();
        setBunchAttrs();
    }

    private void setIDs() {
        rv = findViewById(R.id.bunch_rv);
        loader = findViewById(R.id.bunch_pb);
        NF = findViewById(R.id.nothing_found);
        coverArt = findViewById(R.id.cover_art);
        goBackTo = findViewById(R.id.go_back_to);
        goBackBtn = findViewById(R.id.go_back_btn);
        bunchTitle = findViewById(R.id.bunch_title);
        searchBtn = findViewById(R.id.bunch_search_btn);
        shuffleAllBtn = findViewById(R.id.bunch_shuffle_all_btn);
        sequenceAllBtn = findViewById(R.id.bunch_sequence_all_btn);

        goBackBtn.setOnClickListener(v -> finish());
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);

        OverScrollDecoratorHelper.setUpOverScroll(rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        searchBtn.setOnClickListener(v -> {
            switch (bunchType) {
                case "ALBUMS":
                    if (album != null)
                        startActivity(new Intent(this, Search.class)
                                .putExtra("bunch_title", album.getAlbum())
                                .putExtra("search_type", "custom_search")
                                .putExtra("bunch_list", Utils.getAlbumSongs(this, album.getAlbum_id())));
                    break;
                case "ARTISTS":
                    if (artist != null)
                        startActivity(new Intent(this, Search.class)
                                .putExtra("bunch_title", artist.getArtist())
                                .putExtra("search_type", "custom_search")
                                .putExtra("bunch_list", Utils.getArtistSongs(this, artist.getArtist_id())));
                    break;
                case "GENRES":
                    if (genre != null)
                        startActivity(new Intent(this, Search.class)
                                .putExtra("bunch_title", genre.getGenre())
                                .putExtra("search_type", "custom_search")
                                .putExtra("bunch_list", Utils.getGenreSongs(this, genre.getGenre_id())));
                    break;
                case "PLAYLISTS":
                    if (playlist != null && playlist.getSongs().size() > 0)
                        startActivity(new Intent(this, Search.class)
                                .putExtra("bunch_title", playlist.getName())
                                .putExtra("search_type", "custom_search")
                                .putExtra("bunch_list", playlist.getSongs()));
                    break;
            }
        });
        shuffleAllBtn.setOnClickListener(v -> {
            switch (bunchType) {
                case "ALBUMS":
                    if (album != null) shufflePlay(Utils.getAlbumSongs(this, album.getAlbum_id()));
                    break;
                case "ARTISTS":
                    if (artist != null)
                        shufflePlay(Utils.getArtistSongs(this, artist.getArtist_id()));
                    break;
                case "GENRES":
                    if (genre != null) shufflePlay(Utils.getGenreSongs(this, genre.getGenre_id()));
                    break;
                case "PLAYLISTS":
                    if (playlist != null && playlist.getSongs().size() > 0)
                        shufflePlay(playlist.getSongs());
                    break;
            }
        });
        sequenceAllBtn.setOnClickListener(v -> {
            switch (bunchType) {
                case "ALBUMS":
                    if (album != null) sequencePlay(Utils.getAlbumSongs(this, album.getAlbum_id()));
                    break;
                case "ARTISTS":
                    if (artist != null)
                        sequencePlay(Utils.getArtistSongs(this, artist.getArtist_id()));
                    break;
                case "GENRES":
                    if (genre != null) sequencePlay(Utils.getGenreSongs(this, genre.getGenre_id()));
                    break;
                case "PLAYLISTS":
                    if (playlist != null && playlist.getSongs().size() > 0)
                        sequencePlay(playlist.getSongs());
                    break;
            }
        });
    }

    private void setBunchAttrs() {
        bunchType = getIntent().getStringExtra("TYPE");
        if (bunchType != null) {

            switch (bunchType) {
                case "ALBUMS":
                    getAlbumContent();
                    break;
                case "ARTISTS":
                    getArtistContent();
                    break;
                case "GENRES":
                    getGenreContent();
                    break;
                case "PLAYLISTS":
                    getPlaylistContent();
                    break;
            }
        }
        if (rv.getAdapter() == null || rv.getAdapter().getItemCount() == 0) disableBtns();
    }

    private void getAlbumContent() {
        goBackTo.setText("Albums");
        album = (Album) getIntent().getSerializableExtra("CONTENT");

        if (album != null) {
            bunchTitle.setText(album.getAlbum());
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(Utils.getAlbumArt(album.getAlbum_id()))
                    .placeholder(R.mipmap.icon)
                    .into(coverArt);

            rv.setAdapter(new AllSongs(this, Utils.getAlbumSongs(this, album.getAlbum_id()), loader, NF));
        }
    }

    private void getArtistContent() {
        goBackTo.setText("Artists");
        artist = (Artist) getIntent().getSerializableExtra("CONTENT");

        if (artist != null) {
            bunchTitle.setText(artist.getArtist());
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(Utils.getAlbumArt(artist.getAlbum_id()))
                    .placeholder(R.mipmap.icon)
                    .into(coverArt);

            rv.setAdapter(new AllSongs(this, Utils.getArtistSongs(this, artist.getArtist_id()), loader, NF));
        }
    }

    private void getGenreContent() {
        goBackTo.setText("Genres");
        genre = (Genre) getIntent().getSerializableExtra("CONTENT");

        if (genre != null) {
            bunchTitle.setText(genre.getGenre());
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(Utils.getAlbumArt(genre.getAlbum_id()))
                    .placeholder(R.mipmap.icon)
                    .into(coverArt);

            rv.setAdapter(new AllSongs(this, Utils.getGenreSongs(this, genre.getGenre_id()), loader, NF));
        }
    }

    private void getPlaylistContent() {
        goBackTo.setText("Playlists");
        playlist = (Playlist) getIntent().getSerializableExtra("CONTENT");

        if (playlist != null) {
            bunchTitle.setText(playlist.getName());
            if (playlist.getSongs().size() > 0)
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(Utils.getAlbumArt(playlist.getSongs().get(0).getAlbum_id()))
                        .placeholder(R.mipmap.icon)
                        .into(coverArt);
            else {
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(R.mipmap.icon)
                        .into(coverArt);
            }
            rv.setAdapter(new AllSongs(this, playlist.getSongs(), loader, NF));
        }
    }

    private void disableBtns() {
        searchBtn.setAlpha(0.4f);
        shuffleAllBtn.setAlpha(0.4f);
        sequenceAllBtn.setAlpha(0.4f);

        searchBtn.setOnClickListener(null);
        shuffleAllBtn.setOnClickListener(null);
        sequenceAllBtn.setOnClickListener(null);

        NF.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);
    }

    private void shufflePlay(ArrayList<Song> list) {
        Instance.songs = list;
        shuffle = true;
        repeat = false;
        position = new Random().nextInt((Instance.songs.size() - 1) + 1);

        engine = new Engine(this);
        engine.startPlayer();
        mp.setOnCompletionListener(this);
    }

    private void sequencePlay(ArrayList<Song> list) {
        Instance.songs = list;
        shuffle = false;
        repeat = false;
        position = 0;

        engine = new Engine(this);
        engine.startPlayer();
        mp.setOnCompletionListener(this);
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