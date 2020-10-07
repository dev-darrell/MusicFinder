package com.dev.darrell.musicfinder.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dev.darrell.musicfinder.R;
import com.dev.darrell.musicfinder.model.Track;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static com.dev.darrell.musicfinder.activity.MusicPlayingNotification.KEY_LOOP;
import static com.dev.darrell.musicfinder.activity.MusicPlayingNotification.KEY_PAUSE_PLAY;
import static com.dev.darrell.musicfinder.activity.MusicPlayingNotification.KEY_STOP;

public class TrackPlayer extends AppCompatActivity implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    public static final String TRACK_EXTRA = "com.dev.darrell.musicfinder.activity.TrackPlayer";
    private static final String TAG = "TrackPlayer";
    private String mTrackTitle;
    private String mArtistName;
    private String mAlbumCover;
    private String mTrackAudio;

    private TextView mTvTrackName;
    private ImageView mIvAlbumArt;
    private SeekBar mSeekBar;
    private ImageButton mPauseNdPlay;
    private ImageButton mLoop;
    private ImageButton mStopPlayback;
    private TextView mCurrentPosition;
    private TextView mTrackDuration;
    private int mFileDuration;
    private Handler mHandler;
    private int mCurrentTrackId;
    public static MediaPlayer mMediaPlayer;
    private ProgressBar mLoadProgressBar;
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_player);

        Intent intent = getIntent();
        mCurrentTrackId = intent.getIntExtra(TRACK_EXTRA, -1);

        Track currentTrack = getCurrentTrack();

        if (currentTrack != null) {
            mTrackTitle = currentTrack.getTitle();
            mArtistName = currentTrack.getArtist();
            mAlbumCover = currentTrack.getAlbumCover();
            mTrackAudio = currentTrack.getPreviewLink().toString();
        }

        mTvTrackName = findViewById(R.id.tv_track_playing);
        mIvAlbumArt = findViewById(R.id.img_album_playing);
        mSeekBar = findViewById(R.id.duration_seekbar);
        mPauseNdPlay = findViewById(R.id.btn_play_pause);
        mLoop = findViewById(R.id.btn_loop);
        mStopPlayback = findViewById(R.id.btn_stop);
        mLoadProgressBar = findViewById(R.id.load_progress_bar);
        mCurrentPosition = findViewById(R.id.track_current_position);
        mTrackDuration = findViewById(R.id.track_duration);

        registerBroadcastReceiver();
        LoadLayoutItems();
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(KEY_LOOP);
        filter.addAction(KEY_PAUSE_PLAY);
        filter.addAction(KEY_STOP);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (Objects.requireNonNull(intent.getAction())) {
                    case KEY_LOOP:
                        loopPlayback(mMediaPlayer);
                        break;
                    case KEY_PAUSE_PLAY:
                        pauseOrPlayTrack(mMediaPlayer);
                        break;
                    case KEY_STOP:
                        stopPlayback(mMediaPlayer);
                        break;
                }
            }
        };

        registerReceiver(mBroadcastReceiver, filter);
    }

    private Track getCurrentTrack() {
        ArrayList<Track> trackList = MainActivity.mTrackArrayList;
        for (int i = 0; i < trackList.size(); i++) {
            Track currentTrack = trackList.get(i);
            if (currentTrack.getId() == mCurrentTrackId) {
                return currentTrack;
            }
        }
        return null;
    }

    private void LoadLayoutItems() {
        Log.d(TAG, "LoadLayoutItems: Loading track name and cover");
        mTvTrackName.setText(mTrackTitle);

        mLoadProgressBar.setVisibility(View.VISIBLE);

        Picasso.get().load(mAlbumCover)
                .fit()
                .placeholder(R.drawable.sharp_headset_black_18dp)
                .error(R.drawable.sharp_headset_black_18dp)
                .into(mIvAlbumArt);

        createMediaPlayer();
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mMediaPlayer != null && fromUser) {
                    mMediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
//                    mMediaPlayer.seekTo(seekBar.getProgress());
//                }
            }
        });
    }

    private void createMediaPlayer() {
        Log.d(TAG, "createMediaPlayer: creating media player instance");

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

        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);

        mMediaPlayer.prepareAsync();
    }

    @Override
    public void onPrepared(final MediaPlayer mediaPlayer) {
        Log.d(TAG, "onPrepared: Media player in prepared state");

        mFileDuration = mediaPlayer.getDuration();
        getDurationTimer();
        mSeekBar.setMax(mFileDuration);

        mLoadProgressBar.setVisibility(View.GONE);
        mediaPlayer.start();
        mPauseNdPlay.setImageResource(R.drawable.ic_pause_media);

//      Display notification in status bar
        showMusicNotification();

        mHandler = new Handler();
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    mSeekBar.setProgress(currentPosition);

                    final long minutes = (currentPosition / 1000) / 60;
                    final int seconds = ((currentPosition / 1000) % 60);
                    mCurrentPosition.setText(minutes + ":" + seconds);
                }
                mHandler.postDelayed(this, 1000);
            }
        });

        mPauseNdPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseOrPlayTrack(mediaPlayer);
            }
        });

        mLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loopPlayback(mediaPlayer);
            }
        });

        mStopPlayback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlayback(mediaPlayer);
            }
        });
    }
//  end of onPrepare method

    private void stopPlayback(MediaPlayer mediaPlayer) {
        mPauseNdPlay.setImageResource(R.drawable.ic_play_media);
        mediaPlayer.stop();
    }

    private void loopPlayback(MediaPlayer mediaPlayer) {
        if (!mediaPlayer.isLooping()) {
            mediaPlayer.setLooping(true);
            mLoop.setImageResource(R.drawable.loop_one);
            MusicPlayingNotification.updateLoopAction(this);
        } else {
            mediaPlayer.setLooping(false);
            mLoop.setImageResource(R.drawable.loop);
            MusicPlayingNotification.updateLoopAction(this);
        }
    }

    private void pauseOrPlayTrack(MediaPlayer mediaPlayer) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mPauseNdPlay.setImageResource(R.drawable.ic_play_media);
        } else if (mMediaPlayer == null) {
            createMediaPlayer();
        } else {
            mediaPlayer.start();
            mPauseNdPlay.setImageResource(R.drawable.ic_pause_media);
        }
        MusicPlayingNotification.updateActions(this);
    }



    private void showMusicNotification() {
        MusicPlayingNotification.createNotification(
                this, mAlbumCover, mTrackTitle, mArtistName, mCurrentTrackId);
    }

    public void getDurationTimer() {
        final long minutes = (mFileDuration / 1000) / 60;
        final int seconds = (int) ((mFileDuration / 1000) % 60);
        mTrackDuration.setText(minutes + ":" + seconds);
    }

//    public void getCurrentTimer(){
//
//    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mPauseNdPlay.setImageResource(R.drawable.ic_play_media);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onBackPressed() {
        mMediaPlayer.release();
        mMediaPlayer = null;
        super.onBackPressed();
    }

}