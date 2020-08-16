package com.ash.studios.musify.Activities.Categories;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.Activities.Player;
import com.ash.studios.musify.Activities.SearchList.BunchSearch;
import com.ash.studios.musify.Adapters.AllSongAdapter;
import com.ash.studios.musify.Interfaces.IControl;
import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Engine;
import com.ash.studios.musify.Utils.Instance;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.ash.studios.musify.Utils.Instance.songs;
import static com.ash.studios.musify.Utils.Utils.setUpUI;

public class BunchList extends AppCompatActivity implements MediaPlayer.OnCompletionListener, IControl {
    ImageView coverArt, shuffleAllBtn, sequenceAllBtn, searchBtn, optionBtn, snippetArt, snippetPlayBtn;
    TextView bunchTitle, goBackTo, NF, snippetTitle, snippetArtist;
    ConstraintLayout goBackBtn;
    ProgressBar loader;
    CardView snippet;
    RecyclerView rv;

    Engine engine;
    String listName;
    Context context;
    ArrayList<Song> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bunch_list);
        setUpUI(this);

        setIDs();
        goBackBtn.setOnClickListener(v -> finish());
        snippet.setOnClickListener(v -> startActivity(new Intent(context, Player.class)));
        searchBtn.setOnClickListener(v -> startActivity(new Intent(context, BunchSearch.class)
                .putExtra("list_name", listName)
                .putExtra("list", list)));
        new Thread() {
            @Override
            public void run() {
                super.run();
                shuffleAllBtn.setOnClickListener(v -> {
                    Instance.shuffle = true;
                    Instance.songs = list;
                    Utils.putShflStatus(context, true);
                    Instance.position = new Random().nextInt((songs.size() - 1) + 1);

                    engine.startPlayer();
                    Instance.mp.setOnCompletionListener(BunchList.this);
                    updateSnippet();
                    Toast.makeText(context, "Shuffle all songs", Toast.LENGTH_SHORT).show();
                });
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                super.run();
                sequenceAllBtn.setOnClickListener(v -> {
                    Instance.shuffle = false;
                    Instance.songs = list;
                    Utils.putShflStatus(context, false);
                    Instance.position = 0;

                    engine.startPlayer();
                    Instance.mp.setOnCompletionListener(BunchList.this);
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

    @SuppressWarnings("unchecked")
    private void setIDs() {
        listName = getIntent().getStringExtra("list_name");
        ArrayList<Song> temp = (ArrayList<Song>) getIntent().getSerializableExtra("list");
        list = temp == null ? new ArrayList<>() : temp;

        context = this;
        engine = new Engine(context);
        rv = findViewById(R.id.bunch_rv);
        snippet = findViewById(R.id.snippet);
        loader = findViewById(R.id.bunch_pb);
        NF = findViewById(R.id.nothing_found);
        coverArt = findViewById(R.id.cover_art);
        goBackTo = findViewById(R.id.go_back_to);
        goBackBtn = findViewById(R.id.go_back_btn);
        bunchTitle = findViewById(R.id.bunch_title);
        searchBtn = findViewById(R.id.bunch_search_btn);
        optionBtn = findViewById(R.id.bunch_option_btn);
        snippetTitle = snippet.findViewById(R.id.snip_title);
        snippetArt = snippet.findViewById(R.id.snip_album_art);
        snippetArtist = snippet.findViewById(R.id.snip_artist);
        shuffleAllBtn = findViewById(R.id.bunch_shuffle_all_btn);
        snippetPlayBtn = snippet.findViewById(R.id.snip_play_btn);
        sequenceAllBtn = findViewById(R.id.bunch_sequence_all_btn);

        bunchTitle.setText(listName);
        snippetTitle.setSelected(true);
        goBackTo.setText(getIntent().getStringExtra("list_from"));
        if (list.size() > 0) Glide.with(getApplicationContext())
                .asBitmap().load(Utils.getAlbumArt(list.get(0).getAlbum_id()))
                .placeholder(R.mipmap.icon)
                .into(coverArt);

        if (Instance.songs != null) updateSnippet();
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setAdapter(new AllSongAdapter(context, list, loader, NF));
        if (rv.getAdapter() != null && rv.getAdapter().getItemCount() == 0) hideAttributes();
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
        searchBtn.setAlpha(0.4f);
        shuffleAllBtn.setAlpha(0.4f);
        sequenceAllBtn.setAlpha(0.4f);

        searchBtn.setOnClickListener(null);
        shuffleAllBtn.setOnClickListener(null);
        sequenceAllBtn.setOnClickListener(null);
    }

    @Override
    public void onStartPlayer() {
        engine.startPlayer();
        updateSnippet();
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