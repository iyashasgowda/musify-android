package com.ash.studios.musify.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;

import com.ash.studios.musify.Model.Song;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@SuppressLint("InlinedApi, DefaultLocale")
public class Utils {

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
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);

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


    /*public static Song getFolderSong(String sortOrder, Context context, String path) {
        Song song = new Song();

        if (path == null) return null;
        Cursor c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Audio.Media.DATA + " like ? ", new String[]{"%" + path + "%"}, sortOrder);

        if (c != null && c.moveToFirst()) {
            String data = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
            String album = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            String title = c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String artist = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));

            long id = c.getLong(c.getColumnIndex(MediaStore.Audio.Media._ID));
            long albumId = c.getLong(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            long duration = c.getLong(c.getColumnIndex(MediaStore.Audio.Media.DURATION));

            int track = c.getInt(c.getColumnIndex(MediaStore.Audio.Media.TRACK));
            Uri albumArt = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);

            song.setId(id);
            song.setData(data);
            song.setTitle(title);
            song.setTrack(track);
            song.setAlbum(album);
            song.setArtist(artist);
            song.setAlbum_id(albumId);
            song.setDuration(duration);
            song.setAlbum_art(albumArt);
        }
        if (c != null) c.close();
        return song;
    }

    public static String getFolderPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        else
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
    }*/
}
