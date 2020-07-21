package com.ash.studios.musify.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;

import com.ash.studios.musify.Model.Album;
import com.ash.studios.musify.Model.Artist;
import com.ash.studios.musify.Model.Genre;
import com.ash.studios.musify.Model.Song;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@SuppressLint("InlinedApi, DefaultLocale")
public class Utils extends Application {

    public static ArrayList<Genre> getGenres(Context context) {
        ArrayList<Genre> genres = new ArrayList<>();

        Uri uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Genres._ID,
                MediaStore.Audio.Genres.NAME,
        };
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);

        if (c != null) {
            while (c.moveToNext()) {
                long genres_id = c.getLong(0);
                String genre = c.getString(1);

                genres.add(new Genre(genres_id, genre));
            }
            c.close();
        }
        return genres;
    }

    public static String getNewColor() {
        return String.format("#%06X", new Random().nextInt(0xFFFFFF + 1));
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
        Cursor c = context.getContentResolver().query(uri, projection, null, null, "title asc");

        if (c != null) {

            while (c.moveToNext()) {
                long id = c.getLong(0);
                String data = c.getString(1);
                String title = c.getString(2);
                String album = c.getString(3);
                String artist = c.getString(4);
                long duration = c.getLong(5);
                long album_id = c.getLong(6);

                songs.add(new Song(id, data, title, album, artist, duration, album_id));
            }
            c.close();
        }
        return songs;
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
        Cursor c = context.getContentResolver().query(uri, projection, null, null, "album asc");

        if (c != null) {

            while (c.moveToNext()) {
                long album_id = c.getLong(0);
                String album = c.getString(1);
                String artist = c.getString(2);
                int no_of_songs = c.getInt(3);

                albums.add(new Album(album, artist, album_id, no_of_songs));
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
        Cursor c = context.getContentResolver().query(uri, projection, null, null, "title asc");

        if (c != null) {

            while (c.moveToNext()) {
                long id = c.getLong(0);
                String data = c.getString(1);
                String title = c.getString(2);
                String album = c.getString(3);
                String artist = c.getString(4);
                long duration = c.getLong(5);
                long album_id = c.getLong(6);

                songs.add(new Song(id, data, title, album, artist, duration, album_id));
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
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        };
        Cursor c = context.getContentResolver().query(uri, projection, null, null, "artist asc");

        if (c != null) {
            while (c.moveToNext()) {
                long artist_id = c.getLong(0);
                String artist = c.getString(1);
                int no_of_albums = c.getInt(2);
                int no_of_songs = c.getInt(3);

                artists.add(new Artist(artist_id, artist, no_of_albums, no_of_songs));
            }
            c.close();
        }
        return artists;
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
        Cursor c = context.getContentResolver().query(uri, projection, selection, null, "title asc");

        if (c != null) {

            while (c.moveToNext()) {
                long id = c.getLong(0);
                String data = c.getString(1);
                String title = c.getString(2);
                String album = c.getString(3);
                String artist = c.getString(4);
                long duration = c.getLong(5);
                long album_id = c.getLong(6);

                songs.add(new Song(id, data, title, album, artist, duration, album_id));
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
        Cursor c = context.getContentResolver().query(uri, projection, selection, null, "title asc");

        if (c != null) {

            while (c.moveToNext()) {
                long id = c.getLong(0);
                String data = c.getString(1);
                String title = c.getString(2);
                String album = c.getString(3);
                String artist = c.getString(4);
                long duration = c.getLong(5);
                long album_id = c.getLong(6);

                songs.add(new Song(id, data, title, album, artist, duration, album_id));
            }
            c.close();
        }
        return songs;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        getAlbums(this);
        getArtists(this);
        getAllSongs(this);
    }
}
