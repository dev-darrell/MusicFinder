package com.dev.darrell.musicfinder.activity;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dev.darrell.musicfinder.R;
import com.dev.darrell.musicfinder.model.Track;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class TrackPlayer extends AppCompatActivity implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    public static final String TRACK_EXTRA = "com.dev.darrell.musicfinder.activity.TrackPlayer";
    private static final String TAG = "TrackPlayer";
    private String mtrackTitle;
    private String mArtistName;
    private String mAlbumCover;
    private String mTrackAudio;

    private TextView mTvTrackName;
    private ImageView mIvAlbumArt;
    private SeekBar mSeekBar;
    private FloatingActionButton mPauseNdPlay;
    private FloatingActionButton mLoop;
    private FloatingActionButton mStopPlayback;
    private MediaPlayer mMediaPlayer = null;
    private boolean mMediaPlayerPrepped;
    private TextView mCurrentPosition;
    private TextView mTrackDuration;
    private int mFileDuration;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_player);

        Intent intent = getIntent();
        Track currentTrack = intent.getParcelableExtra(TRACK_EXTRA);

        mtrackTitle = currentTrack.getTitle();
        mArtistName = currentTrack.getArtist();
        mAlbumCover = currentTrack.getAlbumCover();
        mTrackAudio = currentTrack.getPreviewLink().toString();

        mTvTrackName = findViewById(R.id.tv_track_playing);
        mIvAlbumArt = findViewById(R.id.img_album_playing);
        mSeekBar = findViewById(R.id.duration_seekbar);
        mPauseNdPlay = findViewById(R.id.fab_pause_play);
        mLoop = findViewById(R.id.fab_loop);
        mStopPlayback = findViewById(R.id.fab_stop_playback);
        mCurrentPosition = findViewById(R.id.track_current_position);
        mTrackDuration = findViewById(R.id.track_duration);

        LoadLayoutItems();
    }

    private void LoadLayoutItems() {
        Log.d(TAG, "LoadLayoutItems: Loading track name and cover");
        mTvTrackName.setText(mtrackTitle);

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
        // TODO: Set seekbar up to show playback progress and seek through songs.
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

        Toast toast = Toast.makeText(this, "MediaPlayer Prepared", Toast.LENGTH_LONG);
        toast.show();
        mMediaPlayerPrepped = true;
        mFileDuration = mMediaPlayer.getDuration();
        getDurationTimer();
        mSeekBar.setMax(mFileDuration);

        mediaPlayer.start();
        mPauseNdPlay.setImageResource(R.drawable.ic_pause_media);

        mHandler = new Handler();
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer != null) {
                    int currentPosition = mMediaPlayer.getCurrentPosition();
                    mSeekBar.setProgress(currentPosition);

                    final long minutes = (currentPosition / 1000) / 60;
                    final int seconds = (int) ((currentPosition / 1000) % 60);
                    mCurrentPosition.setText(minutes + ":" + seconds);
                }
                mHandler.postDelayed(this, 1000);
            }
        });
        mPauseNdPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mPauseNdPlay.setImageResource(R.drawable.ic_play_media);
                } else if (mMediaPlayer == null) {
                    createMediaPlayer();
                } else {
                    mediaPlayer.start();
                    mPauseNdPlay.setImageResource(R.drawable.ic_pause_media);
                }
            }
        });

        mLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mediaPlayer.isLooping()) {
                    mediaPlayer.setLooping(true);
                    mLoop.setFocusable(true);
                } else {
                    mediaPlayer.setLooping(false);
                    mLoop.setSelected(false);
                }
            }
        });

        mStopPlayback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPauseNdPlay.setImageResource(R.drawable.ic_play_media);
                mediaPlayer.stop();
            }
        });
    }
    // end of onPrepare method

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