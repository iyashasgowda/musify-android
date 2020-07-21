package com.ash.studios.musify.Model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Genre implements Serializable {
    private long genre_id;
    private String genre;

    public Genre(long genre_id, String genre) {
        this.genre_id = genre_id;
        this.genre = genre;
    }

    public long getGenre_id() {
        return genre_id;
    }

    public void setGenre_id(long genre_id) {
        this.genre_id = genre_id;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre)) return false;
        Genre genre1 = (Genre) o;
        return getGenre_id() == genre1.getGenre_id() &&
                Objects.equals(getGenre(), genre1.getGenre());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGenre_id(), getGenre());
    }

    @NonNull
    @Override
    public String toString() {
        return "Genre{" +
                "genre_id=" + genre_id +
                ", genre='" + genre + '\'' +
                '}';
    }
}
