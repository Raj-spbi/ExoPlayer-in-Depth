package com.aakash.databindingtest;

import android.annotation.SuppressLint;
import android.app.PictureInPictureParams;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;

public class CustomMediaPlayerActivity extends AppCompatActivity {


    private boolean isShowingTrackSelectionDialog;
    private DefaultTrackSelector trackSelector;
    String[] speed = {"0.25x", "0.5x", "Normal", "1.5x", "2x"};
    //demo url
//    String url5 = "https://5b44cf20b0388.streamlock.net:8443/vod/smil:bbb.smil/playlist.m3u8";
    String url5 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4";
    String url2 = "https://multiplatform-f.akamaihd.net/i/multi/will/bunny/big_buck_bunny_,640x360_400,640x360_700,640x360_1000,950x540_1500,.f4v.csmil/master.m3u8";
    String url3 = "https://multiplatform-f.akamaihd.net/i/multi/april11/sintel/sintel-hd_,512x288_450_b,640x360_700_b,768x432_1000_b,1024x576_1400_m,.mp4.csmil/master.m3u8";
    String url4 = "http://d3rlna7iyyu8wu.cloudfront.net/skip_armstrong/skip_armstrong_stereo_subs.m3u8";
    String url1 = "https://livesim.dashif.org/livesim/chunkdur_1/ato_7/testpic4_8s/Manifest.mpd";
    PlayerView playerView;
    SimpleExoPlayer simpleExoPlayer;
    RelativeLayout zoom_layout, zoom_container;

    float scale_factor = 1.0f;
    ScaleGestureDetector scaleGestureDetector;
    TextView precentageView;
    ImageView muteOnBtn, muteOffBtn, brightnessCtrlBtn, pipBtn;
    boolean isMute = false;
    PictureInPictureParams.Builder pictureInPicture;
    boolean isCrossChecked = false;


    @SuppressLint({"Range", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_media_player);

        trackSelector = new DefaultTrackSelector(this);
        simpleExoPlayer = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build();
        playerView = findViewById(R.id.exoPlayerView);
        playerView.setPlayer(simpleExoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(url5);
        simpleExoPlayer.addMediaItem(mediaItem);
        simpleExoPlayer.prepare();
        simpleExoPlayer.play();


        ImageView farwordBtn = playerView.findViewById(R.id.fwd);
        ImageView rewBtn = playerView.findViewById(R.id.rew);
        ImageView setting = playerView.findViewById(R.id.exo_track_selection_view);
        ImageView speedBtn = playerView.findViewById(R.id.exo_playback_speed);
        TextView speedTxt = playerView.findViewById(R.id.speed);
        zoom_layout = playerView.findViewById(R.id.zoom_layout);
        zoom_container = playerView.findViewById(R.id.zoom_container);
        precentageView = playerView.findViewById(R.id.precentageView);
        ImageView lockIconBtn = playerView.findViewById(R.id.lockIconBtn);
        ImageView lockExoBtn = playerView.findViewById(R.id.lockExoBtn);
        muteOffBtn = playerView.findViewById(R.id.muteOffBtn);
        muteOnBtn = playerView.findViewById(R.id.muteOnBtn);
        brightnessCtrlBtn = playerView.findViewById(R.id.brightnessCtrlBtn);
        pipBtn = playerView.findViewById(R.id.pipBtn);
        ConstraintLayout rootView = playerView.findViewById(R.id.rootView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pictureInPicture = new PictureInPictureParams.Builder();
        }


        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleDetector());

        speedBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(CustomMediaPlayerActivity.this);
            builder.setTitle("Set Speed");
            builder.setItems(speed, (dialog, which) -> {
                // the user clicked on colors[which]

                if (which == 0) {

                    speedTxt.setVisibility(View.VISIBLE);
                    speedTxt.setText("0.25X");
                    PlaybackParameters param = new PlaybackParameters(0.5f);
                    simpleExoPlayer.setPlaybackParameters(param);


                }
                if (which == 1) {

                    speedTxt.setVisibility(View.VISIBLE);
                    speedTxt.setText("0.5X");
                    PlaybackParameters param = new PlaybackParameters(0.5f);
                    simpleExoPlayer.setPlaybackParameters(param);


                }
                if (which == 2) {

                    speedTxt.setVisibility(View.GONE);
                    PlaybackParameters param = new PlaybackParameters(1f);
                    simpleExoPlayer.setPlaybackParameters(param);


                }
                if (which == 3) {
                    speedTxt.setVisibility(View.VISIBLE);
                    speedTxt.setText("1.5X");
                    PlaybackParameters param = new PlaybackParameters(1.5f);
                    simpleExoPlayer.setPlaybackParameters(param);

                }
                if (which == 4) {


                    speedTxt.setVisibility(View.VISIBLE);
                    speedTxt.setText("2X");

                    PlaybackParameters param = new PlaybackParameters(2f);
                    simpleExoPlayer.setPlaybackParameters(param);


                }


            });
            builder.show();

        });

        pipBtn.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Rational aspectRatio = new Rational(16, 9);
                pictureInPicture.setAspectRatio(aspectRatio);
                enterPictureInPictureMode(pictureInPicture.build());
            } else {
                Log.wtf("not oreo", "yes");
                //Not oreo "Yes"
            }
        });

        brightnessCtrlBtn.setOnClickListener(v -> {
            VolumeDialog volumeDialog = new VolumeDialog();
            volumeDialog.show(getSupportFragmentManager(), "dialog");
        });

        playerView.setOnTouchListener((view, motionEvent) -> {
            scaleGestureDetector.onTouchEvent(motionEvent);
            zoom_container.setVisibility(View.VISIBLE);
            return false;
        });

        lockExoBtn.setOnClickListener(v -> {
            rootView.setVisibility(View.INVISIBLE);
            lockIconBtn.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Locked", Toast.LENGTH_SHORT).show();
        });

        lockIconBtn.setOnClickListener(v -> {
            rootView.setVisibility(View.VISIBLE);
            lockIconBtn.setVisibility(View.GONE);
        });

        muteOnBtn.setOnClickListener(v -> {
            muteOnBtn.setVisibility(View.GONE);
            muteOffBtn.setVisibility(View.VISIBLE);
            simpleExoPlayer.setVolume(0);
        });
        muteOffBtn.setOnClickListener(v -> {
            muteOnBtn.setVisibility(View.VISIBLE);
            muteOffBtn.setVisibility(View.GONE);
            simpleExoPlayer.setVolume(50.0F);
        });
        farwordBtn.setOnClickListener(v -> simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() + 10000));
        rewBtn.setOnClickListener(v -> {

            long num = simpleExoPlayer.getCurrentPosition() - 10000;
            if (num < 0) {

                simpleExoPlayer.seekTo(0);


            } else {

                simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() - 10000);

            }


        });


        ImageView fullscreenButton = playerView.findViewById(R.id.fullscreen);
        fullscreenButton.setOnClickListener(view -> {


            int orientation = CustomMediaPlayerActivity.this.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // code for portrait mode

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


            } else {
                // code for landscape mode

//                    Toast.makeText(CustomMediaPlayerActivity.this, "Land", Toast.LENGTH_SHORT).show();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            }


        });

        findViewById(R.id.exo_play).setOnClickListener(v -> simpleExoPlayer.play());
        findViewById(R.id.exo_pause).setOnClickListener(v -> simpleExoPlayer.pause());


        simpleExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == ExoPlayer.STATE_ENDED) {

                }
            }
        });


        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShowingTrackSelectionDialog
                        && TrackSelectionDialog.willHaveContent(trackSelector)) {
                    isShowingTrackSelectionDialog = true;
                    TrackSelectionDialog trackSelectionDialog =
                            TrackSelectionDialog.createForTrackSelector(
                                    trackSelector,
                                    /* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false);
                    trackSelectionDialog.show(getSupportFragmentManager(), /* tag= */ null);

                }


            }
        });


    }






    protected void releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
            trackSelector = null;
        }


    }


    @Override
    public void onStop() {
        super.onStop();
        if (isCrossChecked) {
            releasePlayer();
        }
    }

    private class ScaleDetector extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (gestureTolerance(detector)) {
                // performing scaling
                scale_factor *= detector.getScaleFactor();
                scale_factor = Math.max(0.25f, Math.min(scale_factor, 3.0f));
                zoom_layout.setScaleX(scale_factor);
                zoom_layout.setScaleY(scale_factor);
                int percentage = (int) (scale_factor * 100);
                precentageView.setText(percentage + "%");
                zoom_container.setVisibility(View.VISIBLE);

            }
            return true;
        }

        /*    return super.onScale(detector);*/

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            zoom_container.setVisibility(View.GONE);

            super.onScaleEnd(detector);
        }


    }

    private static final int SPAN_SLOP = 7;

    private boolean gestureTolerance(@NonNull ScaleGestureDetector detector) {
        final float spanDelta = Math.abs(detector.getCurrentSpan() - detector.getPreviousSpan());
        return spanDelta > SPAN_SLOP;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        isCrossChecked = isInPictureInPictureMode;

        if (isInPictureInPictureMode) {
            playerView.hideController();
        } else {
            playerView.showController();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPause() {
        super.onPause();
        simpleExoPlayer.setPlayWhenReady(false);
        simpleExoPlayer.getPlaybackState();
        if (isInPictureInPictureMode()) {
            simpleExoPlayer.setPlayWhenReady(true);
        } else {
            simpleExoPlayer.setPlayWhenReady(false);
            simpleExoPlayer.getPlaybackState();
        }

    }
}