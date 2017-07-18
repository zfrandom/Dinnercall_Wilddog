package com.zf.android.dinnercallwilddog.Service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.zf.android.dinnercallwilddog.Activity.RepeatActivity;
import com.zf.android.dinnercallwilddog.R;

import java.io.IOException;

/**
 * Created by zifeifeng on 6/16/17.
 */

public class RingtonePlayService extends Service {
    static MediaPlayer mMediaSong;
    boolean isPlaying = false;
    private static final String TAG = "RingtonePlayService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean play = intent.getBooleanExtra(RepeatActivity.playMusicFlag, false);
        Log.i(TAG, "the play is " + play);
        Uri ringToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if(play) {
            mMediaSong = new MediaPlayer();
//            try {
//                mMediaSong = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
//                mMediaSong.setDataSource(this, ringToneUri);
//                mMediaSong.setAudioStreamType(AudioManager.STREAM_RING);
//                  mMediaSong.prepare();
//                  mMediaSong.start();
//
//            }
//            catch (IOException e){
//                mMediaSong = MediaPlayer.create(this, R.raw.ringtone);
//                mMediaSong.setAudioStreamType(AudioManager.STREAM_RING);
//                try {
//                    mMediaSong.prepare();
//                    mMediaSong.start();
//                }
//                catch (IOException ioe){
//                    Toast.makeText(getApplicationContext(), "no sound", Toast.LENGTH_SHORT).show();
//                }
//
//            }


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
        else {
            if(mMediaSong!=null&&mMediaSong.isPlaying()) {
                mMediaSong.pause();
                mMediaSong.release();
                mMediaSong=null;
                Log.e(TAG, "Stop the song");
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isPlaying = false;
    }
}
