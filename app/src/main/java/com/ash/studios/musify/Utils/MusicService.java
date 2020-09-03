package com.ash.studios.musify.Utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.ash.studios.musify.Activities.Player;
import com.ash.studios.musify.Model.Song;
import com.ash.studios.musify.R;

import static com.ash.studios.musify.Utils.App.CHANNEL_ID;

public class MusicService extends Service {
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notifyIntent = new Intent(context, Player.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, 0);

        Song song = Instance.songs.get(Instance.position);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                .setLargeIcon(getBitmap(Utils.getAlbumArt(song.getAlbum_id())))
                .setSmallIcon(R.drawable.ic_icon)

                .setContentTitle(song.getTitle())
                .setContentText(song.getArtist())

                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        return START_NOT_STICKY;
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

    private Bitmap getBitmap(Uri uri) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (Exception e) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
        }
        return bitmap;
    }
}
