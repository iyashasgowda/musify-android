package com.ash.studios.musify.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.Adapters.AllSongs;
import com.ash.studios.musify.Model.Album;
import com.ash.studios.musify.Model.Artist;
import com.ash.studios.musify.Model.Genre;
import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

@SuppressLint("SetTextI18n")
public class Bunch extends AppCompatActivity {
    TextView bunchTitle, goBackTo;
    ConstraintLayout goBackBtn;
    ImageView coverArt;
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bunch_list);
        Utils.setUpUI(this);

        setIDs();
        setBunchAttrs();
    }

    private void setIDs() {
        rv = findViewById(R.id.bunch_rv);
        coverArt = findViewById(R.id.cover_art);
        goBackTo = findViewById(R.id.go_back_to);
        goBackBtn = findViewById(R.id.go_back_btn);
        bunchTitle = findViewById(R.id.bunch_title);

        goBackBtn.setOnClickListener(v -> finish());
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setBunchAttrs() {
        String type = getIntent().getStringExtra("TYPE");
        if (type != null) {

            switch (type) {
                case "ALBUMS":
                    getAlbumContent();
                    break;
                case "ARTISTS":
                    getArtistContent();
                    break;
                case "GENRES":
                    getGenreContent();
                    break;
            }
        }
    }

    private void getAlbumContent() {
        goBackTo.setText("Albums");
        Album album = (Album) getIntent().getSerializableExtra("CONTENT");

        if (album != null) {
            bunchTitle.setText(album.getAlbum());
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(Utils.getAlbumArt(album.getAlbum_id()))
                    .placeholder(R.mipmap.icon)
                    .into(coverArt);

            rv.setAdapter(new AllSongs(this, Utils.getAlbumSongs(this, album.getAlbum_id())));
        }
    }

    private void getArtistContent() {
        goBackTo.setText("Artists");
        Artist artist = (Artist) getIntent().getSerializableExtra("CONTENT");

        if (artist != null) {
            ArrayList<Song> songs = Utils.getArtistSongs(this, artist.getArtist_id());

            bunchTitle.setText(artist.getArtist());
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(Utils.getAlbumArt(songs.get(0).getAlbum_id()))
                    .placeholder(R.mipmap.icon)
                    .into(coverArt);

            rv.setAdapter(new AllSongs(this, songs));
        }
    }

    private void getGenreContent() {
        goBackTo.setText("Genres");
        Genre genre = (Genre) getIntent().getSerializableExtra("CONTENT");

        if (genre != null) {
            ArrayList<Song> songs = Utils.getGenreSongs(this, genre.getGenre_id());

            bunchTitle.setText(genre.getGenre());
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(Utils.getAlbumArt(songs.get(0).getAlbum_id()))
                    .placeholder(R.mipmap.icon)
                    .into(coverArt);

            rv.setAdapter(new AllSongs(this, songs));
        }
    }
}