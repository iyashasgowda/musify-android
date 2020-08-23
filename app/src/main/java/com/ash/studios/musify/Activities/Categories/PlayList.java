package com.ash.studios.musify.Activities.Categories;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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
import com.ash.studios.musify.Adapters.PlaylistAdapter;
import com.ash.studios.musify.Interfaces.IControl;
import com.ash.studios.musify.Model.Playlist;
import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Utils.Engine;
import com.ash.studios.musify.Utils.Instance;
import com.ash.studios.musify.Utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.Random;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.ash.studios.musify.Utils.Instance.songs;
import static com.ash.studios.musify.Utils.Utils.setUpUI;

public class PlayList extends AppCompatActivity implements MediaPlayer.OnCompletionListener, IControl {
    ImageView icon, shufflePlay, sequencePlay, searchBtn, optionsBtn, snippetArt, snippetPlayBtn;
    TextView title, NF, snippetTitle, snippetArtist;
    ConstraintLayout backToLib;
    AppBarLayout appBar;
    ProgressBar loader;
    CardView snippet;
    RecyclerView rv;

    Engine engine;
    Dialog dialog;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_list);
        setUpUI(this);

        setIDs();
        backToLib.setOnClickListener(v -> finish());
        optionsBtn.setOnClickListener(v -> openOptionsDialog());
        snippet.setOnClickListener(v -> startActivity(new Intent(context, Player.class)));
        searchBtn.setOnClickListener(v -> {
            if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0)
                startActivity(new Intent(context, CategorySearch.class)
                        .putExtra("cat_key", 5).putExtra("cat_name", "Playlists"));
        });
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0)

                    shufflePlay.setOnClickListener(v -> {
                        Instance.shuffle = true;
                        Utils.putShflStatus(context, true);

                        new Thread(() -> {
                            ArrayList<Song> list = new ArrayList<>();
                            for (Playlist playlist : Utils.getPlaylists(context))
                                list.addAll(playlist.getSongs());

                            shufflePlay.post(() -> {
                                Instance.songs = list;
                                Instance.position = new Random().nextInt((songs.size() - 1) + 1);

                                if (Instance.songs.size() > 0) engine.startPlayer();
                                Instance.mp.setOnCompletionListener(PlayList.this);

                                updateSnippet();
                                Toast.makeText(context, "Shuffle all songs", Toast.LENGTH_SHORT).show();
                            });
                        }).start();
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
                        Utils.putShflStatus(context, false);

                        new Thread(() -> {
                            ArrayList<Song> list = new ArrayList<>();
                            for (Playlist playlist : Utils.getPlaylists(context))
                                list.addAll(playlist.getSongs());

                            shufflePlay.post(() -> {
                                Instance.songs = list;
                                Instance.position = 0;

                                if (Instance.songs.size() > 0) engine.startPlayer();
                                Instance.mp.setOnCompletionListener(PlayList.this);

                                updateSnippet();
                                Toast.makeText(context, "Sequence all songs", Toast.LENGTH_SHORT).show();
                            });
                        }).start();
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

        snippetTitle.setSelected(true);
        title.setText(new StringBuilder("Playlists"));
        icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_playlist));
        icon.setColorFilter(Color.parseColor(getIntent().getStringExtra("icon_color")));

        if (Instance.songs != null) updateSnippet();
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setAdapter(new PlaylistAdapter(context, Utils.getPlaylists(context), loader, NF));

        if (rv.getAdapter() == null || rv.getAdapter().getItemCount() == 0) hideAttributes();
        OverScrollDecoratorHelper.setUpOverScroll(rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
    }

    private void openOptionsDialog() {
        dialog = Utils.getDialog(context, R.layout.options_dg);

        TextView dialogName = dialog.findViewById(R.id.dialog_name);
        ImageView dialogIcon = dialog.findViewById(R.id.dialog_icon);
        ConstraintLayout SF = dialog.findViewById(R.id.select_folders);
        ConstraintLayout RM = dialog.findViewById(R.id.rescan_media);
        ConstraintLayout LO = dialog.findViewById(R.id.listing_options);
        ConstraintLayout AN = dialog.findViewById(R.id.add_new);

        AN.setVisibility(View.VISIBLE);
        dialogName.setText(title.getText());
        dialogIcon.setImageDrawable(icon.getDrawable());

        RM.setOnClickListener(rm -> {
            dialog.dismiss();
            rv.setAdapter(null);
            NF.setVisibility(View.GONE);
            loader.setVisibility(View.VISIBLE);

            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    rv.setAdapter(new PlaylistAdapter(context, Utils.getPlaylists(context), loader, NF)), 10);

            new Handler(Looper.myLooper()).postDelayed(() -> {
                if (rv.getAdapter() == null || rv.getAdapter().getItemCount() == 0)
                    disableBtn();
            }, 20);
        });

        AN.setOnClickListener(an -> {
            dialog.dismiss();

            Dialog nameDialog = Utils.getDialog(context, R.layout.name_dg);
            TextView okBtn = nameDialog.findViewById(R.id.ok_btn);
            TextView cancel = nameDialog.findViewById(R.id.cancel_btn);
            EditText plEditText = nameDialog.findViewById(R.id.playlist_edit_text);
            plEditText.requestFocus();

            cancel.setOnClickListener(c -> nameDialog.dismiss());
            okBtn.setOnClickListener(ok -> {
                String playlistName = plEditText.getText().toString().trim();

                if (!TextUtils.isEmpty(playlistName)) {
                    Utils.createNewPlaylist(context, playlistName);
                    PlaylistAdapter adapter = new PlaylistAdapter(context, Utils.getPlaylists(context), loader, NF);
                    rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    nameDialog.dismiss();
                }

                if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0) {
                    searchBtn.setAlpha(1f);
                    NF.setVisibility(View.GONE);
                    searchBtn.setOnClickListener(sb ->
                            startActivity(new Intent(context, CategorySearch.class)
                                    .putExtra("cat_key", 5).putExtra("cat_name", "Playlists")));
                }
            });
        });
    }

    private void disableBtn() {
        searchBtn.setAlpha(0.4f);
        sequencePlay.setAlpha(0.4f);
        shufflePlay.setAlpha(0.4f);

        NF.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);

        searchBtn.setOnClickListener(null);
        sequencePlay.setOnClickListener(null);
        shufflePlay.setOnClickListener(null);
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
                    .placeholder(R.mipmap.icon)
                    .load(Utils.getAlbumArt(Instance.songs.get(Instance.position).getAlbum_id()))
                    .into(snippetArt);

            if (Instance.mp != null && Instance.mp.isPlaying())
                snippetPlayBtn.setImageResource(R.drawable.ic_pause);
            else
                snippetPlayBtn.setImageResource(R.drawable.ic_play_small);
        } else snippet.setVisibility(View.GONE);
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
