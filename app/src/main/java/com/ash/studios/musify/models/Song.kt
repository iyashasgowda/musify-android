package com.ash.studios.musify.models

import java.io.Serializable
import java.util.*

class Song(
    val id: Long,
    val path: String,
    val title: String,
    val album: String,
    val artist: String,
    val year: String,
    val track: String,
    val composer: String,
    val album_artist: String,
    val duration: Long,
    val album_id: Long
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val song = other as Song
        return id == song.id && duration == song.duration && album_id == song.album_id && path == song.path && title == song.title && album == song.album && artist == song.artist && year == song.year && track == song.track && composer == song.composer && album_artist == song.album_artist
    }

    override fun hashCode(): Int {
        return Objects.hash(id, path, title, album, artist, year, track, composer, album_artist, duration, album_id)
    }

    override fun toString(): String {
        return "Song{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", title='" + title + '\'' +
                ", album='" + album + '\'' +
                ", artist='" + artist + '\'' +
                ", year='" + year + '\'' +
                ", track='" + track + '\'' +
                ", composer='" + composer + '\'' +
                ", album_artist='" + album_artist + '\'' +
                ", duration=" + duration +
                ", album_id=" + album_id +
                '}'
    }
}