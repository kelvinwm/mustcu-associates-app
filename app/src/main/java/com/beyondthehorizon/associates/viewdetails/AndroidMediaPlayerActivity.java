package com.beyondthehorizon.associates.viewdetails;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondthehorizon.associates.R;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.beyondthehorizon.associates.MainActivity.CHAT_PREFS;
import static com.beyondthehorizon.associates.chats.ChatActivity.MY_SHARED_PREF;

public class AndroidMediaPlayerActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    public TextView songName, duration, songTotalDuration;
    private double timeElapsed = 0, finalTime = 0;
    private int forwardTime = 2000, backwardTime = 2000;
    private Handler durationHandler = new Handler();
    private SeekBar seekbar;
    private SharedPreferences pref;
    private Uri myUri;
    private boolean isPlaying = false;
    private ImageButton media_play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_media_player);
        pref = getApplicationContext().getSharedPreferences(CHAT_PREFS, 0); // 0 - for private mode
        Intent intent = getIntent();
        getSupportActionBar().setTitle(intent.getStringExtra("audioName"));
        //initialize views
        initializeViews();
    }

    @SuppressLint("DefaultLocale")
    public void initializeViews() {
        songName = findViewById(R.id.songName);
        media_play = findViewById(R.id.media_play);
        Intent intent = getIntent();
        myUri = Uri.parse(intent.getStringExtra("audioUri"));
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(AndroidMediaPlayerActivity.this, myUri);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finalTime = mediaPlayer.getDuration();
        duration = findViewById(R.id.songDuration);
        songTotalDuration = findViewById(R.id.songTotalDuration);
        seekbar = findViewById(R.id.seekBar);
        songName.setText(intent.getStringExtra("audioName"));


        seekbar.setMax(mediaPlayer.getDuration() / 1000);
//        seekbar.setMax((int) finalTime);
//        seekbar.setClickable(false);

        // DRAG SONG TO POSITION
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }
        });
    }

    // play mp3 song
    @SuppressLint("DefaultLocale")
    public void play(View view) {
//        seekbar.setProgress((int) timeElapsed);
        durationHandler.postDelayed(updateSeekBarTime, 100);
        if (isPlaying) {
            //PAUSED
            mediaPlayer.pause();
            isPlaying = false;
            media_play.setImageResource(android.R.drawable.ic_media_play);
        } else {
            //PLAYING AUDIO
            mediaPlayer.start();
            timeElapsed = mediaPlayer.getCurrentPosition();
            isPlaying = true;
            media_play.setImageResource(android.R.drawable.ic_media_pause);
//            holder.playAudio.setBackgroundResource(R.drawable.ic_pause);
            seekbar.setMax(mediaPlayer.getDuration() / 1000);

            songTotalDuration.setText(String.format("%d min %d sec", TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getDuration()),
                    TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getDuration()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                            toMinutes((long) mediaPlayer.getDuration()))));

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @SuppressLint("DefaultLocale")
                @Override
                public void run() {
                    seekbar.setProgress(mediaPlayer.getCurrentPosition() / 1000);
//                    if (mediaPlayer.OnCompletionListener) {
//                        holder.playAudio.setBackgroundResource(R.drawable.ic_play_arrow);
//                        isPlaying = false;
//                        mediaPlayer.seekTo(0);
//                        holder.audioProgrs.setProgress(0);
//                        mediaPlayer.pause();
//                    }
                }
            }, 0, 100);

        }
    }

    //handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        @SuppressLint("DefaultLocale")
        public void run() {
            //get current position
            timeElapsed = mediaPlayer.getCurrentPosition();
            double timeRemaining = timeElapsed;
            duration.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining),
                    TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));

            //repeat yourself that again in 100 miliseconds
            durationHandler.postDelayed(this, 100);
        }
    };

    // pause mp3 song
    public void pause(View view) {
        mediaPlayer.pause();
    }

    // go forward at forwardTime seconds
    public void forward(View view) {
        //check if we can go forward at forwardTime seconds before song ends
        if ((mediaPlayer.getCurrentPosition() + forwardTime) <= mediaPlayer.getDuration()) {
            timeElapsed = timeElapsed + forwardTime;
            //seek to the exact second of the track
            mediaPlayer.seekTo((int) timeElapsed);
        }
    }

    public void rewind(View view) {
        //check if we can go backward at backwardTime seconds before song ends
        if ((mediaPlayer.getCurrentPosition() - backwardTime) >= 0) {
            timeElapsed = timeElapsed - backwardTime;
            //seek to the exact second of the track
            mediaPlayer.seekTo((int) timeElapsed);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.reset();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        finish();
    }

}