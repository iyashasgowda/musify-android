package com.ash.studios.musify.models;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Year implements Serializable {
    private final String year;
    private final Bitmap albumArt;
    private final ArrayList<Song> songs;

    public Year(String year, Bitmap albumArt, ArrayList<Song> songs) {
        this.year = year;
        this.albumArt = albumArt;
        this.songs = songs;
    }

    public String getYear() {
        return year;
    }

    public Bitmap getAlbumArt() {
        return albumArt;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Year year1 = (Year) o;
        return Objects.equals(year, year1.year) &&
                Objects.equals(albumArt, year1.albumArt) &&
                Objects.equals(songs, year1.songs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, albumArt, songs);
    }

    @NonNull
    @Override
    public String toString() {
        return "Year{" +
                "year='" + year + '\'' +
                ", albumArt=" + albumArt +
                ", songs=" + songs +
                '}';
    }
}
