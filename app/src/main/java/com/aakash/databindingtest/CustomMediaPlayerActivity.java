package com.aakash.databindingtest;

import android.annotation.SuppressLint;
import android.app.PictureInPictureParams;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
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

        float scale_factor = 1.0f;//old
//    float scale_factor = 0f;
    ScaleGestureDetector scaleGestureDetector;
    TextView precentageView;
    ImageView muteOnBtn, muteOffBtn, brightnessCtrlBtn, pipBtn;
    boolean isMute = false;
    PictureInPictureParams.Builder pictureInPicture;
    boolean isCrossChecked = false;
    boolean success = false;

//swipe and zoom variables

    private int device_width, device_height, brightness, media_volume;
    boolean start = false, swipe_move = false;
    boolean left, right;
    private float baseX, baseY;
    private long diffX, diffY;

    public static final int MINIMUM_DISTANCE = 100;


    ProgressBar vol_progress, brt_progress;
    LinearLayout vol_progress_container, vol_text_container, brt_progress_container, brt_text_container;
    TextView brt_text, vol_text;
    ImageView vol_icon, brt_icon;
    AudioManager audioManager;
    ContentResolver contentResolver;
    Window window;
    boolean singleTap = false;

    int orientation;
    boolean doZoom = false;

    Vibrator v;

    @SuppressLint({"Range", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_media_player);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        device_width = displayMetrics.widthPixels;
        device_height = displayMetrics.heightPixels;

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        trackSelector = new DefaultTrackSelector(this);
        simpleExoPlayer = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build();
        playerView = findViewById(R.id.exoPlayerView);
        playerView.setPlayer(simpleExoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(url5);
        simpleExoPlayer.addMediaItem(mediaItem);
        simpleExoPlayer.prepare();
        simpleExoPlayer.play();

        checkOrientation();

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


//        Volume and Brightness variables................
        vol_progress = playerView.findViewById(R.id.vol_progress);
        brt_progress = playerView.findViewById(R.id.brt_progress);
        vol_progress_container = playerView.findViewById(R.id.vol_progress_container);
        vol_text_container = playerView.findViewById(R.id.vol_text_container);
        brt_progress_container = playerView.findViewById(R.id.brt_progress_container);
        brt_text_container = playerView.findViewById(R.id.brt_text_container);
        brt_text = playerView.findViewById(R.id.brt_text);
        vol_text = playerView.findViewById(R.id.vol_text);
        vol_icon = playerView.findViewById(R.id.vol_icon);
        brt_icon = playerView.findViewById(R.id.brt_icon);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //        Volume and Brightness variables................End

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

        playerView.setOnTouchListener(new onSwipeTouchListener(this) {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent1) {
                switch (motionEvent1.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        start = true;
                        if (motionEvent1.getX() < (device_width / 2)) {
                            left = true;
                            right = false;
                        } else if (motionEvent1.getX() > (device_width / 2)) {
                            left = false;
                            right = true;
                        }
                        baseX = motionEvent1.getX();
                        baseY = motionEvent1.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        swipe_move = true;
                        diffX = (long) Math.ceil(motionEvent1.getX() - baseX);
                        diffY = (long) Math.ceil(motionEvent1.getY() - baseY);
                        double brightnessSpeed = 0.01;
                        if (Math.abs(diffY) > MINIMUM_DISTANCE) {
                            start = true;
                            if (Math.abs(diffY) > Math.abs(diffX)) {
                                boolean value;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    value = Settings.System.canWrite(getApplicationContext());
                                    if (value) {
                                        if (left) {
                                            try {
                                                contentResolver = getContentResolver();
                                                window = getWindow();
                                                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                                                brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
                                            } catch (Settings.SettingNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                            int newBrightness = (int) (brightness - (diffY * brightnessSpeed));
                                            if (newBrightness > 250) {
                                                newBrightness = 250;
                                            } else if (newBrightness < 1) {
                                                newBrightness = 1;
                                            }
                                            double brt_percentage = Math.ceil(((double) newBrightness / (double) 250) * (double) 100);
                                            brt_progress_container.setVisibility(View.VISIBLE);
                                            brt_text_container.setVisibility(View.VISIBLE);
                                            brt_progress.setProgress((int) brt_percentage);
                                            if (brt_percentage < 30) {
                                                brt_icon.setImageResource(R.drawable.ic_baseline_brightness_5_24);
                                            } else if (brt_percentage > 30 && brt_percentage < 80) {
                                                brt_icon.setImageResource(R.drawable.ic_baseline_brightness_medium_24);
                                            } else {
                                                brt_icon.setImageResource(R.drawable.ic_baseline_brightness_high_24);
                                            }

                                            brt_text.setText(" " + (int) brt_percentage + " %");
                                            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, (newBrightness));
                                            WindowManager.LayoutParams layoutParams = window.getAttributes();
                                            layoutParams.screenBrightness = brightness / (float) 255;
                                            window.setAttributes(layoutParams);
                                            //
//                                            Toast.makeText(CustomMediaPlayerActivity.this, "left swipe", Toast.LENGTH_SHORT).show();
                                        } else if (right) {

                                            vol_text_container.setVisibility(View.VISIBLE);
                                            media_volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                            int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                                            double cal = (double) diffY * ((double) maxVol / ((double) (device_height * 2) - brightnessSpeed));

                                            int newMediaVolume = media_volume - (int) cal;
                                            if (newMediaVolume > maxVol) {
                                                newMediaVolume = maxVol;
                                            } else if (newMediaVolume < 1) {
                                                newMediaVolume = 0;
                                            }
                                            if (cal > 0) {
                                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
                                            } else {
                                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                                            }
//                                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newMediaVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                                            double volPer = Math.ceil((((double) newMediaVolume / (double) maxVol) * (double) 100));
                                            vol_text.setText(" " + (int) volPer + " %");
                                            if (volPer < 1) {
                                                vol_icon.setImageResource(R.drawable.ic_baseline_volume_off_24);
                                                vol_text.setVisibility(View.VISIBLE);
                                                vol_text.setText("Off");
                                            } else if (volPer >= 1) {
                                                vol_icon.setImageResource(R.drawable.ic_baseline_volume_up_24);
                                                vol_text.setVisibility(View.VISIBLE);
                                            }
                                            vol_progress_container.setVisibility(View.VISIBLE);
                                            vol_progress.setProgress((int) volPer);


//                                            Toast.makeText(CustomMediaPlayerActivity.this, "right swipe", Toast.LENGTH_SHORT).show();
                                        }
                                        success = true;
                                    } else {
                                        Toast.makeText(CustomMediaPlayerActivity.this, "Allow write settings for swipe controls", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                        intent.setData(Uri.parse("package:" + getPackageName()));

                                        startActivityForResult(intent, 111);
                                    }
                                }
                            }
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        swipe_move = false;
                        start = false;
                        precentageView.setVisibility(View.GONE);
                        vol_progress_container.setVisibility(View.GONE);
                        brt_progress_container.setVisibility(View.GONE);
                        vol_text_container.setVisibility(View.GONE);
                        brt_text_container.setVisibility(View.GONE);
                        break;

                }
                scaleGestureDetector.onTouchEvent(motionEvent1);

                zoom_container.setVisibility(View.VISIBLE);
                return false;

            }

            @Override
            public void onDoubleTouch() {
                super.onDoubleTouch();
            }

            @Override
            public void onSingleTouch() {
                super.onSingleTouch();
                if (singleTap) {
                    playerView.showController();
                    singleTap = false;
                } else {
                    playerView.hideController();
                    singleTap = true;
                }
            }
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

    private void checkOrientation() {
        orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            doZoom = true;
        } else {

            doZoom = false;
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

            // In portrait
        }

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

            scale_factor = detector.getScaleFactor();

            Log.e("sdfgh", "onScale: "+scale_factor );
            if (gestureTolerance(detector)) {
                // performing scaling
                scale_factor *= detector.getScaleFactor();
                scale_factor = Math.max(0.25f, Math.min(scale_factor, 3.0f));
                zoom_layout.setScaleX(scale_factor);
                zoom_layout.setScaleY(scale_factor);
                int percentage = (int) (scale_factor * 100);
                precentageView.setVisibility(View.VISIBLE);

                precentageView.setText(percentage + "%");
                zoom_container.setVisibility(View.VISIBLE);

            }
            return true;
        }

        /*    return super.onScale(detector);*/

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
                zoom_container.setVisibility(View.GONE);
                precentageView.setVisibility(View.GONE);
          /*  if (doZoom) {
                doVibrate();
                if (scale_factor > 1) {
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                } else {
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
//                player.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            }*/

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            boolean value;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                value = Settings.System.canWrite(getApplicationContext());
                if (value) {
                    success = true;
                } else {
                    Toast.makeText(this, "Not Granted", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        checkOrientation();
    }


    private void doVibrate(){
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(100);
        }
    }
}