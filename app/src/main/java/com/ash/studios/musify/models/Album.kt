package com.ash.studios.musify.models

import java.io.Serializable
import java.util.*

class Album(val album: String, val artist: String, val album_id: Long, val song_count: Int) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Album) return false
        return album_id == other.album_id && song_count == other.song_count && album == other.album && artist == other.artist
    }

    override fun hashCode(): Int {
        return Objects.hash(album, artist, album_id, song_count)
    }

    override fun toString(): String {
        return "Album{" +
                "album='" + album + '\'' +
                ", artist='" + artist + '\'' +
                ", album_id=" + album_id +
                ", no_of_songs=" + song_count +
                '}'
    }
}