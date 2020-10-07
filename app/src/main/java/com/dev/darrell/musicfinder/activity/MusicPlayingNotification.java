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
import android.graphics.drawable.Icon;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.dev.darrell.musicfinder.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import static com.dev.darrell.musicfinder.activity.TrackPlayer.mMediaPlayer;

public class MusicPlayingNotification {
    public static final String KEY_LOOP = "com.dev.darrell.musicfinder.activity.TrackPlayer.loopPlayback";
    public static final String KEY_PAUSE_PLAY = "com.dev.darrell.musicfinder.activity.TrackPlayer.startOrStopPlayback";
    public static final String KEY_STOP = "com.dev.darrell.musicfinder.activity.TrackPlayer.stopPlayback";
    private static final String NOTIFICATION_TAG = "MusicPlayback";
    private static final String CHANNEL_ID = "MusicPlaybackID";
    private static final String CHANNEL_NAME = "Music Playback";
    private NotificationManager mManager;
    private static NotificationCompat.Builder mBuilder;
    private static int mPauseNdPlay = R.drawable.ic_pause_media;
    private static int mLoop = R.drawable.loop;
    private static PendingIntent mPausePlayPendingIntent;
    private static PendingIntent mLoopPendingIntent;
    private static Notification mNotification;

    public static void createNotification(final Context context, final String trackCover, final String track,
                                          final String artist, final int trackId) {

//        TODO: Add functionality to clear notification when stop is pressed or back pressed.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel musicPlayback = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW);
            musicPlayback.enableVibration(false);
            musicPlayback.enableLights(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                musicPlayback.setAllowBubbles(false);
            }

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(musicPlayback);
        }

        Intent trackPlayerIntent = new Intent(context, TrackPlayer.class);
        trackPlayerIntent.putExtra(TrackPlayer.TRACK_EXTRA, trackId);

        Intent loopIntent = new Intent(KEY_LOOP);
        mLoopPendingIntent = PendingIntent.getBroadcast(context, 0, loopIntent, 0);

        Intent pausePlayIntent = new Intent(KEY_PAUSE_PLAY);
        mPausePlayPendingIntent = PendingIntent.getBroadcast(context, 0, pausePlayIntent, 0);

        Intent stopIntent = new Intent(KEY_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, 0);

        mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSmallIcon(R.drawable.sharp_headset_black_18dp)
                .setContentTitle(track)
                .setContentText(artist)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(false)
                .addAction(R.drawable.loop, "Repeat", mLoopPendingIntent)
                .addAction(R.drawable.ic_pause_media, "Pause", mPausePlayPendingIntent)
                .addAction(R.drawable.ic_stop_media, "Stop", stopPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(stopPendingIntent))
                .setContentIntent(PendingIntent.getActivity(context, 0, trackPlayerIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT))
                .setTicker(track + " by " + artist);

//        Picasso loads track cover into large icon and starts the notification
        Picasso.get().load(trackCover).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mBuilder.setLargeIcon(bitmap);
                mNotification = mBuilder.build();

                MusicPlayingNotification.notify(context, mNotification);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                MusicPlayingNotification.notify(context, mNotification);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    public static void updateActions(Context context) {
        checkIfPlaying();
        Notification.Action.Builder playActionBuilder = new Notification.Action.Builder(
                Icon.createWithResource(context, mPauseNdPlay), "Play", mPausePlayPendingIntent);
        mNotification.actions[1] = playActionBuilder.build();
        notify(context, mNotification);
    }

    public static void updateLoopAction(Context context) {
        checkIfLooping();
        Notification.Action.Builder loopActionBuilder = new Notification.Action.Builder(
                Icon.createWithResource(context, mLoop), "Loop", mLoopPendingIntent);

        mNotification.actions[0] = loopActionBuilder.build();
        notify(context, mNotification);
    }

    private static void checkIfLooping() {
        if (mMediaPlayer.isLooping()) {
            mLoop = R.drawable.loop_one;
        } else if (!mMediaPlayer.isLooping()) {
            mLoop = R.drawable.loop;
        }
    }

    private static void checkIfPlaying() {
        if (mMediaPlayer.isPlaying()) {
            mPauseNdPlay = R.drawable.ic_pause_media;
        } else if (!mMediaPlayer.isPlaying()) {
            mPauseNdPlay = R.drawable.ic_play_media;
        }
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(Context context, Notification notification) {
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_TAG, 0, notification);
    }
}
