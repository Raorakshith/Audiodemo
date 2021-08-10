package com.example.audiodemo;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity1 extends AppCompatActivity {
private ImageView play,muteopt;
boolean muteplayer = false;
private SeekBar progressBar;
private TextView musicdurat;
private MediaPlayer mediaPlayer;
private Handler handler = new Handler();
private String totalduration;
private String audioUrl = "https://firebasestorage.googleapis.com/v0/b/life-of-book-7c4cd.appspot.com/o/Relaxing%20Music%20for%20Sleep%2C%20Meditation%20(No%20Copyright)%20Mood%20Videos.mp3?alt=media&token=9e469f7a-69d2-4c42-9bcd-269dac862455";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_1);
        play = findViewById(R.id.music_play);
        muteopt = findViewById(R.id.soundopt);
        progressBar = findViewById(R.id.progressBar);
        musicdurat = findViewById(R.id.musicduration);
        mediaPlayer = new MediaPlayer();
        progressBar.setMax(100);
        muteopt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!muteplayer){
                    muteplayer = true;
                    muteopt.setImageResource(R.drawable.ic_baseline_volume_off_24);
                    mediaPlayer.setVolume(0,0);
                }else {
                    muteplayer = false;
                    muteopt.setImageResource(R.drawable.ic_baseline_volume_up_24);
                    mediaPlayer.setVolume(1,1);
                }
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    play.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                }else {
                    mediaPlayer.start();
                    play.setImageResource(R.drawable.ic_baseline_pause_24);
                    updateProgressBar();
                }
            }
        });
        prepareMediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                progressBar.setSecondaryProgress(percent);
            }
        });
        progressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SeekBar progressBar1 = (SeekBar) v;
                int playPosition = (mediaPlayer.getDuration()/100)*progressBar1.getProgress();
                mediaPlayer.seekTo(playPosition);
                musicdurat.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()) + " / "+totalduration);
                return false;
            }
        });
    }
    private void prepareMediaPlayer(){
        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepare();
            totalduration = milliSecondsToTimer(mediaPlayer.getDuration());
            musicdurat.setText("00:00 / "+milliSecondsToTimer(mediaPlayer.getDuration()));
        }catch (Exception exception){
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateProgressBar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            musicdurat.setText(milliSecondsToTimer(currentDuration) + " / "+totalduration);
        }
    };
    private void updateProgressBar(){
        if(mediaPlayer.isPlaying()){
            progressBar.setProgress((int)(((float)mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration())*100));
            handler.postDelayed(updater,1000);
        }
    }
    private String milliSecondsToTimer(long milliseconds){
        String timeString = "";
        String secondsString;
        int hours = (int) (milliseconds/(1000*60*60));
        int minutes = (int) ((milliseconds % (1000*60*60))/(1000*60));
        int seconds = (int) ((milliseconds % (1000*60*60))%(1000*60)/1000);
        if(hours > 0){
            timeString = hours + ":";
        }
        if(seconds<10){
            secondsString = "0"+seconds;
        }else {
            secondsString = ""+seconds;
        }
        timeString = timeString + minutes+":" + secondsString;
        return timeString;
    }
}