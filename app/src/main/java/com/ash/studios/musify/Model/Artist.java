package com.ash.studios.musify.Model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Artist implements Serializable {
    private long artist_id;
    private String artist;
    private int album_count;
    private int song_count;

    public Artist() {
    }

    public Artist(long artist_id, String artist, int album_count, int song_count) {
        this.artist_id = artist_id;
        this.artist = artist;
        this.album_count = album_count;
        this.song_count = song_count;
    }

    public long getArtist_id() {
        return artist_id;
    }

    public String getArtist() {
        return artist;
    }

    public int getAlbum_count() {
        return album_count;
    }

    public int getSong_count() {
        return song_count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Artist)) return false;
        Artist artist1 = (Artist) o;
        return getArtist_id() == artist1.getArtist_id() &&
                getAlbum_count() == artist1.getAlbum_count() &&
                getSong_count() == artist1.getSong_count() &&
                Objects.equals(getArtist(), artist1.getArtist());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getArtist_id(), getArtist(), getAlbum_count(), getSong_count());
    }

    @NonNull
    @Override
    public String toString() {
        return "Artist{" +
                "artist_id=" + artist_id +
                ", artist='" + artist + '\'' +
                ", album_count=" + album_count +
                ", song_sount=" + song_count +
                '}';
    }
}
