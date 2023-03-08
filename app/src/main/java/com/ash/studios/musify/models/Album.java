package com.ash.studios.musify.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Album implements Serializable {
    private final String album;
    private final String artist;
    private final long album_id;
    private final int song_count;

    public Album(String album, String artist, long album_id, int song_count) {
        this.album = album;
        this.artist = artist;
        this.album_id = album_id;
        this.song_count = song_count;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public long getAlbum_id() {
        return album_id;
    }

    public int getSong_count() {
        return song_count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Album)) return false;
        Album album1 = (Album) o;
        return getAlbum_id() == album1.getAlbum_id() &&
                getSong_count() == album1.getSong_count() &&
                Objects.equals(getAlbum(), album1.getAlbum()) &&
                Objects.equals(getArtist(), album1.getArtist());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAlbum(), getArtist(), getAlbum_id(), getSong_count());
    }

    @NonNull
    @Override
    public String toString() {
        return "Album{" +
                "album='" + album + '\'' +
                ", artist='" + artist + '\'' +
                ", album_id=" + album_id +
                ", no_of_songs=" + song_count +
                '}';
    }
}
