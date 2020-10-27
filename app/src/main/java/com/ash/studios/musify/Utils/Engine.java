package com.ash.studios.musify.Utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;

import com.ash.studios.musify.Services.MusicService;

import java.util.Random;

import static com.ash.studios.musify.Utils.Instance.mp;
import static com.ash.studios.musify.Utils.Instance.position;
import static com.ash.studios.musify.Utils.Instance.repeat;
import static com.ash.studios.musify.Utils.Instance.shuffle;
import static com.ash.studios.musify.Utils.Instance.songs;
import static com.ash.studios.musify.Utils.Instance.uri;

public class Engine {
    private Context context;

    public Engine() {
    }

    public Engine(Context context) {
        this.context = context;
    }

    public void startPlayer() {

        if (songs != null) uri = Uri.parse(songs.get(position).getPath());

        if (mp != null) {
            mp.stop();
            mp.reset();
        }

        if (uri == null && uri.equals(Uri.EMPTY)) return;

        mp = MediaPlayer.create(context, uri);
        mp.start();

        Instance.playing = true;
        context.startService(new Intent(context, MusicService.class).setAction(Constants.ACTION.CREATE));
        setCurrentPlayBack();
    }

    public void playNextSong() {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();

                if (shuffle && !repeat) position = new Random().nextInt((songs.size() - 1) + 1);
                else if (!shuffle && !repeat) position = ((position + 1) % songs.size());

                uri = Uri.parse(songs.get(position).getPath());
                mp = MediaPlayer.create(context, uri);

                mp.setOnCompletionListener((MediaPlayer.OnCompletionListener) context);
                mp.start();
            } else {
                mp.stop();
                mp.reset();
                if (shuffle && !repeat) position = new Random().nextInt((songs.size() - 1) + 1);
                else if (!shuffle && !repeat) position = ((position + 1) % songs.size());

                uri = Uri.parse(songs.get(position).getPath());
                mp = MediaPlayer.create(context, uri);

                mp.setOnCompletionListener((MediaPlayer.OnCompletionListener) context);
            }
        } else {
            if (shuffle && !repeat) position = new Random().nextInt((songs.size() - 1) + 1);
            else if (!shuffle && !repeat) position = ((position + 1) % songs.size());

            uri = Uri.parse(songs.get(position).getPath());
            mp = MediaPlayer.create(context, uri);

            mp.setOnCompletionListener((MediaPlayer.OnCompletionListener) context);
        }
        context.startService(new Intent(context, MusicService.class).setAction(Constants.ACTION.CREATE));
        setCurrentPlayBack();
    }

    public void playPrevSong() {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();

                if (shuffle && !repeat)
                    position = new Random().nextInt((songs.size() - 1) + 1);
                else if (!shuffle && !repeat)
                    position = ((position - 1) < 0 ? (songs.size() - 1) : (position - 1));

                uri = Uri.parse(songs.get(position).getPath());
                mp = MediaPlayer.create(context, uri);

                mp.setOnCompletionListener((MediaPlayer.OnCompletionListener) context);
                mp.start();
            } else {
                mp.stop();
                mp.reset();

                if (shuffle && !repeat)
                    position = new Random().nextInt((songs.size() - 1) + 1);
                else if (!shuffle && !repeat)
                    position = ((position - 1) < 0 ? (songs.size() - 1) : (position - 1));

                uri = Uri.parse(songs.get(position).getPath());
                mp = MediaPlayer.create(context, uri);

                mp.setOnCompletionListener((MediaPlayer.OnCompletionListener) context);
            }
        } else {
            if (shuffle && !repeat)
                position = new Random().nextInt((songs.size() - 1) + 1);
            else if (!shuffle && !repeat)
                position = ((position - 1) < 0 ? (songs.size() - 1) : (position - 1));

            uri = Uri.parse(songs.get(position).getPath());
            mp = MediaPlayer.create(context, uri);

            mp.setOnCompletionListener((MediaPlayer.OnCompletionListener) context);
        }
        context.startService(new Intent(context, MusicService.class).setAction(Constants.ACTION.CREATE));
        setCurrentPlayBack();
    }

    private void setCurrentPlayBack() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Utils.putCurrentList(context, songs);
                Utils.putCurrentPosition(context, position);
            }
        }.start();
    }
}
