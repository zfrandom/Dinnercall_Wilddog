package com.zf.android.dinnercallwilddog.Activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogauth.WilddogAuth;
import com.zf.android.dinnercallwilddog.Service.NetworkReceiver;
import com.zf.android.dinnercallwilddog.Utils.ChatMessage;
import com.zf.android.dinnercallwilddog.Utils.MyAlarmManager;
import com.zf.android.dinnercallwilddog.R;
import com.zf.android.dinnercallwilddog.Service.RingtonePlayService;

import java.io.Serializable;

/**
 * Created by zifeifeng on 6/14/17.
 */

public class RepeatActivity extends AppCompatActivity implements Serializable, NetworkReceiver.MyNetworkCallback{
    private Button mYesButton;
    private Button mNoButton;
    private Button mIDKButton;
    public static final String playMusicFlag = "play";
    private CountDownTimer mCountDownTimer;
    public static final int YES_RESPONSE=234;
    public static final int NO_RESPONSE=235;
    private PendingIntent mPlayRingtoneIntent;
    public static final String THIS_IS_RA = "thisisme";
    private WilddogAuth mAuth;
    private SyncReference mSync;
    private setTmrAlarmCallBack mSetTmrAlarmCallBack;
    public static String ACTIVITY_INTENT = "activity";
    private MediaPlayer mMediaSong;
    Uri ringToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    private boolean isPlaying;
    private final String TAG = "RepeatActivity";

    @Override
    public void hasNetWork() {

    }

    @Override
    public void loseNetWork() {

    }

    public interface setTmrAlarmCallBack {
        void setTmr();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("RepeatActivity", "Alarm service starts now");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.repeat_activity);
        mSetTmrAlarmCallBack = MyAlarmManager.get(getApplicationContext());
        mAuth = WilddogAuth.getInstance();
        mSync = WilddogSync.getInstance().getReference().child("chat");
        mYesButton = (Button)findViewById(R.id.yes_button);
        mNoButton = (Button) findViewById(R.id.no_button);
        mIDKButton = (Button) findViewById(R.id.idk_btn);
        final Intent stopRingtoneIntent = new Intent(RepeatActivity.this, RingtonePlayService.class);
        stopRingtoneIntent.putExtra(playMusicFlag, false);
        mSetTmrAlarmCallBack.setTmr();
        mCountDownTimer = new CountDownTimer(3*10000, 2000) {
            @Override
            public void onTick(long l) {
                //Toast.makeText(getApplicationContext(), "Waiting for response is counting down"+l/1000, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
//                startService(stopRingtoneIntent);
                stopMusic();
                //Toast.makeText(getApplicationContext(), "The alarm will ring every 10 sec if there the timer is out", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(RepeatActivity.this, MainActivity.class);
                i.putExtra(THIS_IS_RA, true);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            }
        };

        mCountDownTimer.start();

        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSync.push()
                        .setValue(new ChatMessage("YES", mAuth.getCurrentUser().getDisplayName()));
                mCountDownTimer.cancel();
                stopMusic();

                finish();

            }
        });
        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mSync.push()
                            .setValue(new ChatMessage("NO", mAuth.getCurrentUser().getDisplayName()));
                }
                catch (Exception e){
                    Log.e("Repeat Activity ",e.toString());
                }
                mCountDownTimer.cancel();
//                startService(stopRingtoneIntent);
                stopMusic();
                finish();
            }
        });
        mIDKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCountDownTimer.cancel();
                //Toast.makeText(getApplicationContext(), "IDK time start", Toast.LENGTH_SHORT).show();
                startService(stopRingtoneIntent);
                Intent i = new Intent(RepeatActivity.this, MainActivity.class);
                i.putExtra(THIS_IS_RA, true);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            }
        });
        playMusic();
    }


    private void playMusic(){
        mMediaSong = new MediaPlayer();


        try {
            mMediaSong.setVolume(1.0f, 1.0f);
            Log.e("Ringtone Service", ringToneUri.toString());
            mMediaSong.setDataSource(this, ringToneUri);
            mMediaSong.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaSong.setLooping(true);
            mMediaSong.prepare();
            mMediaSong.start();
            isPlaying =true;

        } catch (Exception e) {
            mMediaSong.release();
            Log.e("RingtonePlayService", "no suond");
        }
    }

    private void stopMusic(){
        isPlaying = false;
        if(mMediaSong!=null&&mMediaSong.isPlaying()) {
            mMediaSong.pause();
            mMediaSong.release();
            mMediaSong=null;
            Log.e(TAG, "Stop the song");
        }
    }







}
