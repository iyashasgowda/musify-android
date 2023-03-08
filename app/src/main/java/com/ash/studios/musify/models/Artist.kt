package com.ash.studios.musify.models

import java.io.Serializable
import java.util.*

class Artist(val artist_id: Long, val artist: String, val song_count: Int, val album_id: Long) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Artist) return false
        return artist_id == other.artist_id && song_count == other.song_count && album_id == other.album_id && artist == other.artist
    }

    override fun hashCode(): Int {
        return Objects.hash(artist_id, artist, song_count, album_id)
    }

    override fun toString(): String {
        return "Artist{" +
                "artist_id=" + artist_id +
                ", artist='" + artist + '\'' +
                ", song_count=" + song_count +
                ", album_id=" + album_id +
                '}'
    }
}