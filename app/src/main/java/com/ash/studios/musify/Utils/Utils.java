package com.ash.studios.musify.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.Toast;

import com.ash.studios.musify.Model.Album;
import com.ash.studios.musify.Model.Artist;
import com.ash.studios.musify.Model.Genre;
import com.ash.studios.musify.Model.Playlist;
import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

@SuppressLint("InlinedApi, DefaultLocale")
public class Utils {
    public static ArrayList<Song> songs;
    public static ArrayList<Album> albums;
    public static ArrayList<Genre> genres;
    public static ArrayList<Artist> artists;

    public static String getNewColor() {
        return String.format("#%06X", new Random().nextInt((0xFFFFFF - 0x777777) + 1) + 0x777777);
    }

    public static Uri getAlbumArt(long id) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), id);
    }

    public static void setUpUI(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                new Handler(Looper.getMainLooper()).postDelayed(() -> activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN), 1500);
        });
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static ArrayList<Song> getTR(Context c) {
        return new Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(c).getString("TOP_RATED", null),
                new TypeToken<ArrayList<Song>>() {
                }.getType());
    }

    public static ArrayList<Song> getLR(Context c) {
        return new Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(c).getString("LOW_RATED", null),
                new TypeToken<ArrayList<Song>>() {
                }.getType());
    }

    public static String getDuration(long duration) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    public static void saveToTR(Context c, Song song) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        ArrayList<Song> list = new Gson().fromJson(prefs.getString("TOP_RATED", null),
                new TypeToken<ArrayList<Song>>() {
                }.getType());

        if (list != null) {
            if (!list.contains(song)) list.add(song);
            else return;
        } else {
            list = new ArrayList<>();
            list.add(song);
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("TOP_RATED", new Gson().toJson(list)).apply();
    }

    public static void saveToLR(Context c, Song song) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        ArrayList<Song> list = new Gson().fromJson(prefs.getString("LOW_RATED", null),
                new TypeToken<ArrayList<Song>>() {
                }.getType());

        if (list != null) {
            if (!list.contains(song)) list.add(song);
            else return;
        } else {
            list = new ArrayList<>();
            list.add(song);
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("LOW_RATED", new Gson().toJson(list)).apply();
    }

    public static Dialog getDialog(Context c, int layout) {
        Dialog dialog = new Dialog(c);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dg_anim;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        ScrollView scrollView = dialog.findViewById(R.id.dialog_scroll_view);
        if (scrollView != null) OverScrollDecoratorHelper.setUpOverScroll(scrollView);
        return dialog;
    }

    public static void deleteFromTR(Context c, Song song) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        ArrayList<Song> list = new Gson().fromJson(prefs.getString("TOP_RATED", null),
                new TypeToken<ArrayList<Song>>() {
                }.getType());

        if (list != null) list.remove(song);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("TOP_RATED", new Gson().toJson(list)).apply();
    }

    public static void deleteFromLR(Context c, Song song) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        ArrayList<Song> list = new Gson().fromJson(prefs.getString("LOW_RATED", null),
                new TypeToken<ArrayList<Song>>() {
                }.getType());

        if (list != null) list.remove(song);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("LOW_RATED", new Gson().toJson(list)).apply();
    }

    public static ArrayList<Playlist> getPlaylists(Context c) {
        return new Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(c).getString("PLAYLISTS", null),
                new TypeToken<ArrayList<Playlist>>() {
                }.getType());
    }

    public static ArrayList<Genre> getGenres(Context context) {
        ArrayList<Genre> genres = new ArrayList<>();

        Uri uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Genres._ID,
                MediaStore.Audio.Genres.NAME,
        };
        Cursor c = context.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                null
        );

        if (c != null) {
            while (c.moveToNext()) {
                long genres_id = c.getLong(0);
                String genre = c.getString(1);
                ArrayList<Song> songs = getGenreSongs(context, genres_id);

                if (songs.size() > 0)
                    genres.add(
                            new Genre(
                                    genres_id,
                                    genre,
                                    songs.get(0).getAlbum_id(),
                                    songs.size()
                            )
                    );
            }
            c.close();
        }
        return genres;
    }

    public static ArrayList<Album> getAlbums(Context context) {
        ArrayList<Album> albums = new ArrayList<>();

        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS
        };
        Cursor c = context.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                "album asc"
        );

        if (c != null) {

            while (c.moveToNext()) {
                long album_id = c.getLong(0);
                String album = c.getString(1);
                String artist = c.getString(2);
                int no_of_songs = c.getInt(3);

                if (!album.equals("<unknown>"))
                    albums.add(
                            new Album(
                                    album,
                                    artist,
                                    album_id,
                                    no_of_songs
                            )
                    );
            }
            c.close();
        }
        return albums;
    }

    public static ArrayList<Song> getAllSongs(Context context) {
        ArrayList<Song> songs = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        };
        Cursor c = context.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                "title asc"
        );

        if (c != null) {

            while (c.moveToNext()) {
                long id = c.getLong(0);
                String data = c.getString(1);
                String title = c.getString(2);
                String album = c.getString(3);
                String artist = c.getString(4);
                long duration = c.getLong(5);
                long album_id = c.getLong(6);

                if (title != null && album != null && artist != null)
                    if (!title.equals("<unknown>") && !album.equals("<unknown>") && !artist.equals("<unknown>"))
                        songs.add(
                                new Song(
                                        id,
                                        data,
                                        title,
                                        album,
                                        artist,
                                        duration,
                                        album_id
                                )
                        );
            }
            c.close();
        }
        return songs;
    }

    public static ArrayList<Artist> getArtists(Context context) {
        ArrayList<Artist> artists = new ArrayList<>();

        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        };
        Cursor c = context.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                "artist asc"
        );

        if (c != null) {
            while (c.moveToNext()) {
                long artist_id = c.getLong(0);
                String artist = c.getString(1);
                int no_of_songs = c.getInt(2);
                ArrayList<Song> songs = getArtistSongs(context, artist_id);

                if (songs.size() > 0)
                    if (!artist.equals("<unknown>"))
                        artists.add(
                                new Artist(
                                        artist_id,
                                        artist,
                                        no_of_songs,
                                        getArtistSongs(context, artist_id).get(0).getAlbum_id()
                                )
                        );
            }
            c.close();
        }
        return artists;
    }

    public static void createNewPlaylist(Context c, String name) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        ArrayList<Playlist> list = new Gson().fromJson(prefs.getString("PLAYLISTS", null),
                new TypeToken<ArrayList<Playlist>>() {
                }.getType());

        Playlist playlist = new Playlist(name, new ArrayList<>());

        if (list != null) {
            if (!list.contains(playlist)) list.add(playlist);
            else return;
        } else {
            list = new ArrayList<>();
            list.add(playlist);
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("PLAYLISTS", new Gson().toJson(list)).apply();
    }

    public static ArrayList<Song> getRecentlyAdded(Context context) {
        ArrayList<Song> songs = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        };
        String selection = MediaStore.Audio.Media.DATE_ADDED + ">" + (System.currentTimeMillis() / 1000 - 1);

        Cursor c = context.getContentResolver().query(
                uri,
                projection,
                selection,
                null,
                "title asc"
        );

        if (c != null) {

            while (c.moveToNext()) {
                long id = c.getLong(0);
                String data = c.getString(1);
                String title = c.getString(2);
                String album = c.getString(3);
                String artist = c.getString(4);
                long duration = c.getLong(5);
                long album_id = c.getLong(6);

                if (title != null && album != null && artist != null)
                    if (!title.equals("<unknown>") && !album.equals("<unknown>") && !artist.equals("<unknown>"))
                        songs.add(
                                new Song(
                                        id,
                                        data,
                                        title,
                                        album,
                                        artist,
                                        duration,
                                        album_id
                                )
                        );
            }
            c.close();
        }
        return songs;
    }

    public static void songToPlaylist(Context c, int position, Song song) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        ArrayList<Playlist> list = new Gson().fromJson(prefs.getString("PLAYLISTS", null),
                new TypeToken<ArrayList<Playlist>>() {
                }.getType());

        //Clearing the list
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("PLAYLISTS");

        //Checking if song already exist in any of the playlists
        Playlist playlist = list.get(position);
        if (playlist.getSongs().contains(song)) {
            Toast.makeText(c, "Song already exist in that playlist", Toast.LENGTH_SHORT).show();
            return;
        }

        //Removing the playlist before add a new one with song included
        list.remove(position);

        //Adding song to the playlist and playlist to the playlists
        playlist.getSongs().add(song);
        list.add(playlist);

        //Saving back the playlists to the shared preference
        editor.putString("PLAYLISTS", new Gson().toJson(list)).apply();
        Toast.makeText(c, song.getTitle() + " added to the " + playlist.getName(), Toast.LENGTH_SHORT).show();
    }

    public static ArrayList<Song> getGenreSongs(Context context, long genreId) {
        ArrayList<Song> songs = new ArrayList<>();

        Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", genreId);
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        };
        Cursor c = context.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                "title asc"
        );

        if (c != null) {

            while (c.moveToNext()) {
                long id = c.getLong(0);
                String data = c.getString(1);
                String title = c.getString(2);
                String album = c.getString(3);
                String artist = c.getString(4);
                long duration = c.getLong(5);
                long album_id = c.getLong(6);

                songs.add(
                        new Song(
                                id,
                                data,
                                title,
                                album,
                                artist,
                                duration,
                                album_id
                        )
                );
            }
            c.close();
        }
        return songs;
    }

    public static ArrayList<Song> getAlbumSongs(Context context, long albumId) {
        ArrayList<Song> songs = new ArrayList<>();
        String selection = "is_music != 0";

        if (albumId > 0) selection = selection + " and album_id = " + albumId;

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        };
        Cursor c = context.getContentResolver().query(
                uri,
                projection,
                selection,
                null,
                "title asc"
        );

        if (c != null) {

            while (c.moveToNext()) {
                long id = c.getLong(0);
                String data = c.getString(1);
                String title = c.getString(2);
                String album = c.getString(3);
                String artist = c.getString(4);
                long duration = c.getLong(5);
                long album_id = c.getLong(6);

                songs.add(
                        new Song(
                                id,
                                data,
                                title,
                                album,
                                artist,
                                duration,
                                album_id
                        )
                );
            }
            c.close();
        }
        return songs;
    }

    public static ArrayList<Song> getArtistSongs(Context context, long artistId) {
        ArrayList<Song> songs = new ArrayList<>();
        String selection = "is_music != 0";

        if (artistId > 0) selection = selection + " and artist_id = " + artistId;

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        };
        Cursor c = context.getContentResolver().query(
                uri,
                projection,
                selection,
                null,
                "title asc"
        );

        if (c != null) {

            while (c.moveToNext()) {
                long id = c.getLong(0);
                String data = c.getString(1);
                String title = c.getString(2);
                String album = c.getString(3);
                String artist = c.getString(4);
                long duration = c.getLong(5);
                long album_id = c.getLong(6);

                songs.add(
                        new Song(
                                id,
                                data,
                                title,
                                album,
                                artist,
                                duration,
                                album_id
                        )
                );
            }
            c.close();
        }
        return songs;
    }

    public static void fetchAllSongs(Context c) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                songs = getAllSongs(c);
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                super.run();
                albums = getAlbums(c);
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                super.run();
                artists = getArtists(c);
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                super.run();
                genres = getGenres(c);
            }
        }.start();
    }
}