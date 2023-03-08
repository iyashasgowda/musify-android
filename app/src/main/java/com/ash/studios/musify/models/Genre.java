package com.ash.studios.musify.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Genre implements Serializable {
    private final long genre_id;
    private final String genre;
    private final long album_id;
    private final int song_count;

    public Genre(long genre_id, String genre, long album_id, int song_count) {
        this.genre_id = genre_id;
        this.genre = genre;
        this.album_id = album_id;
        this.song_count = song_count;
    }

    public long getGenre_id() {
        return genre_id;
    }

    public String getGenre() {
        return genre;
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
        if (!(o instanceof Genre)) return false;
        Genre genre1 = (Genre) o;
        return getGenre_id() == genre1.getGenre_id() &&
                getAlbum_id() == genre1.getAlbum_id() &&
                getSong_count() == genre1.getSong_count() &&
                Objects.equals(getGenre(), genre1.getGenre());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGenre_id(), getGenre(), getAlbum_id(), getSong_count());
    }

    @NonNull
    @Override
    public String toString() {
        return "Genre{" +
                "genre_id=" + genre_id +
                ", genre='" + genre + '\'' +
                ", album_id=" + album_id +
                ", song_count=" + song_count +
                '}';
    }
}
