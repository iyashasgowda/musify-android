package com.ash.studios.musify.Activities.Categories;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.Activities.Player;
import com.ash.studios.musify.Activities.SearchList.CategorySearch;
import com.ash.studios.musify.Adapters.AlbumAdapter;
import com.ash.studios.musify.BottomSheets.AlbumsSort;
import com.ash.studios.musify.Interfaces.IControl;
import com.ash.studios.musify.Interfaces.IService;
import com.ash.studios.musify.Models.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Services.MusicService;
import com.ash.studios.musify.Utils.App;
import com.ash.studios.musify.Utils.Constants;
import com.ash.studios.musify.Utils.Engine;
import com.ash.studios.musify.Utils.Instance;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.Random;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.ash.studios.musify.Utils.Constants.ALBUMS_SORT;
import static com.ash.studios.musify.Utils.Instance.mp;
import static com.ash.studios.musify.Utils.Instance.songs;
import static com.ash.studios.musify.Utils.Utils.setUpUI;

public class AlbumList extends AppCompatActivity implements
        MediaPlayer.OnCompletionListener, IControl, IService {
    ImageView icon, shufflePlay, sequencePlay, searchBtn, optionsBtn, snippetArt, snippetPlayBtn;
    TextView title, NF, snippetTitle, snippetArtist;
    ConstraintLayout backToLib;
    AppBarLayout appBar;
    ProgressBar loader;
    CardView snippet;
    RecyclerView rv;

    Engine engine;
    Context context;
    SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_list);
        setUpUI(this);

        setIDs();
        backToLib.setOnClickListener(v -> finish());
        snippet.setOnClickListener(v -> startActivity(new Intent(context, Player.class)));
        searchBtn.setOnClickListener(v -> {
            if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0)
                startActivity(new Intent(context, CategorySearch.class)
                        .putExtra("cat_key", 2).putExtra("cat_name", "Albums"));
        });
        new Thread(() -> {
            if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0)
                shufflePlay.setOnClickListener(v -> {

                    String order_by = prefs.getBoolean("order_by", false) ? "desc" : "asc";
                    ArrayList<Song> list = Utils.getAllSongsByCategory(context, "album " + order_by);
                    if (list.size() > 0) {
                        Instance.songs = list;
                        Instance.shuffle = true;
                        Instance.position = new Random().nextInt((songs.size() - 1) + 1);

                        engine.startPlayer();
                        mp.setOnCompletionListener(AlbumList.this);

                        updateSnippet();
                        Utils.putShflStatus(context, true);
                        Toast.makeText(context, "Shuffle all songs", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(context, "No songs found in any albums :(", Toast.LENGTH_SHORT).show();
                });
        }).start();
        new Thread(() -> {
            if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0)
                sequencePlay.setOnClickListener(v -> {

                    String order_by = prefs.getBoolean("order_by", false) ? "desc" : "asc";
                    ArrayList<Song> list = Utils.getAllSongsByCategory(context, "album " + order_by);
                    if (list.size() > 0) {
                        Instance.songs = list;
                        Instance.shuffle = false;
                        Instance.position = 0;

                        engine.startPlayer();
                        mp.setOnCompletionListener(AlbumList.this);

                        updateSnippet();
                        Utils.putShflStatus(context, false);
                        Toast.makeText(context, "Sequence all songs", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(context, "No songs found in any albums :(", Toast.LENGTH_SHORT).show();
                });
        }).start();
        new Thread(() -> snippetPlayBtn.setOnClickListener(v -> {
            if (Instance.mp != null) {
                if (Instance.mp.isPlaying()) {
                    snippetPlayBtn.setImageResource(R.drawable.ic_play_small);
                    Instance.mp.pause();
                    stopService(new Intent(context, MusicService.class));
                } else {
                    snippetPlayBtn.setImageResource(R.drawable.ic_pause);
                    Instance.mp.start();
                    startService(new Intent(context, MusicService.class).setAction(Constants.ACTION.CREATE));
                }
            } else {
                engine.startPlayer();
                snippetPlayBtn.setImageResource(R.drawable.ic_pause);
            }
        })).start();
        optionsBtn.setOnClickListener(v -> {
            Dialog dialog = Utils.getDialog(context, R.layout.options_dg);

            TextView dialogName = dialog.findViewById(R.id.dialog_name);
            ImageView dialogIcon = dialog.findViewById(R.id.dialog_icon);
            ConstraintLayout SF = dialog.findViewById(R.id.select_folders);
            ConstraintLayout RM = dialog.findViewById(R.id.rescan_media);
            ConstraintLayout LO = dialog.findViewById(R.id.listing_options);
            ConstraintLayout ST = dialog.findViewById(R.id.settings);

            dialogName.setText(title.getText());
            dialogIcon.setImageDrawable(icon.getDrawable());

            ST.setVisibility(View.GONE);
            SF.setOnClickListener(sf -> {
                dialog.dismiss();
                Toast.makeText(context, "In development", Toast.LENGTH_SHORT).show();
            });
            RM.setOnClickListener(rm -> {
                dialog.dismiss();

                rv.setAdapter(null);
                NF.setVisibility(View.GONE);
                loader.setVisibility(View.VISIBLE);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    rv.setAdapter(new AlbumAdapter(context, Utils.getAlbums(context), loader, NF));
                    if (rv.getAdapter() == null || rv.getAdapter().getItemCount() == 0)
                        hideAttributes();
                }, 500);
            });
            LO.setOnClickListener(lo -> {
                dialog.dismiss();
                AlbumsSort albumsSort = new AlbumsSort(context, rv, loader, NF);
                albumsSort.show(getSupportFragmentManager(), null);
            });
        });
    }

    private void setIDs() {
        context = this;
        engine = new Engine(context);
        rv = findViewById(R.id.song_list);
        appBar = findViewById(R.id.app_bar);
        snippet = findViewById(R.id.snippet);
        NF = findViewById(R.id.nothing_found);
        icon = findViewById(R.id.activity_icon);
        backToLib = findViewById(R.id.lib_back);
        loader = findViewById(R.id.list_loader);
        title = findViewById(R.id.activity_title);
        searchBtn = findViewById(R.id.search_btn);
        optionsBtn = findViewById(R.id.options_btn);
        sequencePlay = findViewById(R.id.play_all_btn);
        shufflePlay = findViewById(R.id.shuffle_all_btn);
        snippetTitle = snippet.findViewById(R.id.snip_title);
        snippetArt = snippet.findViewById(R.id.snip_album_art);
        snippetArtist = snippet.findViewById(R.id.snip_artist);
        snippetPlayBtn = snippet.findViewById(R.id.snip_play_btn);
        prefs = getSharedPreferences(ALBUMS_SORT, MODE_PRIVATE);

        snippetTitle.setSelected(true);
        title.setText(new StringBuilder("Albums"));
        icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_album));
        icon.setColorFilter(Color.parseColor(getIntent().getStringExtra("icon_color")));

        if (Instance.songs != null) updateSnippet();
        rv.setLayoutManager(new LinearLayoutManager(context));
        if (Utils.albums == null || Utils.albums.size() == 0)
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    rv.setAdapter(new AlbumAdapter(context, Utils.getAlbums(context), loader, NF)), 10);
        else rv.setAdapter(new AlbumAdapter(context, Utils.albums, loader, NF));

        if (rv.getAdapter() == null || rv.getAdapter().getItemCount() == 0) hideAttributes();
        OverScrollDecoratorHelper.setUpOverScroll(rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
    }

    private void hideAttributes() {
        shufflePlay.setAlpha(0.4f);
        sequencePlay.setAlpha(0.4f);
        searchBtn.setAlpha(0.4f);

        shufflePlay.setOnClickListener(null);
        sequencePlay.setOnClickListener(null);
        searchBtn.setOnClickListener(null);
    }

    private void updateSnippet() {
        if (Instance.songs.size() > 0) {
            snippet.setVisibility(View.VISIBLE);
            snippetTitle.setText(Instance.songs.get(Instance.position).getTitle());
            snippetArtist.setText(Instance.songs.get(Instance.position).getArtist());
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .placeholder(R.mipmap.ic_abstract)
                    .load(Utils.getAlbumArt(Instance.songs.get(Instance.position).getAlbum_id()))
                    .into(snippetArt);

            int[] accents = Utils.getSecondaryColors(context, Utils.getAlbumArt(Instance.songs.get(Instance.position).getAlbum_id()));
            snippet.setCardBackgroundColor(accents[0]);
            snippetTitle.setTextColor(accents[1]);
            snippetArtist.setTextColor(accents[1]);
            snippetPlayBtn.setColorFilter(accents[1], PorterDuff.Mode.SRC_IN);

            if (Instance.mp != null && Instance.mp.isPlaying())
                snippetPlayBtn.setImageResource(R.drawable.ic_pause);
            else
                snippetPlayBtn.setImageResource(R.drawable.ic_play_small);
        } else snippet.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((App) getApplicationContext()).setCurrentContext(context);
        if (Instance.songs != null) updateSnippet();
        if (Instance.mp != null) Instance.mp.setOnCompletionListener(this);
        if (rv.getAdapter() != null) {
            rv.getAdapter().notifyDataSetChanged();
            if (rv.getAdapter().getItemCount() == 0) hideAttributes();
        }
    }

    @Override
    public void onStartService() {
        engine.startPlayer();
        updateSnippet();
    }

    @Override
    public void onStopService() {
        if (Instance.mp != null) {
            Instance.mp.stop();
            Instance.mp.release();
            Instance.mp = null;
        }
        snippetPlayBtn.setImageResource(R.drawable.ic_play_small);
        stopService(new Intent(context, MusicService.class));
    }

    @Override
    public void onPrevClicked() {
        engine.playPrevSong();
        updateSnippet();
        startService(new Intent(context, MusicService.class).setAction(Constants.ACTION.CREATE));
    }

    @Override
    public void onNextClicked() {
        engine.playNextSong();
        updateSnippet();
        startService(new Intent(context, MusicService.class).setAction(Constants.ACTION.CREATE));
    }

    @Override
    public void onPlayClicked() {
        if (Instance.mp != null) mp.start();
        else {
            engine.startPlayer();
            mp.setOnCompletionListener(AlbumList.this);
        }
        updateSnippet();
    }

    @Override
    public void onPauseClicked() {
        if (Instance.mp != null) Instance.mp.pause();
        updateSnippet();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        engine.playNextSong();
        if (Instance.mp != null) {
            Instance.mp = MediaPlayer.create(getApplicationContext(), Instance.uri);
            Instance.mp.start();
            Instance.mp.setOnCompletionListener(this);
        }
        Utils.putCurrentPosition(context, Instance.position);
        updateSnippet();
    }
}
