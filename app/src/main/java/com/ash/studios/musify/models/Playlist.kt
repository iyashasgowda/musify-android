package com.ash.studios.musify.models

import java.io.Serializable
import java.util.*

class Playlist(val name: String, val songs: ArrayList<Song>) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Playlist) return false
        return name == other.name && songs == other.songs
    }

    override fun hashCode(): Int {
        return Objects.hash(name, songs)
    }

    override fun toString(): String {
        return "Playlist{" +
                "name='" + name + '\'' +
                ", songs=" + songs +
                '}'
    }
}