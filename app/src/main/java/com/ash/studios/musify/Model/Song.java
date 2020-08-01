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
    private String year;
    private String track;
    private String composer;
    private String album_artist;
    private long duration;
    private long album_id;

    public Song(long id, String path, String title, String album, String artist, String year, String track, String composer, String album_artist, long duration, long album_id) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.year = year;
        this.track = track;
        this.composer = composer;
        this.album_artist = album_artist;
        this.duration = duration;
        this.album_id = album_id;
    }

    public long getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getYear() {
        return year;
    }

    public String getTrack() {
        return track;
    }

    public String getComposer() {
        return composer;
    }

    public String getAlbum_artist() {
        return album_artist;
    }

    public long getDuration() {
        return duration;
    }

    public long getAlbum_id() {
        return album_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return id == song.id &&
                duration == song.duration &&
                album_id == song.album_id &&
                Objects.equals(path, song.path) &&
                Objects.equals(title, song.title) &&
                Objects.equals(album, song.album) &&
                Objects.equals(artist, song.artist) &&
                Objects.equals(year, song.year) &&
                Objects.equals(track, song.track) &&
                Objects.equals(composer, song.composer) &&
                Objects.equals(album_artist, song.album_artist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, path, title, album, artist, year, track, composer, album_artist, duration, album_id);
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
                ", year='" + year + '\'' +
                ", track='" + track + '\'' +
                ", composer='" + composer + '\'' +
                ", album_artist='" + album_artist + '\'' +
                ", duration=" + duration +
                ", album_id=" + album_id +
                '}';
    }
}
