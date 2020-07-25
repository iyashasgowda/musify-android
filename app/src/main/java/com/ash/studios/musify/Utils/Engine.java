package com.ash.studios.musify.Utils;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.util.Random;

import static com.ash.studios.musify.Utils.Instance.mp;
import static com.ash.studios.musify.Utils.Instance.position;
import static com.ash.studios.musify.Utils.Instance.repeat;
import static com.ash.studios.musify.Utils.Instance.shuffle;
import static com.ash.studios.musify.Utils.Instance.song;
import static com.ash.studios.musify.Utils.Instance.songs;
import static com.ash.studios.musify.Utils.Instance.uri;

public class Engine extends Application {
    private Context context;

    public Engine() {
    }

    public Engine(Context context) {
        this.context = context;
    }

    public void startPlayer() {
        if (songs != null) {
            song = songs.get(position);
            uri = Uri.parse(songs.get(position).getPath());
        }

        if (mp != null) {
            mp.stop();
            mp.release();
        }
        mp = MediaPlayer.create(context, uri);
        mp.start();
    }

    public void playNextSong() {
        if (mp.isPlaying()) {
            mp.stop();
            mp.release();

            if (shuffle && !repeat) position = new Random().nextInt((songs.size() - 1) + 1);
            else if (!shuffle && !repeat) position = ((position + 1) % songs.size());

            uri = Uri.parse(songs.get(position).getPath());
            mp = MediaPlayer.create(this, uri);
            song = songs.get(position);

            mp.setOnCompletionListener((MediaPlayer.OnCompletionListener) context);
            mp.start();
        } else {
            mp.stop();
            mp.release();
            if (shuffle && !repeat) position = new Random().nextInt((songs.size() - 1) + 1);
            else if (!shuffle && !repeat) position = ((position + 1) % songs.size());

            uri = Uri.parse(songs.get(position).getPath());
            mp = MediaPlayer.create(context, uri);
            song = songs.get(position);

            mp.setOnCompletionListener((MediaPlayer.OnCompletionListener) context);
        }
    }

    public void playprevSong() {
        if (mp.isPlaying()) {
            mp.stop();
            mp.release();

            if (shuffle && !repeat)
                position = new Random().nextInt((songs.size() - 1) + 1);
            else if (!shuffle && !repeat)
                position = ((position - 1) < 0 ? (songs.size() - 1) : (position - 1));

            uri = Uri.parse(songs.get(position).getPath());
            mp = MediaPlayer.create(getApplicationContext(), uri);
            song = songs.get(position);

            mp.setOnCompletionListener((MediaPlayer.OnCompletionListener) context);
            mp.start();
        } else {
            mp.stop();
            mp.release();

            if (shuffle && !repeat)
                position = new Random().nextInt((songs.size() - 1) + 1);
            else if (!shuffle && !repeat)
                position = ((position - 1) < 0 ? (songs.size() - 1) : (position - 1));

            uri = Uri.parse(songs.get(position).getPath());
            mp = MediaPlayer.create(getApplicationContext(), uri);
            song = songs.get(position);

            mp.setOnCompletionListener((MediaPlayer.OnCompletionListener) context);
        }
    }
}
