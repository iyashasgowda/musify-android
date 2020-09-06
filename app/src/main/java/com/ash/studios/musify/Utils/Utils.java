package com.ash.studios.musify.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

import androidx.palette.graphics.Palette;

import com.ash.studios.musify.Model.Album;
import com.ash.studios.musify.Model.Artist;
import com.ash.studios.musify.Model.Genre;
import com.ash.studios.musify.Model.Playlist;
import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static android.content.Context.MODE_PRIVATE;

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

    public static String getDuration(long duration) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
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

    public static int[] getSecondaryColors(Context c, Uri uri) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(c.getContentResolver(), uri);
        } catch (IOException e) {
            bitmap = BitmapFactory.decodeResource(c.getResources(), R.mipmap.ic_abstract);
        }

        Palette palette = Palette.from(bitmap).generate();
        Palette.Swatch vibrant = palette.getDarkVibrantSwatch();
        Palette.Swatch muted = palette.getDarkMutedSwatch();
        Palette.Swatch dominant = palette.getDominantSwatch();

        int[] colors = new int[2];
        if (vibrant != null) {
            colors[0] = vibrant.getRgb();
            colors[1] = vibrant.getBodyTextColor();
        } else if (muted != null) {
            colors[0] = muted.getRgb();
            colors[1] = muted.getBodyTextColor();
        } else if (dominant != null) {
            colors[0] = dominant.getRgb();
            colors[1] = dominant.getBodyTextColor();
        } else {
            colors[0] = Color.parseColor("#202020");
            colors[1] = Color.WHITE;
        }
        return colors;
    }

    public static void searchAndDelete(Context ctx, Song song) {
        songs.remove(song);
        if (getTR(ctx) != null && getTR(ctx).contains(song)) deleteFromTR(ctx, song);
        if (getLR(ctx) != null && getLR(ctx).contains(song)) deleteFromLR(ctx, song);
        ArrayList<Playlist> playlists = getPlaylists(ctx) == null ? new ArrayList<>() : getPlaylists(ctx);
        for (int i = 0; i < playlists.size(); i++)
            if (playlists.get(i).getSongs().contains(song))
                delFrmPlaylist(ctx, i, song);
    }


    //Save and retrieve current played song position
    public static void putCurrentPosition(Context c, int pos) {
        c.getSharedPreferences("CURRENT_POS", MODE_PRIVATE)
                .edit().putInt("current_position", pos).apply();
    }

    public static int getCurrentPosition(Context c) {
        return c.getSharedPreferences("CURRENT_POS", MODE_PRIVATE)
                .getInt("current_position", 0);
    }


    //Save and retrieve shuffle status
    public static void putShflStatus(Context c, boolean shfl) {
        c.getSharedPreferences("SHUFFLE_STATUS", MODE_PRIVATE)
                .edit().putBoolean("shuffle_status", shfl).apply();
    }

    public static boolean getShflStatus(Context c) {
        return c.getSharedPreferences("SHUFFLE_STATUS", MODE_PRIVATE)
                .getBoolean("shuffle_status", false);
    }


    //Save and retrieve repeat status
    public static void putRepStatus(Context c, boolean rep) {
        c.getSharedPreferences("REPEAT_STATUS", MODE_PRIVATE)
                .edit().putBoolean("repeat_status", rep).apply();
    }

    public static boolean getRepStatus(Context c) {
        return c.getSharedPreferences("REPEAT_STATUS", MODE_PRIVATE)
                .getBoolean("repeat_status", false);
    }


    //Save and retrieve current played song list
    public static void putCurrentList(Context c, ArrayList<Song> songs) {
        c.getSharedPreferences("CURRENT_LIST", MODE_PRIVATE)
                .edit().putString("current_list", new Gson()
                .toJson(songs)).apply();
    }

    public static ArrayList<Song> getCurrentList(Context c) {
        return new Gson()
                .fromJson(c.getSharedPreferences("CURRENT_LIST", MODE_PRIVATE)
                        .getString("current_list", null), new TypeToken<ArrayList<Song>>() {
                }.getType());
    }


    //Get all songs and songs with categories
    public static ArrayList<Song> getAllSongs(Context c) {
        ArrayList<Song> songs = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.COMPOSER,
                MediaStore.Audio.Media.ALBUM_ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        };
        Cursor cursor = c.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                "title asc"
        );

        if (cursor != null) {

            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String data = cursor.getString(1);
                String title = cursor.getString(2);
                String album = cursor.getString(3);
                String artist = cursor.getString(4);
                String year = cursor.getString(5);
                String track = cursor.getString(6);
                String composer = cursor.getString(7);
                String album_artist = cursor.getString(8);
                long duration = cursor.getLong(9);
                long album_id = cursor.getLong(10);

                if (title != null && album != null && artist != null)
                    if (!title.equals("<unknown>"))
                        songs.add(
                                new Song(
                                        id,
                                        data,
                                        title,
                                        album,
                                        artist,
                                        year,
                                        track,
                                        composer,
                                        album_artist,
                                        duration,
                                        album_id
                                )
                        );
            }
            cursor.close();
        }
        return songs;
    }

    public static ArrayList<Song> getAllSongsByCategory(Context c, String sortBy) {
        ArrayList<Song> songs = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.COMPOSER,
                MediaStore.Audio.Media.ALBUM_ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        };
        Cursor cursor = c.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                sortBy
        );

        if (cursor != null) {

            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String data = cursor.getString(1);
                String title = cursor.getString(2);
                String album = cursor.getString(3);
                String artist = cursor.getString(4);
                String year = cursor.getString(5);
                String track = cursor.getString(6);
                String composer = cursor.getString(7);
                String album_artist = cursor.getString(8);
                long duration = cursor.getLong(9);
                long album_id = cursor.getLong(10);

                if (title != null && album != null && artist != null)
                    if (!title.equals("<unknown>"))
                        songs.add(
                                new Song(
                                        id,
                                        data,
                                        title,
                                        album,
                                        artist,
                                        year,
                                        track,
                                        composer,
                                        album_artist,
                                        duration,
                                        album_id
                                )
                        );
            }
            cursor.close();
        }
        return songs;
    }


    //Save, fetch and delete top-rated
    public static ArrayList<Song> getTR(Context c) {
        ArrayList<Song> list = new Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(c).getString("TOP_RATED", null),
                new TypeToken<ArrayList<Song>>() {
                }.getType());

        if (list == null) list = new ArrayList<>();
        Collections.reverse(list);
        return list;
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

    public static void deleteFromTR(Context c, Song song) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        ArrayList<Song> list = new Gson().fromJson(prefs.getString("TOP_RATED", null),
                new TypeToken<ArrayList<Song>>() {
                }.getType());

        if (list != null) list.remove(song);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("TOP_RATED", new Gson().toJson(list)).apply();
    }


    //Save, fetch and delete low-rated
    public static ArrayList<Song> getLR(Context c) {
        ArrayList<Song> list = new Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(c).getString("LOW_RATED", null),
                new TypeToken<ArrayList<Song>>() {
                }.getType());

        if (list == null) list = new ArrayList<>();
        Collections.reverse(list);
        return list;
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

    public static void deleteFromLR(Context c, Song song) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        ArrayList<Song> list = new Gson().fromJson(prefs.getString("LOW_RATED", null),
                new TypeToken<ArrayList<Song>>() {
                }.getType());

        if (list != null) list.remove(song);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("LOW_RATED", new Gson().toJson(list)).apply();
    }


    //Get category based songs
    public static ArrayList<Song> getGenreSongs(Context c, long genreId) {
        ArrayList<Song> songs = new ArrayList<>();

        Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", genreId);
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.COMPOSER,
                MediaStore.Audio.Media.ALBUM_ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        };
        Cursor cursor = c.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                "title asc"
        );

        if (cursor != null) {

            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String data = cursor.getString(1);
                String title = cursor.getString(2);
                String album = cursor.getString(3);
                String artist = cursor.getString(4);
                String year = cursor.getString(5);
                String track = cursor.getString(6);
                String composer = cursor.getString(7);
                String album_artist = cursor.getString(8);
                long duration = cursor.getLong(9);
                long album_id = cursor.getLong(10);

                if (title != null && album != null && artist != null)
                    if (!title.equals("<unknown>"))
                        songs.add(
                                new Song(
                                        id,
                                        data,
                                        title,
                                        album,
                                        artist,
                                        year,
                                        track,
                                        composer,
                                        album_artist,
                                        duration,
                                        album_id
                                )
                        );
            }
            cursor.close();
        }
        return songs;
    }

    public static ArrayList<Song> getAlbumSongs(Context c, long albumId) {
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
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.COMPOSER,
                MediaStore.Audio.Media.ALBUM_ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        };
        Cursor cursor = c.getContentResolver().query(
                uri,
                projection,
                selection,
                null,
                "title asc"
        );

        if (cursor != null) {

            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String data = cursor.getString(1);
                String title = cursor.getString(2);
                String album = cursor.getString(3);
                String artist = cursor.getString(4);
                String year = cursor.getString(5);
                String track = cursor.getString(6);
                String composer = cursor.getString(7);
                String album_artist = cursor.getString(8);
                long duration = cursor.getLong(9);
                long album_id = cursor.getLong(10);

                if (title != null && album != null && artist != null)
                    if (!title.equals("<unknown>"))
                        songs.add(
                                new Song(
                                        id,
                                        data,
                                        title,
                                        album,
                                        artist,
                                        year,
                                        track,
                                        composer,
                                        album_artist,
                                        duration,
                                        album_id
                                )
                        );
            }
            cursor.close();
        }
        return songs;
    }

    public static ArrayList<Song> getArtistSongs(Context c, long artistId) {
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
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.COMPOSER,
                MediaStore.Audio.Media.ALBUM_ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID
        };
        Cursor cursor = c.getContentResolver().query(
                uri,
                projection,
                selection,
                null,
                "title asc"
        );

        if (cursor != null) {

            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String data = cursor.getString(1);
                String title = cursor.getString(2);
                String album = cursor.getString(3);
                String artist = cursor.getString(4);
                String year = cursor.getString(5);
                String track = cursor.getString(6);
                String composer = cursor.getString(7);
                String album_artist = cursor.getString(8);
                long duration = cursor.getLong(9);
                long album_id = cursor.getLong(10);

                if (title != null && album != null && artist != null)
                    if (!title.equals("<unknown>"))
                        songs.add(
                                new Song(
                                        id,
                                        data,
                                        title,
                                        album,
                                        artist,
                                        year,
                                        track,
                                        composer,
                                        album_artist,
                                        duration,
                                        album_id
                                )
                        );
            }
            cursor.close();
        }
        return songs;
    }


    //Save, update and delete playlists
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

    public static void addToPlaylist(Context c, int position, Song song) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        ArrayList<Playlist> list = new Gson().fromJson(prefs.getString("PLAYLISTS", null),
                new TypeToken<ArrayList<Playlist>>() {
                }.getType());

        //Clearing the list
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("PLAYLISTS");

        //Checking if song already exist in any of the playlists
        if (list != null) {
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
    }

    public static void delFrmPlaylist(Context c, int position, Song song) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        ArrayList<Playlist> list = new Gson().fromJson(prefs.getString("PLAYLISTS", null),
                new TypeToken<ArrayList<Playlist>>() {
                }.getType());

        //Clearing the list
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("PLAYLISTS");

        if (list != null) {
            Playlist playlist = list.get(position);
            list.remove(position);
            playlist.getSongs().remove(song);
            list.add(playlist);
            editor.putString("PLAYLISTS", new Gson().toJson(list)).apply();
        }
    }

    public static void deletePlaylist(Context c, Playlist pl) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        ArrayList<Playlist> list = new Gson().fromJson(prefs.getString("PLAYLISTS", null),
                new TypeToken<ArrayList<Playlist>>() {
                }.getType());

        if (list != null) list.remove(pl);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("PLAYLISTS", new Gson().toJson(list)).apply();
    }


    //Get categories
    public static ArrayList<Album> getAlbums(Context c) {
        ArrayList<Album> albums = new ArrayList<>();

        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS
        };
        Cursor cursor = c.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                "album asc"
        );

        if (cursor != null) {

            while (cursor.moveToNext()) {
                long album_id = cursor.getLong(0);
                String album = cursor.getString(1);
                String artist = cursor.getString(2);
                int no_of_songs = cursor.getInt(3);

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
            cursor.close();
        }
        return albums;
    }

    public static ArrayList<Artist> getArtists(Context c) {
        ArrayList<Artist> artists = new ArrayList<>();

        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        };
        Cursor cursor = c.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                "artist asc"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long artist_id = cursor.getLong(0);
                String artist = cursor.getString(1);
                int no_of_songs = cursor.getInt(2);
                ArrayList<Song> songs = getArtistSongs(c, artist_id);

                if (songs.size() > 0)
                    if (!artist.equals("<unknown>"))
                        artists.add(
                                new Artist(
                                        artist_id,
                                        artist,
                                        no_of_songs,
                                        getArtistSongs(c, artist_id).get(0).getAlbum_id()
                                )
                        );
            }
            cursor.close();
        }
        return artists;
    }

    public static ArrayList<Genre> getGenres(Context c) {
        ArrayList<Genre> genres = new ArrayList<>();

        Uri uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Genres._ID,
                MediaStore.Audio.Genres.NAME,
        };
        Cursor cursor = c.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long genres_id = cursor.getLong(0);
                String genre = cursor.getString(1);
                ArrayList<Song> songs = getGenreSongs(c, genres_id);

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
            cursor.close();
        }
        return genres;
    }

    public static ArrayList<Playlist> getPlaylists(Context c) {
        return new Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(c).getString("PLAYLISTS", null),
                new TypeToken<ArrayList<Playlist>>() {
                }.getType());
    }

    public static void fetchAllSongs(Context c) {
        new Thread(() -> songs = getAllSongs(c)).start();
        new Thread(() -> genres = getGenres(c)).start();
        new Thread(() -> albums = getAlbums(c)).start();
        new Thread(() -> artists = getArtists(c)).start();
    }
}