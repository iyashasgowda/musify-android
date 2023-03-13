package com.ash.studios.musify.models

import java.io.Serializable
import java.util.*

class Folder(val name: String, val path: String, val songs: ArrayList<Song>) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val folder = other as Folder
        return name == folder.name && path == folder.path && songs == folder.songs
    }

    override fun hashCode(): Int {
        return Objects.hash(name, path, songs)
    }

    override fun toString(): String {
        return "Folder{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", songs=" + songs +
                '}'
    }
}