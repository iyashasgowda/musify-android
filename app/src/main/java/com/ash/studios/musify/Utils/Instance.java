package com.ash.studios.musify.Utils;

import android.media.MediaPlayer;
import android.net.Uri;

import com.ash.studios.musify.Model.Song;

import java.util.ArrayList;

public class Instance {
    public static final String[] fileExtensions = new String[]{
            ".aac", ".mp3", ".wav", ".ogg", ".midi", ".3gp", ".mp4", ".m4a", ".amr", ".flac"
    };
    public static Uri uri;
    public static boolean repeat;
    public static MediaPlayer mp;
    public static boolean shuffle;
    public static ArrayList<Song> songs = new ArrayList<>();
}
