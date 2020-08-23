package com.ash.studios.musify.Activities.Categories;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.ash.studios.musify.Adapters.AllSongAdapter;
import com.ash.studios.musify.Interfaces.IControl;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Engine;
import com.ash.studios.musify.Utils.Instance;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;

import java.util.Random;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.ash.studios.musify.Utils.Instance.songs;
import static com.ash.studios.musify.Utils.Utils.setUpUI;

public class AllSongList extends AppCompatActivity implements MediaPlayer.OnCompletionListener, IControl {
    ImageView icon, shufflePlay, sequencePlay, searchBtn, optionsBtn, snippetArt, snippetPlayBtn;
    TextView title, NF, snippetTitle, snippetArtist;
    ConstraintLayout backToLib;
    AppBarLayout appBar;
    ProgressBar loader;
    CardView snippet;
    RecyclerView rv;

    Engine engine;
    Context context;

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
                        .putExtra("cat_key", 0).putExtra("cat_name", "All Songs"));
        });

        new Thread() {
            @Override
            public void run() {
                super.run();
                if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0)
                    shufflePlay.setOnClickListener(v -> {
                        Instance.shuffle = true;
                        Instance.songs = Utils.songs;
                        Utils.putShflStatus(context, true);
                        Instance.position = new Random().nextInt((songs.size() - 1) + 1);

                        engine.startPlayer();
                        Instance.mp.setOnCompletionListener(AllSongList.this);
                        updateSnippet();
                        Toast.makeText(context, "Shuffle all songs", Toast.LENGTH_SHORT).show();
                    });
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0)
                    sequencePlay.setOnClickListener(v -> {
                        Instance.shuffle = false;
                        Instance.songs = Utils.songs;
                        Utils.putShflStatus(context, false);
                        Instance.position = 0;

                        engine.startPlayer();
                        Instance.mp.setOnCompletionListener(AllSongList.this);
                        updateSnippet();
                        Toast.makeText(context, "Sequence all songs", Toast.LENGTH_SHORT).show();
                    });
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                super.run();
                snippetPlayBtn.setOnClickListener(v -> {
                    if (Instance.mp != null) {
                        if (Instance.mp.isPlaying()) {
                            snippetPlayBtn.setImageResource(R.drawable.ic_play_small);
                            Instance.mp.pause();
                        } else {
                            snippetPlayBtn.setImageResource(R.drawable.ic_pause);
                            Instance.mp.start();
                        }
                    } else {
                        engine.startPlayer();
                        snippetPlayBtn.setImageResource(R.drawable.ic_pause);
                    }
                });
            }
        }.start();
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

        title.setText(new StringBuilder("All Songs"));
        snippetTitle.setSelected(true);
        icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_all_songs));
        icon.setColorFilter(Color.parseColor(getIntent().getStringExtra("icon_color")));

        if (Instance.songs != null) updateSnippet();
        rv.setLayoutManager(new LinearLayoutManager(context));
        if (Utils.songs == null || Utils.songs.size() == 0)
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    rv.setAdapter(new AllSongAdapter(context, Utils.getAllSongs(context), loader, NF)), 10);
        else rv.setAdapter(new AllSongAdapter(context, Utils.songs, loader, NF));

        if (rv.getAdapter() == null || rv.getAdapter().getItemCount() == 0) hideAttributes();
        OverScrollDecoratorHelper.setUpOverScroll(rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
    }

    private void updateSnippet() {
        if (Instance.songs.size() > 0) {
            snippet.setVisibility(View.VISIBLE);
            snippetTitle.setText(Instance.songs.get(Instance.position).getTitle());
            snippetArtist.setText(Instance.songs.get(Instance.position).getArtist());
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .placeholder(R.mipmap.icon)
                    .load(Utils.getAlbumArt(Instance.songs.get(Instance.position).getAlbum_id()))
                    .into(snippetArt);

            if (Instance.mp != null && Instance.mp.isPlaying())
                snippetPlayBtn.setImageResource(R.drawable.ic_pause);
            else
                snippetPlayBtn.setImageResource(R.drawable.ic_play_small);
        } else snippet.setVisibility(View.GONE);
    }

    private void hideAttributes() {
        shufflePlay.setAlpha(0.4f);
        sequencePlay.setAlpha(0.4f);
        searchBtn.setAlpha(0.4f);

        shufflePlay.setOnClickListener(null);
        sequencePlay.setOnClickListener(null);
        searchBtn.setOnClickListener(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Instance.songs != null) updateSnippet();
        if (Instance.mp != null) Instance.mp.setOnCompletionListener(this);
        if (rv.getAdapter() != null) {
            rv.getAdapter().notifyDataSetChanged();
            if (rv.getAdapter().getItemCount() == 0) hideAttributes();
        }
    }

    @Override
    public void onStartPlayer() {
        engine.startPlayer();
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
