package com.ash.studios.musify.models

import android.graphics.Bitmap
import java.io.Serializable
import java.util.*

class Year(val year: String, val albumArt: Bitmap, val songs: ArrayList<Song>) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val year1 = other as Year
        return year == year1.year && albumArt == year1.albumArt && songs == year1.songs
    }

    override fun hashCode(): Int {
        return Objects.hash(year, albumArt, songs)
    }

    override fun toString(): String {
        return "Year{" +
                "year='" + year + '\'' +
                ", albumArt=" + albumArt +
                ", songs=" + songs +
                '}'
    }
}