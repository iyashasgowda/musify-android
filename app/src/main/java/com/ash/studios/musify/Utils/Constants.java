package com.ash.studios.musify.Utils;

public class Constants {
    public static final String LIBRARY_OPTIONS = "MUSIFY_LIB_OPTIONS";
    public static final String ALL_SONGS_SORT = "MUSIFY_ALL_SONGS_SORT";
    public static final String ALBUMS_SORT = "MUSIFY_ALBUMS_SORT";
    public static final String ARTISTS_SORT = "MUSIFY_ARTISTS_SORT";
    public static final String GENRES_SORT = "MUSIFY_GENRES_SORT";
    public static final String YEARS_SORT = "MUSIFY_YEARS_SORT";
    public static final String COMMON_SORT = "MUSIFY_COMMON_SORT";
    public static final String TR_SORT = "MUSIFY_TR_SORT";
    public static final String LR_SORT = "MUSIFY_LR_SORT";


    public interface ACTION {
        String PLAY = "com.ash.studios.musify.Utils.action.play";
        String PAUSE = "com.ash.studios.musify.Utils.action.pause";
        String NEXT = "com.ash.studios.musify.Utils.action.next";
        String PREV = "com.ash.studios.musify.Utils.action.prev";
        String CREATE = "com.ash.studios.musify.Utils.action.create";
        String STOP_SERVICE = "com.ash.studios.musify.Utils.action.stopservice";
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }
}
