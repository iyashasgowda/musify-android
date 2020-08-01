package com.ash.studios.musify.BtmSheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class InfoSheet extends BottomSheetDialogFragment {
    TextView path, title, track, year, artist, album, albumArtist, composer;
    ImageView close;

    private Song song;

    public InfoSheet(Song song) {
        this.song = song;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.info_sheet, container, false);
        setID(v);

        setSongInfo();
        return v;
    }

    private void setSongInfo() {
        path.setText(song.getPath() == null ? "-" : song.getPath());
        year.setText(song.getYear() == null ? "-" : song.getYear());
        title.setText(song.getTitle() == null ? "-" : song.getTitle());
        track.setText(song.getTrack() == null ? "-" : song.getTrack());
        album.setText(song.getAlbum() == null ? "-" : song.getAlbum());
        artist.setText(song.getArtist() == null ? "-" : song.getArtist());
        composer.setText(song.getComposer() == null ? "-" : song.getComposer());
        albumArtist.setText(song.getAlbum_artist() == null ? "-" : song.getAlbum_artist());
    }

    private void setID(View v) {
        path = v.findViewById(R.id.song_path);
        year = v.findViewById(R.id.song_year);
        title = v.findViewById(R.id.song_title);
        track = v.findViewById(R.id.song_track);
        album = v.findViewById(R.id.song_album);
        artist = v.findViewById(R.id.song_artist);
        close = v.findViewById(R.id.close_sheet_btn);
        composer = v.findViewById(R.id.song_composer);
        albumArtist = v.findViewById(R.id.song_album_artist);

        close.setOnClickListener(close -> dismiss());
    }
}
