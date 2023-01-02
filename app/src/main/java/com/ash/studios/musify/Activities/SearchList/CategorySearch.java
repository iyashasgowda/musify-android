package com.ash.studios.musify.Activities.SearchList;

import static com.ash.studios.musify.utils.Instance.mp;
import static com.ash.studios.musify.utils.Instance.songs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ash.studios.musify.Activities.Player;
import com.ash.studios.musify.Adapters.AlbumAdapter;
import com.ash.studios.musify.Adapters.AllSongAdapter;
import com.ash.studios.musify.Adapters.ArtistAdapter;
import com.ash.studios.musify.Adapters.FolderAdapter;
import com.ash.studios.musify.Adapters.GenreAdapter;
import com.ash.studios.musify.Adapters.PlaylistAdapter;
import com.ash.studios.musify.Adapters.YearAdapter;
import com.ash.studios.musify.Interfaces.IControl;
import com.ash.studios.musify.Interfaces.IService;
import com.ash.studios.musify.Models.Album;
import com.ash.studios.musify.Models.Artist;
import com.ash.studios.musify.Models.Folder;
import com.ash.studios.musify.Models.Genre;
import com.ash.studios.musify.Models.Playlist;
import com.ash.studios.musify.Models.Song;
import com.ash.studios.musify.Models.Year;
import com.ash.studios.musify.R;
import com.ash.studios.musify.Services.MusicService;
import com.ash.studios.musify.utils.App;
import com.ash.studios.musify.utils.Constants;
import com.ash.studios.musify.utils.Engine;
import com.ash.studios.musify.utils.Instance;
import com.ash.studios.musify.utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class CategorySearch extends AppCompatActivity implements MediaPlayer.OnCompletionListener, IControl, IService {
    ImageView shuffleBtn, sequenceBtn, optionBtn, snipArt, snipPlayBtn, close;
    TextView searchType, snipTitle, snipArtist;
    EditText searchText;
    CardView snippet;
    RecyclerView rv;

    int type;
    Engine engine;
    Context context;
    String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        setIDs();
        close.setOnClickListener(v -> finish());
        snippet.setOnClickListener(v -> startActivity(new Intent(context, Player.class)));
        new Thread(() -> snipPlayBtn.setOnClickListener(v -> {
            if (Instance.mp != null) {
                if (Instance.mp.isPlaying()) {
                    snipPlayBtn.setImageResource(R.drawable.ic_play);
                    Instance.mp.pause();
                    stopService(new Intent(context, MusicService.class));
                } else {
                    snipPlayBtn.setImageResource(R.drawable.ic_pause);
                    Instance.mp.start();
                    Instance.playing = true;
                    startService(new Intent(context, MusicService.class).setAction(Constants.ACTION.CREATE.getLabel()));
                }
            } else {
                engine.startPlayer();
                snipPlayBtn.setImageResource(R.drawable.ic_pause);
            }
        })).start();
    }

    private void setIDs() {
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
        categoryName = getIntent().getStringExtra("cat_name");
        type = getIntent().getIntExtra("cat_key", 0);

        searchText.requestFocus();
        optionBtn.setAlpha(0.4f);
        shuffleBtn.setAlpha(0.4f);
        sequenceBtn.setAlpha(0.4f);
        snipTitle.setSelected(true);

        searchType.setText(categoryName);
        if (Instance.songs != null) updateSnippet();
        rv.setLayoutManager(new LinearLayoutManager(context));
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
                    switch (type) {
                        case 0:
                            ArrayList<Song> tempSongs = new ArrayList<>();
                            for (Song s : Utils.getAllSongs(context))
                                if (s.getTitle().toLowerCase().contains(editable.toString().toLowerCase()))
                                    tempSongs.add(s);

                            updateSongs(tempSongs);
                            break;

                        case 1:
                            ArrayList<Folder> tempFolders = new ArrayList<>();
                            for (Folder fo : Utils.getFolders(context))
                                if (fo.getName().toLowerCase().contains(editable.toString().toLowerCase()))
                                    tempFolders.add(fo);

                            updateFolders(tempFolders);
                            break;

                        case 2:
                            ArrayList<Album> tempAlbums = new ArrayList<>();
                            for (Album al : Utils.albums)
                                if (al.getAlbum().toLowerCase().contains(editable.toString().toLowerCase()))
                                    tempAlbums.add(al);

                            updateAlbums(tempAlbums);
                            break;

                        case 3:
                            ArrayList<Artist> tempArtists = new ArrayList<>();
                            for (Artist ar : Utils.artists)
                                if (ar.getArtist().toLowerCase().contains(editable.toString().toLowerCase()))
                                    tempArtists.add(ar);

                            updateArtists(tempArtists);
                            break;

                        case 4:
                            ArrayList<Genre> tempGenres = new ArrayList<>();
                            for (Genre g : Utils.genres)
                                if (g.getGenre().toLowerCase().contains(editable.toString().toLowerCase()))
                                    tempGenres.add(g);

                            updateGenres(tempGenres);
                            break;

                        case 5:
                            ArrayList<Playlist> tempPlaylists = new ArrayList<>();
                            for (Playlist p : Utils.getPlaylists(context))
                                if (p.getName().toLowerCase().contains(editable.toString().toLowerCase()))
                                    tempPlaylists.add(p);

                            updatePlaylists(tempPlaylists);
                            break;

                        case 6:
                            ArrayList<Song> tempTR = new ArrayList<>();
                            for (Song s : Utils.getTR(context))
                                if (s.getTitle().toLowerCase().contains(editable.toString().toLowerCase()))
                                    tempTR.add(s);

                            updateSongs(tempTR);
                            break;

                        case 7:
                            ArrayList<Song> tempLR = new ArrayList<>();
                            for (Song s : Utils.getLR(context))
                                if (s.getTitle().toLowerCase().contains(editable.toString().toLowerCase()))
                                    tempLR.add(s);

                            updateSongs(tempLR);
                            break;
                        case 8:
                            ArrayList<Year> tempYear = new ArrayList<>();
                            for (Year year : Utils.years)
                                if (year.getYear().contains(editable.toString()))
                                    tempYear.add(year);

                            updateYears(tempYear);
                            break;

                    }
                } else hideBtnAndAdapter();
            }
        });
    }

    private void updateFolders(ArrayList<Folder> list) {
        rv.setAdapter(new FolderAdapter(context, list, null, null));
        if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0) {

            new Thread(() -> {

                ArrayList<Song> folderSongs = new ArrayList<>();
                for (Folder folder : list)
                    folderSongs.addAll(folder.getSongs());

                if (folderSongs.size() > 0)
                    shuffleBtn.post(() -> {

                        if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0) {

                            shuffleBtn.setAlpha(1f);
                            sequenceBtn.setAlpha(1f);
                            shuffleBtn.setOnClickListener(v -> shufflePlay(folderSongs));
                            sequenceBtn.setOnClickListener(v -> sequencePlay(folderSongs));
                        } else hideBtnAndAdapter();
                    });
            }).start();
        } else hideBtnAndAdapter();
    }

    private void updateYears(ArrayList<Year> list) {
        rv.setAdapter(new YearAdapter(context, list, null, null));
        if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0) {

            new Thread(() -> {

                ArrayList<Song> yearSongs = new ArrayList<>();
                for (Year year : list)
                    yearSongs.addAll(year.getSongs());

                if (yearSongs.size() > 0)
                    shuffleBtn.post(() -> {

                        if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0) {
                            shuffleBtn.setAlpha(1f);
                            sequenceBtn.setAlpha(1f);
                            shuffleBtn.setOnClickListener(v -> shufflePlay(yearSongs));
                            sequenceBtn.setOnClickListener(v -> sequencePlay(yearSongs));
                        } else hideBtnAndAdapter();
                    });
            }).start();
        } else hideBtnAndAdapter();
    }

    private void updateSongs(ArrayList<Song> list) {
        rv.setAdapter(new AllSongAdapter(context, list, null, null));
        if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0) {
            shuffleBtn.setAlpha(1f);
            sequenceBtn.setAlpha(1f);

            shuffleBtn.setOnClickListener(v -> shufflePlay(list));
            sequenceBtn.setOnClickListener(v -> sequencePlay(list));
        } else hideBtnAndAdapter();
    }

    private void updateAlbums(ArrayList<Album> list) {
        rv.setAdapter(new AlbumAdapter(context, list, null, null));
        if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0) {

            new Thread(() -> {

                ArrayList<Song> albumSongs = new ArrayList<>();
                for (Album album : list)
                    albumSongs.addAll(Utils.getAlbumSongs(context, album.getAlbum_id()));

                if (albumSongs.size() > 0)
                    shuffleBtn.post(() -> {

                        if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0) {

                            shuffleBtn.setAlpha(1f);
                            sequenceBtn.setAlpha(1f);
                            shuffleBtn.setOnClickListener(v -> shufflePlay(albumSongs));
                            sequenceBtn.setOnClickListener(v -> sequencePlay(albumSongs));
                        } else hideBtnAndAdapter();
                    });
            }).start();
        } else hideBtnAndAdapter();
    }

    private void updateArtists(ArrayList<Artist> list) {
        rv.setAdapter(new ArtistAdapter(context, list, null, null));
        if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0) {

            new Thread(() -> {

                ArrayList<Song> artistSongs = new ArrayList<>();
                for (Artist artist : list)
                    artistSongs.addAll(Utils.getArtistSongs(context, artist.getArtist_id()));

                if (artistSongs.size() > 0)
                    shuffleBtn.post(() -> {

                        if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0) {
                            shuffleBtn.setAlpha(1f);
                            sequenceBtn.setAlpha(1f);
                            shuffleBtn.setOnClickListener(v -> shufflePlay(artistSongs));
                            sequenceBtn.setOnClickListener(v -> sequencePlay(artistSongs));
                        } else hideBtnAndAdapter();
                    });
            }).start();
        } else hideBtnAndAdapter();
    }

    private void updateGenres(ArrayList<Genre> list) {
        rv.setAdapter(new GenreAdapter(context, list, null, null));
        if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0) {

            new Thread(() -> {

                ArrayList<Song> genreSongs = new ArrayList<>();
                for (Genre genre : list)
                    genreSongs.addAll(Utils.getGenreSongs(context, genre.getGenre_id()));

                if (genreSongs.size() > 0)
                    shuffleBtn.post(() -> {

                        if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0) {

                            shuffleBtn.setAlpha(1f);
                            sequenceBtn.setAlpha(1f);
                            shuffleBtn.setOnClickListener(v -> shufflePlay(genreSongs));
                            sequenceBtn.setOnClickListener(v -> sequencePlay(genreSongs));
                        } else hideBtnAndAdapter();
                    });
            }).start();
        } else hideBtnAndAdapter();
    }

    private void updatePlaylists(ArrayList<Playlist> list) {
        rv.setAdapter(new PlaylistAdapter(context, list, null, null));
        if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0) {

            new Thread(() -> {

                ArrayList<Song> playlistSongs = new ArrayList<>();
                for (Playlist playlist : list)
                    playlistSongs.addAll(playlist.getSongs());

                if (playlistSongs.size() > 0)
                    shuffleBtn.post(() -> {

                        if (rv.getAdapter() != null && rv.getAdapter().getItemCount() > 0) {

                            shuffleBtn.setAlpha(1f);
                            sequenceBtn.setAlpha(1f);
                            shuffleBtn.setOnClickListener(v -> shufflePlay(playlistSongs));
                            sequenceBtn.setOnClickListener(v -> sequencePlay(playlistSongs));
                        } else hideBtnAndAdapter();
                    });
            }).start();
        } else hideBtnAndAdapter();
    }

    private void hideBtn() {
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
                    .placeholder(R.drawable.placeholder)
                    .load(Utils.getAlbumArt(Instance.songs.get(Instance.position).getAlbum_id()))
                    .into(snipArt);

            int[] accents = Utils.getSecondaryColors(context, Utils.getAlbumArt(Instance.songs.get(Instance.position).getAlbum_id()));
            snippet.setCardBackgroundColor(accents[0]);
            snipTitle.setTextColor(accents[1]);
            snipArtist.setTextColor(accents[1]);
            snipPlayBtn.setColorFilter(accents[1], PorterDuff.Mode.SRC_IN);

            if (Instance.mp != null && Instance.mp.isPlaying())
                snipPlayBtn.setImageResource(R.drawable.ic_pause);
            else
                snipPlayBtn.setImageResource(R.drawable.ic_play);
        } else snippet.setVisibility(View.GONE);
    }

    private void hideBtnAndAdapter() {
        rv.setAdapter(null);
        shuffleBtn.setAlpha(0.4f);
        sequenceBtn.setAlpha(0.4f);

        shuffleBtn.setOnClickListener(null);
        sequenceBtn.setOnClickListener(null);
    }

    private void shufflePlay(ArrayList<Song> list) {
        Instance.songs = list;
        Instance.shuffle = true;
        Utils.putShflStatus(context, true);
        Instance.position = new Random().nextInt((songs.size() - 1) + 1);

        engine.startPlayer();
        Instance.mp.setOnCompletionListener(CategorySearch.this);
        updateSnippet();
    }

    private void sequencePlay(ArrayList<Song> list) {
        Instance.songs = list;
        Instance.shuffle = false;
        Utils.putShflStatus(context, false);
        Instance.position = 0;

        engine.startPlayer();
        Instance.mp.setOnCompletionListener(CategorySearch.this);
        updateSnippet();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((App) getApplicationContext()).setCurrentContext(context);
        if (Instance.songs != null) updateSnippet();
        if (Instance.mp != null) Instance.mp.setOnCompletionListener(this);
        if (rv.getAdapter() != null) {
            rv.getAdapter().notifyDataSetChanged();
            if (rv.getAdapter().getItemCount() == 0) hideBtn();
        }
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
            mp.setOnCompletionListener(CategorySearch.this);
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
        snipPlayBtn.setImageResource(R.drawable.ic_play);
        stopService(new Intent(context, MusicService.class));
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