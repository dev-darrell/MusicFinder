package com.dev.darrell.musicfinder.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dev.darrell.musicfinder.R;
import com.dev.darrell.musicfinder.activity.MainActivity;
import com.dev.darrell.musicfinder.activity.MusicPlayingNotification;
import com.dev.darrell.musicfinder.model.Track;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import static com.dev.darrell.musicfinder.activity.MusicPlayingNotification.KEY_LOOP;
import static com.dev.darrell.musicfinder.activity.MusicPlayingNotification.KEY_PAUSE_PLAY;
import static com.dev.darrell.musicfinder.activity.MusicPlayingNotification.KEY_STOP;
import static com.dev.darrell.musicfinder.player.PlayerService.MEDIA_DURATION;
import static com.dev.darrell.musicfinder.player.PlayerService.MEDIA_PREPARED;
import static com.dev.darrell.musicfinder.player.PlayerService.TRACK_AUDIO;

public class TrackPlayer extends AppCompatActivity implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    public static final String TRACK_EXTRA = "com.dev.darrell.musicfinder.player.TrackPlayer";
    public static final String KEY_PROGRESS_CHANGED =
            "com.dev.darrell.musicfinder.player.playerService.seekbarProgress";
    private static final String TAG = "TrackPlayer";
    public static final String SEEKBAR_PROGRESS = "seekbar_progress";
    public static final String ALBUM_COVER = "albumCover";
    public static final String TRACK_TITLE = "trackTitle";
    public static final String ARTIST_NAME = "artistName";
    public static final String TRACK_ID = "trackId";
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
        filter.addAction(MEDIA_PREPARED);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Objects.equals(intent.getAction(), MEDIA_PREPARED)) {
                    mediaPlayerPrepared(intent.getIntExtra(MEDIA_DURATION, -1));
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);
    }

    private void mediaPlayerPrepared(int trackDuration) {
        mLoadProgressBar.setVisibility(View.GONE);
        if (trackDuration != -1) {
            mFileDuration = trackDuration;
        }
        getDurationTimer();
        mSeekBar.setMax(mFileDuration);

        mPauseNdPlay.setImageResource(R.drawable.ic_pause_media);

//      Display notification in status bar
        showMusicNotification();

//        mHandler = new Handler();
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (mMediaPlayer != null) {
//                    int currentPosition = mediaPlayer.getCurrentPosition();
//                    mSeekBar.setProgress(currentPosition);
//
//                    final long minutes = (currentPosition / 1000) / 60;
//                    final int seconds = ((currentPosition / 1000) % 60);
//                    mCurrentPosition.setText(minutes + ":" + seconds);
//                }
//                mHandler.postDelayed(this, 1000);
//            }
//        });


        mPauseNdPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseOrPlayTrack();
            }
        });

        mLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loopPlayback();
            }
        });

        mStopPlayback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlayback();
            }
        });


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
                    Intent intent = new Intent(KEY_PROGRESS_CHANGED);
                    intent.putExtra(SEEKBAR_PROGRESS, progress);
                    LocalBroadcastManager.getInstance(TrackPlayer.this).sendBroadcast(intent);

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
        Intent serviceIntent = new Intent(this, PlayerService.class);
        serviceIntent.putExtra(TRACK_AUDIO, mTrackAudio);
        serviceIntent.putExtra(ALBUM_COVER, mAlbumCover);
        serviceIntent.putExtra(TRACK_TITLE, mTrackTitle);
        serviceIntent.putExtra(ARTIST_NAME, mArtistName);
        serviceIntent.putExtra(TRACK_ID, mCurrentTrackId);
        startService(serviceIntent);

        /* Moved to service
         */
    }

    @Override
    public void onPrepared(final MediaPlayer mediaPlayer) {
        Log.d(TAG, "onPrepared: Media player in prepared state");

//        mFileDuration = mediaPlayer.getDuration();
//        getDurationTimer();
//        mSeekBar.setMax(mFileDuration);

//        mLoadProgressBar.setVisibility(View.GONE);

//        mediaPlayer.start();
//        mPauseNdPlay.setImageResource(R.drawable.ic_pause_media);

////      Display notification in status bar
//        showMusicNotification();

//        mHandler = new Handler();
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (mMediaPlayer != null) {
//                    int currentPosition = mediaPlayer.getCurrentPosition();
//                    mSeekBar.setProgress(currentPosition);
//
//                    final long minutes = (currentPosition / 1000) / 60;
//                    final int seconds = ((currentPosition / 1000) % 60);
//                    mCurrentPosition.setText(minutes + ":" + seconds);
//                }
//                mHandler.postDelayed(this, 1000);
//            }
//        });

//        mPauseNdPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                pauseOrPlayTrack(mediaPlayer);
//            }
//        });
//
//        mLoop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                loopPlayback(mediaPlayer);
//            }
//        });
//
//        mStopPlayback.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                stopPlayback(mediaPlayer);
//            }
//        });
    }
//  end of onPrepare method

    private void stopPlayback() {
        Intent intent = new Intent(KEY_STOP);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void loopPlayback() {
        Intent intent = new Intent(KEY_LOOP);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void pauseOrPlayTrack() {
        Intent intent = new Intent(KEY_PAUSE_PLAY);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
        super.onBackPressed();
    }

}