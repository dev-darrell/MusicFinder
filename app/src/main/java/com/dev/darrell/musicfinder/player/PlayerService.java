package com.dev.darrell.musicfinder.player;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dev.darrell.musicfinder.activity.MusicPlayingNotification;

import java.io.IOException;
import java.util.Objects;

import static com.dev.darrell.musicfinder.activity.MusicPlayingNotification.KEY_LOOP;
import static com.dev.darrell.musicfinder.activity.MusicPlayingNotification.KEY_PAUSE_PLAY;
import static com.dev.darrell.musicfinder.activity.MusicPlayingNotification.KEY_STOP;
import static com.dev.darrell.musicfinder.player.TrackPlayer.KEY_PROGRESS_CHANGED;
import static com.dev.darrell.musicfinder.player.TrackPlayer.SEEKBAR_PROGRESS;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener {
    public static final String MEDIA_PREPARED = "com.dev.darrell.musicfinder.player.TrackPlayer.mediaPrepared";
    public static final String MEDIA_DURATION = "media_duration";
    public static final String TRACK_AUDIO = "track_audio";
    private static final String TAG = "PlayerService";
    private static final int PLAYER_SERVICE_NOTIFICATION_ID = 1;
    public static MediaPlayer mMediaPlayer;
    private String mTrackAudio;
    private String mAlbumCover;
    private String mTrackTitle;
    private String mArtistName;
    private int mCurrentTrackId;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mTrackAudio = intent.getStringExtra(TRACK_AUDIO);
                mAlbumCover = intent.getStringExtra(TrackPlayer.ALBUM_COVER);
                mTrackTitle = intent.getStringExtra(TrackPlayer.TRACK_TITLE);
                mArtistName = intent.getStringExtra(TrackPlayer.ARTIST_NAME);
                mCurrentTrackId = intent.getIntExtra(TrackPlayer.TRACK_ID, -1);

                registerBroadcastReceiver();
                instantiatePlayer();
            }
        }).start();

        return START_NOT_STICKY;
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(KEY_STOP);
        filter.addAction(KEY_PAUSE_PLAY);
        filter.addAction(KEY_LOOP);
        filter.addAction(KEY_PROGRESS_CHANGED);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (Objects.requireNonNull(intent.getAction())) {
                    case KEY_STOP:
                        stopTrack();
                        break;
                    case KEY_PAUSE_PLAY:
                        pauseOrPlayTrack();
                        break;
                    case KEY_LOOP:
                        loopTrack();
                        break;
                    case KEY_PROGRESS_CHANGED:
                        changeSeekbarProgress(intent.getIntExtra(SEEKBAR_PROGRESS, -1));
                        break;
                }
            }
        };
        registerReceiver(receiver, filter);
    }

    private void changeSeekbarProgress(int progress) {
        if (progress != -1) {
            mMediaPlayer.seekTo(progress);
        }
    }

    private void loopTrack() {
        if (!mMediaPlayer.isLooping()) {
            mMediaPlayer.setLooping(true);
//            mLoop.setImageResource(R.drawable.loop_one);
        } else {
            mMediaPlayer.setLooping(false);
//            mLoop.setImageResource(R.drawable.loop);
        }
        MusicPlayingNotification.updateLoopAction(this);
    }

    private void pauseOrPlayTrack() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
//            mPauseNdPlay.setImageResource(R.drawable.ic_play_media);
        } else if (mMediaPlayer == null) {
            instantiatePlayer();
        } else {
            mMediaPlayer.start();
//            mPauseNdPlay.setImageResource(R.drawable.ic_pause_media);
        }
        MusicPlayingNotification.updateActions(this);
    }

    private void stopTrack() {
//        mPauseNdPlay.setImageResource(R.drawable.ic_play_media);
//      TODO: Fix next line - Contains a bug.
//        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void instantiatePlayer() {

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        try {
            mMediaPlayer.setDataSource(mTrackAudio);
        } catch (IOException e) {
            Log.d(TAG, "createMediaPlayer: Data source error =" + e.toString());
            e.printStackTrace();
        }

        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        int duration = mediaPlayer.getDuration();
        Intent intent = new Intent(MEDIA_PREPARED);
        intent.putExtra(MEDIA_DURATION, duration);
//        intent.putExtra("mediaplayer", (Parcelable) mediaPlayer);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        mediaPlayer.start();

        createNotification();
        startForeground(PLAYER_SERVICE_NOTIFICATION_ID, MusicPlayingNotification.mNotification);
    }

    private void createNotification() {
        MusicPlayingNotification.createNotification(
                this, mAlbumCover, mTrackTitle, mArtistName, mCurrentTrackId);
    }
}
