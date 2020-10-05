package com.dev.darrell.musicfinder.activity;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.dev.darrell.musicfinder.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class MusicPlayingNotification {
    public static final String KEY_LOOP = "com.dev.darrell.musicfinder.activity.TrackPlayer.loopPlayback";
    public static final String KEY_PAUSE_PLAY = "com.dev.darrell.musicfinder.activity.TrackPlayer.startOrStopPlayback";
    public static final String KEY_STOP = "com.dev.darrell.musicfinder.activity.TrackPlayer.stopPlayback";
    private static final String NOTIFICATION_TAG = "MusicPlayback";
    private static String channelID = "MusicPlaybackID";
    private static String channelName = "MusicPlayback";
    private static NotificationManager mNotificationManager;
    private NotificationManager mManager;

    public static void notify(final Context context, final String trackCover, final String track,
                              final String artist, final int trackId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel musicPlayback = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_LOW);
            musicPlayback.enableVibration(false);
            musicPlayback.enableLights(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                musicPlayback.setAllowBubbles(false);
            }

            mNotificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(musicPlayback);
        }

        Intent trackPlayerIntent = new Intent(context, TrackPlayer.class);
        trackPlayerIntent.putExtra(TrackPlayer.TRACK_EXTRA, trackId);

        Intent loopIntent = new Intent(KEY_LOOP);
        PendingIntent loopPendingIntent = PendingIntent.getBroadcast(context, 0, loopIntent, 0);

        Intent pausePlayIntent = new Intent(KEY_PAUSE_PLAY);
        PendingIntent pausePlayPendingIntent = PendingIntent.getBroadcast(context, 0, pausePlayIntent, 0);

        Intent stopIntent = new Intent(KEY_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, 0);


        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSmallIcon(R.drawable.sharp_headset_black_18dp)
                .setContentTitle(track)
                .setContentText(artist)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .addAction(R.drawable.ic_loop_media, "Repeat", loopPendingIntent)
                .addAction(R.drawable.ic_pause_media, "Pause", pausePlayPendingIntent)
                .addAction(R.drawable.ic_stop_media, "Stop", stopPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(stopPendingIntent))
                .setContentIntent(PendingIntent.getActivity(context, 0, trackPlayerIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT))
                .setTicker(track + " by " + artist);

        Picasso.get().load(trackCover).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                builder.setLargeIcon(bitmap);

                MusicPlayingNotification.notify(context, builder.build());
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                MusicPlayingNotification.notify(context, builder.build());
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(Context context, Notification notification) {
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_TAG, 0, notification);
    }
}
