package com.ash.studios.musify.utils

object Constants {
    const val LIBRARY_OPTIONS = "MUSIFY_LIB_OPTIONS"
    const val ALL_SONGS_SORT = "MUSIFY_ALL_SONGS_SORT"
    const val ALBUMS_SORT = "MUSIFY_ALBUMS_SORT"
    const val ARTISTS_SORT = "MUSIFY_ARTISTS_SORT"
    const val GENRES_SORT = "MUSIFY_GENRES_SORT"
    const val YEARS_SORT = "MUSIFY_YEARS_SORT"
    const val COMMON_SORT = "MUSIFY_COMMON_SORT"
    const val TR_SORT = "MUSIFY_TR_SORT"
    const val LR_SORT = "MUSIFY_LR_SORT"
    const val FOREGROUND_SERVICE = 101

    enum class ACTION(val label: String) {
        PLAY("com.ash.studios.musify.Utils.action.play"),
        PAUSE("com.ash.studios.musify.Utils.action.pause"),
        NEXT("com.ash.studios.musify.Utils.action.next"),
        PREV("com.ash.studios.musify.Utils.action.prev"),
        CREATE("com.ash.studios.musify.Utils.action.create"),
        STOP_SERVICE("com.ash.studios.musify.Utils.action.stopservice");
    }
}