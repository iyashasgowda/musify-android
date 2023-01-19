package com.ash.studios.musify.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.ash.studios.musify.Activities.Player;
import com.ash.studios.musify.Interfaces.IControl;
import com.ash.studios.musify.Interfaces.IService;
import com.ash.studios.musify.Models.Song;
import com.ash.studios.musify.R;
import com.ash.studios.musify.utils.App;
import com.ash.studios.musify.utils.Constants;
import com.ash.studios.musify.utils.Engine;
import com.ash.studios.musify.utils.Instance;
import com.ash.studios.musify.utils.Utils;

import static com.ash.studios.musify.utils.App.CHANNEL_ID;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    Context context;
    MediaSessionCompat mediaSession;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        mediaSession = new MediaSessionCompat(context, "MUSIFY");
        mediaSession.setMetadata(MediaMetadataCompat.fromMediaMetadata(
                new MediaMetadata.Builder().putLong(MediaMetadata.METADATA_KEY_DURATION, -1L).build()
        ));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        switch (intent.getAction()) {
            case Constants.ACTION.CREATE:
                if (Instance.playing) notificationForPlay();
                else notificationForPause();
                break;
            case Constants.ACTION.STOP_SERVICE:
                ((IService) ((App) getApplicationContext()).getCurrentContext()).onStopService();
                stopSelf();
                break;
            case Constants.ACTION.PREV:
                ((IControl) ((App) getApplicationContext()).getCurrentContext()).onPrevClicked();
                break;
            case Constants.ACTION.PLAY:
                Instance.playing = true;
                ((IControl) ((App) getApplicationContext()).getCurrentContext()).onPlayClicked();
                notificationForPlay();
                break;
            case Constants.ACTION.PAUSE:
                Instance.playing = false;
                ((IControl) ((App) getApplicationContext()).getCurrentContext()).onPauseClicked();
                notificationForPause();
                break;
            case Constants.ACTION.NEXT:
                ((IControl) ((App) getApplicationContext()).getCurrentContext()).onNextClicked();
                break;
        }

        return START_NOT_STICKY;
    }

    private void notificationForPlay() {
        Intent mainIntent = new Intent(context, Player.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent mainPending = PendingIntent.getActivity(context, 0, mainIntent, 0);

        Intent stopIntent = new Intent(context, MusicService.class).setAction(Constants.ACTION.STOP_SERVICE);
        PendingIntent stopPending = PendingIntent.getService(context, 0, stopIntent, 0);

        Intent prevIntent = new Intent(context, MusicService.class).setAction(Constants.ACTION.PREV);
        PendingIntent prevPending = PendingIntent.getService(context, 0, prevIntent, 0);

        Intent pauseIntent = new Intent(context, MusicService.class).setAction(Constants.ACTION.PAUSE);
        PendingIntent pausePending = PendingIntent.getService(context, 0, pauseIntent, 0);

        Intent nextIntent = new Intent(context, MusicService.class).setAction(Constants.ACTION.NEXT);
        PendingIntent nextPending = PendingIntent.getService(context, 0, nextIntent, 0);

        Song song = Instance.songs.get(Instance.position);
        StringBuilder count = new StringBuilder().append(Instance.position + 1).append("/").append(Instance.songs.size());
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                .addAction(R.drawable.ic_previous, "Previous", prevPending)
                .addAction(R.drawable.ic_pause, "Pause", pausePending)
                .addAction(R.drawable.ic_next, "Next", nextPending)
                .addAction(R.drawable.ic_close, "Stop", stopPending)

                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1, 2, 3)
                        .setMediaSession(mediaSession.getSessionToken()))
                .setLargeIcon(getBitmap(Utils.getAlbumArt(song.getAlbum_id())))
                .setSmallIcon(R.drawable.ic_icon)

                .setContentTitle(song.getTitle())
                .setContentText(song.getArtist())
                .setSubText(count)

                .setContentIntent(mainPending)
                .build();

        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
    }

    private void notificationForPause() {
        Intent mainIntent = new Intent(context, Player.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent mainPending = PendingIntent.getActivity(context, 0, mainIntent, 0);

        Intent stopIntent = new Intent(context, MusicService.class).setAction(Constants.ACTION.STOP_SERVICE);
        PendingIntent stopPending = PendingIntent.getService(context, 3, stopIntent, 0);

        Intent prevIntent = new Intent(context, MusicService.class).setAction(Constants.ACTION.PREV);
        PendingIntent prevPending = PendingIntent.getService(context, 0, prevIntent, 0);

        Intent playIntent = new Intent(context, MusicService.class).setAction(Constants.ACTION.PLAY);
        PendingIntent playPending = PendingIntent.getService(context, 1, playIntent, 0);

        Intent nextIntent = new Intent(context, MusicService.class).setAction(Constants.ACTION.NEXT);
        PendingIntent nextPending = PendingIntent.getService(context, 2, nextIntent, 0);

        Song song = Instance.songs.get(Instance.position);
        StringBuilder count = new StringBuilder().append(Instance.position + 1).append("/").append(Instance.songs.size());
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                .addAction(R.drawable.ic_previous, "Previous", prevPending)
                .addAction(R.drawable.ic_play, "Play", playPending)
                .addAction(R.drawable.ic_next, "Next", nextPending)
                .addAction(R.drawable.ic_close, "Stop", stopPending)

                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1, 2, 3)
                        .setMediaSession(mediaSession.getSessionToken()))
                .setLargeIcon(getBitmap(Utils.getAlbumArt(song.getAlbum_id())))
                .setSmallIcon(R.drawable.ic_icon)

                .setContentTitle(song.getTitle())
                .setContentText(song.getArtist())
                .setSubText(count)

                .setContentIntent(mainPending)
                .build();

        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
    }

    private Bitmap getBitmap(Uri uri) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (Exception e) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
        }
        return bitmap;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        new Engine(context).playNextSong();
        if (Instance.mp != null) {
            Instance.mp = MediaPlayer.create(getApplicationContext(), Instance.uri);
            Instance.mp.start();
            Instance.mp.setOnCompletionListener(this);
        }
        Utils.putCurrentPosition(context, Instance.position);
        notificationForPlay();
    }
}
