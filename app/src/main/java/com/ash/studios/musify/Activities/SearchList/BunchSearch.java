package com.ash.studios.musify.Activities.SearchList;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.Activities.Player;
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

public class BunchSearch extends AppCompatActivity implements MediaPlayer.OnCompletionListener, IControl {
    ImageView shuffleBtn, sequenceBtn, optionBtn, snipArt, snipPlayBtn;
    TextView searchType, snipTitle, snipArtist;
    EditText searchText;
    CardView snippet;
    ImageView close;
    RecyclerView rv;

    Engine engine;
    Context context;
    ArrayList<Song> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        setUpUI(this);

        setIDs();
        snippet.setOnClickListener(v -> startActivity(new Intent(context, Player.class)));
        new Thread() {
            @Override
            public void run() {
                super.run();
                snipPlayBtn.setOnClickListener(v -> {
                    if (Instance.mp != null) {
                        if (Instance.mp.isPlaying()) {
                            snipPlayBtn.setImageResource(R.drawable.ic_play_small);
                            Instance.mp.pause();
                        } else {
                            snipPlayBtn.setImageResource(R.drawable.ic_pause);
                            Instance.mp.start();
                        }
                    } else {
                        engine.startPlayer();
                        snipPlayBtn.setImageResource(R.drawable.ic_pause);
                    }
                });
            }
        }.start();
    }

    @SuppressWarnings("unchecked")
    private void setIDs() {
        ArrayList<Song> temp = (ArrayList<Song>) getIntent().getSerializableExtra("list");
        list = temp == null ? new ArrayList<>() : temp;

        context = this;
        engine = new Engine(context);
        close = findViewById(R.id.close);
        rv = findViewById(R.id.search_rv);
        snippet = findViewById(R.id.snippet);
        searchText = findViewById(R.id.search_text);
        searchType = findViewById(R.id.search_type);
        optionBtn = findViewById(R.id.search_option);
        shuffleBtn = findViewById(R.id.search_shuffle);
        sequenceBtn = findViewById(R.id.search_sequence);
        snipTitle = snippet.findViewById(R.id.snip_title);
        snipArt = snippet.findViewById(R.id.snip_album_art);
        snipArtist = snippet.findViewById(R.id.snip_artist);
        snipPlayBtn = snippet.findViewById(R.id.snip_play_btn);

        searchText.requestFocus();
        shuffleBtn.setAlpha(0.4f);
        sequenceBtn.setAlpha(0.4f);
        snipTitle.setSelected(true);

        if (Instance.songs != null) updateSnippet();
        rv.setLayoutManager(new LinearLayoutManager(context));
        searchType.setText(getIntent().getStringExtra("list_name"));
        OverScrollDecoratorHelper.setUpOverScroll(rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && !editable.toString().trim().equals("")) {
                    ArrayList<Song> tempList = new ArrayList<>();
                    for (Song songs : list)
                        if (songs.getTitle().toLowerCase().contains(editable.toString().toLowerCase()))
                            tempList.add(songs);

                    rv.setAdapter(new AllSongAdapter(context, tempList, null, null));
                    if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0) {
                        shuffleBtn.setAlpha(1f);
                        sequenceBtn.setAlpha(1f);

                        shuffleBtn.setOnClickListener(v -> shufflePlay(tempList));
                        sequenceBtn.setOnClickListener(v -> sequencePlay(tempList));
                    } else hideBtnAndAdapter();
                } else hideBtnAndAdapter();
            }
        });
    }

    private void sequencePlay(ArrayList<Song> list) {
        Instance.songs = list;
        Instance.shuffle = false;
        Utils.putShflStatus(context, false);
        Instance.position = 0;

        engine.startPlayer();
        Instance.mp.setOnCompletionListener(BunchSearch.this);
        updateSnippet();
    }

    private void shufflePlay(ArrayList<Song> list) {
        Instance.songs = list;
        Instance.shuffle = true;
        Utils.putShflStatus(context, true);
        Instance.position = new Random().nextInt((songs.size() - 1) + 1);

        engine.startPlayer();
        Instance.mp.setOnCompletionListener(BunchSearch.this);
        updateSnippet();
    }

    private void hideBtnAndAdapter() {
        rv.setAdapter(null);
        shuffleBtn.setAlpha(0.4f);
        sequenceBtn.setAlpha(0.4f);

        shuffleBtn.setOnClickListener(null);
        sequenceBtn.setOnClickListener(null);
    }

    private void updateSnippet() {
        if (Instance.songs.size() > 0) {
            snippet.setVisibility(View.VISIBLE);
            snipTitle.setText(Instance.songs.get(Instance.position).getTitle());
            snipArtist.setText(Instance.songs.get(Instance.position).getArtist());
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .placeholder(R.mipmap.icon)
                    .load(Utils.getAlbumArt(Instance.songs.get(Instance.position).getAlbum_id()))
                    .into(snipArt);

            if (Instance.mp != null && Instance.mp.isPlaying())
                snipPlayBtn.setImageResource(R.drawable.ic_pause);
            else
                snipPlayBtn.setImageResource(R.drawable.ic_play_small);
        } else snippet.setVisibility(View.GONE);
    }

    private void hideBtn() {
        shuffleBtn.setAlpha(0.4f);
        sequenceBtn.setAlpha(0.4f);

        shuffleBtn.setOnClickListener(null);
        sequenceBtn.setOnClickListener(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Instance.songs != null) updateSnippet();
        if (Instance.mp != null) Instance.mp.setOnCompletionListener(this);
        if (rv.getAdapter() != null) {
            rv.getAdapter().notifyDataSetChanged();

            if (rv.getAdapter().getItemCount() == 0) hideBtn();
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