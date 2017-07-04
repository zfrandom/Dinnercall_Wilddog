package com.zf.android.dinnercallwilddog;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogauth.WilddogAuth;

/**
 * Created by zifeifeng on 6/14/17.
 */

public class RepeatActivity extends AppCompatActivity {
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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("RepeatActivity", "Alarm service starts now");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.repeat_activity);

        mAuth = WilddogAuth.getInstance();
        mSync = WilddogSync.getInstance().getReference().child("chat");
        mYesButton = (Button)findViewById(R.id.yes_button);
        mNoButton = (Button) findViewById(R.id.no_button);
        mIDKButton = (Button) findViewById(R.id.idk_btn);
        final Intent stopRingtoneIntent = new Intent(RepeatActivity.this, RingtonePlayService.class);
        stopRingtoneIntent.putExtra(playMusicFlag, false);
        mCountDownTimer = new CountDownTimer(10000, 2000) {
            @Override
            public void onTick(long l) {
                Toast.makeText(getApplicationContext(), "Waiting for response is counting down"+l/1000, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                startService(stopRingtoneIntent);
                Toast.makeText(getApplicationContext(), "The alarm will ring every 10 sec if there the timer is out", Toast.LENGTH_SHORT).show();
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
                startService(stopRingtoneIntent);
                //MainActivity.mReringToneAlarmManager.cancel(mPlayRingtoneIntent);
                finish();

            }
        });
        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSync.push()
                        .setValue(new ChatMessage("NO", mAuth.getCurrentUser().getDisplayName()));
                mCountDownTimer.cancel();
                startService(stopRingtoneIntent);
                //MainActivity.mReringToneAlarmManager.cancel(mPlayRingtoneIntent);
                finish();
            }
        });
        mIDKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCountDownTimer.cancel();
                Toast.makeText(getApplicationContext(), "IDK time start", Toast.LENGTH_SHORT).show();
                startService(stopRingtoneIntent);
                Intent i = new Intent(RepeatActivity.this, MainActivity.class);
                i.putExtra(THIS_IS_RA, true);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            }
        });
        Intent intentToPlayRingTone = new Intent(RepeatActivity.this, RingtonePlayService.class);
        intentToPlayRingTone.putExtra(playMusicFlag, true);
        getApplicationContext().startService(intentToPlayRingTone);
    }





}
