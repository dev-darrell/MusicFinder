package com.dev.darrell.musicfinder.activity;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dev.darrell.musicfinder.R;
import com.dev.darrell.musicfinder.model.Track;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class TrackPlayer extends AppCompatActivity implements MediaPlayer.OnPreparedListener {
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

        LoadLayoutItems();
        createMediaPlayer();
    }

    private void LoadLayoutItems() {
        Log.d(TAG, "LoadLayoutItems: Loading track name and cover");
        mTvTrackName.setText(mtrackTitle);

        Picasso.get().load(mAlbumCover)
                .fit()
                .placeholder(R.drawable.sharp_headset_black_18dp)
                .error(R.drawable.sharp_headset_black_18dp)
                .into(mIvAlbumArt);
    }

    private void createMediaPlayer() {
        // TODO: Debug media not playing and complete mediaPlayer implementation (looping, stopping, clearing resources.)
        Log.d(TAG, "createMediaPlayer: creating media player instance");
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        try {
            mediaPlayer.setDataSource(mTrackAudio);
        } catch (IOException e) {
            Log.d(TAG, "createMediaPlayer: Data source error =" + e.toString());
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }

    @Override
    public void onPrepared(final MediaPlayer mediaPlayer) {
        mPauseNdPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    mPauseNdPlay.setImageResource(R.drawable.ic_pause_media);
                } else {
                    mediaPlayer.pause();
                    mPauseNdPlay.setImageResource(R.drawable.ic_play_media);
                }
            }
        });
    }
}