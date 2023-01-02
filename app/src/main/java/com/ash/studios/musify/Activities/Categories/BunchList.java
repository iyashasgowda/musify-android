package com.ash.studios.musify.Activities.Categories;

import static com.ash.studios.musify.utils.Constants.COMMON_SORT;
import static com.ash.studios.musify.utils.Instance.mp;
import static com.ash.studios.musify.utils.Instance.songs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
import com.ash.studios.musify.BottomSheets.CommonSort;
import com.ash.studios.musify.Interfaces.IControl;
import com.ash.studios.musify.Interfaces.IService;
import com.ash.studios.musify.Models.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Services.MusicService;
import com.ash.studios.musify.utils.App;
import com.ash.studios.musify.utils.Constants;
import com.ash.studios.musify.utils.Engine;
import com.ash.studios.musify.utils.Instance;
import com.ash.studios.musify.utils.Utils;
import com.bumptech.glide.Glide;
import com.futuremind.recyclerviewfastscroll.FastScroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class BunchList extends AppCompatActivity implements
        MediaPlayer.OnCompletionListener, IControl, IService {
    ImageView coverArt, shuffleAllBtn, sequenceAllBtn, searchBtn, optionBtn, snippetArt, snippetPlayBtn;
    TextView bunchTitle, goBackTo, NF, snippetTitle, snippetArtist;
    ConstraintLayout goBackBtn;
    ProgressBar loader;
    CardView snippet;
    RecyclerView rv;
    FastScroller fs;

    Engine engine;
    String listName;
    Context context;
    ArrayList<Song> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bunch_list);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        setIDs();
        goBackBtn.setOnClickListener(v -> finish());
        snippet.setOnClickListener(v -> startActivity(new Intent(context, Player.class)));
        searchBtn.setOnClickListener(v -> {
            if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0)
                startActivity(new Intent(context, BunchSearch.class)
                        .putExtra("list_name", listName)
                        .putExtra("list", list));
        });
        new Thread(() -> {
            if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0)

                shuffleAllBtn.setOnClickListener(v -> {
                    if (list.size() > 0) {
                        Instance.songs = list;
                        Instance.shuffle = true;
                        Instance.position = new Random().nextInt((songs.size() - 1) + 1);

                        engine.startPlayer();
                        mp.setOnCompletionListener(BunchList.this);

                        updateSnippet();
                        Utils.putShflStatus(context, true);
                        Toast.makeText(context, "Shuffle all songs", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(context, "No songs found in the current list :(", Toast.LENGTH_SHORT).show();
                });
        }).start();
        new Thread(() -> {
            if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0)

                sequenceAllBtn.setOnClickListener(v -> {
                    if (list.size() > 0) {
                        Instance.songs = list;
                        Instance.shuffle = false;
                        Instance.position = 0;

                        engine.startPlayer();
                        mp.setOnCompletionListener(BunchList.this);

                        updateSnippet();
                        Utils.putShflStatus(context, false);
                        Toast.makeText(context, "Sequence all songs", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(context, "No songs found in the current list :(", Toast.LENGTH_SHORT).show();
                });
        }).start();
        new Thread(() -> snippetPlayBtn.setOnClickListener(v -> {
            if (Instance.mp != null) {
                if (Instance.mp.isPlaying()) {
                    snippetPlayBtn.setImageResource(R.drawable.ic_play);
                    Instance.mp.pause();
                    stopService(new Intent(context, MusicService.class));
                } else {
                    snippetPlayBtn.setImageResource(R.drawable.ic_pause);
                    Instance.mp.start();
                    Instance.playing = true;
                    startService(new Intent(context, MusicService.class).setAction(Constants.ACTION.CREATE.getLabel()));
                }
            } else {
                engine.startPlayer();
                snippetPlayBtn.setImageResource(R.drawable.ic_pause);
            }
        })).start();
        optionBtn.setOnClickListener(v -> {
            Dialog dialog = Utils.getDialog(context, R.layout.bunch_dg);

            ImageView bunchArt = dialog.findViewById(R.id.bunch_op_art);
            TextView title = dialog.findViewById(R.id.bunch_op_title);
            TextView count = dialog.findViewById(R.id.bunch_op_count);
            ConstraintLayout LO = dialog.findViewById(R.id.bunch_op_listing);

            title.setText(bunchTitle.getText());
            title.setSelected(true);
            bunchArt.setImageDrawable(coverArt.getDrawable());
            count.setText(new StringBuilder("\u266B ").append(list.size()));

            LO.setOnClickListener(lo -> {
                dialog.dismiss();
                CommonSort commonSort = new CommonSort(context, rv, loader, NF, list);
                commonSort.show(getSupportFragmentManager(), null);
            });
        });
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
        fs = findViewById(R.id.fast_bunch_list);
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
                .placeholder(R.drawable.placeholder)
                .into(coverArt);

        if (Instance.songs != null) updateSnippet();
        rv.setLayoutManager(new LinearLayoutManager(context));
        OverScrollDecoratorHelper.setUpOverScroll(rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        getData();
    }

    private void getData() {
        SharedPreferences prefs = context.getSharedPreferences(COMMON_SORT, MODE_PRIVATE);
        String sort_by = prefs.getString("sort_by", "title");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            switch (sort_by) {
                case "title":
                    list.sort((song1, song2) -> song1.getTitle().compareTo(song2.getTitle()));
                    break;
                case "album":
                    list.sort((song1, song2) -> song1.getAlbum().compareTo(song2.getAlbum()));
                    break;
                case "artist":
                    list.sort((song1, song2) -> song1.getArtist().compareTo(song2.getArtist()));
                    break;
            }
        }
        if (prefs.getBoolean("order_by", false)) Collections.reverse(list);
        rv.setAdapter(new AllSongAdapter(context, list, loader, NF));
        if (rv.getAdapter() == null || rv.getAdapter().getItemCount() == 0) hideAttributes();

        fs.setRecyclerView(rv);
    }

    private void updateSnippet() {
        if (Instance.songs.size() > 0) {
            snippet.setVisibility(View.VISIBLE);
            snippetTitle.setText(Instance.songs.get(Instance.position).getTitle());
            snippetArtist.setText(Instance.songs.get(Instance.position).getArtist());
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .placeholder(R.drawable.placeholder)
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
                snippetPlayBtn.setImageResource(R.drawable.ic_play);
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
    public void onStartService() {
        engine.startPlayer();
        updateSnippet();
    }

    @Override
    public void onPrevClicked() {
        engine.playPrevSong();
        updateSnippet();
        startService(new Intent(context, MusicService.class).setAction(Constants.ACTION.CREATE.getLabel()));
    }

    @Override
    public void onNextClicked() {
        engine.playNextSong();
        updateSnippet();
        startService(new Intent(context, MusicService.class).setAction(Constants.ACTION.CREATE.getLabel()));
    }

    @Override
    public void onPlayClicked() {
        if (Instance.mp != null) mp.start();
        else {
            engine.startPlayer();
            mp.setOnCompletionListener(BunchList.this);
        }
        updateSnippet();
    }

    @Override
    public void onPauseClicked() {
        if (Instance.mp != null) Instance.mp.pause();
        updateSnippet();
    }

    @Override
    public void onStopService() {
        if (Instance.mp != null) {
            Instance.mp.stop();
            Instance.mp.release();
            Instance.mp = null;
        }
        snippetPlayBtn.setImageResource(R.drawable.ic_play);
        stopService(new Intent(context, MusicService.class));
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