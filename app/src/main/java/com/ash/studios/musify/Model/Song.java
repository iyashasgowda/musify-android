package com.ash.studios.musify.Model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Song implements Serializable {
    private long id;
    private String path;
    private String title;
    private String album;
    private String artist;
    private long duration;
    private long album_id;

    public Song() {
    }

    public Song(long id, String path, String title, String album, String artist, long duration, long album_id) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.duration = duration;
        this.album_id = album_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(long album_id) {
        this.album_id = album_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        Song song = (Song) o;
        return getId() == song.getId() &&
                getDuration() == song.getDuration() &&
                getAlbum_id() == song.getAlbum_id() &&
                Objects.equals(getPath(), song.getPath()) &&
                Objects.equals(getTitle(), song.getTitle()) &&
                Objects.equals(getAlbum(), song.getAlbum()) &&
                Objects.equals(getArtist(), song.getArtist());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getPath(), getTitle(), getAlbum(), getArtist(), getDuration(), getAlbum_id());
    }

    @NonNull
    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", title='" + title + '\'' +
                ", album='" + album + '\'' +
                ", artist='" + artist + '\'' +
                ", duration=" + duration +
                ", album_id=" + album_id +
                '}';
    }
}
