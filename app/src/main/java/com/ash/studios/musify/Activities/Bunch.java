package com.ash.studios.musify.Activities;

import android.annotation.SuppressLint;
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
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.ash.studios.musify.Utils.Utils.setUpUI;

@SuppressLint("SetTextI18n")
public class Bunch extends AppCompatActivity {
    TextView bunchTitle, goBackTo, NF;
    ConstraintLayout goBackBtn;
    ProgressBar loader;
    ImageView coverArt;
    RecyclerView rv;

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

        goBackBtn.setOnClickListener(v -> finish());
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);

        OverScrollDecoratorHelper.setUpOverScroll(rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
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
                case "PLAYLISTS":
                    getPlaylistContent();
                    break;
            }
        }

        if (rv.getAdapter() == null || rv.getAdapter().getItemCount() == 0) {
            NF.setVisibility(View.VISIBLE);
            loader.setVisibility(View.GONE);
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

            rv.setAdapter(new AllSongs(this, Utils.getAlbumSongs(this, album.getAlbum_id()), loader, NF));
        }
    }

    private void getArtistContent() {
        goBackTo.setText("Artists");
        Artist artist = (Artist) getIntent().getSerializableExtra("CONTENT");

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
        Genre genre = (Genre) getIntent().getSerializableExtra("CONTENT");

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
        Playlist playlist = (Playlist) getIntent().getSerializableExtra("CONTENT");

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
}