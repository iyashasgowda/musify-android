package com.ash.studios.musify.Activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.Adapters.Albums;
import com.ash.studios.musify.Adapters.AllSongs;
import com.ash.studios.musify.Adapters.Artists;
import com.ash.studios.musify.Adapters.Genres;
import com.ash.studios.musify.Adapters.Playlists;
import com.ash.studios.musify.Model.Album;
import com.ash.studios.musify.Model.Artist;
import com.ash.studios.musify.Model.Genre;
import com.ash.studios.musify.Model.Playlist;
import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Engine;
import com.ash.studios.musify.Utils.Instance;
import com.ash.studios.musify.Utils.Utils;

import java.util.ArrayList;
import java.util.Random;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.ash.studios.musify.Utils.Instance.mp;
import static com.ash.studios.musify.Utils.Instance.position;
import static com.ash.studios.musify.Utils.Instance.repeat;
import static com.ash.studios.musify.Utils.Instance.shuffle;
import static com.ash.studios.musify.Utils.Instance.uri;
import static com.ash.studios.musify.Utils.Utils.getDialog;
import static com.ash.studios.musify.Utils.Utils.getLR;
import static com.ash.studios.musify.Utils.Utils.getNewColor;
import static com.ash.studios.musify.Utils.Utils.getRecentlyAdded;
import static com.ash.studios.musify.Utils.Utils.getTR;
import static com.ash.studios.musify.Utils.Utils.setUpUI;

public class Search extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    ImageView close;
    RecyclerView rv;
    EditText searchText;
    TextView searchType;
    ImageView shuffleBtn, sequenceBtn, optionBtn;

    String type;
    Engine engine;
    Context context;
    ArrayList<Song> songs;
    ArrayList<Genre> genres;
    ArrayList<Album> albums;
    ArrayList<Artist> artists;
    ArrayList<Playlist> playlists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        setUpUI(this);

        setIDs();
    }

    private void setIDs() {
        context = this;
        close = findViewById(R.id.close);
        rv = findViewById(R.id.search_rv);
        searchText = findViewById(R.id.search_text);
        searchType = findViewById(R.id.search_type);
        optionBtn = findViewById(R.id.search_option);
        shuffleBtn = findViewById(R.id.search_shuffle);
        sequenceBtn = findViewById(R.id.search_sequence);
        getListForSearch();

        rv.setLayoutManager(new LinearLayoutManager(context));
        OverScrollDecoratorHelper.setUpOverScroll(rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        searchText.requestFocus();
        shuffleBtn.setAlpha(0.4f);
        sequenceBtn.setAlpha(0.4f);
        close.setOnClickListener(v -> finish());
        optionBtn.setOnClickListener(v -> {
            Dialog dialog = getDialog(context, R.layout.options_dg);
            TextView title = dialog.findViewById(R.id.dialog_name);
            ImageView icon = dialog.findViewById(R.id.dialog_icon);
            ConstraintLayout RM = dialog.findViewById(R.id.rescan_media);
            ConstraintLayout SF = dialog.findViewById(R.id.select_folders);
            ConstraintLayout LO = dialog.findViewById(R.id.listing_options);

            SF.setVisibility(View.GONE);
            RM.setVisibility(View.GONE);

            title.setText("Search");
            icon.setImageResource(R.drawable.ic_search);
            icon.setColorFilter(Color.parseColor(getNewColor()), PorterDuff.Mode.SRC_IN);

            LO.setOnClickListener(lo -> {
                dialog.dismiss();
                Toast.makeText(context, "Whoo whooo!", Toast.LENGTH_SHORT).show();
            });
        });
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().trim().equals(""))
                    switch (type) {
                        case "all_songs":
                            ArrayList<Song> tempSongs = new ArrayList<>();
                            for (Song s : songs)
                                if (s.getTitle().toLowerCase().contains(editable.toString().toLowerCase()))
                                    tempSongs.add(s);

                            updateSongs(tempSongs);
                            break;

                        case "folders":
                            //Todo:yet to get folders
                            break;

                        case "albums":
                            ArrayList<Album> tempAlbums = new ArrayList<>();
                            for (Album al : albums)
                                if (al.getAlbum().toLowerCase().contains(editable.toString().toLowerCase()))
                                    tempAlbums.add(al);

                            updateAlbums(tempAlbums);
                            break;

                        case "artists":
                            ArrayList<Artist> tempArtists = new ArrayList<>();
                            for (Artist ar : artists)
                                if (ar.getArtist().toLowerCase().contains(editable.toString().toLowerCase()))
                                    tempArtists.add(ar);

                            updateArtists(tempArtists);
                            break;

                        case "genres":
                            ArrayList<Genre> tempGenres = new ArrayList<>();
                            for (Genre g : genres)
                                if (g.getGenre().toLowerCase().contains(editable.toString().toLowerCase()))
                                    tempGenres.add(g);

                            updateGenres(tempGenres);
                            break;

                        case "playlists":
                            ArrayList<Playlist> tempPlaylists = new ArrayList<>();
                            for (Playlist p : playlists)
                                if (p.getName().toLowerCase().contains(editable.toString().toLowerCase()))
                                    tempPlaylists.add(p);

                            updatePlaylists(tempPlaylists);
                            break;

                        case "top_rated":
                            ArrayList<Song> tempTR = new ArrayList<>();
                            for (Song s : getTR(context))
                                if (s.getTitle().toLowerCase().contains(editable.toString().toLowerCase()))
                                    tempTR.add(s);

                            updateSongs(tempTR);
                            break;

                        case "low_rated":
                            ArrayList<Song> tempLR = new ArrayList<>();
                            for (Song s : getLR(context))
                                if (s.getTitle().toLowerCase().contains(editable.toString().toLowerCase()))
                                    tempLR.add(s);

                            updateSongs(tempLR);
                            break;

                        case "recently_added":
                            ArrayList<Song> tempRA = new ArrayList<>();
                            for (Song s : getRecentlyAdded(context))
                                if (s.getTitle().toLowerCase().contains(editable.toString().toLowerCase()))
                                    tempRA.add(s);

                            updateSongs(tempRA);
                            break;
                    }
                else hideBtnAndAdapter();
            }
        });
    }

    private void updateSongs(ArrayList<Song> tempSongs) {
        rv.setAdapter(new AllSongs(context, tempSongs, null, null));
        if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0) {
            shuffleBtn.setAlpha(1f);
            sequenceBtn.setAlpha(1f);

            shuffleBtn.setOnClickListener(v -> shufflePlay(tempSongs));
            sequenceBtn.setOnClickListener(v -> sequencePlay(tempSongs));
        } else hideBtnAndAdapter();
    }

    private void updateAlbums(ArrayList<Album> tempAlbums) {
        rv.setAdapter(new Albums(context, tempAlbums, null, null));
        hideBtn();
    }

    private void updateArtists(ArrayList<Artist> tempArtists) {
        rv.setAdapter(new Artists(context, tempArtists, null, null));
        hideBtn();
    }

    private void updateGenres(ArrayList<Genre> tempGenres) {
        rv.setAdapter(new Genres(context, tempGenres, null, null));
        hideBtn();
    }

    private void updatePlaylists(ArrayList<Playlist> tempPlaylists) {
        rv.setAdapter(new Playlists(context, tempPlaylists, null, null));
        hideBtn();
    }

    private void hideBtn() {
        shuffleBtn.setAlpha(0.4f);
        sequenceBtn.setAlpha(0.4f);

        shuffleBtn.setOnClickListener(null);
        sequenceBtn.setOnClickListener(null);
    }

    private void hideBtnAndAdapter() {
        rv.setAdapter(null);
        shuffleBtn.setAlpha(0.4f);
        sequenceBtn.setAlpha(0.4f);

        shuffleBtn.setOnClickListener(null);
        sequenceBtn.setOnClickListener(null);
    }

    private void getListForSearch() {
        type = getIntent().getStringExtra("search_type");
        if (type != null) {

            switch (type) {
                case "all_songs":
                    searchType.setText("All Songs");
                    songs = Utils.songs;
                    break;
                case "folders":
                    searchType.setText("Folders");
                    //TODO:yet to get all the folders
                    break;
                case "albums":
                    searchType.setText("Albums");
                    albums = Utils.albums;
                    break;
                case "artists":
                    searchType.setText("Artists");
                    artists = Utils.artists;
                    break;
                case "genres":
                    searchType.setText("Genres");
                    genres = Utils.genres;
                    break;
                case "playlists":
                    searchType.setText("Playlists");
                    playlists = Utils.getPlaylists(context);
                    break;
                case "top_rated":
                    searchType.setText("Top Rated");
                    songs = getTR(context);
                    break;
                case "low_rated":
                    searchType.setText("Low Rated");
                    songs = Utils.getLR(context);
                    break;
                case "recently_added":
                    searchType.setText("Recently Added");
                    songs = Utils.getRecentlyAdded(context);
                    break;
            }
        }
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