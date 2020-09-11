package com.ash.studios.musify.Models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Folder implements Serializable {
    private String name;
    private String path;
    private ArrayList<Song> songs;

    public Folder(String name, String path, ArrayList<Song> songs) {
        this.name = name;
        this.path = path;
        this.songs = songs;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Folder folder = (Folder) o;
        return Objects.equals(name, folder.name) &&
                Objects.equals(path, folder.path) &&
                Objects.equals(songs, folder.songs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path, songs);
    }

    @NonNull
    @Override
    public String toString() {
        return "Folder{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", songs=" + songs +
                '}';
    }
}
