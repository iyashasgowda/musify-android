package com.ash.studios.musify.models

import java.io.Serializable
import java.util.*

class Genre(val genre_id: Long, val genre: String, val album_id: Long, val song_count: Int) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Genre) return false
        return genre_id == other.genre_id && album_id == other.album_id && song_count == other.song_count && genre == other.genre
    }

    override fun hashCode(): Int {
        return Objects.hash(genre_id, genre, album_id, song_count)
    }

    override fun toString(): String {
        return "Genre{" +
                "genre_id=" + genre_id +
                ", genre='" + genre + '\'' +
                ", album_id=" + album_id +
                ", song_count=" + song_count +
                '}'
    }
}